/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.plugins.converters.stub;

import com.cybernostics.jsp2thymeleaf.api.common.taglib.ConverterRegistration;
import static com.cybernostics.jsp2thymeleaf.api.elements.JspTagElementConverter.converterFor;
import com.cybernostics.jsp2thymeleaf.api.elements.TagConverterSource;
import com.cybernostics.jsp2thymeleaf.api.expressions.function.FunctionConverterSource;
import com.cybernostics.jsp2thymeleaf.api.common.AvailableConverters;
import org.jdom2.Namespace;

/**
 *
 * @author jason
 */
public class StubConverterRegistration implements ConverterRegistration
{

    public static Namespace TEST = Namespace.getNamespace("TEST", "http://newtestnamespace");

    @Override
    public void run()
    {
        // Add taglib tag converter converts <atag test="x"> into <TEST:block if="x">
        final TagConverterSource testTaglibConverterSource = new TagConverterSource()
                .withConverters(
                        converterFor("atag")
                                .withNewName("block", TEST)
                                .renamesAttribute("test", "if", TEST)
                );

        AvailableConverters.addConverter("http://oldnamespace", testTaglibConverterSource);

        // Add taglib function converter
        AvailableConverters.addConverter(
                FunctionConverterSource
                        .forUri("http://oldnamespace/fn")
                        .convertsMethodsUsingFormat("#newObj.${method}", "methoda", "methodb"));
    }
}
