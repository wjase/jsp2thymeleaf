/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf;

import com.cybernostics.jsp.parser.JSPLexer;
import com.cybernostics.jsp.parser.JSPParser;
import com.cybernostics.jsp.parser.JSPParserBaseListener;
import static com.cybernostics.jsp2thymeleaf.api.util.StringFunctions.trimQuotes;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 *
 * @author jason
 */
public class TokenisedFile implements Comparable<TokenisedFile>
{

    private final Path filePath;
    private final Path jspRoot;
    private List<Path> includedPaths = new ArrayList<>();

    public TokenisedFile(Path forPath, Path jspRoot)
    {
        java.io.InputStream is = null;
        this.jspRoot = jspRoot;
        this.filePath = forPath;
        try
        {
            is = new FileInputStream(this.filePath.toFile());
            lexer = new JSPLexer(new org.antlr.v4.runtime.ANTLRInputStream(is));
            gatherIncludes(lexer);

        } catch (Throwable ex)
        {
            throw new RuntimeException(ex);
        } finally
        {
            try
            {
                is.close();
            } catch (IOException ex)
            {
                Logger.getLogger(JSP2Thymeleaf.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    private JSPLexer lexer;

    public Path getFilePath()
    {
        return filePath;
    }

    public JSPLexer getLexer()
    {
        lexer.reset();
        return lexer;
    }

    public List<Path> getIncludedPaths()
    {
        return includedPaths;
    }

    private void gatherIncludes(JSPLexer lexer)
    {
        lexer.reset();
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // Pass the tokens to the parser
        JSPParser parser = new JSPParser(tokens);
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new IncludeGatherer(includedPaths, filePath), parser.jspDocument());

    }

    @Override
    public int compareTo(TokenisedFile otherFile)
    {
        final int fileCompare = this.filePath.compareTo(otherFile.filePath);
        if (fileCompare == 0)
        {
            return 0;
        }
        if (includedPaths.contains(otherFile.filePath))
        {
            return -1;
        }
        if (otherFile.includedPaths.contains(this.filePath))
        {
            return 1;
        }
        final int includeSizeCompare = Integer.valueOf(includedPaths.size()).compareTo(Integer.valueOf(otherFile.includedPaths.size()));
        if (includeSizeCompare != 0)
        {
            return includeSizeCompare;
        }
        return fileCompare;
    }

    private class IncludeGatherer extends JSPParserBaseListener
    {

        private final List<Path> includedPaths;
        private final Path filePath;

        private IncludeGatherer(List<Path> includedPaths, Path filePath)
        {
            this.includedPaths = includedPaths;
            this.filePath = filePath;
        }

        @Override
        public void enterJspDirective(JSPParser.JspDirectiveContext ctx)
        {
            if (ctx.name.getText().equals("include"))
            {
                final Optional<JSPParser.HtmlAttributeContext> fileAttribute = ctx.atts.stream()
                        .filter(att -> att.name.getText().equals("file"))
                        .findAny();
                if (fileAttribute.isPresent())
                {
                    Path includePath = absolutePathFor(Paths.get(trimQuotes(fileAttribute.get().value.getText())));
                    includedPaths.add(includePath);
                }
            }
        }

        private Path absolutePathFor(Path includedPathSpec)
        {
            if (!includedPathSpec.isAbsolute())
            {
                return filePath.getParent().resolve(includedPathSpec);
            }

            Path asRelative = includedPathSpec.getRoot().relativize(includedPathSpec);
            // check for /blah/WEB-INF/file_a including WEB-INF/file_b
            return jspRoot.resolve(asRelative);

        }

    }

    @Override
    public String toString()
    {
        return getRelativePathString();
    }

    public String getRelativePathString()
    {
        return relativise(filePath).toString();
    }

    public Path relativise(Path value)
    {
        return jspRoot.relativize(value);
    }

    public Path getPath()
    {
        return filePath;
    }
}
