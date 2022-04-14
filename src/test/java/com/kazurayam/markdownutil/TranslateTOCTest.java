package com.kazurayam.markdownutil;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TranslateTOCTest {

    private Logger logger = LoggerFactory.getLogger(TranslateTOCTest.class);

    private static Path fixtureDir =
            Paths.get(System.getProperty("user.dir"))
                    .resolve("src/test/resources/fixtures");

    @Test
    public void test_translateDocument() throws Exception {
        Path sample = fixtureDir.resolve("README.md");
        Reader reader =
                new InputStreamReader(
                        new FileInputStream(sample.toFile()), StandardCharsets.UTF_8);
        Writer writer = new StringWriter();
        TranslateTOC.translateDocument(reader, writer);
        writer.close();
        //System.out.println(writer.toString());
        String content = writer.toString();
        assertTrue(content.contains("#my-document"),
                "the content does not contain a string \"#my-document\"");
        assertTrue(content.contains("#_bar_baz"),
                "the content does not contain a string \"#_bar_baz\"");
    }

    @Test
    public void test_translateLine() {
        String input    = "    -    [My previous solution](#_my_previous_solution)";
        String expected = "    -    [My previous solution](#my-previous-solution)";
        String actual = TranslateTOC.translateLine(input);
        assertEquals(expected, actual);
    }

    @Test
    public void test_PTN_LINK() {
        Pattern ptn = Pattern.compile(TranslateTOC.PTN_LINK);
        Matcher m = ptn.matcher("(#_foo_bar)");
        assertTrue(m.matches(), m.toString());
        showMatchResult(m);
    }

    @Test
    public void test_PTN_HEADER() {
        Pattern ptn = Pattern.compile(TranslateTOC.PTN_HEADER);
        Matcher m = ptn.matcher("    -    [foo]");
        assertTrue(m.matches(), m.toString());
        showMatchResult(m);
    }

    @Test
    public void test_PTN_LEAD() {
        Pattern ptn = Pattern.compile(TranslateTOC.PTN_LEAD);
        Matcher m = ptn.matcher("    -    ");
        assertTrue(m.matches());
        showMatchResult(m);
    }

    public void showMatchResult(Matcher m) {
        logger.info("m.groupCount()=" + m.groupCount());
        for (int i = 1; i <= m.groupCount() ; i++){
            logger.info("m.group(" + i + ")='" + m.group(i) + "'");
        }

    }
}
