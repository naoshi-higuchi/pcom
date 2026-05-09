package jp.nhiguchi.libs.pcom.opp;

import jp.nhiguchi.libs.flist.FList;
import org.junit.jupiter.api.Test;

import java.util.NavigableMap;
import java.util.TreeMap;

import static jp.nhiguchi.libs.flist.FList.flist;
import static jp.nhiguchi.libs.pcom.Parsers.string;
import static org.junit.jupiter.api.Assertions.*;

public class OpTableTest {

    @Test
    public void testCreate() {
        NavigableMap<Integer, FList<Operator<String>>> map = new TreeMap<>();
        Operator<String> op1 = Operator.infixL(100, (a, b) -> a + b, string("+"));
        map.put(100, flist(op1));

        OpTable<String> table = OpTable.create(map);
        assertNotNull(table);

        // Check for defensive copy
        map.clear();
        assertFalse(table.isEmpty());
    }

    @Test
    public void testHead() {
        NavigableMap<Integer, FList<Operator<String>>> map = new TreeMap<>();
        Operator<String> op1 = Operator.infixL(100, (a, b) -> a + b, string("+"));
        Operator<String> op2 = Operator.infixL(200, (a, b) -> a + b, string("-"));
        map.put(100, flist(op1));
        map.put(200, flist(op2));

        OpTable<String> table = OpTable.create(map);
        assertEquals(flist(op1), table.head());
    }

    @Test
    public void testTail() {
        NavigableMap<Integer, FList<Operator<String>>> map = new TreeMap<>();
        Operator<String> op1 = Operator.infixL(100, (a, b) -> a + b, string("+"));
        Operator<String> op2 = Operator.infixL(200, (a, b) -> a + b, string("-"));
        map.put(100, flist(op1));
        map.put(200, flist(op2));

        OpTable<String> table = OpTable.create(map);
        OpTable<String> tail = table.tail();

        assertFalse(tail.isEmpty());
        assertEquals(flist(op2), tail.head());
        assertTrue(tail.tail().isEmpty());
    }

    @Test
    public void testIsEmpty() {
        NavigableMap<Integer, FList<Operator<String>>> map = new TreeMap<>();
        OpTable<String> table = OpTable.create(map);
        assertTrue(table.isEmpty());

        Operator<String> op1 = Operator.infixL(100, (a, b) -> a + b, string("+"));
        map.put(100, flist(op1));
        table = OpTable.create(map);
        assertFalse(table.isEmpty());
    }
}
