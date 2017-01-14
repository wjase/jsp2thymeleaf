/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toSet;

/**
 *
 * @author jason
 */
public class JSPIncludeSorter
{

    private static final Pattern includePattern = Pattern.compile("<%@include\\s+file\\s*=\\s*\"([^\"]+)\"\\s+%>");
    public static JSPFileComparator jSPFileComparator = new JSPFileComparator();

    public static List<Path> directedAcyclicSort(Set<Path> jspFiles)
    {
        return jspFiles
                .stream()
                .map(jsp -> new DefaultJSPFile(jsp))
                .sorted(jSPFileComparator)
                .map(it -> it.getPath())
                .collect(Collectors.toList());
    }

    public static class JSPFileComparator implements Comparator<JSPFile>
    {

        @Override
        public int compare(JSPFile o1, JSPFile o2)
        {
            Path o1Path = o1.getPath();
            Path o2Path = o2.getPath();
            Optional<Integer> result = Optional.empty();
            if (o1Path.equals(o2Path))
            {
                result = Optional.of(0);
            } else
            {
                boolean o1IncludesO2 = o1.includesFile(o2);
                boolean o2Includeso1 = o2.includesFile(o1);
                if (o1IncludesO2 || o2Includeso1)
                {
                    if (o1IncludesO2 && o2Includeso1)
                    {
                        throw new RuntimeException("Circular includes:" + o1.getPath().toString() + " and " + o2.getPath().toString());
                    }
                    if (o1IncludesO2)
                    {
                        result = Optional.of(1);
                    } else if (o2Includeso1)
                    {
                        result = Optional.of(-1);
                    }
                }
                if (!result.isPresent())
                {
                    // more inlcudes should sort lower in tree
                    int compare = Integer.valueOf(o2.getIncludedCount()).compareTo(Integer.valueOf(o1.getIncludedCount()));
                    if (compare != 0)
                    {
                        result = Optional.of(compare);
                    }
                }
                if (!result.isPresent())
                {
                    return o1.getPath().toString().compareTo(o2.getPath().toString());
                }
            }
            return result.orElseThrow(() -> new RuntimeException("Couldn't compare: " + o1.getPath().toString() + " and " + o2.getPath().toString()));
        }

    }

    public static class DefaultJSPFile implements JSPFile
    {

        private Path path;

        public DefaultJSPFile(Path path)
        {
            this.path = path;
            this.includedFiles = parse(path);
        }

        private Set<Path> includedFiles = Collections.EMPTY_SET;

        @Override
        public boolean includesFile(JSPFile file)
        {
            return includedFiles.contains(file.getPath());
        }

        private static Set<Path> parse(Path jspFile)
        {
            try
            {
                return Files.readAllLines(jspFile)
                        .stream()
                        .filter(line -> line.contains("<%@include"))
                        .map(line -> pathFromInclude(line, jspFile))
                        .collect(toSet());
            } catch (IOException ex)
            {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public Path getPath()
        {
            return path;
        }

        @Override
        public int getIncludedCount()
        {
            return includedFiles.size();
        }
    }

    private static Path pathFromInclude(String line, Path jspFile)
    {
        final Matcher matcher = includePattern.matcher(line);
        if (matcher.find())
        {
            final Path includedPath = Paths.get(matcher.group(1));
            if (includedPath.isAbsolute())
            {
                return includedPath;
            }
            return jspFile.resolve(includedPath);

        } else
        {
            throw new RuntimeException("Could not parse include in " + line + " in file:" + jspFile.toString());
        }
    }

}
