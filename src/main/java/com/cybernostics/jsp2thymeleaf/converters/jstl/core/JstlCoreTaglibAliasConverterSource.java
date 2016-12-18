/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters.jstl.core;

/**
 *
 * @author jason
 */
public class JstlCoreTaglibAliasConverterSource extends JstlCoreTaglibConverterSource
{

    public JstlCoreTaglibAliasConverterSource()
    {
    }

    @Override
    public String getTaglibURI()
    {
        return "http://java.sun.com/jsp/jstl/core";
    }

}
