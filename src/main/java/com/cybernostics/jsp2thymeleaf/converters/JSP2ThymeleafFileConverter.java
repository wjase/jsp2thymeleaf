/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters;

import com.cybernostics.jsp.parser.JSPParser;
import com.cybernostics.jsp2thymeleaf.JSP2ThymeleafConfiguration;
import com.cybernostics.jsp2thymeleaf.api.elements.*;
import static com.cybernostics.jsp2thymeleaf.converters.AvailableConverters.scanForConverters;
import com.cybernostics.jsp2thymeleaf.parser.JSP2ThymeleafTransformerListener;
import com.cybernostics.jsp2thymeleaf.parser.TokenisedFile;
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

    public List<Exception> convert(TokenisedFile file, File toWrite)
    {

        final JSP2ThymeleafErrorCollector jsP2ThymeleafErrorCollector = new JSP2ThymeleafErrorCollector(file);
        try
        {
            CommonTokenStream tokens = new CommonTokenStream(file.getLexer());
            // Pass the tokens to the parser
            JSPParser parser = new JSPParser(tokens);
            parser.addErrorListener(jsP2ThymeleafErrorCollector);
            // Specify our entry point
            JSPParser.JspDocumentContext documentContext = parser.jspDocument();
            // Walk it and attach our listener
            ParseTreeWalker walker = new ParseTreeWalker();
            JSP2ThymeleafTransformerListener listener = new JSP2ThymeleafTransformerListener();
            listener.setShowBanner(showBanner);
            walker.walk(listener, documentContext);

            listener.write(new FileOutputStream(toWrite));
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(JSP2ThymeleafFileConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jsP2ThymeleafErrorCollector.getExceptions();
    }

}
