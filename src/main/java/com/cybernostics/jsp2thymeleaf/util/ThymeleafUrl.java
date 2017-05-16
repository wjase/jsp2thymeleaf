/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.util;

import java.util.Map;
import java.util.Optional;
import static java.util.stream.Collectors.joining;

/**
 *
 * @author jason
 */
public class ThymeleafUrl
{

    public static String formatUrl(Optional<String> url, Optional<String> context, Map<String, String> params)
    {
        return String.format("@{%s%s%s}",
                formatContext(context),
                url.orElse("#"),
                formatParams(params));
    }

    private static String formatContext(Optional<String> context)
    {
        final String contextStr = context.orElse("");

        return contextStr.isEmpty() ? contextStr : "~" + contextStr + "/";
    }

    private static Object formatParams(Map<String, String> params)
    {
        return params.isEmpty() ? ""
                : "(" + params
                        .entrySet()
                        .stream()
                        .map(e -> String.format("%s=%s", e.getKey(), e.getValue()))
                        .collect(joining(",")) + ")";
    }
}
