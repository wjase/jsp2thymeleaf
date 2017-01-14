/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters;

import java.nio.file.Path;

/**
 *
 * @author jason
 */
public class JSP2ThymeLeafConverterException extends Exception
{

    private Path affectedFile;

    public Path getAffectedFile()
    {
        return affectedFile;
    }

    public JSP2ThymeLeafConverterException(Exception cause, Path affectedFile)
    {
        super(cause);
        this.affectedFile = affectedFile;
    }

}
