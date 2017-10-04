/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.postprocessors;

import static com.cybernostics.jsp2thymeleaf.api.common.Namespaces.TH;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jdom2.Content;
import org.jdom2.Element;

/**
 * Searches for &lt;span th:text=""&gt; elements in scripts and converts them
 * into inline
 *
 * @author jason
 */
public class ScriptInlineSpanConverter extends ScriptContextAwareDomVisitor
{

    public static ScriptInlineSpanConverter get()
    {
        return new ScriptInlineSpanConverter();
    }

    /**
     *
     */
    private ScriptInlineSpanConverter()
    {

    }

    @Override
    protected void onNewLeaf(Content c)
    {
        super.onNewLeaf(c);

        Optional<Element> maybeScript = asContentWithTagName("script", c);
        maybeScript.ifPresent((Element script) ->
        {
            List<? extends Content> children = script.removeContent();
            List<Content> childrenWithoutSpans = new ArrayList();
            Optional<Content> lastChild = Optional.empty();
            while (!children.isEmpty())
            {
                Content child = children.remove(0);
                Optional<Element> trySpan = asContentWithTagName("span", child);
                if (trySpan.isPresent())
                {
                    Element span = trySpan.get();
                    span.setName("block");
                    span.setNamespace(TH);
                    childrenWithoutSpans.add(span);
                } else
                {
                    childrenWithoutSpans.add(child);
                }
            }
            script.setContent(childrenWithoutSpans);
        });
    }

}
