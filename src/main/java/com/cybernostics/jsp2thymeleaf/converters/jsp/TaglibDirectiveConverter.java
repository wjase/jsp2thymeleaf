/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters.jsp;

import com.cybernostics.forks.jsp2x.JspTree;
import com.cybernostics.jsp2thymeleaf.AvailableConverters;
import com.cybernostics.jsp2thymeleaf.api.elements.ActiveTaglibConverters;
import com.cybernostics.jsp2thymeleaf.api.elements.JspTreeConverter;
import com.cybernostics.jsp2thymeleaf.api.elements.JspTreeConverterContext;
import com.cybernostics.jsp2thymeleaf.api.elements.TagConverterSource;
import com.cybernostics.jsp2thymeleaf.api.expressions.ActiveExpressionConverters;
import com.cybernostics.jsp2thymeleaf.api.expressions.FunctionConverterSource;
import static com.cybernostics.jsp2thymeleaf.api.util.JspTreeUtils.getAttribute;
import static java.util.Collections.EMPTY_LIST;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.jdom2.Content;

/**
 *
 * @author jason
 */
public class TaglibDirectiveConverter implements JspTreeConverter
{

    @Override
    public List<Content> processElement(JspTree jspTree, JspTreeConverterContext context)
    {
        String prefix = getAttribute(jspTree, "prefix")
                .map(JspTree::value)
                .orElseThrow(rex("missing taglib prefix attribute"));
        String uri = getAttribute(jspTree, "uri")
                .map(JspTree::value)
                .orElseThrow(rex("Missing taglib uri attribute"));

        final Optional<TagConverterSource> taglibConverter = AvailableConverters.elementConverterforUri(uri);
        if (taglibConverter.isPresent())
        {
            ActiveTaglibConverters.addTaglibConverter(prefix, taglibConverter.get());
        } else
        {
            final Optional<FunctionConverterSource> functionConverter = AvailableConverters.functionConverterforUri(uri);
            ActiveExpressionConverters.addTaglibConverter(prefix,
                    functionConverter
                            .orElseThrow(rex("No converters for uri:"
                                    + uri
                                    + ". Add converter jars or scripts to classpath ")));

        }

        return EMPTY_LIST;
    }

    @Override
    public boolean canHandle(JspTree jspTree)
    {
        return true;
    }

    public static Supplier<RuntimeException> rex(String message)
    {
        return () -> new RuntimeException(message);
    }
}
