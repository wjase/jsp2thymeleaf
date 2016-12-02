/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf;

import com.cybernostics.jsp2thymeleaf.api.JspConverterContext;
import com.cybernostics.jsp2thymeleaf.api.JspTreeConverter;
import com.cybernostics.jsp2thymeleaf.converters.jstl.CoutJspConverter;
import com.cybernostics.forks.jsp2x.JspLexer;
import com.cybernostics.forks.jsp2x.JspParser;
import static com.cybernostics.forks.jsp2x.JspParser.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.compress.utils.IOUtils;
import com.cybernostics.forks.jsp2x.JspTree;
import com.cybernostics.jsp2thymeleaf.api.ElementConverter;
import static com.cybernostics.jsp2thymeleaf.api.ElementConverter.doWithChildren;
import static com.cybernostics.jsp2thymeleaf.api.ElementConverter.nameOrNone;
import com.cybernostics.jsp2thymeleaf.api.TagConverter;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.Text;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.output.support.AbstractXMLOutputProcessor;
import org.jdom2.output.support.FormatStack;

/**
 *
 * @author jason
 */
public class JSP2Thymeleaf implements JspConverterContext
{

    public static final Logger logger = Logger.getLogger(JSP2Thymeleaf.class.getName());
    private boolean showBanner;
    private JspTreeConverter elementConverter = new ElementConverter();
    private Map<String, JspTreeConverter> tagToConverterMap = new HashMap<>();
    private final Namespace thns = Namespace.getNamespace("th", "http://www.thymeleaf.org");
    private final Namespace xmlns = Namespace.getNamespace("http://www.w3.org/1999/xhtml");
    private final Pattern whitespace = Pattern.compile("^\\s+$");

    public JSP2Thymeleaf()
    {

    }

    public void setShowBanner(boolean showBanner)
    {
        this.showBanner = showBanner;
    }

    public void registerConverter(JspTreeConverter converter)
    {
        if (converter instanceof TagConverter)
        {
            tagToConverterMap.put(((TagConverter) converter).getApplicableTag(), converter);
        }

    }

    public void loadAndRegister(String className)
    {
        try
        {
            final Class<?> clazz = Class.forName(className);
            if (JspTreeConverter.class.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers()))
            {
                JspTreeConverter converter = (JspTreeConverter) clazz.newInstance();
                registerConverter(converter);
            }
        } catch (Exception ex)
        {
            Logger.getLogger(JSP2Thymeleaf.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }

    }

    public void convert(InputStream inputStream, OutputStream outputStream)
    {
        initConverters();
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

    private void initConverters()
    {
        if (tagToConverterMap.isEmpty())
        {
            ScanResult scanResult = new FastClasspathScanner(CoutJspConverter.class.getPackage().getName())
                    .scan();

            List<String> tagConverterNames
                    = scanResult.getNamesOfAllClasses();

            tagConverterNames
                    .stream()
                    .forEach((converter -> loadAndRegister(converter)));

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
                            //                            .getRawFormat()
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

    private AbstractXMLOutputProcessor getFormatter()
    {
        return //                            .setTextMode(Format.TextMode.PRESERVE),
                new AbstractXMLOutputProcessor()
        {
            @Override
            protected void printDocType(Writer out, FormatStack fstack, DocType docType) throws IOException
            {
                super.printDocType(out, fstack, docType);
                out.append(NEWLINE);

            }
        };
    }
    public static final String NEWLINE = System.getProperty("line.separator");

//    private List<Content> convert(JspTree jspTree, Element parent){
//        final int type = jspTree.getType();
//        switch (type)
//        {
//            case PCDATA:
//                contentForPCData(jspTree);
//                break;
//            case JSP_DIRECTIVE:
//                contentForJspDirective(jspTree, doc, parent);
//                break;
//            case ELEMENT:
//                contentForElement(jspTree, doc, parent);
//                break;
//            case ATTRIBUTES:
//                contentForAttributes(jspTree, doc, parent);
//                break;
//            case NAMECHAR:
//                contentForNameChar(jspTree, doc, parent);
//                break;
//            case LETTER:
//                contentForLetter(jspTree, doc, parent);
//                break;
//            case TAG_EMPTY_CLOSE:
//                contentForTagEmptyClose(jspTree, doc, parent);
//                break;
//            case COMMENT:
//                contentForComment(jspTree, doc, parent);
//                break;
//            case JSP_DIRECTIVE_CLOSE:
//                contentForDirectiveClose(jspTree, doc, parent);
//                break;
//
//            case EL_EXPR:
//                contentForElExpression(jspTree, doc, parent);
//                break;
//
//            case WHITESPACE:
//                contentForWhitespace(jspTree, doc, parent);
//                break;
//
//            case JSP_SCRIPTLET:
//                contentForScriptlet(jspTree, doc, parent);
//                break;
//
//            case JSP_DIRECTIVE_OPEN:
//                contentForJspDirectiveOpen(jspTree, doc, parent);
//                break;
//
//            case TAG_START_OPEN:
//                contentForTagStartOpen(jspTree, doc, parent);
//                break;
//
//            case EOF:
//                contentForEof(jspTree, doc, parent);
//                break;
//
//            case ATTRIBUTE:
//                contentForAttribute(jspTree, doc, parent);
//                break;
//
//            case ATTR_VALUE_CLOSE:
//                contentForAttributeValueClose(jspTree, doc, parent);
//                break;
//
//            case GENERIC_ID:
//                contentForGenericId(jspTree, doc, parent);
//                break;
//
//            case JSP_COMMENT:
//                contentForJspComment(jspTree, doc, parent);
//                break;
//
//            case ATTR_EQ:
//                contentForAttributeEq(jspTree, doc, parent);
//                break;
//
//            case TAG_END_OPEN:
//                contentForTagEndOpen(jspTree, doc, parent);
//                break;
//
//            case DIGIT:
//                contentForDigit(jspTree, doc, parent);
//                break;
//
//            case JSP_EXPRESSION:
//                contentForJspExpression(jspTree, doc, parent);
//                break;
//
//            case ATTR_VALUE_OPEN:
//                contentForAttributevalueOpen(jspTree, doc, parent);
//                break;
//
//            case PROCESSING_INSTRUCTION:
//                contentForProcessingInstruction(jspTree, doc, parent);
//                break;
//
//            case DOCTYPE_DEFINITION:
//                contentForDoctypeDefinition(jspTree, doc, parent);
//                break;
//
//            case TAG_CLOSE:
//                contentForTagClose(jspTree, doc, parent);
//                break;
//            case 0:
//                System.out.println("Zero tree node type, recursing");
//            default:
//                System.out.println("New type"+type);
//        }
//        doWithChildren(jspTree, (eachChild) -> writeTree(eachChild, doc, null));
//
//    }


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
            htmlElement.addNamespaceDeclaration(thns);
            return Arrays.asList(new DocType("html", "http://thymeleaf.org/dtd/xhtml-strict-thymeleaf.dtd"), htmlElement);
        } else
        {
            Element thFragment = createFragmentDef(contents);
            return Arrays.asList(new DocType("html", "http://thymeleaf.org/dtd/xhtml-strict-thymeleaf.dtd"), thFragment);
        }

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

    public List<Content> contentFor(JspTree jspTree, JspConverterContext context)
    {
        List<Content> contents = new ArrayList<>();
        logger.fine(nameOrNone(jspTree));
        boolean traversedChildren = false;
        int type = jspTree.getType();
        switch (type)
        {
            case ELEMENT:
                contents.addAll(elementContentFor(jspTree));
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
            case JSP_DIRECTIVE:
                logger.info("Dropping jsp directive:" + jspTree.toStringTree());
                traversedChildren = true;
                break;

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

    private List<Content> elementContentFor(JspTree jspTree)
    {
        JspTreeConverter converter = getConverter(jspTree);
        return converter.elementContentFor(jspTree, this);
    }

    private JspTreeConverter getConverter(JspTree jspTree)
    {
        final String tagname = jspTree.name();
        if (tagToConverterMap.containsKey(tagname))
        {
            return tagToConverterMap.get(tagname);
        }
        return elementConverter;
    }

    private Element createFragmentDef(List<Content> contents)
    {
        Element html = new Element("html", xmlns);
        html.addNamespaceDeclaration(thns);
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

}
