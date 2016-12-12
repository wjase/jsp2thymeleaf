/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters.jstl.core;

import com.cybernostics.forks.jsp2x.JspTree;
import com.cybernostics.jsp2thymeleaf.api.elements.JspTagElementConverter;
import com.cybernostics.jsp2thymeleaf.api.elements.JspTreeConverterContext;
import static com.cybernostics.jsp2thymeleaf.api.elements.NewAttributeBuilder.humanReadable;
import static com.cybernostics.jsp2thymeleaf.api.util.JspTreeUtils.getAttribute;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Namespace;
import org.jdom2.Text;

/**
 *
 * @author jason
 */
public class CoutJspConverter extends JspTagElementConverter
{

    public CoutJspConverter()
    {
        super("out", "span");
        removesAtributes("value");
        addsAttributes((currentValues) -> Arrays.asList(new Attribute("text", currentValues.get("value"), thymeleafNS)));
    }

    @Override
    protected List<Content> getChildContent(JspTree jspTree, JspTreeConverterContext context)
    {
        final Optional<JspTree> maybeValueNode = getAttribute(jspTree, "value");
        String value = expressionConverter.convert(maybeValueNode.map((item) -> item.treeValue().toStringTree()).orElse(""));
        return Arrays.asList(new Text(humanReadable(value)));
    }

    @Override
    protected Namespace newNamespaceForElement(JspTree jspTree)
    {
        return xmlNS;
    }

}
