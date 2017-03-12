/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters.jstl.functions;

import com.cybernostics.jsp2thymeleaf.api.common.AvailableConverters;
import com.cybernostics.jsp2thymeleaf.api.common.taglib.ConverterRegistration;

/**
 *
 * @author jason
 */
public class JstlCoreFunctionsExpressionConverterRegistration implements ConverterRegistration
{

    @Override
    public void run()
    {
        JstlCoreFunctionsExpressionConverterSource converterSource = new JstlCoreFunctionsExpressionConverterSource();
        AvailableConverters.addConverter("http://java.sun.com/jstl/functions", converterSource);
        AvailableConverters.addConverter("http://java.sun.com/jsp/jstl/functions", converterSource);
    }

}
