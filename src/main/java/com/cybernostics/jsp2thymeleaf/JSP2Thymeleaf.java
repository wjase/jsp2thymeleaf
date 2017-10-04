/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf;

import com.cybernostics.jsp2thymeleaf.api.common.TokenisedFile;
import com.cybernostics.jsp2thymeleaf.api.elements.ScopedJSPConverters;
import com.cybernostics.jsp2thymeleaf.api.exception.JSP2ThymeLeafException;
import com.cybernostics.jsp2thymeleaf.converters.JSP2ThymeleafFileConverter;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toMap;

/**
 * Application entry point for JSP2Thymeleaf when called from
 * the command line.
 * @author jason
 */
public class JSP2Thymeleaf
{

    public static void main(String[] args) {
        final JSP2ThymeleafConfiguration config = JSP2ThymeleafConfiguration.parse(args);
        final JSP2Thymeleaf jsP2Thymeleaf = new JSP2Thymeleaf(config);
        jsP2Thymeleaf.run();
    }


    private static final Logger logger = Logger.getLogger(JSP2Thymeleaf.class.getName());
    private JSP2ThymeleafConfiguration configuration;
    private Set<TokenisedFile> alreadyProcessed = new TreeSet<>();
    private List<JSP2ThymeLeafException> exceptions;
    private JSP2ThymeleafFileConverter converter;

    public JSP2Thymeleaf(JSP2ThymeleafConfiguration configuration)
    {
        this.configuration = configuration;
        converter = new JSP2ThymeleafFileConverter(configuration);
        converter.setShowBanner(configuration.isShowBanner());
        exceptions = new ArrayList<>();
    }

    public List<JSP2ThymeLeafException> run()
    {
        tokenisedFilesMap = configuration
                .getFilesToProcess()
                .stream()
                .map(path -> new TokenisedFile(path, configuration.getRootFolder()))
                .sorted()
                .collect(toMap(TokenisedFile::getPath, it -> it));

        return tokenisedFilesMap
                .values()
                .stream()
                .sorted()
                .flatMap(eachInputFile -> convertFile(eachInputFile).stream())
                .collect(Collectors.toList());
    }

    private Map<Path, TokenisedFile> tokenisedFilesMap;

    private List<JSP2ThymeLeafException> convertFile(TokenisedFile eachInputFile)
    {
        return convertFile(eachInputFile, null);
    }

    private List<JSP2ThymeLeafException> convertFile(TokenisedFile fileToConvert, ScopedJSPConverters parentScope)
    {
        ScopedJSPConverters myConverter = new ScopedJSPConverters(parentScope);
        List<JSP2ThymeLeafException> exceptions = new ArrayList<>();
        if (!alreadyProcessed.contains(fileToConvert))
        {
            final File outputFilePath = configuration.getOutputPathFor(fileToConvert.getFilePath()).toFile();
            try
            {
                logger.log(Level.INFO, "JSP2Thymeleaf processing:" + fileToConvert.toString());
                exceptions.addAll(converter.convert(fileToConvert, outputFilePath, myConverter));
                logger.log(Level.INFO, "JSP2Thymeleaf wrote:" + outputFilePath.toString());
                List<TokenisedFile> includedFiles = fileToConvert.getIncludedPaths()
                        .stream()
                        .map(path -> tokenisedFilesMap.get(path))
                        .collect(Collectors.toList());
                exceptions.addAll(
                        includedFiles.stream()
                                .flatMap(includedFile -> convertFile(includedFile, myConverter).stream())
                                .collect(Collectors.toList()));
            } catch (Throwable exception)
            {
                exceptions.add(JSP2ThymeLeafException.jsp2ThymeLeafExceptionBuilder(exception).build());
            } finally
            {
                alreadyProcessed.add(fileToConvert);
            }

        }
        return exceptions;
    }

}
