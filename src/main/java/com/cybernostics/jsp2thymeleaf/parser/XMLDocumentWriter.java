/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.parser;

import com.cybernostics.jsp2thymeleaf.api.util.NoEscapeXMLOutputter;
import static com.cybernostics.jsp2thymeleaf.parser.JSP2ThymeleafTransformerListener.NEWLINE;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.support.AbstractXMLOutputProcessor;
import org.jdom2.output.support.FormatStack;

/**
 *
 * @author jason
 */
public class XMLDocumentWriter
{

    public static enum ShowNamespaces
    {
        SHOWNS,
        HIDENS
    }

    public static void write(Document doc, OutputStream outputStream)
    {
        NoEscapeXMLOutputter out = getXMLStreamWriter(ShowNamespaces.SHOWNS);
        try
        {
            out.output(doc, outputStream);
        } catch (IOException ex)
        {
            Logger.getLogger(XMLDocumentWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void write(Element el, OutputStream outputStream)
    {
        NoEscapeXMLOutputter out = getXMLStreamWriter(ShowNamespaces.HIDENS);
        try
        {
            out.output(el, outputStream);
        } catch (IOException ex)
        {
            Logger.getLogger(XMLDocumentWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static NoEscapeXMLOutputter getXMLStreamWriter(ShowNamespaces showNamespaces)
    {
        NoEscapeXMLOutputter out = new NoEscapeXMLOutputter(
                Format.getPrettyFormat()
                        .setEscapeStrategy(ch -> false)
                        .setLineSeparator(NEWLINE)
                        .setOmitDeclaration(true));
        out.setXMLOutputProcessor(new AbstractXMLOutputProcessor()
        {
            @Override
            protected void attributeEscapedEntitiesFilter(Writer out, FormatStack fstack, String value) throws IOException
            {
                write(out, value);
            }

            @Override
            protected void printNamespace(Writer out, FormatStack fstack, Namespace ns) throws IOException
            {
                if (showNamespaces == ShowNamespaces.SHOWNS)
                {
                    super.printNamespace(out, fstack, ns); //To change body of generated methods, choose Tools | Templates.
                }
            }

        });
        return out;
    }

}
