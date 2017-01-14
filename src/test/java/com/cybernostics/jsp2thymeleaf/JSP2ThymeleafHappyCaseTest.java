package com.cybernostics.jsp2thymeleaf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author jason
 */
@RunWith(Parameterized.class)
public class JSP2ThymeleafHappyCaseTest
{

    private static Logger LOG = Logger.getLogger(JSP2ThymeleafHappyCaseTest.class.getName());

    private final File jspFile;

    public JSP2ThymeleafHappyCaseTest(String name, File JSPFile)
    {
        this.jspFile = JSPFile;

    }

    @Test
    public void JSPToThymeleafShouldConvert()
    {

        JSP2ThymeleafStreamConverter jSP2Thymeleaf = new JSP2ThymeleafStreamConverter();
        jSP2Thymeleaf.setShowBanner(false);
        try
        {
            ByteArrayOutputStream convertedFile = new ByteArrayOutputStream();
            File expectedThymeleafFilename = new File(jspFile.getAbsolutePath().replaceAll(".jsp$", ".html"));
            String expectedContent = FileUtils.readFileToString(expectedThymeleafFilename, Charset.defaultCharset());

            jSP2Thymeleaf.convert(new FileInputStream(jspFile), convertedFile);
            final String convertedContent = convertedFile.toString();
            LOG.info("\n" + convertedContent);
            assertThat(convertedContent.replaceAll("\\s+", " "), is(expectedContent.replaceAll("\\s+", " ")));
        } catch (IOException ex)
        {
            Logger.getLogger(JSP2ThymeleafHappyCaseTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail("exception thrown");
        }
    }

    @Parameterized.Parameters(name = "{0}")
    public static Iterable<Object[]> data()
    {
        final URL resource = JSP2ThymeleafHappyCaseTest.class.getClassLoader().getResource("happy_case_files");
        final File file = new File(resource.getFile());
        return Arrays.asList(file.listFiles())
                .stream()
                .filter(it -> it.getName().contains("jsp"))
                .filter(it -> it.getName().contains("clean"))
                .sorted()
                .map(eachFile -> Arrays.asList((Object) eachFile.getName(), (Object) eachFile).toArray())
                .collect(Collectors.toList());
    }
}
