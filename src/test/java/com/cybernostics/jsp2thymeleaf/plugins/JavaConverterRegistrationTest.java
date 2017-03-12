/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.plugins;

import com.cybernostics.jsp2thymeleaf.JSP2ThymeleafConfiguration;
import com.cybernostics.jsp2thymeleaf.JSP2ThymeleafConfiguration.JSP2ThymeleafConfigurationBuilder;
import com.cybernostics.jsp2thymeleaf.api.common.AvailableConverters;
import static com.cybernostics.jsp2thymeleaf.converters.ConverterScanner.scanForConverters;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import static org.hamcrest.CoreMatchers.is;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 *
 * @author jason
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class JavaConverterRegistrationTest
{

    @Before
    public void resetConverters()
    {
        AvailableConverters.reset();
    }

    @Test
    public void shouldRegisterJavaTaglibConverter()
    {
        MatcherAssert.assertThat(AvailableConverters.elementConverterforUri("http://oldnamespace").isPresent(), is(false));
        MatcherAssert.assertThat(AvailableConverters.functionConverterforUri("http://oldnamespace/fn").isPresent(), is(false));

        JSP2ThymeleafConfiguration configuration = JSP2ThymeleafConfiguration.parse("-p", "com.cybernostics.jsp2thymeleaf.plugins.converters.stub");
        scanForConverters(configuration);

        MatcherAssert.assertThat(AvailableConverters.elementConverterforUri("http://oldnamespace").isPresent(), is(true));
        MatcherAssert.assertThat(AvailableConverters.functionConverterforUri("http://oldnamespace/fn").isPresent(), is(true));
    }

    @Test
    public void shouldRegisterGroovyTaglibConverter() throws URISyntaxException
    {
        final URI uri = getClass().getClassLoader().getResource("com/cybernostics/jsp2thymeleaf/plugins/converters/stub/").toURI();
        MatcherAssert.assertThat(AvailableConverters.elementConverterforUri("http://oldnamespace").isPresent(), is(false));
        MatcherAssert.assertThat(AvailableConverters.functionConverterforUri("http://oldnamespace/fn").isPresent(), is(false));
        JSP2ThymeleafConfiguration configuration = new JSP2ThymeleafConfigurationBuilder()
                .withConverterScripts(Paths.get(uri).toString()).build();
        scanForConverters(configuration);

        MatcherAssert.assertThat(AvailableConverters.elementConverterforUri("http://oldnamespace").isPresent(), is(true));
        MatcherAssert.assertThat(AvailableConverters.functionConverterforUri("http://oldnamespace/fn").isPresent(), is(true));
    }
}
