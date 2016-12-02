/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters.jstl;

import com.cybernostics.forks.jsp2x.JspTree;
import com.cybernostics.jsp2thymeleaf.api.JspConverterContext;
import static com.cybernostics.jsp2thymeleaf.api.NewAttributeBuilder.humanReadable;
import com.cybernostics.jsp2thymeleaf.api.JspTagElementConverter;
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
        super("c:out","span");
        removesAtributes("value");
        addsAttributes((currentValues)->Arrays.asList(new Attribute("text", currentValues.get("value"), thymeleafNS)));
    }
    
    @Override
    protected List<Content> getChildContent(JspTree jspTree, JspConverterContext context)
    {
        final Optional<JspTree> maybeValueNode = getAttribute(jspTree, "value");
        String value = maybeValueNode.map((item) -> item.treeValue().toStringTree()).orElse("");
        return Arrays.asList(new Text(humanReadable(value)));
    }

    @Override
    protected Namespace newNamespaceForElement(JspTree jspTree)
    {
        return xmlNS; //To change body of generated methods, choose Tools | Templates.
    }
    
    

}
