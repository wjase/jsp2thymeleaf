/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.converters.jstl.core;

import com.cybernostics.jsp2thymeleaf.api.JspTagElementConverter;
import java.util.Arrays;
import java.util.Map;
import org.jdom2.Attribute;

/**
 *
 * @author jason
 */
public class ForeachJspConverter extends JspTagElementConverter
{

    public ForeachJspConverter()
    {
        super("foreach", "block");
        removesAtributes("var", "begin", "end", "step", "varStatus", "items", "step");
        addsAttributes((currentValues)
                -> Arrays.asList(
                        new Attribute("each", combineEachValue(currentValues), thymeleafNS)));
    }

    public static String fmtIfPresent(Map<String, String> map, String key, String fmt, String deflt)
    {
        String value = map.getOrDefault(key, deflt);
        return value.length() == 0 ? "" : String.format(fmt, value);

    }

    public static String combineEachValue(Map<String, String> atts)
    {

        String optionalStatus = fmtIfPresent(atts, "varStatus", ", %s", "");

        String items = atts.getOrDefault("items",
                String.format("#numbers.sequence(%s,%s%s)",
                        atts.get("begin"),
                        atts.get("end"),
                        fmtIfPresent(atts, "step", ",%s", "")));
        return String.format("%s%s : ${%s}",
                atts.get("var"),
                optionalStatus,
                items);
    }
}
