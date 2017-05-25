/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters.jstl.functions;

import com.cybernostics.jsp2thymeleaf.api.expressions.ExpressionVisitor;
import static com.cybernostics.jsp2thymeleaf.api.expressions.function.DefaultFunctionExpressionConverter.convertsMethodCall;
import com.cybernostics.jsp2thymeleaf.api.expressions.function.FunctionConverterSource;
import com.cybernostics.jsp2thymeleaf.api.util.SimpleStringTemplateProcessor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 *
 * @author jason
 */
public class JstlCoreFunctionsExpressionConverterSource extends FunctionConverterSource
{

    private static String[] simpleConversions =
    {
        "contains",
        "containsIgnoreCase",
        "endsWith",
        "startsWith",
        "escapeXml",
        "indexOf",
        "substring",
        "substring",
        "substringAfter",
        "substringBefore",
        "toLowerCase",
        "toUpperCase",
        "trim",
        "join",
        "length",
        "replace",
        "split"
    };
    private static List< ExpressionVisitor> jstlConverters
            = Arrays.stream(simpleConversions)
                    .map(method -> convertsMethodCall(method).toMethodCall(String.format("#strings.%s", method)))
                    .collect(Collectors.toList());

    public JstlCoreFunctionsExpressionConverterSource()
    {
        super("http://java.sun.com/jstl/functions", jstlConverters);
    }

    public FunctionConverterSource convertsMethodsUsingFormat(String methodFormat, String... inputFunctions)
    {
        Arrays.stream(inputFunctions)
                .map(method -> convertsMethodCall(method).toMethodCall(format(methodFormat, method)))
                .forEach(converter -> add(converter));
        return this;
    }

    private String format(String methodFormat, String method)
    {
        Map<String, Object> values = new TreeMap<>();
        values.put("method", method);
        return SimpleStringTemplateProcessor.generate(methodFormat, values);
    }

}
