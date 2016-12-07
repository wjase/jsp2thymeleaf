/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters.jsp;

import com.cybernostics.forks.jsp2x.JspTree;
import com.cybernostics.jsp2thymeleaf.api.ConverterSource;
import com.cybernostics.jsp2thymeleaf.api.JspTreeConverter;
import java.util.Optional;

/**
 *
 * @author jason
 */
public class JSPDirectiveConverterSource implements ConverterSource
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
