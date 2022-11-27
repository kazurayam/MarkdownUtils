package com.kazurayam.markdownutils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PandocMarkdownTranslatorTest {
    private final Logger logger = LoggerFactory.getLogger(PandocMarkdownTranslatorTest.class);
    private Path fixturesDir;
    private Path testClassOutputDir;
    private PandocMarkdownTranslator translator;

    @BeforeEach
    public void beforeEach() {
        testClassOutputDir = TestHelper.createTestClassOutputDir(this);
        fixturesDir = TestHelper.getFixturesDirectory();
        translator = new PandocMarkdownTranslator();
    }

    @Test
    public void test_translateREADME() throws Exception {
        Path sample = fixturesDir.resolve("README.md");
        Reader reader = new InputStreamReader(
                Files.newInputStream(sample.toFile().toPath()), StandardCharsets.UTF_8);
        Writer writer = new StringWriter();
        translator.translateContent(reader, writer);
        writer.close();
        //System.out.println(writer.toString());
        String content = writer.toString();
        assertTrue(content.contains("#my-document"),
                "the content should contain a string \"#my-document\" but not");
        assertTrue(content.contains("#_bar_baz"),
                "the content should contain a string \"#_bar_baz\" in the section quoted by triple back ticks but not");
    }



    @Test
    public void test_translateFile_1() throws Exception {
        Path dir = testClassOutputDir.resolve("test_translateFile_1");
        Files.createDirectories(dir);
        Path workFile = dir.resolve("README.md");
        //
        Path fixtureFile = fixturesDir.resolve("README.md");
        Files.copy(fixtureFile, workFile, StandardCopyOption.REPLACE_EXISTING);
        //
        String content = readAllLines(workFile);
        assertTrue(content.contains("(#_my_document)"));
        assertFalse(content.contains("#my-document)"), "found unexpected #my-document");
        assertTrue(content.contains("(#_bar_baz)"));

        String relativePath = TestHelper.getCWD().relativize(workFile).toString();
        //System.out.println("relativePath=" + relativePath);
        assertTrue(relativePath.startsWith("build/tmp"), "relativePath=" + relativePath);
        //
        translator.translateFile(workFile);
        //
        content = readAllLines(workFile);
        assertFalse(content.contains("(#_my_document)"), "found unexpected #_my_document");
        assertTrue(content.contains("#my-document)"));
        assertTrue(content.contains("(#_bar_baz)"));
    }

    @Test
    public void test_translateFile_2() throws Exception {
        Path dir = testClassOutputDir.resolve("test_translateFile_2");
        Files.createDirectories(dir);
        Path fixtureFile = fixturesDir.resolve("README.md");
        Path inputFile = dir.resolve("README.md");
        Path outputFile = dir.resolve("temp.md");
        Files.copy(fixtureFile, inputFile, StandardCopyOption.REPLACE_EXISTING);
        //
        translator.translateFile(inputFile, outputFile);
        //
        String content = readAllLines(outputFile);
        assertFalse(content.contains("(#_my_document)"), "found unexpected #_my_document");
        assertTrue(content.contains("#my-document)"));
        assertTrue(content.contains("(#_bar_baz)"));
    }

    @Test
    public void test_main() throws Exception {
        Path inputFile = fixturesDir.resolve("README.md");
        Path dir = testClassOutputDir.resolve("test_main");
        Files.createDirectories(dir);
        Path workFile = dir.resolve("README.md");
        Files.copy(inputFile, workFile, StandardCopyOption.REPLACE_EXISTING);
        String relativePath = TestHelper.getCWD().relativize(workFile).toString();
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
    public void test_PTN_MD_LINK() {
        Pattern ptn = Pattern.compile(PandocMarkdownTranslator.PTN_MD_LINK);
        Matcher m = ptn.matcher("(#_foo_bar)");
        assertTrue(m.matches(), m.toString());
        showMatchResult(m);
    }

    @Test
    public void test_PTN_MD_HEADER() {
        Pattern ptn = Pattern.compile(PandocMarkdownTranslator.PTN_MD_HEADER);
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

    @Test
    public void test_PATTERN_AS_HTML_ANCHOR() {
        String s = " - <a href=\"#_foo_bar\" id=\"toc-_foo_bar\">baz</a>";
        Matcher m = PandocMarkdownTranslator.PATTERN_AS_HTML_ANCHOR.matcher(s);
        //System.out.println(m);
        assertTrue(m.matches());
        assertEquals(5, m.groupCount());
        assertEquals(" - <a href=\"#", m.group(1));
        assertEquals("_foo_bar", m.group(2));
        assertEquals("\" id=\"toc-", m.group(3));
        assertEquals("_foo_bar", m.group(4));
        assertEquals("\">baz</a>", m.group(5));
    }

    @Test
    public void test_underline2hyphen() {
        assertEquals("foo-bar", PandocMarkdownTranslator.underline2hyphen("_foo_bar"));
        assertEquals("foo-bar", PandocMarkdownTranslator.underline2hyphen("foo_bar"));
    }

    @Test
    public void test_translateREADME_TOC_as_anchor() throws Exception {
        Path input = fixturesDir.resolve("README_TOC_as_anchor.md");
        Path dir = testClassOutputDir.resolve("test_translateREADME_TOC_as_anchor");
        Files.createDirectories(dir);
        Path output = dir.resolve("README_TOC_as_anchor.md");
        translator.translateFile(input, output);
        String content = readAllLines(output);
        assertTrue(content.contains("href=\"#problem-to-solve\""),
                "the content should contain a string 'href=\"#problem-to-solve\"'");
        assertTrue(content.contains("id=\"toc-problem-to-solve\""),
                "the content should contain a string 'id=\"toc-problem-to-solve\"'");
    }

    private void showMatchResult(Matcher m) {
        logger.info("m.groupCount()=" + m.groupCount());
        for (int i = 1; i <= m.groupCount() ; i++){
            logger.info("m.group(" + i + ")='" + m.group(i) + "'");
        }
    }
}
