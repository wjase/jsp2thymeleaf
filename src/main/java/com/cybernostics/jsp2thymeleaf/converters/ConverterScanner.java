/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters;

import com.cybernostics.jsp2thymeleaf.JSP2Thymeleaf;
import com.cybernostics.jsp2thymeleaf.JSP2ThymeleafConfiguration;
import com.cybernostics.jsp2thymeleaf.api.common.AvailableConverters;
import com.cybernostics.jsp2thymeleaf.api.common.DefaultElementConverterSource;
import com.cybernostics.jsp2thymeleaf.api.common.DefaultFunctionConverterSource;
import com.cybernostics.jsp2thymeleaf.api.common.taglib.ConverterRegistration;
import com.cybernostics.jsp2thymeleaf.api.elements.TagConverterSource;
import com.cybernostics.jsp2thymeleaf.api.expressions.function.FunctionConverterSource;
import groovy.lang.GroovyShell;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import static java.util.Arrays.stream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.joining;

/**
 * Scans for available converters on classpath
 *
 * @author jason
 */
public class ConverterScanner
{

    public static void scanForConverters(JSP2ThymeleafConfiguration configuration)
    {

        registerConvertersFromClasspath(configuration);
        registerConvertersFromScripts(configuration);

    }

    private static void registerConvertersFromClasspath(JSP2ThymeleafConfiguration configuration)
    {
        String converterPackages = configuration.getConverterPackages().stream().collect(joining(":"));

        List<String> paths = Arrays.stream(converterPackages.split(":")).filter(it -> !it.isEmpty()).collect(Collectors.toList());
        for (String path : paths)
        {

            ScanResult scanResult = new FastClasspathScanner(path).scan();
            List<String> tagConverterNames
                    = scanResult.getNamesOfClassesImplementing(ConverterRegistration.class);

            tagConverterNames
                    .stream()
                    .forEach((converter -> loadAndRegister(converter)));

        }
    }

    public void addConverter(String forURI, TagConverterSource converterSource)
    {
        AvailableConverters.addConverter(forURI, converterSource);
    }

    public void addConverter(String forURI, FunctionConverterSource converterSource)
    {
        AvailableConverters.addConverter(forURI, converterSource);
    }

    public static void addConverter(TagConverterSource converterSource)
    {
        AvailableConverters.addConverter(converterSource.getTaglibURI(), converterSource);
    }

    public static void addConverter(FunctionConverterSource converterSource)
    {
        AvailableConverters.addConverter(converterSource.getTaglibURI(), converterSource);
    }

    private static void loadAndRegister(String className)
    {
        try
        {
            final Class<?> clazz = Class.forName(className);
            if (!Modifier.isAbstract(clazz.getModifiers()))
            {
                ConverterRegistration converterRegistration = (ConverterRegistration) clazz.newInstance();
                converterRegistration.run();
            }
        } catch (Throwable ex)
        {
            Logger.getLogger(JSP2Thymeleaf.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new RuntimeException(ex);

        }

    }
    private TagConverterSource defaultConverterSource = new DefaultElementConverterSource();
    private FunctionConverterSource defaultFunctionConverterSource = new DefaultFunctionConverterSource();

    public TagConverterSource getDetaultTagConverter()
    {
        return defaultConverterSource;
    }

    public FunctionConverterSource getDefaultFunctionConverterSource()
    {
        return defaultFunctionConverterSource;
    }

    private static void registerConvertersFromScripts(JSP2ThymeleafConfiguration configuration)
    {
        configuration.getConverterScripts().forEach(scriptPath -> executeScript(scriptPath));
    }

    private static void executeScript(Path scriptPath)
    {
        final File scriptPathAsFile = scriptPath.toFile();
        if (scriptPathAsFile.exists())
        {
            if (scriptPathAsFile.isDirectory())
            {
                final String[] files = scriptPathAsFile.list();
                stream(files)
                        .map(it -> scriptPath.resolve(it))
                        .filter(it -> it.toString().endsWith(".groovy") || it.toFile().isDirectory())
                        .forEach(ConverterScanner::executeScript);
            } else
            {
                try
                {
                    String groovyScript = new String(Files.readAllBytes(scriptPath));
                    // call groovy expressions from Java code
                    GroovyShell shell = new GroovyShell(Thread.currentThread().getContextClassLoader());
                    shell.evaluate(groovyScript);
                } catch (IOException ex)
                {
                    Logger.getLogger(ConverterScanner.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        } else
        {
            throw new IllegalArgumentException("Path does not exist:" + scriptPath);
        }
    }

}
