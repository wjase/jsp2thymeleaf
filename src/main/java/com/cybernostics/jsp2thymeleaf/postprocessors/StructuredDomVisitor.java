/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.postprocessors;

import java.util.Optional;
import java.util.Stack;
import java.util.function.Consumer;
import org.jdom2.Content;
import org.jdom2.Element;

/**
 *
 * @author jason
 */
public class StructuredDomVisitor implements Consumer<Content>
{

    protected final Stack<Content> breadCrumbs;

    public StructuredDomVisitor()
    {
        this.breadCrumbs = new Stack<>();
    }

    @Override
    public void accept(Content t)
    {
        updateBreadcrumbs(t);
    }

    protected void updateBreadcrumbs(Content t)
    {
        if (isNewLeaf(t))
        {
        }
        if (isNewSibling(t))
        {
            onExitLeaf(breadCrumbs.pop());
        } else
        {
            while (!isNewLeaf(t))
            {
                onExitLeaf(breadCrumbs.pop());
            }
        }
        breadCrumbs.push(t);
        onNewLeaf(t);
    }

    protected boolean isNewLeaf(Content t)
    {
        return breadCrumbs.isEmpty() || t.getParent() == breadCrumbs.peek();
    }

    protected boolean isNewSibling(Content t)
    {
        return !breadCrumbs.isEmpty() && t.getParent() == breadCrumbs.peek().getParent();
    }

    protected void onNewLeaf(Content c)
    {
    }

    protected void onExitLeaf(Content c)
    {
    }

    protected Optional<Element> asContentWithTagName(String name, Content c)
    {
        if (c instanceof Element)
        {
            Element el = (Element) c;
            if (el.getName().equals(name))
            {
                return Optional.of(el);
            }
        }
        return Optional.empty();
    }

}
