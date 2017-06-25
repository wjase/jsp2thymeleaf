/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.parser;

import com.cybernostics.jsp2thymeleaf.ScriptContextAwareDomVisitor;
import static com.cybernostics.jsp2thymeleaf.api.common.Namespaces.XMLNS;
import static com.cybernostics.jsp2thymeleaf.api.util.SetUtils.setOf;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Text;

/**
 *
 * @author jason
 */
public class DomBlockCleaner extends ScriptContextAwareDomVisitor
{

    public static DomBlockCleaner get()
    {
        return new DomBlockCleaner();
    }

    private DomBlockCleaner()
    {

    }

    @Override
    public void accept(Content c)
    {
        super.accept(c);
        if (c == null)
        {
            return;
        }
        if (c instanceof Element)
        {
            Element el = (Element) c;
            if ("block".equals(el.getName()))
            {
                if (!getCurrentScriptContext().isPresent())
                {
                    if (!doesTHBlockOnlyWrapHTMLBlockElements(el))
                    {
                        el.setName("span");
                        el.setNamespace(XMLNS);
                    }
                    Optional<Element> singleWrappedBlockElement = findSingleWrappedBlockElement(el);
                    if (singleWrappedBlockElement.isPresent())
                    {
                        Element wrappedBlock = singleWrappedBlockElement.get();
                        List<Content> content = wrappedBlock.getContent();
                        content.forEach(it ->
                        {
                            it.detach();
                            el.addContent(it);
                        });
                        el.setName(wrappedBlock.getName());
                        el.setNamespace(wrappedBlock.getNamespace());
                        wrappedBlock.getAttributes().forEach(att ->
                        {
                            att.detach();
                            el.setAttribute(att);
                        });
                        wrappedBlock.detach();
                    }

                }
            }
        }
    }

    private static Set<String> blockElementNames = setOf("p,h1,h2,h3,h4,h5,h6,ol,ul,pre,address,blockquote,dl,div,fieldset,form,hr,noscript,table".split(","));

    private static boolean isBlockElement(Content c)
    {
        return c instanceof Element && blockElementNames.contains(((Element) c).getName());
    }

    private static boolean doesTHBlockOnlyWrapHTMLBlockElements(Element el)
    {
        return el.getContent()
                .stream()
                .filter(c -> isBlockElement(c))
                .findAny().isPresent();
    }

    private static Optional<Element> findSingleWrappedBlockElement(Element el)
    {
        final Wrapper blockElement = new Wrapper();
        boolean foundOnlyOneBlock = !el.getContent()
                .stream()
                .filter(c -> nonWhitespace(c))
                .filter(c -> isBlockElement(c))
                .peek(c -> blockElement.setWrapped(Optional.of((Element) c)))
                .skip(1) // skip one if present then see if any left
                .findAny().isPresent();
        if (foundOnlyOneBlock)
        {
            return blockElement.getWrapped();
        }
        return Optional.empty();
    }

    private static boolean nonWhitespace(Content c)
    {
        return !(c instanceof Text) || (((Text) c).getValue().matches("\\w"));
    }

    private static class Wrapper
    {

        Optional<Element> wrapped = Optional.empty();

        public Optional<Element> getWrapped()
        {
            return wrapped;
        }

        public void setWrapped(Optional<Element> wrapped)
        {
            this.wrapped = wrapped;
        }
    }
}
