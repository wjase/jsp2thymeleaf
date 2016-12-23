/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters.jstl.core;

import com.cybernostics.forks.jsp2x.JspTree;
import com.cybernostics.jsp2thymeleaf.api.elements.JspTagElementConverter;
import static com.cybernostics.jsp2thymeleaf.api.elements.NewAttributeBuilder.attributeNamed;
import static com.cybernostics.jsp2thymeleaf.api.util.AlternateFormatStrings.fromFormats;
import org.jdom2.Namespace;

/**
 *
 * @author jason
 */
public class CurlJspConverter extends JspTagElementConverter
{

    public CurlJspConverter()
    {
        super("url", "span");
        //removesAtributes("value", "var", "scope", "context");
        addsAttributes(attributeNamed("text", TH)
                .withValue(fromFormats("@{${value}}"))
        );

    }

    @Override
    protected Namespace newNamespaceForElement(JspTree jspTree)
    {
        return XMLNS;
    }

    @Override
    protected String newNameForElement(JspTree jspTree)
    {
        return super.newNameForElement(jspTree);
    }
}