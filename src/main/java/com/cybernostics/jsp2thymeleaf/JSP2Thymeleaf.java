/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf;

import static com.cybernostics.jsp2thymeleaf.converters.AvailableConverters.scanForConverters;
import com.cybernostics.jsp2thymeleaf.converters.JSP2ThymeLeafConverterException;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toMap;

/**
 *
 * @author jason
 */
public class JSP2Thymeleaf
{

    public static final Logger logger = Logger.getLogger(JSP2Thymeleaf.class.getName());
    private JSP2ThymeleafConfiguration configuration;
    private Set<TokenisedFile> alreadyProcessed = new TreeSet<>();

    public JSP2Thymeleaf(JSP2ThymeleafConfiguration configuration)
    {
        scanForConverters();
        this.configuration = configuration;
        converter = new JSP2ThymeleafFileConverter();
        converter.setShowBanner(configuration.isShowBanner());
        exceptions = new ArrayList<>();

    }
    private List<JSP2ThymeLeafConverterException> exceptions;
    private JSP2ThymeleafFileConverter converter;

    public static void main(String[] args)
    {
        final JSP2ThymeleafConfiguration config = JSP2ThymeleafConfiguration.parse(args);
        final JSP2Thymeleaf jsP2Thymeleaf = new JSP2Thymeleaf(config);
        jsP2Thymeleaf.run();
    }

    public List<JSP2ThymeLeafConverterException> run()
    {
        tokenisedFilesMap = configuration
                .getFilesToProcess()
                .stream()
                .map(path -> new TokenisedFile(path, configuration.getRootFolder()))
                .sorted()
                .collect(toMap(TokenisedFile::getPath, it -> it));

        tokenisedFilesMap.values().stream().sorted().forEachOrdered(eachInputFile -> convertFile(eachInputFile));
        return exceptions;
    }
    private Map<Path, TokenisedFile> tokenisedFilesMap;

    private void convertFile(TokenisedFile eachInputFile)
    {
        if (!alreadyProcessed.contains(eachInputFile))
        {
            final File outputFilePath = configuration.getOutputPathFor(eachInputFile.getFilePath()).toFile();
            try
            {
                logger.log(Level.INFO, "JSP2Thymeleaf processing:" + eachInputFile.toString());
                converter.convert(eachInputFile, outputFilePath);
                logger.log(Level.INFO, "JSP2Thymeleaf wrote:" + outputFilePath.toString());
                eachInputFile.getIncludedPaths()
                        .stream()
                        .map(path -> tokenisedFilesMap.get(path))
                        .forEach(tokenisedFile -> convertFile(tokenisedFile));

            } catch (Exception exception)
            {
                exceptions.add(new JSP2ThymeLeafConverterException(exception, eachInputFile.getFilePath()));
            } finally
            {
                alreadyProcessed.add(eachInputFile);
            }

        }
    }

}
