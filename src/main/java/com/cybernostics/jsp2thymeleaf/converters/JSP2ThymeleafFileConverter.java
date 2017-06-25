/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters;

import com.cybernostics.jsp.parser.JSPParser;
import com.cybernostics.jsp2thymeleaf.JSP2ThymeleafConfiguration;
import com.cybernostics.jsp2thymeleaf.ScriptInlineSpanConverter;
import com.cybernostics.jsp2thymeleaf.api.common.TokenisedFile;
import com.cybernostics.jsp2thymeleaf.api.common.dom.DomWalker;
import com.cybernostics.jsp2thymeleaf.api.elements.ScopedJSPConverters;
import com.cybernostics.jsp2thymeleaf.api.exception.JSP2ThymeLeafException;
import static com.cybernostics.jsp2thymeleaf.converters.ConverterScanner.scanForConverters;
import com.cybernostics.jsp2thymeleaf.parser.DomBlockCleaner;
import com.cybernostics.jsp2thymeleaf.parser.JSP2ThymeleafTransformerListener;
import static com.cybernostics.jsp2thymeleaf.parser.XMLDocumentWriter.write;
import java.io.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.jdom2.Document;

/**
 *
 * @author jason
 */
public class JSP2ThymeleafFileConverter
{

    public static final Logger logger = Logger.getLogger(JSP2ThymeleafFileConverter.class.getName());
    private boolean showBanner = true;

    public JSP2ThymeleafFileConverter(JSP2ThymeleafConfiguration configuration)
    {
        scanForConverters(configuration);

    }

    public void setShowBanner(boolean showBanner)
    {
        this.showBanner = showBanner;
    }

    public List<JSP2ThymeLeafException> convert(TokenisedFile file, File toWrite, ScopedJSPConverters converterScope)
    {

        JSP2ThymeleafTransformerListener parsedElementListener = new JSP2ThymeleafTransformerListener(converterScope);
        final JSP2ThymeleafErrorCollector jsp2ThymeleafErrorCollector = new JSP2ThymeleafErrorCollector(file);
        try
        {
            CommonTokenStream tokens = new CommonTokenStream(file.getLexer());
            // Pass the tokens to the parser
            JSPParser parser = new JSPParser(tokens);
            parser.addErrorListener(jsp2ThymeleafErrorCollector);
            // Specify our entry point
            JSPParser.JspDocumentContext documentContext = parser.jspDocument();
            // Walk it and attach our parsedElementListener
            ParseTreeWalker walker = new ParseTreeWalker();
            parsedElementListener.setShowBanner(showBanner);
            walker.walk(parsedElementListener, documentContext);

            final Document document = parsedElementListener.getDocument();
            if (document.hasRootElement())
            {
                DomWalker docwalker = new DomWalker(DomBlockCleaner.get(),
                        ScriptInlineSpanConverter.get());
                docwalker.walk(document.getRootElement());
                write(document, new FileOutputStream(toWrite));
            }

            jsp2ThymeleafErrorCollector.add(parsedElementListener.getProblems());
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(JSP2ThymeleafFileConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jsp2ThymeleafErrorCollector.getExceptions();
    }

}
