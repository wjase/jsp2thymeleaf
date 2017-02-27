/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author jason
 */
public class JSPParserHandler extends DefaultHandler
{

    @Override
    public void characters(char[] chars, int i, int i1) throws SAXException
    {
        super.characters(chars, i, i1); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void endElement(String string, String string1, String string2) throws SAXException
    {
        super.endElement(string, string1, string2); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void startElement(String string, String string1, String string2, Attributes atrbts) throws SAXException
    {
        super.startElement(string, string1, string2, atrbts); //To change body of generated methods, choose Tools | Templates.
    }
    
}
