/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.postprocessors;

import java.util.Optional;
import java.util.function.Consumer;
import org.jdom2.Content;
import org.jdom2.Element;

/**
 * Searches for &lt;span th:text=""&gt; elements in scripts and converts them
 * into inline
 *
 * @author jason
 */
public class ScriptContextAwareDomVisitor extends StructuredDomVisitor implements Consumer<Content>
{

    private Optional<Element> currentScriptContext = Optional.empty();

    /**
     *
     */
    protected ScriptContextAwareDomVisitor()
    {
    }

    @Override
    protected void onExitLeaf(Content c)
    {
        asContentWithTagName("script", c).ifPresent(s -> currentScriptContext = Optional.empty());
    }

    @Override
    protected void onNewLeaf(Content c)
    {
        if (!currentScriptContext.isPresent())
        {
            currentScriptContext = asContentWithTagName("script", c);
        }
    }

    public Optional<Element> getCurrentScriptContext()
    {
        return currentScriptContext;
    }

}
