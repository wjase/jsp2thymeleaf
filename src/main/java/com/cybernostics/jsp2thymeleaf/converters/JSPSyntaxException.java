/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters;

import com.cybernostics.jsp2thymeleaf.parser.TokenisedFile;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNSimulator;

/**
 *
 * @author jason
 */
public class JSPSyntaxException extends RuntimeException
{

    JSPSyntaxException(TokenisedFile file, Recognizer<?, ? extends ATNSimulator> rcgnzr, Object o, int line, int column, String message, RecognitionException re)
    {
        super(String.format("Syntax Error File: %s Line:%4d, Col:%4d  %s", file.getRelativePathString(), line, column, message));
    }

}
