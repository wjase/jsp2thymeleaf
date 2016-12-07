/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters.identity;

import com.cybernostics.forks.jsp2x.JspTree;
import com.cybernostics.jsp2thymeleaf.api.DomTag;
import com.cybernostics.jsp2thymeleaf.api.ElementConverter;
import com.cybernostics.jsp2thymeleaf.api.JspTreeConverter;
import com.cybernostics.jsp2thymeleaf.api.TagConverter;
import com.cybernostics.jsp2thymeleaf.api.TagConverterSource;
import java.util.Optional;

/**
 *
 * @author jason
 */
public class DefaultElementConverterSource extends TagConverterSource
{
    private static TagConverter[] tags = new TagConverter[0];
    ElementConverter converter = new ElementConverter();
    
    public DefaultElementConverterSource()
    {
        super("", tags);
    }

    @Override
    public Optional<JspTreeConverter> converterFor(JspTree jspTree)
    {
        return Optional.of(converter); 
    }

    @Override
    public Optional<JspTreeConverter> converterFor(DomTag domTag)
    {
        return Optional.of(converter);
    }
    
    
    
}
