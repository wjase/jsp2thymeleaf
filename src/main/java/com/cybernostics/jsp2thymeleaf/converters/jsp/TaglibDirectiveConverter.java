/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters.jsp;

import com.cybernostics.forks.jsp2x.JspTree;
import com.cybernostics.jsp2thymeleaf.ActiveTaglibConverters;
import com.cybernostics.jsp2thymeleaf.AvailableTaglibConverters;
import com.cybernostics.jsp2thymeleaf.api.JspConverterContext;
import com.cybernostics.jsp2thymeleaf.api.JspTreeConverter;
import static com.cybernostics.jsp2thymeleaf.api.JspTreeUtils.getAttribute;
import com.cybernostics.jsp2thymeleaf.api.TagConverterSource;
import static java.util.Collections.EMPTY_LIST;
import java.util.List;
import java.util.function.Supplier;
import org.jdom2.Content;

/**
 *
 * @author jason
 */
public class TaglibDirectiveConverter implements JspTreeConverter
{

    @Override
    public List<Content> processElement(JspTree jspTree, JspConverterContext context)
    {
        String prefix = getAttribute(jspTree,"prefix")
                .map(JspTree::value)
                .orElseThrow(rex("missing taglib prefix attribute"));
        String uri = getAttribute(jspTree,"uri")
                .map(JspTree::value)
                .orElseThrow(rex("Missing taglib uri attribute"));
        
        final TagConverterSource taglibConverter = AvailableTaglibConverters.forUri(uri);
        ActiveTaglibConverters.addTaglibConverter(prefix, taglibConverter);
        return EMPTY_LIST;
    }

    @Override
    public boolean canHandle(JspTree jspTree)
    {
        return true;
    }

    public static Supplier<RuntimeException> rex(String message){
        return ()-> new RuntimeException(message);
    }
}
