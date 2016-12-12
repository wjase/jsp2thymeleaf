/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters.jstl.functions;

import static com.cybernostics.jsp2thymeleaf.api.expressions.DefaultFunctionExpressionConverter.convertsMethodCall;
import com.cybernostics.jsp2thymeleaf.api.expressions.ExpressionFunctionConverter;
import com.cybernostics.jsp2thymeleaf.api.expressions.FunctionConverterSource;
import java.util.Arrays;
import java.util.List;
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
        "containsIgnoreCase"
    };
    private static List<ExpressionFunctionConverter> jstlConverters
            = Arrays.stream(simpleConversions)
                    .map(method -> convertsMethodCall(method).toMethodCall(String.format("#strings.%s", method)))
                    .collect(Collectors.toList());

    public JstlCoreFunctionsExpressionConverterSource()
    {
        super("http://java.sun.com/jstl/functions", jstlConverters);
    }
}
