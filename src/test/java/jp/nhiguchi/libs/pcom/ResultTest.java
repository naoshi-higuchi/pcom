package jp.nhiguchi.libs.pcom;

import org.junit.jupiter.api.Test;

import static jp.nhiguchi.libs.pcom.Parsers.string;
import static org.junit.jupiter.api.Assertions.*;

public class ResultTest {
    @Test
    public void testSuccess() {
        Source src = Source.source("abc");
        Position pos = Position.startOf(src).next(1);
        Result<String> r = Result.success("a", pos);

        assertTrue(r.isSuccess());
        assertFalse(r.isFail());
        assertEquals("a", r.value());
        assertEquals(pos, r.rest());
        assertThrows(UnsupportedOperationException.class, r::error);
    }

    @Test
    public void testFail() {
        Source src = Source.source("abc");
        Position pos = Position.startOf(src);
        Parser<String> p = string("a");
        Result<String> r = Result.fail(p, pos);

        assertFalse(r.isSuccess());
        assertTrue(r.isFail());
        assertNotNull(r.error());
        assertEquals(p, r.error().parser());
        assertEquals(pos, r.error().position());
        assertThrows(UnsupportedOperationException.class, r::value);
        assertThrows(UnsupportedOperationException.class, r::rest);
    }
}
