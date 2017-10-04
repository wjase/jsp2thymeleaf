/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.parser;

import com.cybernostics.jsp.parser.JSPParser;
import com.cybernostics.jsp.parser.JSPParserBaseListener;
import org.jdom2.Document;

/**
 *
 * @author jason
 */
public class JSPTransformer extends JSPParserBaseListener
{

    private Document doc = new Document();

    @Override
    public void enterJspElement(JSPParser.JspElementContext ctx)
    {
        super.enterJspElement(ctx); 
    }

    @Override
    public void enterScriptlet(JSPParser.ScriptletContext ctx)
    {
        super.enterScriptlet(ctx); 
    }

    @Override
    public void enterJspDirective(JSPParser.JspDirectiveContext ctx)
    {
        super.enterJspDirective(ctx); 
    }

    @Override
    public void enterJspDocument(JSPParser.JspDocumentContext ctx)
    {
        super.enterJspDocument(ctx); 
    }

}
