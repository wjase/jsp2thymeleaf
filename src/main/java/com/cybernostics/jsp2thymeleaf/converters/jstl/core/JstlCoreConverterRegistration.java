/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters.jstl.core;

import com.cybernostics.jsp2thymeleaf.api.common.AvailableConverters;
import com.cybernostics.jsp2thymeleaf.api.common.taglib.ConverterRegistration;
import static com.cybernostics.jsp2thymeleaf.api.elements.JspTagElementConverter.CN;
import static com.cybernostics.jsp2thymeleaf.api.elements.JspTagElementConverter.TH;
import static com.cybernostics.jsp2thymeleaf.api.elements.JspTagElementConverter.XMLNS;
import static com.cybernostics.jsp2thymeleaf.api.elements.JspTagElementConverter.converterFor;
import static com.cybernostics.jsp2thymeleaf.api.elements.JspTagElementConverter.ignore;
import static com.cybernostics.jsp2thymeleaf.api.elements.NewAttributeBuilder.attributeNamed;
import com.cybernostics.jsp2thymeleaf.api.elements.TagConverterSource;
import static com.cybernostics.jsp2thymeleaf.api.util.AlternateFormatStrings.constant;
import static com.cybernostics.jsp2thymeleaf.api.util.AlternateFormatStrings.fromFormats;
import static com.cybernostics.jsp2thymeleaf.util.ThymeleafUrl.formatUrl;

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
                .withConverters(converterFor("if")
                        .withNewName("block", TH)
                        .renamesAttribute("test", "if", TH),
                        converterFor("choose")
                                .withNewName("firstTrueChild", CN),
                        converterFor("when")
                                .withNewName("block", TH)
                                .renamesAttribute("test", "if", TH),
                        converterFor("otherwise")
                                .withNewName("block", TH)
                                .addsAttributes(
                                        attributeNamed("test", TH)
                                                .withValue(constant("${true}"))),
                        converterFor("out")
                                .withNewName("span", XMLNS)
                                .renamesAttribute("value", "text", TH)
                                .withNewTextContent("%{value!humanReadable}"),
                        converterFor("fortokens")
                                .withNewName("block", TH)
                                .removesAtributes("var", "varStatus", "items", "delims")
                                .addsAttributes(
                                        attributeNamed("each", TH)
                                                .withValue(fromFormats(
                                                        "%{var}%{varStatus|!addCommaPrefix} : ${#strings.split('%{items}'%{delims|!singleQuoted,addCommaPrefix})"))),
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
                                .withChildElementAtributes((nodeAndContext) ->
                                {
                                    return nodeAndContext.paramsBy("name", "value");
                                })
                                .withNewName("span", XMLNS)
                                .removesAtributes("value", "var", "scope", "context")
                                .addsAttributes(
                                        attributeNamed("text", TH)
                                                .withValue(fromFormats(
                                                        "@{~%{context}%{value}}(%{_childAtts!kvMap})",
                                                        "@{%{value}}(%{_childAtts!kvMap})",
                                                        "@{~%{context}%{value}}",
                                                        "@{%{value}}"))
                                )
                                .whenQuoted((node) ->
                                {
                                    node.warnParamsNotInQuoted("scope", "var");
                                    return formatUrl(node.attAsValue("value"),
                                            node.attAsValue("context"),
                                            node.paramsBy("name", "value"));
                                }),
                        ignore("param")
                );

        AvailableConverters.addConverter("http://java.sun.com/jstl/core", jstlCoreTaglibConverterSource);
        AvailableConverters.addConverter("http://java.sun.com/jsp/jstl/core", jstlCoreTaglibConverterSource);

    }

}
