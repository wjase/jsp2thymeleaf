/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf;

import com.cybernostics.jsp2thymeleaf.api.elements.ELExpressionConverter;
import com.cybernostics.jsp2thymeleaf.api.elements.ScopedJSPConverters;
import com.cybernostics.jsp2thymeleaf.converters.jstl.functions.JstlCoreFunctionsExpressionConverterSource;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.commons.el.parser.ParseException;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 *
 * @author jason
 */
@RunWith(Parameterized.class)
public class ExpressionConverterTest
{

    private final String fromName;
    private final String toName;

    public ExpressionConverterTest(String fromName, String toName)
    {
        this.fromName = fromName;
        this.toName = toName;
    }

    @Before
    public void setupConverters()
    {
        converters = new ScopedJSPConverters();
        converters.addTaglibFunctionConverter("fn", new JstlCoreFunctionsExpressionConverterSource());
    }
    private ScopedJSPConverters converters;

    @Test
    public void expressionConverterShouldConvert() throws ParseException
    {
        ELExpressionConverter eLExpressionConverter = new ELExpressionConverter();

        assertThat(eLExpressionConverter.convert(fromName, converters), is(toName));
    }

    @Parameters(name = "{0} -> {1}")
    public static Iterable<Object[]> data()
    {
        try
        {
            final URL resource = ExpressionConverterTest.class.getClassLoader().getResource("jstl_expressions/expression_transforms.txt");
            return Files
                    .lines(Paths.get(resource.toURI()))
                    .filter(line -> line.contains("==>"))
                    .filter(line -> !line.startsWith("#"))
                    .map(line -> line.split(" ==> "))
                    .map(eachList -> Arrays.asList((Object) eachList[0], (Object) eachList[1]).toArray())
                    .collect(Collectors.toList());

        } catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

}
