/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.parser;

import com.cybernostics.jsp.parser.JSPParser;
import com.cybernostics.jsp.parser.JSPParser.DtdContext;
import com.cybernostics.jsp.parser.JSPParser.HtmlChardataContext;
import com.cybernostics.jsp.parser.JSPParser.JspDirectiveContext;
import com.cybernostics.jsp.parser.JSPParser.JspElementContext;
import com.cybernostics.jsp.parser.JSPParser.ScriptletContext;
import com.cybernostics.jsp.parser.JSPParser.XhtmlCDATAContext;
import com.cybernostics.jsp.parser.JSPParserBaseListener;
import com.cybernostics.jsp2thymeleaf.api.common.Namespaces;
import static com.cybernostics.jsp2thymeleaf.api.common.Namespaces.TH;
import com.cybernostics.jsp2thymeleaf.api.elements.ELExpressionConverter;
import com.cybernostics.jsp2thymeleaf.api.elements.JSP2ThymeleafExpressionParseException;
import com.cybernostics.jsp2thymeleaf.api.elements.JSPDirectiveConverter;
import com.cybernostics.jsp2thymeleaf.api.elements.JSPElementNodeConverter;
import com.cybernostics.jsp2thymeleaf.api.elements.JSPNodeConverterSource;
import static com.cybernostics.jsp2thymeleaf.api.elements.JspTagElementConverter.XMLNS;
import com.cybernostics.jsp2thymeleaf.api.elements.ScopedJSPConverters;
import static com.cybernostics.jsp2thymeleaf.api.elements.ScopedJSPConverters.defaultSource;
import com.cybernostics.jsp2thymeleaf.api.exception.JSP2ThymeLeafException;
import com.cybernostics.jsp2thymeleaf.api.exception.JSPNodeException;
import com.cybernostics.jsp2thymeleaf.api.util.MapUtils;
import com.cybernostics.jsp2thymeleaf.api.util.PrefixedName;
import static com.cybernostics.jsp2thymeleaf.api.util.PrefixedName.prefixedNameFor;
import com.cybernostics.jsp2thymeleaf.converters.jsp.JSPDirectiveConverterSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import org.apache.commons.el.parser.ParseException;
import org.jdom2.Attribute;
import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.Text;

/**
 *
 * @author jason
 */
public class JSP2ThymeleafTransformerListener extends JSPParserBaseListener implements JSPElementNodeConverter
{

    private Document doc = new Document();

    public Document getDocument()
    {
        return doc;
    }
    private Element currentElement;
    private JSPDirectiveConverterSource jspDirectives = new JSPDirectiveConverterSource();
    protected ELExpressionConverter expressionConverter = new ELExpressionConverter();
    private final Logger logger = Logger.getLogger(JSP2ThymeleafTransformerListener.class.getName());
    private static final String THYMELEAF_DTD = "http://thymeleaf.org/dtd/xhtml-strict-thymeleaf.dtd";
    private final Pattern whitespace = Pattern.compile("^\\s+$");
    private final List<JSP2ThymeLeafException> problems = new ArrayList<>();
    private ScopedJSPConverters converters;
    private boolean showBanner;

    public static final String NEWLINE = System.getProperty("line.separator");

    public JSP2ThymeleafTransformerListener(ScopedJSPConverters converters)
    {
        showBanner = false;
        this.converters = converters;
    }

    public List<JSP2ThymeLeafException> getProblems()
    {
        return problems;
    }

    @Override
    public void enterJspElement(JspElementContext ctx)
    {
        logger.log(Level.FINE, "enterJspElement");
        try
        {
            final JSPElementNodeConverter converterForNode = converterForNode(ctx);
            final List<Content> content = converterForNode.process(ctx, this);
            addContent(content);
            pushElement(content);

        } catch (JSP2ThymeLeafException exception)
        {
            problems.add(exception);
        }
    }

    @Override
    public void exitJspElement(JspElementContext ctx)
    {
        logger.log(Level.FINE, "exitJspElement");

        try
        {
            popElement();

        } catch (Exception e)
        {
            problems.add(new JSPNodeException(e.getMessage(), e, ctx));
        }
    }

    @Override
    public void exitJspDocument(JSPParser.JspDocumentContext ctx)
    {
        if (doc.hasRootElement())
        {
            Element rootElement = doc.getRootElement();
            namespacesFor(rootElement).stream().filter(it -> !it.getPrefix().equals(rootElement.getNamespace().getPrefix())).forEach(ns -> rootElement.addNamespaceDeclaration(ns));
        }
    }

    @Override
    public void enterJspExpression(JSPParser.JspExpressionContext ctx)
    {
        try
        {
            super.enterJspExpression(ctx);
            Element expression = new Element("span", XMLNS);
            expression.setAttribute("text", expressionConverter.convert(ctx.getText(), converters), TH);
            expression.removeNamespaceDeclaration(XMLNS);
            addContent(expression);
        } catch (ParseException ex)
        {
            problems.add(new JSP2ThymeleafExpressionParseException(ex, ctx.start.getLine(), ctx.start.getCharPositionInLine()));
            Logger.getLogger(JSP2ThymeleafTransformerListener.class.getName()).log(Level.SEVERE, null, ex);
            addContent(new Comment("Expression with errors:" + ctx.getText()));
        }
    }

    @Override
    public void enterXhtmlCDATA(XhtmlCDATAContext ctx)
    {
        logger.log(Level.FINE, "enterXhtmlCDATA" + ctx.getText());
        addContent(new CDATA(unEscapeText(ctx.getText())));
    }

    @Override
    public void enterHtmlChardata(HtmlChardataContext ctx)
    {
        logger.log(Level.FINE, "enterHtmlCharData" + ctx.getText());
        addContent(new Text(unEscapeText(ctx.getText())));
    }

    private String unEscapeText(String s)
    {
        return s.replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("&amp;", "&")
                .replaceAll("&#xD;", "\r");
    }

    @Override
    public void enterHtmlCommentText(JSPParser.HtmlCommentTextContext ctx)
    {
        logger.log(Level.FINE, "enterHtmlCommentText" + ctx.getText());
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

        try
        {
            final Optional<JSPDirectiveConverter> converter = jspDirectives.converterFor(ctx);
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

            return defaultSource();
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
        return defaultSource().converterFor(node).get();
    }

    private Element createFragmentDef(List<Content> contents)
    {
        Element html = new Element("html", XMLNS);

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
        Element blockContainer = new Element("block", TH);
        body.addContent(blockContainer);
        blockContainer.setAttribute("fragment", "content", TH);
        blockContainer.addContent(contents);
        blockContainer.addContent(NEWLINE);
        currentElement = body;
        namespacesFor(html).stream().filter(it -> !it.getPrefix().equals(html.getNamespace().getPrefix())).forEach(ns -> html.addNamespaceDeclaration(ns));
        return html;
    }

    private static Set<Namespace> namespacesFor(Element html)
    {
        Set<Namespace> namespaces = new HashSet<Namespace>();
        namespaces.addAll(html.getNamespacesInScope());
        namespaces.addAll(html.getChildren().stream().flatMap(it -> namespacesFor(it).stream()).collect(toSet()));
        return namespaces;
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
            htmlElement.setAttribute("fragment", "content", TH);

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
        if (!content.isEmpty())
        {
            if (currentElement == null)
            {
                content = rootContentFor(content);
                currentElement = (Element) content.stream().filter(JSP2ThymeleafTransformerListener::isHtmlElement).findFirst().get();
                doc.addContent(content);

            } else
            {
                currentElement.addContent(content);
            }

        }

    }

    private void pushElement(List<Content> content)
    {

        // push to stack if empty push current element (duped) in place of skipped element
        final List<Content> elements = content.stream().filter(it -> it instanceof Element).collect(toList());
        if (!elements.isEmpty())
        {
            currentElement = (Element) elements.get(elements.size() - 1);
        }
    }

    private void popElement()
    {
        if (currentElement != null)
        {
            Element childElement = currentElement;
            currentElement = childElement.getParentElement();
            Attribute replaceParentAttributeName = childElement.getAttribute("data-replace-parent-attribute-name");
            if (replaceParentAttributeName != null)
            {
                String parentAttributeName = replaceParentAttributeName.getValue();
                Attribute replaceParentAttributeValue = childElement.getAttributes()
                        .stream()
                        .filter(it -> it.getName().equals("data-replace-parent-attribute-value"))
                        .findFirst().orElseThrow(() -> new RuntimeException("Unable to convert element embedded in attribute. Add a whenQuotedInAttributeReplaceWith drective to your element converter registration for this element."));
                currentElement.removeAttribute(parentAttributeName);
                currentElement.setAttribute(parentAttributeName, replaceParentAttributeValue.getValue(), replaceParentAttributeValue.getNamespace());
            }
            if (currentElement != null)
            {
                currentElement.getChildren()
                        .stream()
                        .filter(it -> it.getName().equals("deleteme"))
                        .forEach(it -> currentElement.removeChild(it.getName(), it.getNamespace()));
            }
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
