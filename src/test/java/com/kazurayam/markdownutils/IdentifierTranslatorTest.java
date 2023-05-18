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
    private static Path dataDir;
    private static String JAR_VERSION = "0.2.0";

    @BeforeAll
    public static void beforeAll() throws IOException, InterruptedException {
        fixturesDir = Paths.get("src/test/fixtures");
        dataDir = fixturesDir.resolve(IdentifierTranslatorTest.class.getSimpleName());
        Path index = dataDir.resolve("index_.adoc");
        FileUtils.copyFile(
                Paths.get(String.format("build/libs/MarkdownUtils-%s.jar", JAR_VERSION)).toFile(),
                dataDir.resolve(String.format("MarkdownUtils-%s.jar", JAR_VERSION)).toFile()
        );
        FileUtils.copyFile(
                Paths.get("docs/indexconv.sh").toFile(),
                dataDir.resolve("indexconv.sh").toFile()
        );
        Subprocess.CompletedProcess cp;
        cp = new Subprocess()
                .cwd(dataDir.toFile())
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
