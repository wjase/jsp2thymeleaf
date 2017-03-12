/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters;

import com.cybernostics.jsp2thymeleaf.api.common.TokenisedFile;
import com.cybernostics.jsp2thymeleaf.api.exception.JSP2ThymeLeafException;
import com.cybernostics.jsp2thymeleaf.api.exception.MutableFileLocation;
import static com.cybernostics.jsp2thymeleaf.api.util.MapUtils.entry;
import static com.cybernostics.jsp2thymeleaf.api.util.MapUtils.mapOf;
import com.cybernostics.jsp2thymeleaf.api.util.SimpleStringTemplateProcessor;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.logging.Logger;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

/**
 *
 * @author jason
 */
public class JSP2ThymeleafErrorCollector implements ANTLRErrorListener
{

    public static final Logger LOG = Logger.getLogger(JSP2ThymeleafErrorCollector.class.getName());
    private final List<JSP2ThymeLeafException> exceptions = new ArrayList<>();

    public List<JSP2ThymeLeafException> getExceptions()
    {
        return exceptions;
    }
    private final TokenisedFile file;

    JSP2ThymeleafErrorCollector(TokenisedFile file)
    {
        this.file = file;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> rcgnzr, Object o, int i, int i1, String string, RecognitionException re)
    {
        exceptions.add(new JSPSyntaxException(file, rcgnzr, o, i, i1, string, re));
    }

    @Override
    public void reportAmbiguity(Parser parser, DFA dfa, int line, int column, BitSet bitset, ATNConfigSet atncs)
    {
        LOG.finer(getParserError("Parser ambiguity detected", parser, dfa, line, column));
    }

    public String getParserError(String message, Parser parser, DFA dfa, int line, int column)
    {
        return SimpleStringTemplateProcessor.generate(message + "${message} :${file}: (${line},${column})",
                mapOf(
                        entry("message", message),
                        entry("file", file.getRelativePathString()),
                        entry("line", Integer.toString(line)),
                        entry("column", Integer.toString(column))
                ));

    }

    @Override
    public void reportAttemptingFullContext(Parser parser, DFA dfa, int line, int column, ATNConfigSet atncs)
    {
        LOG.finer(getParserError("Parser attempting full context", parser, dfa, line, column));
    }

    @Override
    public void reportContextSensitivity(Parser parser, DFA dfa, int line, int column, ATNConfigSet atncs)
    {
        LOG.finer(getParserError("Parser ContextSensitivity:", parser, dfa, line, column));
    }

    void add(List<JSP2ThymeLeafException> problems)
    {
        problems.stream().forEach((nodeEx) ->
        {
            if (nodeEx instanceof MutableFileLocation)
            {
                ((MutableFileLocation) nodeEx).setFile(file);
            }
            exceptions.add(nodeEx);
        });
    }

}
