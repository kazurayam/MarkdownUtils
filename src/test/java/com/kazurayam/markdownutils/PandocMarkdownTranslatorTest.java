package com.kazurayam.markdownutils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PandocMarkdownTranslatorTest {

    private final Logger logger = LoggerFactory.getLogger(PandocMarkdownTranslatorTest.class);
    private final Path projectDir = Paths.get(System.getProperty("user.dir"));
    private final Path fixtureDir = projectDir.resolve("src/test/resources/fixtures");
    private final Path classOutputDir = projectDir.resolve("build/tmp/testOutput")
                    .resolve(PandocMarkdownTranslatorTest.class.getName());

    private PandocMarkdownTranslator translator;

    @BeforeEach
    public void beforeEach() {
        translator = new PandocMarkdownTranslator();
    }

    @Test
    public void test_translateContent() throws Exception {
        Path sample = fixtureDir.resolve("README.md");
        Reader reader = new InputStreamReader(
                        new FileInputStream(sample.toFile()), StandardCharsets.UTF_8);
        Writer writer = new StringWriter();
        translator.translateContent(reader, writer);
        writer.close();
        //System.out.println(writer.toString());
        String content = writer.toString();
        assertTrue(content.contains("#my-document"),
                "the content does not contain a string \"#my-document\"");
        assertTrue(content.contains("#_bar_baz"),
                "the content does not contain a string \"#_bar_baz\"");
    }

    @Test
    public void test_translateFile_1() throws Exception {
        Path dir = classOutputDir.resolve("test_translateFile_1");
        Files.createDirectories(dir);
        Path workFile = dir.resolve("README.md");
        Path fixtureFile = fixtureDir.resolve("README.md");
        Files.copy(fixtureFile, workFile, StandardCopyOption.REPLACE_EXISTING);
        String content = readAllLines(workFile);
        assertTrue(content.contains("(#_my_document)"));
        assertFalse(content.contains("#my-document)"), "found unexpected #my-document");
        assertTrue(content.contains("(#_bar_baz)"));
        String relativePath = projectDir.relativize(workFile).toString();
        //System.out.println("relativePath=" + relativePath);
        assertTrue(relativePath.startsWith("build/tmp"), "relativePath=" + relativePath);
        //
        translator.translateFile(relativePath);
        //
        content = readAllLines(workFile);
        assertFalse(content.contains("(#_my_document)"), "found unexpected #_my_document");
        assertTrue(content.contains("#my-document)"));
        assertTrue(content.contains("(#_bar_baz)"));
    }

    @Test
    public void test_translateFile_2() throws Exception {
        Path dir = classOutputDir.resolve("test_translateFile_2");
        Files.createDirectories(dir);
        Path fixtureFile = fixtureDir.resolve("README.md");
        Path inputFile = dir.resolve("README.md");
        Path outputFile = dir.resolve("temp.md");
        Files.copy(fixtureFile, inputFile, StandardCopyOption.REPLACE_EXISTING);
        //
        translator.translateFile(
                projectDir.relativize(inputFile).toString(),
                projectDir.relativize(outputFile).toString());
        //
        String content = readAllLines(outputFile);
        assertFalse(content.contains("(#_my_document)"), "found unexpected #_my_document");
        assertTrue(content.contains("#my-document)"));
        assertTrue(content.contains("(#_bar_baz)"));
    }

    @Test
    public void test_main() throws Exception {
        Path inputFile = fixtureDir.resolve("README.md");
        Path dir = classOutputDir.resolve("test_main");
        Files.createDirectories(dir);
        Path workFile = dir.resolve("README.md");
        Files.copy(inputFile, workFile, StandardCopyOption.REPLACE_EXISTING);
        String relativePath = projectDir.relativize(workFile).toString();
        //
        PandocMarkdownTranslator.main(new String[] { relativePath });
        //
        String content = readAllLines(workFile);
        assertTrue(content.contains("#my-document)"));
    }

    private String readAllLines(Path p) throws IOException {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        List<String> lines = Files.readAllLines(p);
        for (String line : lines) {
            pw.println(line);
        }
        pw.flush();
        pw.close();
        return sw.toString();
    }

    @Test
    public void test_translateTocLink() {
        String input    = "    -    [My previous solution](#_my_previous_solution)";
        String expected = "    -    [My previous solution](#my-previous-solution)";
        String actual = translator.translateTocLink(input);
        assertEquals(expected, actual);
    }

    @Test
    public void test_PTN_LINK() {
        Pattern ptn = Pattern.compile(PandocMarkdownTranslator.PTN_LINK);
        Matcher m = ptn.matcher("(#_foo_bar)");
        assertTrue(m.matches(), m.toString());
        showMatchResult(m);
    }

    @Test
    public void test_PTN_HEADER() {
        Pattern ptn = Pattern.compile(PandocMarkdownTranslator.PTN_HEADER);
        Matcher m = ptn.matcher("    -    [foo]");
        assertTrue(m.matches(), m.toString());
        showMatchResult(m);
    }

    @Test
    public void test_PTN_LEAD() {
        Pattern ptn = Pattern.compile(PandocMarkdownTranslator.PTN_LEAD);
        Matcher m = ptn.matcher("    -    ");
        assertTrue(m.matches());
        showMatchResult(m);
    }

    private void showMatchResult(Matcher m) {
        logger.info("m.groupCount()=" + m.groupCount());
        for (int i = 1; i <= m.groupCount() ; i++){
            logger.info("m.group(" + i + ")='" + m.group(i) + "'");
        }
    }
}
