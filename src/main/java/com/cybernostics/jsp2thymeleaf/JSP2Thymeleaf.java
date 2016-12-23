/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf;

import com.cybernostics.forks.jsp2x.JspLexer;
import com.cybernostics.forks.jsp2x.JspParser;
import static com.cybernostics.forks.jsp2x.JspParser.*;
import com.cybernostics.forks.jsp2x.JspTree;
import static com.cybernostics.jsp2thymeleaf.AvailableConverters.scanForConverters;
import com.cybernostics.jsp2thymeleaf.api.elements.*;
import static com.cybernostics.jsp2thymeleaf.api.elements.ActiveTaglibConverters.addTaglibConverter;
import static com.cybernostics.jsp2thymeleaf.api.elements.ActiveTaglibConverters.forPrefix;
import static com.cybernostics.jsp2thymeleaf.api.util.JspTreeUtils.doWithChildren;
import static com.cybernostics.jsp2thymeleaf.api.util.JspTreeUtils.nameOrNone;
import com.cybernostics.jsp2thymeleaf.api.util.PrefixedName;
import static com.cybernostics.jsp2thymeleaf.api.util.PrefixedName.prefixedNameFor;
import com.cybernostics.jsp2thymeleaf.converters.identity.DefaultElementConverterSource;
import com.cybernostics.jsp2thymeleaf.converters.jsp.JSPDirectiveConverterSource;
import static com.cybernostics.jsp2thymeleaf.converters.jsp.TaglibDirectiveConverter.rex;
import java.io.*;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.io.IOUtils;
import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 *
 * @author jason
 */
public class JSP2Thymeleaf implements JspTreeConverterContext
{

    public static final Logger logger = Logger.getLogger(JSP2Thymeleaf.class.getName());
    private boolean showBanner;
    private JspTreeConverter elementConverter = new CopyElementConverter();
    private final Namespace xmlns = Namespace.getNamespace("http://www.w3.org/1999/xhtml");
    protected final Namespace cnns = Namespace.getNamespace("cn", "http://www.cybernostics.com");

    private final Pattern whitespace = Pattern.compile("^\\s+$");
    private JSPDirectiveConverterSource jspDirectives = new JSPDirectiveConverterSource();

    public JSP2Thymeleaf()
    {
        scanForConverters();
        addTaglibConverter("", new DefaultElementConverterSource());
    }

    public void setShowBanner(boolean showBanner)
    {
        this.showBanner = showBanner;
    }

    public void convert(InputStream inputStream, OutputStream outputStream)
    {
        try
        {
            final JspTree jspTree = parse(new String(IOUtils.toByteArray(inputStream)));
            logger.log(Level.FINE, jspTree.toStringTree());
            writeTree(jspTree, outputStream);
        } catch (IOException ex)
        {
            Logger.getLogger(JSP2Thymeleaf.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RecognitionException ex)
        {
            Logger.getLogger(JSP2Thymeleaf.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private JspTree parse(String jspFileContents) throws IOException, RecognitionException
    {
        final JspLexer lexer = new JspLexer(new ANTLRStringStream(jspFileContents));
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final JspParser parser = new JspParser(tokens);
        parser.setTreeAdaptor(new JspTree.Adaptor());
        final JspTree tree = (JspTree) parser.document().getTree();
        return tree;
    }

    private void writeTree(JspTree jspTree, OutputStream outputStream)
    {
        try
        {
            Document doc = new Document();
            final List<Content> content = rootContentFor(jspTree);

            doc.addContent(content);

            XMLOutputter out = new XMLOutputter(
                    Format
                            .getPrettyFormat()
                            .setTextMode(Format.TextMode.NORMALIZE)
                            .setLineSeparator(NEWLINE)
                            .setOmitDeclaration(true));

            out.output(doc, outputStream);
        } catch (IOException ex)
        {
            Logger.getLogger(JSP2Thymeleaf.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static final String NEWLINE = System.getProperty("line.separator");

    private static Boolean isHtmlElement(Content content)
    {
        return content instanceof Element
                && ((Element) content).getName().equals("html");
    }

    private List<Content> rootContentFor(JspTree jspTree)
    {
        List<Content> contents = new ArrayList<>();
        if (showBanner)
        {
            contents.add(new Comment("Created with JSP2Thymeleaf"));
            contents.add(new Text(NEWLINE));
        }
        contents.addAll(contentFor(jspTree, this));

        final Optional<Content> foundHtmlElement = contents.stream().filter(JSP2Thymeleaf::isHtmlElement).findFirst();

        if (foundHtmlElement.isPresent())
        {
            final Element htmlElement = (Element) foundHtmlElement.get();
            contents.remove(htmlElement);
            trimTrailingWhitespace(contents);
            htmlElement.addContent(contents);
            htmlElement.setNamespace(xmlns);
            ActiveNamespaces.get().forEach(ns -> htmlElement.addNamespaceDeclaration(ns));
            return Arrays.asList(new DocType("html", THYMELEAF_DTD), htmlElement);
        } else
        {
            Element thFragment = createFragmentDef(contents);
            return Arrays.asList(new DocType("html", THYMELEAF_DTD), thFragment);
        }

    }
    private static final String THYMELEAF_DTD = "http://thymeleaf.org/dtd/xhtml-strict-thymeleaf.dtd";

    private void trimTrailingWhitespace(List<Content> contents)
    {
        while (contents.size() > 0
                && whitespace
                        .matcher(contents.get(contents.size() - 1).getValue())
                        .matches())
        {
            contents.remove(contents.size() - 1);
        }
    }

    public List<Content> contentFor(JspTree jspTree, JspTreeConverterContext context)
    {
        List<Content> contents = new ArrayList<>();
        logger.fine(nameOrNone(jspTree));
        boolean traversedChildren = false;
        int type = jspTree.getType();
        switch (type)
        {
            case ELEMENT:
                contents.addAll(getConverter(jspTree).processElement(jspTree, this));
                traversedChildren = true;
                break;
            case JSP_DIRECTIVE:
                contents.addAll(getConverter(jspTree).processElement(jspTree, this));
                //logger.info("Dropping jsp directive:" + jspTree.toStringTree());
                traversedChildren = true;
                break;
            case PCDATA:
                contents.add(new Text(jspTree.getText()));
                traversedChildren = true;
                break;
            case WHITESPACE:
                contents.add(new Text(jspTree.getText()));
                traversedChildren = true;
                break;
//            case DOCTYPE_DEFINITION:
//                break;
//            case EL_EXPR:
//                break;
//            case COMMENT:
//                break;
//            case JSP_EXPRESSION:
//                break;
//            case WHITESPACE:
//                break;
//            case JSP_SCRIPTLET:
//                break;
//            case JSP_COMMENT:
//                break;
            default:
                logger.fine(String.format("No action for: %d %s",
                        jspTree.getType(),
                        jspTree.toString()));
        }

        if (!traversedChildren)
        {
            doWithChildren(jspTree, (i, eachChild) -> contents.addAll(contentFor(eachChild, this)));
        }
        return contents;
    }

    private JspTreeConverter getConverter(JspTree jspTree)
    {
        final int type = jspTree.getType();
        switch (type)
        {
            case ELEMENT:
                PrefixedName domTag = prefixedNameFor(jspTree.name());
                JspTreeConverterSource converterSource1 = getConverterSource(domTag)
                        .orElseThrow(missing(domTag, jspTree));

                return converterSource1.converterFor(jspTree)
                        .orElseThrow(missing(domTag, jspTree));

            case JSP_DIRECTIVE:
                return jspDirectives.converterFor(jspTree)
                        .orElseThrow(() -> new RuntimeException("No jsp directive converter found" + jspTree.toStringTree()));

        }
        return elementConverter;
    }

    private static Supplier<RuntimeException> missing(PrefixedName domTag, JspTree jspTree)
    {
        return rex("No converter source found for tag " + domTag, jspTree);
    }

    private Element createFragmentDef(List<Content> contents)
    {
        Element html = new Element("html", xmlns);

        ActiveNamespaces.get().forEach(ns -> html.addNamespaceDeclaration(ns));
        html.addContent(NEWLINE);
        Element head = new Element("head", xmlns);
        html.addContent(head);
        html.addContent(NEWLINE);
        Element title = new Element("title", xmlns);
        title.setText("Thymeleaf Fragment Definition");
        head.addContent(NEWLINE);
        head.addContent(title);
        head.addContent(NEWLINE);
        Element body = new Element("body", xmlns);
        html.addContent(body);
        html.addContent(NEWLINE);
        body.addContent(contents);
        return html;
    }

    private Optional<JspTreeConverterSource> getConverterSource(PrefixedName domTag)
    {
        return forPrefix(domTag.getPrefix());
    }

}
