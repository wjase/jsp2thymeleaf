package com.cybernostics.jsp2thymeleaf;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 *
 * @author jason
 */
class InputStreamCollector
{

    public static Collector<String, List<String>, InputStream> toInputSteam()
    {
        return new Collector<String, List<String>, InputStream>()
        {
            @Override
            public Supplier<List<String>> supplier()
            {
                return () -> new ArrayList<String>();
            }

            @Override
            public BiConsumer<List<String>, String> accumulator()
            {
                return (accum, item) -> accum.add(item);

            }

            @Override
            public BinaryOperator<List<String>> combiner()
            {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Function<List<String>, InputStream> finisher()
            {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Set<Collector.Characteristics> characteristics()
            {
                return new HashSet<Characteristics>();
            }
        };
    }
}
