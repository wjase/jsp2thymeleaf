/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 *
 * @author jason
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class JSP2ThymeleafConfigurationTest
{

    /**
     * Test of isShowBanner method, of class JSP2ThymeleafConfiguration.
     */
    @Test
    public void shouldShowBannerWhenSpecified()
    {
        JSP2ThymeleafConfiguration config = JSP2ThymeleafConfiguration.parse("-b");
        assertThat(config.isShowBanner(), is(true));
        config = JSP2ThymeleafConfiguration.parse();
        assertThat(config.isShowBanner(), is(false));
    }

    /**
     * Test of getSrcFolder method, of class JSP2ThymeleafConfiguration.
     */
    @Test
    public void shouldCalculateFilestoProcess() throws URISyntaxException
    {
        final Path src = Paths.get(getClass().getClassLoader().getResource("happy_case_files").toURI());
        JSP2ThymeleafConfiguration config = JSP2ThymeleafConfiguration.parse("-s", src.toString());
        assertThat(config.getFilesToProcess(), not(IsEmptyCollection.empty()));
    }

    /**
     * Test of getSrcFolder method, of class JSP2ThymeleafConfiguration.
     */
    @Test
    public void shouldReportMissingFiles() throws URISyntaxException
    {
        try
        {
            final Path src = Paths.get(getClass().getClassLoader().getResource("happy_case_files").toURI());
            JSP2ThymeleafConfiguration config = JSP2ThymeleafConfiguration.parse("-s", src.toString(), "bazza.jsp");
            assertThat(config.getFilesToProcess(), not(IsEmptyCollection.empty()));

        } catch (IllegalArgumentException iae)
        {
            assertThat(iae.getMessage(), containsString("File(s) not found:"));
            assertThat(iae.getMessage(), containsString("bazza.jsp"));
        }
    }

    @Test
    public void shouldCalculateOutputFilesBasedOnInputFileAndInputFilePath() throws URISyntaxException
    {
        final Path src = Paths.get(getClass().getClassLoader().getResource("happy_case_files").toURI());
        JSP2ThymeleafConfiguration config = JSP2ThymeleafConfiguration.parse("-s", src.toString());
        final Set<Path> filesToProcess = config.getFilesToProcess();
        filesToProcess.forEach(file -> assertThat(config.getOutputPathFor(file), not(file)));

    }
}
