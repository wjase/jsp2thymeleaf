/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters.identity;

import com.cybernostics.forks.jsp2x.JspTree;
import com.cybernostics.jsp2thymeleaf.api.util.PrefixedName;
import com.cybernostics.jsp2thymeleaf.api.elements.CopyElementConverter;
import com.cybernostics.jsp2thymeleaf.api.elements.JspTreeConverter;
import com.cybernostics.jsp2thymeleaf.api.elements.TagConverter;
import com.cybernostics.jsp2thymeleaf.api.elements.TagConverterSource;
import java.util.Optional;

/**
 *
 * @author jason
 */
public class DefaultElementConverterSource extends TagConverterSource
{
    private static TagConverter[] tags = new TagConverter[0];
    CopyElementConverter converter = new CopyElementConverter();
    
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
    public Optional<JspTreeConverter> converterFor(PrefixedName domTag)
    {
        return Optional.of(converter);
    }
    
    
    
}
