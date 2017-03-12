/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters;

import com.cybernostics.jsp.parser.JSPParser;
import com.cybernostics.jsp2thymeleaf.JSP2ThymeleafConfiguration;
import com.cybernostics.jsp2thymeleaf.api.common.TokenisedFile;
import com.cybernostics.jsp2thymeleaf.api.elements.*;
import com.cybernostics.jsp2thymeleaf.api.exception.JSP2ThymeLeafException;
import static com.cybernostics.jsp2thymeleaf.converters.ConverterScanner.scanForConverters;
import com.cybernostics.jsp2thymeleaf.parser.JSP2ThymeleafTransformerListener;
import java.io.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 *
 * @author jason
 */
public class JSP2ThymeleafFileConverter
{

    public static final Logger logger = Logger.getLogger(JSP2ThymeleafFileConverter.class.getName());
    private boolean showBanner = true;
    private JSPElementNodeConverter elementConverter = new CopyElementConverter();

    public JSP2ThymeleafFileConverter(JSP2ThymeleafConfiguration configuration)
    {
        scanForConverters(configuration);

    }

    public void setShowBanner(boolean showBanner)
    {
        this.showBanner = showBanner;
    }

    public List<JSP2ThymeLeafException> convert(TokenisedFile file, File toWrite)
    {

        JSP2ThymeleafTransformerListener listener = new JSP2ThymeleafTransformerListener();
        final JSP2ThymeleafErrorCollector jsp2ThymeleafErrorCollector = new JSP2ThymeleafErrorCollector(file);
        try
        {
            CommonTokenStream tokens = new CommonTokenStream(file.getLexer());
            // Pass the tokens to the parser
            JSPParser parser = new JSPParser(tokens);
            parser.addErrorListener(jsp2ThymeleafErrorCollector);
            // Specify our entry point
            JSPParser.JspDocumentContext documentContext = parser.jspDocument();
            // Walk it and attach our listener
            ParseTreeWalker walker = new ParseTreeWalker();
            listener.setShowBanner(showBanner);
            walker.walk(listener, documentContext);

            listener.write(new FileOutputStream(toWrite));

            jsp2ThymeleafErrorCollector.add(listener.getProblems());
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(JSP2ThymeleafFileConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jsp2ThymeleafErrorCollector.getExceptions();
    }

}
