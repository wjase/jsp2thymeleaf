/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.dfa.DFA;

/**
 *
 * @author jason
 */
public class JSPParserError extends RuntimeException
{

    private final DFA dfa;

    private final int line;
    private final int column;

    public JSPParserError(String message, Parser parser, DFA dfa, int line, int column)
    {
        super(message);
        this.dfa = dfa;
        this.line = line;
        this.column = column;
    }

    public DFA getDfa()
    {
        return dfa;
    }

    public int getLine()
    {
        return line;
    }

    public int getColumn()
    {
        return column;
    }

}
