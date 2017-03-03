/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters;

import com.cybernostics.jsp2thymeleaf.api.common.DefaultFunctionConverterSource;
import com.cybernostics.jsp2thymeleaf.JSP2Thymeleaf;
import com.cybernostics.jsp2thymeleaf.JSP2ThymeleafConfiguration;
import com.cybernostics.jsp2thymeleaf.api.common.DefaultElementConverterSource;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.joining;

/**
 * Maintains a pool of available converters for taglibs based on the URI. In
 * some cases different or custom URIs are used to
 *
 * @author jason
 */
public class AvailableConverters
{

    private static final Map<String, TagConverterSource> availableTagConverters = new HashMap<>();
    private static final Map<String, FunctionConverterSource> availableExpressionConverters = new HashMap<>();

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

    public static void addConverter(String forURI, TagConverterSource converterSource)
    {
        availableTagConverters.put(forURI, converterSource);
    }

    public static void addConverter(String forURI, FunctionConverterSource converterSource)
    {
        availableExpressionConverters.put(forURI, converterSource);
    }

    public static void addConverter(TagConverterSource converterSource)
    {
        availableTagConverters.put(converterSource.getTaglibURI(), converterSource);
    }

    public static void addConverter(FunctionConverterSource converterSource)
    {
        availableExpressionConverters.put(converterSource.getTaglibURI(), converterSource);
    }

    public static Optional<TagConverterSource> elementConverterforUri(String uri)
    {
        return Optional.ofNullable(availableTagConverters.getOrDefault(uri, null));
    }

    public static Optional<FunctionConverterSource> functionConverterforUri(String uri)
    {
        return Optional.ofNullable(availableExpressionConverters.getOrDefault(uri, null));
    }

    public static void addUriAlias(String existingUri, String aliasUri)
    {
        if (!availableTagConverters.containsKey(existingUri))
        {
            if (availableExpressionConverters.containsKey(existingUri))
            {
                throw new IllegalArgumentException("Unknown URI:" + existingUri);
            } else
            {
                availableExpressionConverters.put(aliasUri, availableExpressionConverters.get(existingUri));
            }
        } else
        {
            availableTagConverters.put(aliasUri, availableTagConverters.get(existingUri));
        }
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
                        .forEach(AvailableConverters::executeScript);
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
                    Logger.getLogger(AvailableConverters.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        } else
        {
            throw new IllegalArgumentException("Path does not exist:" + scriptPath);
        }
    }

    public static void reset()
    {
        availableTagConverters.clear();
        availableExpressionConverters.clear();
    }

}
