/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters.jstl.core;

import com.cybernostics.jsp2thymeleaf.api.elements.TagConverter;
import com.cybernostics.jsp2thymeleaf.api.elements.TagConverterSource;

/**
 *
 * @author jason
 */
public class JstlCoreTaglibConverterSource extends TagConverterSource
{
    private static TagConverter[] jstlConverters={
        new CifJspConverter(),
        new ForeachJspConverter(),
        new CoutJspConverter()
    };
    
    public JstlCoreTaglibConverterSource()
    {
        super("http://java.sun.com/jstl/core", jstlConverters);
    }
}
