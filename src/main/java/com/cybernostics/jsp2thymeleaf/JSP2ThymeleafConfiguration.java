/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf;

import com.cybernostics.jsp2thymeleaf.api.util.SetUtils;
import com.cybernostics.jsp2thymeleaf.converters.AllJstlConverters;
import com.cybernostics.jsp2thymeleaf.util.Globber;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Arrays;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.EMPTY_LIST;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.PosixParser;

/**
 * Contains parameters to control the conversion process.
 *
 * @author jason
 */
public class JSP2ThymeleafConfiguration
{

    private boolean showBanner;

    private Path srcFolder;
    private Path destFolder;
    private Path rootFolder;

    private static final String[] EMPTY = new String[0];
    private String[] includes = EMPTY;
    private String[] excludes = EMPTY;
    private String[] filenames = EMPTY;

    private Set<Path> filesToProcess;

    private FileAttribute GROUP_WRITABLE = PosixFilePermissions.asFileAttribute(SetUtils.setOf(
            PosixFilePermission.GROUP_READ,
            PosixFilePermission.GROUP_WRITE,
            PosixFilePermission.OWNER_READ,
            PosixFilePermission.OWNER_WRITE
    ));
    private List<String> converterPackages = new ArrayList<>();

    private List<Path> converterScripts = EMPTY_LIST;

    public Set<Path> getFilesToProcess()
    {
        return filesToProcess;
    }

    private JSP2ThymeleafConfiguration()
    {
        converterPackages.addAll(Arrays.asList(AllJstlConverters.class.getPackage().getName()));
    }

    /**
     * Used for API access to the configuration
     */
    public static JSP2ThymeleafConfigurationBuilder getBuilder()
    {
        return new JSP2ThymeleafConfigurationBuilder();
    }

    public static JSP2ThymeleafConfiguration parse(String... args)
    {
        try
        {
            Options options = new Options();
            options.addOption("p", "taglib-converter-pkgs", true, "List of classpath packages containing additional taglib converters.");
            options.addOption("g", "taglib-converter-scripts", true, "The location of script files containing additional taglib converters.");
            options.addOption("s", "source-folder", true, "The location of the jsp files to convert.");
            options.addOption("d", "dest-folder", true, "The location of the jsp files to convert.");
            options.addOption("i", "includes", true, "Comma-separated list of ant patterns to include.\n  Default is *.jsp,*.jspx,*.jspf");
            options.addOption("e", "excludes", true, "Comma-separated list of ant patterns to exclude.\n  Default is empty");
            options.addOption("b", "show-banner", false, "Whether to add the JSP2thymeleaf banner to the JSPs");
            final JSP2ThymeleafConfiguration config = new JSP2ThymeleafConfiguration();

            Parser parser = new PosixParser();
            final CommandLine parsedArgs = parser.parse(options, args);
            config.destFolder = Paths.get(parsedArgs.getOptionValue("d", "")).toAbsolutePath();
            config.srcFolder = Paths.get(parsedArgs.getOptionValue("s", "")).toAbsolutePath();
            config.includes = parsedArgs.getOptionValue("i", "**/*.jsp,**/*.jspx,**/*.jspf").split(",");
            config.excludes = parsedArgs.getOptionValue("e", "").split(",");
            config.showBanner = parsedArgs.hasOption("b");
            config.filenames = parsedArgs.getArgs();
            config.converterPackages.addAll(
                    Arrays.stream(parsedArgs.getOptionValue("p", "")
                            .split(","))
                            .filter(it -> it.length() > 0)
                            .collect(toList()));
            config.converterScripts = Arrays.stream(parsedArgs.getOptionValue("g", "").split(","))
                    .filter(it -> it.length() > 0)
                    .map(filename -> Paths.get(filename))
                    .collect(toList());

            config.processPathParameters();

            return config;
        } catch (ParseException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    public boolean isShowBanner()
    {
        return showBanner;
    }

    public Path getSrcFolder()
    {
        return srcFolder;
    }

    public Path getDestFolder()
    {
        return destFolder;
    }

    public String[] getIncludes()
    {
        return includes;
    }

    public String[] getExcludes()
    {
        return excludes;
    }

    public String[] getFilenames()
    {
        return filenames;
    }

    public Path getOutputPathFor(Path inputFile)
    {
        Path outputPath = inputFile;
        if (inputFile.startsWith(srcFolder))
        {
            Path relative = srcFolder.relativize(inputFile);
            outputPath = destFolder.resolve(relative);
        }

        // now change extension to .html
        String filename = outputPath.getFileName().toString();
        String newFilename = filename.replaceAll("\\.[^.]+$", ".html");

        if (inputFile.equals(outputPath))
        {
            throw new IllegalArgumentException("Source cannot be the same as destination:" + inputFile.toString());
        }
        final Path parentPath = outputPath.getParent();
        ensureExists(parentPath);
        return parentPath.resolve(newFilename);

    }

    private void processPathParameters()
    {
        if (srcFolder != null)
        {
            filesToProcess = Globber.match(srcFolder.toString(), includes);
            final Set<Path> exclusions = Globber.match(srcFolder.toString(), excludes);
            filesToProcess.removeAll(exclusions);

            for (String filename : filenames)
            {
                final Path eachPath = Paths.get(filename);
                if (eachPath.isAbsolute())
                {
                    filesToProcess.add(eachPath);
                } else
                {
                    filesToProcess.add(srcFolder.resolve(eachPath));
                }
            }
            reportAnyMissingFiles();

            ensureExists(destFolder);

        }
    }

    private void reportAnyMissingFiles()
    {
        final List<Path> missingFiles = filesToProcess
                .stream()
                .filter(it -> !it.toFile().exists())
                .collect(toList());
        if (!missingFiles.isEmpty())
        {
            throw new IllegalArgumentException("File(s) not found:"
                    + missingFiles
                            .stream()
                            .map(Path::toString)
                            .collect(Collectors.joining(" , "))
            );
        }
    }

    private void ensureExists(Path destFolder)
    {
        if (!Files.exists(destFolder))
        {
            try
            {
                Files.createDirectories(destFolder);
            } catch (IOException ex)
            {
                Logger.getLogger(JSP2ThymeleafConfiguration.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            }
        }
        if (!Files.exists(destFolder))
        {
            throw new RuntimeException("Unable to create path:" + destFolder.toString() + ".\n Check permissions and storage space.");
        }
    }

    public Path getRootFolder()
    {
        return rootFolder != null ? rootFolder : srcFolder;
    }

    public List<String> getConverterPackages()
    {
        return converterPackages;
    }

    public List<Path> getConverterScripts()
    {
        return converterScripts;
    }

    public static class JSP2ThymeleafConfigurationBuilder
    {

        private JSP2ThymeleafConfiguration configuration = new JSP2ThymeleafConfiguration();

        public JSP2ThymeleafConfigurationBuilder()
        {
        }

        public JSP2ThymeleafConfigurationBuilder withIncludes(String... includes)
        {
            configuration.includes = includes;
            return this;
        }

        public JSP2ThymeleafConfigurationBuilder withExcludes(String... excludes)
        {
            configuration.excludes = excludes;
            return this;
        }

        public JSP2ThymeleafConfigurationBuilder withDestFolder(String destFolder)
        {
            configuration.destFolder = Paths.get(destFolder).toAbsolutePath();;
            return this;
        }

        public JSP2ThymeleafConfigurationBuilder withSrcFolder(String srcFolder)
        {
            configuration.srcFolder = Paths.get(srcFolder).toAbsolutePath();;
            return this;
        }

        public JSP2ThymeleafConfigurationBuilder withShowBanner(Boolean showBanner)
        {
            configuration.showBanner = showBanner;
            return this;
        }

        public JSP2ThymeleafConfigurationBuilder withFileNames(String... filenames)
        {
            configuration.filenames = filenames;
            return this;
        }

        public JSP2ThymeleafConfigurationBuilder withConverterPackages(String... packages)
        {
            configuration.converterPackages.addAll(asList(packages));
            return this;
        }

        public JSP2ThymeleafConfigurationBuilder withConverterScripts(String... converterScripts)
        {
            configuration.converterScripts = stream(converterScripts)
                    .filter(it -> it.length() > 0)
                    .map(it -> Paths.get(it))
                    .collect(toList());
            return this;
        }

        public JSP2ThymeleafConfiguration build()
        {
            configuration.processPathParameters();
            return configuration;
        }

    }

}
