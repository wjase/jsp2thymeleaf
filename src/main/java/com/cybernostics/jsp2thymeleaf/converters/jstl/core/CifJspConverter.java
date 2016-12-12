/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters.jstl.core;

import com.cybernostics.jsp2thymeleaf.api.elements.JspTagElementConverter;
import java.util.Arrays;
import org.jdom2.Attribute;

/**
 *
 * @author jason
 */
public class CifJspConverter extends JspTagElementConverter
{

    public CifJspConverter()
    {
        super("if", "block");
        removesAtributes("test");
        addsAttributes((currentValues)
                -> Arrays.asList(new Attribute("if",
                        currentValues.get("test"), thymeleafNS)));

    }

}
