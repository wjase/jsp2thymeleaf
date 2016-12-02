/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf;

import static java.util.Arrays.asList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jason
 */
public enum TreeNodeType
{
    ATTRIBUTES(com.cybernostics.forks.jsp2x.JspParser.ATTRIBUTES),
    NAMECHAR(com.cybernostics.forks.jsp2x.JspParser.NAMECHAR),
    LETTER(com.cybernostics.forks.jsp2x.JspParser.LETTER),
    TAG_EMPTY_CLOSE(com.cybernostics.forks.jsp2x.JspParser.TAG_EMPTY_CLOSE),
    PCDATA(com.cybernostics.forks.jsp2x.JspParser.PCDATA),
    COMMENT(com.cybernostics.forks.jsp2x.JspParser.COMMENT),
    JSP_DIRECTIVE_CLOSE(com.cybernostics.forks.jsp2x.JspParser.JSP_DIRECTIVE_CLOSE),
    EL_EXPR(com.cybernostics.forks.jsp2x.JspParser.EL_EXPR),
    WHITESPACE(com.cybernostics.forks.jsp2x.JspParser.WHITESPACE),
    JSP_SCRIPTLET(com.cybernostics.forks.jsp2x.JspParser.JSP_SCRIPTLET),
    JSP_DIRECTIVE_OPEN(com.cybernostics.forks.jsp2x.JspParser.JSP_DIRECTIVE_OPEN),
    TAG_START_OPEN(com.cybernostics.forks.jsp2x.JspParser.TAG_START_OPEN),
    EOF(com.cybernostics.forks.jsp2x.JspParser.EOF),
    ATTRIBUTE(com.cybernostics.forks.jsp2x.JspParser.ATTRIBUTE),
    ATTR_VALUE_CLOSE(com.cybernostics.forks.jsp2x.JspParser.ATTR_VALUE_CLOSE),
    GENERIC_ID(com.cybernostics.forks.jsp2x.JspParser.GENERIC_ID),
    JSP_COMMENT(com.cybernostics.forks.jsp2x.JspParser.JSP_COMMENT),
    ATTR_EQ(com.cybernostics.forks.jsp2x.JspParser.ATTR_EQ),
    TAG_END_OPEN(com.cybernostics.forks.jsp2x.JspParser.TAG_END_OPEN),
    DIGIT(com.cybernostics.forks.jsp2x.JspParser.DIGIT),
    JSP_EXPRESSION(com.cybernostics.forks.jsp2x.JspParser.JSP_EXPRESSION),
    ELEMENT(com.cybernostics.forks.jsp2x.JspParser.ELEMENT),
    ATTR_VALUE_OPEN(com.cybernostics.forks.jsp2x.JspParser.ATTR_VALUE_OPEN),
    PROCESSING_INSTRUCTION(com.cybernostics.forks.jsp2x.JspParser.PROCESSING_INSTRUCTION),
    DOCTYPE_DEFINITION(com.cybernostics.forks.jsp2x.JspParser.DOCTYPE_DEFINITION),
    TAG_CLOSE(com.cybernostics.forks.jsp2x.JspParser.TAG_CLOSE),
    CDATA(com.cybernostics.forks.jsp2x.JspParser.CDATA),
    JSP_DIRECTIVE(com.cybernostics.forks.jsp2x.JspParser.JSP_DIRECTIVE);
    
    private static Map<Integer,TreeNodeType> intMap = new HashMap<>();
    static {
        for(TreeNodeType nodeType:asList(TreeNodeType.values())){
         intMap.put(nodeType.value, nodeType);
        }
    }
    
    private final int value;

    private TreeNodeType(int value){
        this.value = value;
    }
    
    public static TreeNodeType valueOf(int i){
        return intMap.get(i);
    }
    
}
