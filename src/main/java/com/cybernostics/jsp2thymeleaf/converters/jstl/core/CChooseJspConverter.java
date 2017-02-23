/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters.jstl.core;

import com.cybernostics.jsp.parser.JSPParser;
import com.cybernostics.jsp2thymeleaf.api.elements.JspTagElementConverter;
import java.util.Arrays;
import org.jdom2.Attribute;
import org.jdom2.Namespace;

/**
 *
 * @author jason
 */
public class CChooseJspConverter extends JspTagElementConverter
{

    public CChooseJspConverter()
    {
        super("choose", "block");
        removesAtributes("test");
        addsAttributes((currentValues)
                -> Arrays.asList(new Attribute("if",
                        currentValues.get("test"), thymeleafCN)));

    }
    protected final Namespace thymeleafCN = Namespace.getNamespace("cn", "http://www.cybernostics.com");

    @Override
    protected Namespace newNamespaceForElement(JSPParser.JspElementContext node)
    {
        return thymeleafCN;
    }

}
