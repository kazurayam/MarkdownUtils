package com.kazurayam.markdownutil;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslatePandocTocToGFMTest {

    private Logger logger = LoggerFactory.getLogger(TranslatePandocTocToGFMTest.class);

    @Test
    public void test_PTN_LINK() {
        Pattern ptn = Pattern.compile(TranslatePandocTocToGFM.PTN_LINK);
        Matcher m = ptn.matcher("(#_foo_bar)");
        Assertions.assertTrue(m.matches(), m.toString());
        showMatchResult(m);
    }

    @Test
    public void test_PTN_HEADER() {
        Pattern ptn = Pattern.compile(TranslatePandocTocToGFM.PTN_HEADER);
        Matcher m = ptn.matcher("    -    [foo]");
        Assertions.assertTrue(m.matches(), m.toString());
        showMatchResult(m);
    }

    @Test
    public void test_PTN_LEAD() {
        Pattern ptn = Pattern.compile(TranslatePandocTocToGFM.PTN_LEAD);
        Matcher m = ptn.matcher("    -    ");
        Assertions.assertTrue(m.matches());
        showMatchResult(m);
    }

    public void showMatchResult(Matcher m) {
        logger.info("m.groupCount()=" + m.groupCount());
        for (int i = 1; i <= m.groupCount() ; i++){
            logger.info("m.group(" + i + ")='" + m.group(i) + "'");
        }

    }
}
