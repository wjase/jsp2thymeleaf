/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters.jsp;

import com.cybernostics.jsp.parser.JSPParser;
import com.cybernostics.jsp2thymeleaf.api.elements.JSPDirectiveConverter;
import java.util.Optional;

/**
 *
 * @author jason
 */
public class JSPDirectiveConverterSource
{

    private final TaglibDirectiveConverter taglibDirectiveConverter = new TaglibDirectiveConverter();

    public JSPDirectiveConverterSource()
    {
    }

    public Optional<JSPDirectiveConverter> converterFor(JSPParser.JspDirectiveContext node)
    {
        return Optional.of(taglibDirectiveConverter);
    }

}
