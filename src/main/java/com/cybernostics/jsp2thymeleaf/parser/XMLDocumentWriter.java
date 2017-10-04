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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.Text;
import org.jdom2.output.Format;
import org.jdom2.output.LineSeparator;
import org.jdom2.output.support.AbstractXMLOutputProcessor;
import org.jdom2.output.support.FormatStack;
import org.jdom2.output.support.Walker;
import org.jdom2.util.NamespaceStack;

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
                    super.printNamespace(out, fstack, ns); 
                }
            }

            @Override
            protected void printElement(Writer out, FormatStack fstack, NamespaceStack nstack, Element element) throws IOException
            {
                nstack.push(element);
                try
                {
                    final List<Content> content = element.getContent();

                    // Print the beginning of the tag plus attributes and any
                    // necessary namespace declarations
                    write(out, "<");

                    write(out, element.getQualifiedName());

                    // Print the element's namespace, if appropriate
                    for (final Namespace ns : nstack.addedForward())
                    {
                        printNamespace(out, fstack, ns);
                    }

                    // Print out attributes
                    if (element.hasAttributes())
                    {
                        for (final Attribute attribute : element.getAttributes())
                        {
                            printAttribute(out, fstack, attribute);
                        }
                    }

                    if (content.isEmpty())
                    {
                        // Case content is empty
                        if (fstack.isExpandEmptyElements())
                        {
                            write(out, "></");
                            write(out, element.getQualifiedName());
                            write(out, ">");
                        } else
                        {
                            write(out, " />");
                        }
                        // nothing more to do.
                        return;
                    }

                    // OK, we have real content to push.
                    fstack.push();
                    try
                    {

                        // Check for xml:space and adjust format settings
                        final String space = element.getAttributeValue("space",
                                Namespace.XML_NAMESPACE);

                        if ("default".equals(space))
                        {
                            fstack.setTextMode(fstack.getDefaultMode());
                        } else if ("preserve".equals(space))
                        {
                            fstack.setTextMode(Format.TextMode.PRESERVE);
                        }

                        boolean isScript = element.getName().equals("script");
                        if (isScript)
                        {
                            fstack.push();
                            fstack.setLevelIndent(null);
                            fstack.setLevelEOL(null);

                        }
                        // note we ensure the FStack is right before creating the walker
                        Walker walker = buildWalker(fstack, content, true);

                        if (!walker.hasNext())
                        {
                            // the walker has formatted out whatever content we had
                            if (fstack.isExpandEmptyElements())
                            {
                                write(out, "></");
                                write(out, element.getQualifiedName());
                                write(out, ">");
                            } else
                            {
                                write(out, " />");
                            }
                            // nothing more to do.
                            return;
                        }
                        // we have some content.
                        write(out, ">");
                        if (!walker.isAllText() || isScript)
                        {
                            textRaw(out, fstack.getPadBetween());
//                             we need to newline/indent
                        }

                        printContent(out, fstack, nstack, walker);
                        if (isScript)
                        {
                            fstack.pop();
                        }

                        if (!walker.isAllText())
                        {
                            // we need to newline/indent
                            textRaw(out, fstack.getPadLast());
                        }
                        write(out, "</");
                        write(out, element.getQualifiedName());
                        write(out, ">");

                    } finally
                    {
                        fstack.pop();
                    }
                } finally
                {
                    nstack.pop();
                }
            }

            @Override
            protected void write(Writer out, String str) throws IOException
            {
//                str = str.replaceAll("\n", "");
                super.write(out, str); 
            }

            @Override
            public void process(Writer out, Format format, Text text) throws IOException
            {
                super.process(out, format, text); 
            }

            @Override
            public void process(Writer out, Format format, Element element) throws IOException
            {
                boolean noNewline = element.getName().equals("span");
                String savedNewline = format.getLineSeparator();
                String savedIndent = format.getIndent();
                if (noNewline)
                {
                    format.setLineSeparator(LineSeparator.NONE);
                    format.setIndent("");
                }
                super.process(out, format, element); 
                if (noNewline)
                {
                    format.setLineSeparator(savedNewline);
                    format.setIndent(savedIndent);
                }
            }

        });
        return out;
    }

}
