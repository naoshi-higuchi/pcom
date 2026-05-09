package jp.nhiguchi.libs.pcom;

import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

public class SourceTest {
    @Test
    public void testStringSource() {
        Source s = Source.source("hello");
        assertEquals("he", s.string(0, 2));
        assertTrue(s.startsWith("he", 0));
        assertFalse(s.isEnd(0));
        assertFalse(s.isEnd(4));
        assertTrue(s.isEnd(5));
    }

    @Test
    public void testReadableSource() {
        Source s = Source.source(new StringReader("world"));
        assertEquals("wo", s.string(0, 2));
        assertTrue(s.startsWith("wo", 0));
        assertFalse(s.isEnd(0));
    }
}
