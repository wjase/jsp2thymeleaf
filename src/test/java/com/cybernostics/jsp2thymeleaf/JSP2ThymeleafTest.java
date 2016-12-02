package com.cybernostics.jsp2thymeleaf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author jason
 */
public class JSP2ThymeleafTest
{
    @Test
    public void shouldConvertJSPToThymeleaf(){
        
        JSP2Thymeleaf jSP2Thymeleaf = new JSP2Thymeleaf();
        jSP2Thymeleaf.setShowBanner(false);
        for ( File jspFile : getJspFiles() )
        {
            try
            {
                ByteArrayOutputStream convertedFile = new ByteArrayOutputStream();
                File expectedThymeleafFilename = new File(jspFile.getAbsolutePath().replaceAll(".jsp$", ".html"));
                String expectedContent = FileUtils.readFileToString(expectedThymeleafFilename, Charset.defaultCharset());
                
                System.out.println("Test "+jspFile.getName() + " -> " + jspFile.getName().replaceAll(".jsp$", ".html"));
                
                jSP2Thymeleaf.convert(new FileInputStream(jspFile), convertedFile);
                final String convertedContent = convertedFile.toString();
                System.out.println(convertedContent);
                assertThat(convertedContent.replaceAll("\\s+", " "), is(expectedContent.replaceAll("\\s+", " ")));
            } catch (IOException ex)
            {
                Logger.getLogger(JSP2ThymeleafTest.class.getName()).log(Level.SEVERE, null, ex);
                Assert.fail("exception thrown");
            }
        }
    }
     
    private List<File> getJspFiles(){
        final URL resource = getClass().getClassLoader().getResource("files");
        final File file = new File(resource.getFile());
        return Arrays.asList(file.listFiles())
                .stream()
                .filter(it -> it.getName().contains("jsp"))
                .sorted()
                .collect(Collectors.toList());
    }
}
