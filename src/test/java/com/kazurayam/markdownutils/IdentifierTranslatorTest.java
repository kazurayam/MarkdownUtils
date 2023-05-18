package com.kazurayam.markdownutils;

import com.kazurayam.subprocessj.Subprocess;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IdentifierTranslatorTest {

    private static Path fixturesDir;
    private static Path docsDir;
    private static String JAR_VERSION = "0.2.0";

    @BeforeAll
    public static void beforeAll() throws IOException, InterruptedException {
        fixturesDir = Paths.get("src/test/fixtures");
        docsDir = Paths.get("docs");
        Path index = docsDir.resolve("IdentifierTranslatorTest_.adoc");
        /*
        FileUtils.copyFile(
                Paths.get(String.format("build/libs/MarkdownUtils-%s.jar", JAR_VERSION)).toFile(),
                docsDir.resolve(String.format("MarkdownUtils-%s.jar", JAR_VERSION)).toFile()
        );
         */
        Subprocess.CompletedProcess cp;
        cp = new Subprocess()
                .cwd(docsDir.toFile())
                .run(Arrays.asList("./indexconv.sh", "-t"));
    }

    @Test
    public void test_underline2hyphen() {
        String input = "_foo_bar";
        String expected = "foo-bar";
        assertEquals(expected, IdentifierTranslator.underline2hyphen(input));
    }

    @Test
    public void test_translate_with_underline2hyphen() {
        String input = "_foo_bar";
        String expected = "foo-bar";
        assertEquals(expected, IdentifierTranslator.translate(input));
    }
}
