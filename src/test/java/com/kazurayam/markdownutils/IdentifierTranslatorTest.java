package com.kazurayam.markdownutils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IdentifierTranslatorTest {

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
