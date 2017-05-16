/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters.jsp;

import com.cybernostics.jsp.parser.JSPParser;
import com.cybernostics.jsp2thymeleaf.api.common.Namespaces;
import static com.cybernostics.jsp2thymeleaf.api.common.Namespaces.TH;
import com.cybernostics.jsp2thymeleaf.api.elements.ActiveNamespaces;
import com.cybernostics.jsp2thymeleaf.api.elements.JSPDirectiveConverter;
import com.cybernostics.jsp2thymeleaf.api.elements.JSPElementNodeConverter;
import static com.cybernostics.jsp2thymeleaf.api.util.StringFunctions.trimQuotes;
import static java.util.Arrays.asList;
import java.util.List;
import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Element;

/**
 *
 * @author jason
 */
public class IncludeDirectiveConverter implements JSPDirectiveConverter
{

    @Override
    public List<Content> process(JSPParser.JspDirectiveContext node, JSPElementNodeConverter context)
    {
        String value = trimQuotes(node.atts.stream()
                .filter(it -> it.name.getText().equals("file"))
                .findFirst().get().value.getText()).replaceFirst("\\.jsp$", ".html");
        final Element element = new Element("div");
        element.removeNamespaceDeclaration(element.getNamespace());
        element.setNamespace(TH);
        Attribute attribute = new Attribute("replace", value, Namespaces.TH);
        element.setAttribute(attribute);
        ActiveNamespaces.add(TH);
        return asList(element);

    }

    @Override
    public boolean canHandle(JSPParser.JspDirectiveContext jspTree)
    {
        return jspTree.name.getText().equals("include");
    }

}
