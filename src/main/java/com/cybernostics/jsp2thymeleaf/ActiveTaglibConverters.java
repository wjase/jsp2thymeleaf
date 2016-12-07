/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf;

import com.cybernostics.jsp2thymeleaf.api.TagConverterSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author jason
 */
public class ActiveTaglibConverters
{
    private static Map<String, TagConverterSource> activeTagConverters = new HashMap<>();

    public static void addTaglibConverter(String prefix, TagConverterSource converterSource){
        activeTagConverters.put(prefix, converterSource);
    }
    
    public static Optional<TagConverterSource> forPrefix(String prefix){
        return Optional.ofNullable(activeTagConverters.getOrDefault(prefix, null));
    }
}
