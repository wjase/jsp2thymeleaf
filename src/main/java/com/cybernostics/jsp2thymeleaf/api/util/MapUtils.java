/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.api.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections.keyvalue.DefaultMapEntry;

/**
 *
 * @author jason
 */
public class MapUtils
{

    public static <L, R> Map<L, R> mapOf(Map.Entry<L, R>... entries)
    {
        Map<L, R> map = new HashMap<>();
        for (Map.Entry<L, R> entry : entries)
        {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    public static <L, R> Map.Entry<L, R> entry(L left, R right)
    {
        return new DefaultMapEntry(left, right);
    }

}
