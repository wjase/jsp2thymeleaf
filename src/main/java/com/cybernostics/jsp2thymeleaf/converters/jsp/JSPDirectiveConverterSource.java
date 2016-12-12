/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters.jsp;

import com.cybernostics.forks.jsp2x.JspTree;
import com.cybernostics.jsp2thymeleaf.api.elements.JspTreeConverter;
import java.util.Optional;
import com.cybernostics.jsp2thymeleaf.api.elements.JspTreeConverterSource;

/**
 *
 * @author jason
 */
public class JSPDirectiveConverterSource implements JspTreeConverterSource
{

    private final TaglibDirectiveConverter taglibDirectiveConverter = new TaglibDirectiveConverter();
    public JSPDirectiveConverterSource()
    {
    }

    @Override
    public Optional<JspTreeConverter> converterFor(JspTree jspTree)
    {
        return Optional.of(taglibDirectiveConverter);
    }
    
    
    
}
