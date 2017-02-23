/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters.jsp;

import com.cybernostics.jsp.parser.JSPParser;
import com.cybernostics.jsp2thymeleaf.api.elements.JSPDirectiveConverter;
import com.cybernostics.jsp2thymeleaf.api.elements.JSPElementNodeConverter;
import com.cybernostics.jsp2thymeleaf.api.elements.TagConverterSource;
import com.cybernostics.jsp2thymeleaf.api.expressions.function.FunctionConverterSource;
import com.cybernostics.jsp2thymeleaf.api.exception.JSPNodeException;
import com.cybernostics.jsp2thymeleaf.converters.AvailableConverters;
import static java.util.Collections.EMPTY_LIST;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.jdom2.Content;

/**
 *
 * @author jason
 */
public class TaglibDirectiveConverter implements JSPDirectiveConverter
{

    @Override
    public List<Content> process(JSPParser.JspDirectiveContext node, JSPElementNodeConverter context)
    {

        String prefix = getAttribute(node, "prefix")
                .map(att -> stripQuotes(att.value.getText()))
                .orElseThrow(rex("missing taglib prefix attribute", node)).toString();
        String uri = getAttribute(node, "uri")
                .map(att -> stripQuotes(att.value.getText()))
                .orElseThrow(rex("Missing taglib uri attribute", node)).toString();

        final Optional<TagConverterSource> taglibConverter = AvailableConverters.elementConverterforUri(uri);
        if (taglibConverter.isPresent())
        {
            context.getScopedConverters().addTaglibConverter(prefix, taglibConverter.get());
        } else
        {
            final Optional<FunctionConverterSource> functionConverter = AvailableConverters.functionConverterforUri(uri);
            context.getScopedConverters().addTaglibFunctionConverter(prefix,
                    functionConverter
                            .orElseThrow(rex("No converters for uri:\""
                                    + uri
                                    + "\". Add converter jars or scripts to classpath.", node)));

        }

        return EMPTY_LIST;
    }

    @Override
    public boolean canHandle(JSPParser.JspDirectiveContext jspTree)
    {
        return true;
    }

    public static Supplier<RuntimeException> rex(String message, JSPParser.JspDirectiveContext jspTree)
    {
        return () -> new JSPNodeException(message, jspTree);
    }

    public static Supplier<RuntimeException> rex(String message, JSPParser.JspElementContext jspTree)
    {
        return () -> new JSPNodeException(message, jspTree);
    }

    public static Supplier<RuntimeException> rex(String message)
    {
        return () -> new RuntimeException(message);
    }

    private String stripQuotes(String text)
    {
        final char firstChar = text.charAt(0);
        final int length = text.length();
        if (length < 2)
        {
            throw new IllegalArgumentException(text + " is not quoted");
        }

        if (firstChar == '\'' || firstChar == '\"')
        {
            return text.substring(1, length - 1);
        }
        throw new IllegalArgumentException(text + " is not quoted");
    }

    private Optional<JSPParser.HtmlAttributeContext> getAttribute(JSPParser.JspDirectiveContext node, String name)
    {
        return node.atts.stream().filter(att -> att.name.getText().equals(name)).findFirst();
    }

    private Optional<JSPParser.HtmlAttributeContext> getAttribute(JSPParser.JspElementContext node, String name)
    {
        return node.atts.stream().filter(att -> att.name.getText().equals(name)).findFirst();
    }

}
