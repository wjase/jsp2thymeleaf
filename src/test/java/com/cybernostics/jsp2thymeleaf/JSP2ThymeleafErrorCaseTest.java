package com.cybernostics.jsp2thymeleaf;

import com.cybernostics.jsp2thymeleaf.api.common.TokenisedFile;
import com.cybernostics.jsp2thymeleaf.api.elements.ScopedJSPConverters;
import com.cybernostics.jsp2thymeleaf.api.exception.JSP2ThymeLeafException;
import com.cybernostics.jsp2thymeleaf.converters.JSP2ThymeleafFileConverter;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Description;
import static org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hamcrest.collection.IsIterableContainingInOrder;
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

            final List<JSP2ThymeLeafException> exceptions = jSP2Thymeleaf.convert(jspFileTok, randomOutFile, new ScopedJSPConverters());

            assertThat(exceptions, IsIterableContainingInOrder.contains(getExpectedExceptionMatchers()));

        } finally
        {
            randomOutFile.delete();
        }
    }

    private TypeSafeDiagnosingMatcher<Exception> exceptionWithMessage(final String expectedMessage)
    {
        return new ExceptionMessageMatcher(expectedMessage);
    }

    private ExceptionMessageMatcher[] getExpectedExceptionMatchers()
    {
        try
        {
            ExceptionMessageMatcher[] templateArray = new ExceptionMessageMatcher[0];
            File expectedErrorTextFilename = new File(jspFile.getAbsolutePath().replaceAll(".jsp$", ".txt"));
            return FileUtils.readLines(expectedErrorTextFilename,
                    Charset.defaultCharset())
                    .stream()
                    .map(text -> exceptionWithMessage(text.replaceAll("#\\{file\\}", jspFile.getAbsolutePath())))
                    .collect(toList()).toArray(templateArray);
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

    private static class ExceptionMessageMatcher extends TypeSafeDiagnosingMatcher<Exception>
    {

        private final String expectedMessage;

        public ExceptionMessageMatcher(String expectedMessage)
        {
            this.expectedMessage = expectedMessage;
        }

        @Override
        protected boolean matchesSafely(Exception item, Description mismatchDescription)
        {
            if (item.getMessage().contains(expectedMessage))
            {
                return true;
            }
            mismatchDescription.appendText(item.getMessage());
            return false;
        }

        @Override
        public void describeTo(Description description)
        {
            description.appendText("Exception with message:" + expectedMessage);

        }
    }
}
