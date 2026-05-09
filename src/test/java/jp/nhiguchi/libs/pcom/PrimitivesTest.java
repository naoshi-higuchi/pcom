package jp.nhiguchi.libs.pcom;

import org.junit.jupiter.api.Test;

import java.util.List;

import static jp.nhiguchi.libs.flist.FList.flist;
import static jp.nhiguchi.libs.pcom.Primitives.*;
import static org.junit.jupiter.api.Assertions.*;

public class PrimitivesTest {
    @Test
    public void testOr() {
        Parser<String> p = or(string("a"), string("b"));

        Result<String> r1 = p.parse("a");
        assertTrue(r1.isSuccess());
        assertEquals("a", r1.value());

        Result<String> r2 = p.parse("b");
        assertTrue(r2.isSuccess());
        assertEquals("b", r2.value());

        Result<String> r3 = p.parse("c");
        assertTrue(r3.isFail());
    }

    @Test
    public void testSeq() {
        Parser<List<String>> p = seq(string("a"), string("b"));

        Result<List<String>> r1 = p.parse("ab");
        assertTrue(r1.isSuccess());
        assertEquals(flist("a", "b"), r1.value());

        Result<List<String>> r2 = p.parse("ac");
        assertTrue(r2.isFail());

        Result<List<String>> r3 = p.parse("a");
        assertTrue(r3.isFail());
    }

    @Test
    public void testAny() {
        Parser<String> p = any();

        Result<String> r1 = p.parse("a");
        assertTrue(r1.isSuccess());
        assertEquals("a", r1.value());
        assertEquals(1, r1.rest().asInt());

        Result<String> r2 = p.parse("");
        assertTrue(r2.isFail());
    }

    @Test
    public void testAnd() {
        Parser<Void> p = and(string("a"));

        Result<Void> r1 = p.parse("a");
        assertTrue(r1.isSuccess());
        assertEquals(0, r1.rest().asInt());

        Result<Void> r2 = p.parse("b");
        assertTrue(r2.isFail());
    }

    @Test
    public void testNot() {
        Parser<Void> p = not(string("a"));

        Result<Void> r1 = p.parse("b");
        assertTrue(r1.isSuccess());
        assertEquals(0, r1.rest().asInt());

        Result<Void> r2 = p.parse("a");
        assertTrue(r2.isFail());
    }
}
