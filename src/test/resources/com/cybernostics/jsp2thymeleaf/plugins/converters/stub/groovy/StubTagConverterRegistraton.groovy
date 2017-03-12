/*
 * This is an example converter
 */

package com.cybernostics.jsp2thymeleaf.plugins.converters.stub.groovy

import com.cybernostics.jsp2thymeleaf.api.elements.TagConverterSource
import com.cybernostics.jsp2thymeleaf.api.expressions.function.FunctionConverterSource
import static com.cybernostics.jsp2thymeleaf.api.elements.JspTagElementConverter.converterFor
import com.cybernostics.jsp2thymeleaf.api.common.AvailableConverters
import org.jdom2.Namespace

def TEST = Namespace.getNamespace("TEST","http://test.boo");

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
    .convertsMethodsUsingFormat("#newObj.\${method}", "methoda", "methodb"));