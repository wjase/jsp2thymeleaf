/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.parser;

import com.cybernostics.jsp.parser.JSPParser.JspDirectiveContext;
import com.cybernostics.jsp.parser.JSPParserBaseListener;
import com.cybernostics.jsp2thymeleaf.converters.jsp.JSPDirectiveConverterSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 *
 * @author jason
 */
public class JSP2IncludeImportCollector extends JSPParserBaseListener
{

    private List<String> includes = new ArrayList<>();

    public List<String> getIncludes()
    {
        return includes;
    }

    public JSP2IncludeImportCollector()
    {
    }

    final Logger logger = Logger.getLogger(JSP2IncludeImportCollector.class.getName());

    private JSPDirectiveConverterSource jspDirectives = new JSPDirectiveConverterSource();

    @Override
    public void enterJspDirective(JspDirectiveContext ctx)
    {
        if (ctx.name.getText().equals("include"))
        {
            final Optional<String> attribute = ctx.atts.stream()
                    .filter(att -> att.name.getText().equals("file"))
                    .findAny()
                    .map(att -> att.value.getText());
            attribute.ifPresent(includes::add);
        }

    }
}
