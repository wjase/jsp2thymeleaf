/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.util;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import static java.util.Arrays.stream;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toList;

/**
 *
 * @author jason
 */
public class Globber
{

    public static Set<Path> match(String location, final String... patterns)
    {
        final List<String> globStrings = stream(patterns)
                .map(eachPath -> eachPath.startsWith("glob:") ? eachPath : prependGlob(eachPath))
                .collect(toList());
        return match(location, toMatchers(globStrings));
    }

    private final static String globFormat = "glob:%s";

    private static String prependGlob(String eachPath)
    {
        return String.format(globFormat, eachPath);
    }

    public static Set<Path> match(String location, final List<PathMatcher> pathMatchers)
    {

        try
        {
            Set<Path> matchingPaths = new TreeSet<>();
            Files.walkFileTree(Paths.get(location), new SimpleFileVisitor<Path>()
            {

                @Override
                public FileVisitResult visitFile(Path path,
                        BasicFileAttributes attrs) throws IOException
                {
                    Optional<PathMatcher> matchingPath
                            = pathMatchers.stream()
                                    .filter(matcher -> matcher.matches(path))
                                    .findFirst();
                    if (matchingPath.isPresent())
                    {
                        matchingPaths.add(path);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc)
                        throws IOException
                {
                    return FileVisitResult.CONTINUE;
                }
            });
            return matchingPaths;
        } catch (IOException ex)
        {
            Logger.getLogger(Globber.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    private static List<PathMatcher> toMatchers(List<String> globs)
    {
        return globs.stream()
                .map(glob -> FileSystems.getDefault().getPathMatcher(glob))
                .collect(toList());
    }
}
