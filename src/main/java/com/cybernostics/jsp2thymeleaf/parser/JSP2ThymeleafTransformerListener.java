/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.parser;

import com.cybernostics.jsp.parser.JSPParser;
import com.cybernostics.jsp.parser.JSPParser.DtdContext;
import com.cybernostics.jsp.parser.JSPParser.HtmlChardataContext;
import com.cybernostics.jsp.parser.JSPParser.HtmlCommentContext;
import com.cybernostics.jsp.parser.JSPParser.JspDirectiveContext;
import com.cybernostics.jsp.parser.JSPParser.JspElementContext;
import com.cybernostics.jsp.parser.JSPParser.ScriptletContext;
import com.cybernostics.jsp.parser.JSPParser.XhtmlCDATAContext;
import com.cybernostics.jsp.parser.JSPParserBaseListener;
import com.cybernostics.jsp2thymeleaf.api.common.Namespaces;
import com.cybernostics.jsp2thymeleaf.api.elements.ActiveNamespaces;
import com.cybernostics.jsp2thymeleaf.api.elements.JSPDirectiveConverter;
import com.cybernostics.jsp2thymeleaf.api.elements.JSPElementNodeConverter;
import com.cybernostics.jsp2thymeleaf.api.elements.JSPNodeConverterSource;
import static com.cybernostics.jsp2thymeleaf.api.elements.JspTagElementConverter.XMLNS;
import com.cybernostics.jsp2thymeleaf.api.elements.ScopedJSPConverters;
import com.cybernostics.jsp2thymeleaf.api.exception.JSP2ThymeLeafException;
import com.cybernostics.jsp2thymeleaf.api.util.MapUtils;
import com.cybernostics.jsp2thymeleaf.api.util.PrefixedName;
import static com.cybernostics.jsp2thymeleaf.api.util.PrefixedName.prefixedNameFor;
import com.cybernostics.jsp2thymeleaf.converters.jsp.JSPDirectiveConverterSource;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toList;
import org.antlr.v4.runtime.ParserRuleContext;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Text;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 *
 * @author jason
 */
public class JSP2ThymeleafTransformerListener extends JSPParserBaseListener implements JSPElementNodeConverter
{

    public JSP2ThymeleafTransformerListener()
    {
        showBanner = false;
    }

    private static final String THYMELEAF_DTD = "http://thymeleaf.org/dtd/xhtml-strict-thymeleaf.dtd";
    private final Pattern whitespace = Pattern.compile("^\\s+$");
    public static final String NEWLINE = System.getProperty("line.separator");
    final Logger logger = Logger.getLogger(JSP2ThymeleafTransformerListener.class.getName());
    private ScopedJSPConverters converters = new ScopedJSPConverters();
    private final List<JSP2ThymeLeafException> problems = new ArrayList<>();

    public List<JSP2ThymeLeafException> getProblems()
    {
        return problems;
    }

    private Document doc = new Document();
    private Element currentElement;

    private JSPDirectiveConverterSource jspDirectives = new JSPDirectiveConverterSource();

    public void write(OutputStream outputStream)
    {
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

    @Override
    public void enterEveryRule(ParserRuleContext ctx)
    {
        super.enterEveryRule(ctx);
    }

    @Override
    public void enterJspElement(JspElementContext ctx)
    {
        logger.log(Level.FINE, "enterJspElement");
        final JSPElementNodeConverter converterForNode = converterForNode(ctx);
        final List<Content> content = converterForNode.process(ctx, this);
        addContent(content);
        pushElement(content);
    }

    @Override
    public void exitJspElement(JspElementContext ctx)
    {
        logger.log(Level.FINE, "exitJspElement");

        popElement();
    }

    @Override
    public void exitJspDocument(JSPParser.JspDocumentContext ctx)
    {
        if (doc.hasRootElement())
        {
            Element rootElement = doc.getRootElement();
            ActiveNamespaces.get().forEach(ns -> rootElement.addNamespaceDeclaration(ns));
        }
    }

    @Override
    public void enterXhtmlCDATA(XhtmlCDATAContext ctx)
    {
        logger.log(Level.FINE, "enterXhtmlCDATA" + ctx.getText());
        addContent(new Text(ctx.getText()));
    }

    @Override
    public void enterHtmlChardata(HtmlChardataContext ctx)
    {
        logger.log(Level.FINE, "enterHtmlCharData" + ctx.getText());
        addContent(new Text(ctx.getText()));
    }

    @Override
    public void enterHtmlComment(HtmlCommentContext ctx)
    {
        logger.log(Level.FINE, "enterHtmlComment" + ctx.getText());
        addContent(new Comment(ctx.getText()));
    }

    private JSPElementNodeConverter converterForNode(JSPParser.JspElementContext node)
    {
        PrefixedName domTag = prefixedNameFor(node.name.getText());
        Optional<JSPNodeConverterSource> converterSource1 = getConverterSource(domTag);
        final Optional<JSPElementNodeConverter> converterFor = converterSource1
                .orElseGet(missingTaglib(domTag, node))
                .converterFor(node);

        return converterFor.orElseGet(missingNodeConverter(domTag, node));

    }

    @Override
    public void enterScriptlet(ScriptletContext ctx)
    {
        logger.log(Level.SEVERE, "EVIL:Scriptlet detected and converted to comment. Over to you human." + ctx.getText());
        addContent(new Comment(ctx.getText()));
    }

    @Override
    public void enterJspDirective(JspDirectiveContext ctx)
    {
        logger.log(Level.FINE, "enterJspDirective" + ctx.getText());

        final Optional<JSPDirectiveConverter> converter = jspDirectives.converterFor(ctx);
        try
        {
            final List<Content> content = converter.get().process(ctx, this);
            addContent(content);
        } catch (JSP2ThymeLeafException exception)
        {
            problems.add(exception);
        }

    }

    @Override
    public void enterDtd(DtdContext ctx)
    {
        DocType dt = new DocType(ctx.dtdElementName().getText(), THYMELEAF_DTD);

        doc.setDocType(dt);
    }

    private Optional<JSPNodeConverterSource> getConverterSource(PrefixedName domTag)
    {
        return converters.forPrefix(domTag.getPrefix());
    }

    private Supplier<JSPNodeConverterSource> missingTaglib(PrefixedName domTag, JSPParser.JspElementContext node)
    {
        return () ->
        {
            if (!domTag.getPrefix().isEmpty())
            {
                problems.add(MapUtils.rex("No taglib converter found for tag " + domTag + ". You need to add a converter lib", node).get());
            }

            return converters.forPrefix("").get();
        };
    }

    private Supplier<JSPElementNodeConverter> missingNodeConverter(PrefixedName domTag, JSPParser.JspElementContext node)
    {

        return () ->
        {
            if (!domTag.getPrefix().isEmpty())
            {
                problems.add(MapUtils.rex("No node converter found for tag " + domTag, node).get());
            }
            return getDefaultCopyNodeConverter(node);
        };
    }

    private JSPElementNodeConverter getDefaultCopyNodeConverter(JSPParser.JspElementContext node)
    {
        return converters.forPrefix("").get().converterFor(node).get();
    }

    private Element createFragmentDef(List<Content> contents)
    {
        Element html = new Element("html", XMLNS);

        ActiveNamespaces.get().forEach(ns -> html.addNamespaceDeclaration(ns));
        html.addContent(NEWLINE);
        Element head = new Element("head", XMLNS);
        html.addContent(head);
        html.addContent(NEWLINE);
        Element title = new Element("title", XMLNS);
        title.setText("Thymeleaf Fragment Definition");
        head.addContent(NEWLINE);
        head.addContent(title);
        head.addContent(NEWLINE);
        Element body = new Element("body", XMLNS);
        html.addContent(body);
        html.addContent(NEWLINE);
        body.addContent(contents);
        currentElement = body;
        return html;
    }

    @Override
    public boolean canHandle(JSPParser.JspElementContext JSPNode)
    {
        return true;
    }

    private List<Content> rootContentFor(List<Content> contents)
    {
        List<Content> amendedContents = new java.util.ArrayList<>();
        amendedContents.addAll(contents);
        if (showBanner)
        {
            amendedContents.add(new Comment("Created with JSP2Thymeleaf"));
            amendedContents.add(new Text(NEWLINE));
        }

        final Optional<Content> foundHtmlElement = amendedContents.stream().filter(JSP2ThymeleafTransformerListener::isHtmlElement).findFirst();

        if (foundHtmlElement.isPresent())
        {
            final Element htmlElement = (Element) foundHtmlElement.get();
            amendedContents.remove(htmlElement);
            trimTrailingWhitespace(amendedContents);
            htmlElement.addContent(amendedContents);
            htmlElement.setNamespace(Namespaces.XMLNS);

            return elementWithDocTypeIfNeeded(htmlElement);
        } else
        {
            Element thFragment = createFragmentDef(amendedContents);
            return elementWithDocTypeIfNeeded(thFragment);
        }

    }

    private List<Content> elementWithDocTypeIfNeeded(final Element htmlElement)
    {

        if (doc.getDocType() != null)
        {
            return Arrays.asList(htmlElement);
        }
        return Arrays.asList(new DocType("html", THYMELEAF_DTD), htmlElement);
    }
    private boolean showBanner;

    private static Boolean isHtmlElement(Content content)
    {
        return content instanceof Element
                && ((Element) content).getName().equals("html");
    }

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

    @Override
    public List<Content> process(JSPParser.JspElementContext node, JSPElementNodeConverter context)
    {
        final PrefixedName prefixedName = PrefixedName.prefixedNameFor(node.name.getText());
        final Optional<JSPNodeConverterSource> converter = converters.forPrefix(prefixedName.getPrefix());
        return converter.get().converterFor(node).get().process(node, this);
    }

    private void addContent(Content... content)
    {
        addContent(Arrays.asList(content));
    }

    private void addContent(List<Content> content)
    {
        if (currentElement == null)
        {
            content = rootContentFor(content);
            if (currentElement == null)
            {
                currentElement = (Element) content.stream().filter(JSP2ThymeleafTransformerListener::isHtmlElement).findFirst().get();
            }
            doc.addContent(content);

        } else
        {
            currentElement.addContent(content);
        }

    }

    private void pushElement(List<Content> content)
    {
        final List<Content> elements = content.stream().filter(it -> it instanceof Element).collect(toList());
        currentElement = (Element) elements.get(elements.size() - 1);

    }

    private void popElement()
    {
        if (currentElement != null)
        {
            currentElement = currentElement.getParentElement();
        }
    }

    public void setShowBanner(boolean showBanner)
    {
        this.showBanner = showBanner;
    }

    @Override
    public ScopedJSPConverters getScopedConverters()
    {
        return converters;
    }

    @Override
    public void setScopedConverters(ScopedJSPConverters scopedConverters)
    {
        converters = scopedConverters;
    }

}
