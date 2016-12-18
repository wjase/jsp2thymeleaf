package com.cybernostics.jsp2thymeleaf;

import com.cybernostics.jsp2thymeleaf.api.util.JspNodeException;
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
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author jason
 */
@RunWith(Parameterized.class)
public class JSP2ThymeleafErrorCaseTest
{

    private static Logger LOG = Logger.getLogger(JSP2ThymeleafErrorCaseTest.class.getName());

    private final File jspFile;

    public JSP2ThymeleafErrorCaseTest(String name, File JSPFile)
    {
        this.jspFile = JSPFile;

    }

    @Test
    public void JSPToThymeleafShouldRaiseErrorFor()
    {

        JSP2Thymeleaf jSP2Thymeleaf = new JSP2Thymeleaf();
        jSP2Thymeleaf.setShowBanner(false);
        try
        {
            ByteArrayOutputStream convertedFile = new ByteArrayOutputStream();
            jSP2Thymeleaf.convert(new FileInputStream(jspFile), convertedFile);
            fail("Should have caused an Error");
        } catch (IOException ex)
        {
            assertThat(ex.getMessage(), is(getExpectedErrorText()));
        } catch (JspNodeException ex)
        {
            assertThat(ex.getMessage(), is(getExpectedErrorText()));
        }
    }

    private String getExpectedErrorText()
    {
        try
        {
            File expectedErrorTextFilename = new File(jspFile.getAbsolutePath().replaceAll(".jsp$", ".txt"));
            return FileUtils.readFileToString(expectedErrorTextFilename, Charset.defaultCharset());
        } catch (IOException ex)
        {
            Logger.getLogger(JSP2ThymeleafErrorCaseTest.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    @Parameterized.Parameters(name = "{0}")
    public static Iterable<Object[]> data()
    {
        final URL resource = JSP2ThymeleafErrorCaseTest.class.getClassLoader().getResource("error_case_files");
        final File file = new File(resource.getFile());
        return Arrays.asList(file.listFiles())
                .stream()
                .filter(it -> it.getName().contains("jsp"))
                .sorted()
                .map(eachFile -> Arrays.asList((Object) eachFile.getName(), (Object) eachFile).toArray())
                .collect(Collectors.toList());
    }
}
