/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters.jstl.core;

import com.cybernostics.forks.jsp2x.JspTree;
import com.cybernostics.jsp2thymeleaf.api.elements.JspTagElementConverter;
import com.cybernostics.jsp2thymeleaf.api.util.ExpressionStringTemplate;
import java.util.Arrays;
import org.jdom2.Attribute;
import org.jdom2.Namespace;

/**
 *
 * Converts something like:
 * <c:set var="salary" scope="session" value="${2000*2}"/>
 * into:
 * <th:block cn:expr="${CNPageParams.putSession('salary',2000*2)}" />
 *
 * @author jason
 */
public class CSetJspConverter extends JspTagElementConverter
{

    protected final Namespace cybernosticsNS = Namespace.getNamespace("cn", "http://www.cybernostics.com");

    private String newExpressionFormat = "$${#CNPageParams.put${scope!ucfirst}(${var},${value!stripEL})}";

    public CSetJspConverter()
    {
        super("set", "block");
        removesAtributes("var", "scope", "value");
        addsAttributes((currentValues)
                -> Arrays.asList(new Attribute("expr",
                        ExpressionStringTemplate.generate(newExpressionFormat, currentValues),
                        cybernosticsNS)));
    }

    @Override
    protected Namespace newNamespaceForElement(JspTree jspTree)
    {
        return thymeleafNS;
    }

}
