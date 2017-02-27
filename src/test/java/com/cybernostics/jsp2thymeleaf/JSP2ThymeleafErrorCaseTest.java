package com.cybernostics.jsp2thymeleaf;

import com.cybernostics.jsp2thymeleaf.converters.JSP2ThymeleafFileConverter;
import com.cybernostics.jsp2thymeleaf.parser.TokenisedFile;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    final URL rootResource;
    final Path rootPath;

    public JSP2ThymeleafErrorCaseTest(String name, File JSPFile) throws URISyntaxException
    {
        this.jspFile = JSPFile;
        rootResource = JSP2ThymeleafHappyCaseTest.class.getClassLoader().getResource("error_case_files/");
        rootPath = Paths.get(rootResource.toURI());
    }

    @Test
    public void JSPToThymeleafShouldRaiseErrorFor() throws IOException
    {

        JSP2ThymeleafConfiguration configuration = JSP2ThymeleafConfiguration.getBuilder().build();
        JSP2ThymeleafFileConverter jSP2Thymeleaf = new JSP2ThymeleafFileConverter(configuration);
        jSP2Thymeleaf.setShowBanner(false);
        File randomOutFile = File.createTempFile("errorCaseTest", ".jsp");
        try
        {
            TokenisedFile jspFileTok = new TokenisedFile(jspFile.toPath(), rootPath);

            jSP2Thymeleaf.convert(jspFileTok, randomOutFile);

            fail("Should have caused an Error:" + getExpectedErrorText());
        } catch (Exception ex)
        {
            assertThat(ex.getMessage(), is(getExpectedErrorText()));
        } finally
        {
            randomOutFile.delete();
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
