/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters.jstl.core;

import com.cybernostics.jsp2thymeleaf.api.common.taglib.ConverterRegistration;
import com.cybernostics.jsp2thymeleaf.converters.AvailableConverters;
import static com.cybernostics.jsp2thymeleaf.api.elements.JspTagElementConverter.CN;
import static com.cybernostics.jsp2thymeleaf.api.elements.JspTagElementConverter.TH;
import static com.cybernostics.jsp2thymeleaf.api.elements.JspTagElementConverter.XMLNS;
import static com.cybernostics.jsp2thymeleaf.api.elements.JspTagElementConverter.converterFor;
import static com.cybernostics.jsp2thymeleaf.api.elements.NewAttributeBuilder.attributeNamed;
import com.cybernostics.jsp2thymeleaf.api.elements.TagConverterSource;
import static com.cybernostics.jsp2thymeleaf.api.util.AlternateFormatStrings.fromFormats;

/**
 *
 * @author jason
 */
public class JstlCoreConverterRegistration implements ConverterRegistration
{

    @Override
    public void run()
    {
        final TagConverterSource jstlCoreTaglibConverterSource = new TagConverterSource()
                .withConverters(
                        converterFor("if")
                                .withNewName("block", TH)
                                .renamesAttribute("test", "if", TH),
                        converterFor("out")
                                .withNewName("span", XMLNS)
                                .renamesAttribute("value", "text", TH)
                                .withNewTextContent("%{value!humanReadable}"),
                        converterFor("foreach")
                                .withNewName("block", TH)
                                .removesAtributes("var", "begin", "end", "step", "varStatus", "items", "step")
                                .addsAttributes(
                                        attributeNamed("each", TH)
                                                .withValue(fromFormats(
                                                        "%{var}%{varStatus|!addCommaPrefix} : %{items}",
                                                        "%{var}%{varStatus|!addCommaPrefix} : ${#numbers.sequence(%{begin},%{end}%{step|!addCommaPrefix})}"))),
                        converterFor("set")
                                .withNewName("block", TH)
                                .removesAtributes("var", "scope", "value")
                                .addsAttributes(
                                        attributeNamed("expr", CN)
                                                .withValue(
                                                        fromFormats("${#CNPageParams.put%{scope|page!ucFirst}(%{var},%{value!stripEL})}"))
                                ),
                        converterFor("url")
                                .withNewName("span", XMLNS)
                                .removesAtributes("value", "var", "scope", "context")
                                .addsAttributes(
                                        attributeNamed("text", TH)
                                                .withValue(fromFormats("@{${value}}"))
                                ));

        AvailableConverters.addConverter("http://java.sun.com/jstl/core", jstlCoreTaglibConverterSource);
        AvailableConverters.addConverter("http://java.sun.com/jsp/jstl/core", jstlCoreTaglibConverterSource);

    }

}
