/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf;

import static com.cybernostics.jsp2thymeleaf.JSP2ThymeleafTransformerListener.NEWLINE;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 *
 * @author jason
 */
public class DTDTest
{

    public static void main(String[] args)
    {
        Document d = new Document();
        DocType doc = new DocType("doctypename");
//        doc.setInternalSubset("internalSubset");
        doc.setPublicID("publicId");
        doc.setSystemID("systemId");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        d.addContent(doc);
        write(d, baos);
        System.out.println(baos.toString());
    }

    public static void write(Document doc, OutputStream outputStream)
    {
        //final List<Content> content = rootContentFor(jspTree);
        //doc.addContent(content);
        XMLOutputter out = new XMLOutputter(
                Format
                        .getPrettyFormat()
                        .setTextMode(Format.TextMode.NORMALIZE)
                        .setLineSeparator(NEWLINE)
                        .setOmitDeclaration(true));
        try
        {
            out.output(doc, outputStream);
        } catch (IOException ex)
        {
            Logger.getLogger(JSP2ThymeleafTransformerListener.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
