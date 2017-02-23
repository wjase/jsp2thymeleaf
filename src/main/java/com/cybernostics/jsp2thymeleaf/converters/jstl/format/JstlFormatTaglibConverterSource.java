/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters.jstl.format;

import static com.cybernostics.jsp2thymeleaf.api.expressions.function.DefaultFunctionExpressionConverter.convertsMethodCall;
import com.cybernostics.jsp2thymeleaf.api.expressions.function.FunctionConverterSource;
import com.cybernostics.jsp2thymeleaf.api.expressions.ExpressionVisitor;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author jason
 */
public class JstlFormatTaglibConverterSource extends FunctionConverterSource
{

    private static String[] simpleConversions =
    {
        "contains",
        "containsIgnoreCase"
    };
    private static List<ExpressionVisitor> jstlConverters
            = Arrays.stream(simpleConversions)
                    .map(method -> convertsMethodCall(method).toMethodCall(method))
                    .collect(Collectors.toList());

    public JstlFormatTaglibConverterSource()
    {
        super("http://java.sun.com/jstl/format", jstlConverters);
    }
}
