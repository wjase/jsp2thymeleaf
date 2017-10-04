/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf;

import com.cybernostics.jsp2thymeleaf.api.common.dom.DomWalker;
import com.cybernostics.jsp2thymeleaf.parser.XMLDocumentWriter;
import com.cybernostics.jsp2thymeleaf.postprocessors.DomBlockCleaner;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Description;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.Text;
import org.jdom2.input.SAXBuilder;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author jason
 */
/**
 *
 * @author jason
 */
@RunWith(Parameterized.class)
public class DomBlockCleanerTest
{

    private static Logger LOG = Logger.getLogger(JSP2ThymeleafHappyCaseTest.class.getName());

    private final File uncleanedFile;

    public DomBlockCleanerTest(String name, File uncompressedFile) throws URISyntaxException
    {
        this.uncleanedFile = uncompressedFile;
    }
    private static final String BLOCK_CLEAN_FILES = "block_clean_files";

    @Test
    public void DocCompressorShouldCompress()
    {
        File expectedFile = new File(uncleanedFile.getAbsolutePath().replaceAll(".html$", "_clean.html"));
        Document expectedDom = readXML(expectedFile).get();
        Document domToProcess = readXML(uncleanedFile).get();
        DomWalker blockCleaner = new DomWalker(DomBlockCleaner.get());
        blockCleaner.walk(domToProcess.getRootElement());
        assertThat(expectedFile.getName() + ":\n", domToProcess, matchesBody(expectedDom));
    }

    public static TypeSafeDiagnosingMatcher<Document> matchesBody(Document expectedDoc)
    {
        Namespace ns = Namespace.getNamespace("", "http://www.w3.org/1999/xhtml");
        return new TypeSafeDiagnosingMatcher<Document>()
        {
            @Override
            protected boolean matchesSafely(Document item, Description mismatchDescription)
            {
                final String expected = bodyText(expectedDoc);
                final String actualBody = bodyText(item);
                if (!expected.trim().equals(actualBody.trim()))
                {
                    mismatchDescription.appendText("but was:\n");
                    mismatchDescription.appendText(actualBody);
                    return false;
                }
                return true;

            }

            @Override
            public void describeTo(Description description)
            {
                description.appendText("Body text containing:\n");
                description.appendText(bodyText(expectedDoc));
            }

            String bodyText(Document item)
            {
                return item.getRootElement().getChild("body", ns)
                        .getContent()
                        .stream()
                        .filter(c -> !(c instanceof Text) || !StringUtils.isBlank(((Text) c).getTextTrim()))
                        .map(c -> asString((Element) c))
                        .collect(Collectors.joining());
            }

        };
    }

    private static String asString(Element el)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        XMLDocumentWriter.write(el, stream);
        return stream.toString();
    }

    @Parameterized.Parameters(name = "{0}")
    public static Iterable<Object[]> data()
    {
        final List<Object[]> files = getTestFiles().
                map(eachFile -> Arrays.asList((Object) eachFile.getName(), (Object) eachFile).toArray())
                .collect(Collectors.toList());
        assertThat(files, not(empty()));
        return files;
    }

    @After
    public void shouldTestAllCases()
    {
        final URL resource = JSP2ThymeleafHappyCaseTest.class.getClassLoader().getResource(BLOCK_CLEAN_FILES);
        final File file = new File(resource.getFile());
        assertThat((int) getTestFiles().count(), is(file.list().length / 2));
    }

    public static Stream<File> getTestFiles()
    {
        final URL resource = JSP2ThymeleafHappyCaseTest.class.getClassLoader().getResource(BLOCK_CLEAN_FILES);
        final File file = new File(resource.getFile());
        return Arrays.asList(file.listFiles())
                .stream()
                .filter(it -> it.getName().contains("html"))
                .filter(it -> !it.getName().contains("_clean"))
                .sorted();
    }

    private static Optional<Document> readXML(File file)
    {
        SAXBuilder builder = new SAXBuilder();
        try
        {
            return Optional.of((Document) builder.build(file));
        } catch (JDOMException ex)
        {
            Logger.getLogger(DomBlockCleanerTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(DomBlockCleanerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Optional.empty();
    }

}
