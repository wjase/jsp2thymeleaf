/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf;

import static com.cybernostics.jsp2thymeleaf.api.elements.ActiveTaglibConverters.addTaglibConverter;
import static com.cybernostics.jsp2thymeleaf.converters.AvailableConverters.scanForConverters;
import com.cybernostics.jsp2thymeleaf.converters.JSP2ThymeLeafConverterException;
import com.cybernostics.jsp2thymeleaf.converters.identity.DefaultElementConverterSource;
import com.cybernostics.jsp2thymeleaf.util.JSPIncludeSorter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jason
 */
public class JSP2Thymeleaf
{

    public static final Logger logger = Logger.getLogger(JSP2Thymeleaf.class.getName());
    private JSP2ThymeleafConfiguration configuration;

    public JSP2Thymeleaf(JSP2ThymeleafConfiguration configuration)
    {
        scanForConverters();
        addTaglibConverter("", new DefaultElementConverterSource());
        this.configuration = configuration;
    }

    public static void main(String[] args)
    {
        final JSP2ThymeleafConfiguration config = JSP2ThymeleafConfiguration.parse(args);
        final JSP2Thymeleaf jsP2Thymeleaf = new JSP2Thymeleaf(config);
        jsP2Thymeleaf.run();
    }

    public List<JSP2ThymeLeafConverterException> run()
    {
        final List<Path> filesToProcess = JSPIncludeSorter.directedAcyclicSort(configuration.getFilesToProcess());
        JSP2ThymeleafStreamConverter converter = new JSP2ThymeleafStreamConverter();
        converter.setShowBanner(configuration.isShowBanner());

        List<JSP2ThymeLeafConverterException> exceptions = new ArrayList<>();

        for (Path eachInputFile : filesToProcess)
        {
            try
            {
                logger.log(Level.INFO, "JSP2Thymeleaf processing:" + eachInputFile.toString());
                java.io.InputStream is = new FileInputStream(eachInputFile.toFile());
                final File outputFilePath = configuration.getOutputPathFor(eachInputFile).toFile();
                java.io.OutputStream os = new FileOutputStream(outputFilePath);
                converter.convert(is, os);
                os.flush();
                os.close();
                is.close();
                logger.log(Level.INFO, "JSP2Thymeleaf wrote:" + outputFilePath.toString());

            } catch (Exception exception)
            {
                exceptions.add(new JSP2ThymeLeafConverterException(exception, eachInputFile));
            }
        }
        return exceptions;
    }

}
