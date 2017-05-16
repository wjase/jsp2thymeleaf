/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters.jsp;

import com.cybernostics.jsp.parser.JSPParser;
import com.cybernostics.jsp2thymeleaf.api.common.AvailableConverters;
import com.cybernostics.jsp2thymeleaf.api.elements.JSPDirectiveConverter;
import com.cybernostics.jsp2thymeleaf.api.elements.JSPElementNodeConverter;
import com.cybernostics.jsp2thymeleaf.api.elements.TagConverterSource;
import com.cybernostics.jsp2thymeleaf.api.expressions.function.FunctionConverterSource;
import com.cybernostics.jsp2thymeleaf.api.util.MapUtils;
import static com.cybernostics.jsp2thymeleaf.api.util.StringFunctions.trimQuotes;
import static java.util.Collections.EMPTY_LIST;
import java.util.List;
import java.util.Optional;
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
                .map(att -> trimQuotes(att.value.getText()))
                .orElseThrow(MapUtils.rex("Missing jsp taglib directive attribute: prefix", node)).toString();
        String uri = getAttribute(node, "uri")
                .map(att -> trimQuotes(att.value.getText()))
                .orElseThrow(MapUtils.rex("Missing jsp taglib directive: uri", node)).toString();

        final Optional<TagConverterSource> taglibConverter = AvailableConverters.elementConverterforUri(uri);
        if (taglibConverter.isPresent())
        {
            context.getScopedConverters().addTaglibConverter(prefix, taglibConverter.get());
        } else
        {
            final Optional<FunctionConverterSource> functionConverter = AvailableConverters.functionConverterforUri(uri);
            context.getScopedConverters().addTaglibFunctionConverter(prefix,
                    functionConverter
                            .orElseThrow(MapUtils.rex("No converters for uri:\""
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

    private Optional<JSPParser.HtmlAttributeContext> getAttribute(JSPParser.JspDirectiveContext node, String name)
    {
        return node.atts.stream().filter(att -> att.name.getText().equals(name)).findFirst();
    }

    private Optional<JSPParser.HtmlAttributeContext> getAttribute(JSPParser.JspElementContext node, String name)
    {
        return node.atts.stream().filter(att -> att.name.getText().equals(name)).findFirst();
    }

}
