/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters;

import com.cybernostics.jsp2thymeleaf.api.common.TokenisedFile;
import com.cybernostics.jsp2thymeleaf.api.exception.DefaultFileErrorLocation;
import com.cybernostics.jsp2thymeleaf.api.exception.JSP2ThymeLeafException;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNSimulator;

/**
 *
 * @author jason
 */
public class JSPSyntaxException extends JSP2ThymeLeafException
{

    JSPSyntaxException(TokenisedFile file, Recognizer<?, ? extends ATNSimulator> rcgnzr, Object o, int line, int column, String message, RecognitionException re)
    {
        super(String.format("Syntax Error: %s", file.getRelativePathString(), message));
        setLocationSource(() -> new DefaultFileErrorLocation(file.getPath().toString(), line, column));

    }

}
