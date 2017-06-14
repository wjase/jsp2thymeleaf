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
import static com.cybernostics.jsp2thymeleaf.api.elements.NewAttributeBuilder.named;
import static com.cybernostics.jsp2thymeleaf.api.elements.NewAttributeBuilder.namedTH;
import com.cybernostics.jsp2thymeleaf.api.elements.TagConverterSource;
import static com.cybernostics.jsp2thymeleaf.api.util.AlternateFormatStrings.chooseFormat;
import static com.cybernostics.jsp2thymeleaf.api.util.AlternateFormatStrings.constant;

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
                        converterFor("choose")
                                .withNewName("block", TH)
                                .addsAttributes(named("filter", CN)
                                        .withValue(constant("first"))),
                        converterFor("when")
                                .withNewName("block", TH)
                                .renamesAttribute("test", "if", TH),
                        converterFor("otherwise")
                                .withNewName("block", TH)
                                .addsAttributes(namedTH("test")
                                        .withValue(constant("${true}"))),
                        converterFor("out")
                                .withNewName("span", XMLNS)
                                .removesAtributes("value", "var", "scope", "context")
                                .addsAttributes(namedTH("text")
                                        .withValue(chooseFormat(
                                                "#setValue('%{var}','%{scope|page}',%{value})",
                                                "%{value}"))
                                )
                                .whenQuotedInAttributeReplaceWith("text")
                                .withNewTextContent("%{value!humanReadable}"),
                        converterFor("forTokens")
                                .withNewName("block", TH)
                                .removesAtributes("var", "varStatus", "items", "delims")
                                .addsAttributes(namedTH("each")
                                        .withValue(chooseFormat(
                                                "%{var}%{varStatus|!addCommaPrefix} : ${#strings.split('%{items}'%{delims|!singleQuoted,addCommaPrefix})"))),
                        converterFor("forEach")
                                .withNewName("block", TH)
                                .removesAtributes("var", "begin", "end", "step", "varStatus", "items", "step")
                                .addsAttributes(namedTH("each")
                                        .withValue(chooseFormat(
                                                "%{var}%{varStatus|!addCommaPrefix} : %{items}",
                                                "%{var}%{varStatus|!addCommaPrefix} : ${#numbers.sequence(%{begin},%{end}%{step|!addCommaPrefix})}"))),
                        converterFor("set")
                                .withNewName("span")
                                .removesAtributes("var", "scope", "value")
                                .addsAttributes(named("if", TH)
                                        .withValue(
                                                chooseFormat("${%{scope|page}Scope.put('%{var}',%{value!stripEL})}")
                                        )),
                        converterFor("url")
                                .withChildElementAtributes((nodeAndContext) ->
                                {
                                    return nodeAndContext.paramsBy("name", "value");
                                })
                                .withNewName("span", XMLNS)
                                .removesAtributes("value", "var", "scope", "context")
                                .addsAttributes(namedTH("text")
                                        .withValue(chooseFormat(
                                                "${%{scope|page}Params.put('%{var}',@{_<_~%{context}_>_%{value}_<_(%{_childAtts!kvMap})_>_})",
                                                "@{_<_~%{context}_>_%{value}_<_(%{_childAtts!kvMap})_>_}"))
                                )
                                .whenQuotedInAttributeReplaceWith("text"),
                        ignore("param")
                );

        AvailableConverters.addConverter("http://java.sun.com/jstl/core", jstlCoreTaglibConverterSource);
        AvailableConverters.addConverter("http://java.sun.com/jsp/jstl/core", jstlCoreTaglibConverterSource);
    }

}
