/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf;

import com.cybernostics.jsp2thymeleaf.api.common.TokenisedFile;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.Test;

/**
 *
 * @author jason
 */
public class TokenisedFileTest
{

    final URL rootResource;
    final Path rootPath;

    public TokenisedFileTest() throws URISyntaxException
    {
        rootResource = JSP2ThymeleafHappyCaseTest.class.getClassLoader().getResource("includesort/");
        rootPath = Paths.get(rootResource.toURI());
    }

    @Test
    public void shouldParseFileWithNoIncludes() throws URISyntaxException, MalformedURLException
    {
        TokenisedFile file = getFile("file_a.jsp");
        assertThat(file.getIncludedPaths(), not(empty()));
        final List<Path> includedPaths = file.getIncludedPaths();
        final List<String> relativeIncludePaths = includedPaths.stream().map(path -> rootPath.relativize(path).toString()).collect(toList());
        assertThat(relativeIncludePaths, contains("subdir/file_b.jsp", "file_d.jsp"));
//        includedPaths.stream().forEach(path -> System.out.println(path));

    }

    @Test
    public void shouldOrderByInverseOfIncludeSizeThenAlphaPath() throws MalformedURLException, URISyntaxException
    {
        TokenisedFile filea = getFile("file_a.jsp");
        TokenisedFile fileb = getFile("subdir/file_b.jsp");
        TokenisedFile filec = getFile("subdir/file_c.jsp");
        TokenisedFile filed = getFile("file_d.jsp");
        TokenisedFile filee = getFile("file_e.jsp");
        final List<TokenisedFile> unsorted = Arrays.asList(filea, fileb, filec, filed, filee);
        final List<TokenisedFile> sorted = unsorted.stream().sorted().collect(toList());

//        sorted.stream().forEach(it -> System.out.println(it));
        assertThat(sorted, contains(filea, filed, filee, fileb, filec));
    }

    private TokenisedFile getFile(String name) throws MalformedURLException, URISyntaxException
    {
        final URL resource = new URL(rootResource, name);
        final TokenisedFile tokenisedFile = new TokenisedFile(Paths.get(resource.toURI()), rootPath);
        assertThat(tokenisedFile, notNullValue());
        return tokenisedFile;
    }

}
