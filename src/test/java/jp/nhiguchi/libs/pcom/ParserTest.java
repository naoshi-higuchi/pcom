/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.nhiguchi.libs.pcom;

import org.junit.jupiter.api.*;

import java.util.*;
import static jp.nhiguchi.libs.flist.FList.*;

import static jp.nhiguchi.libs.pcom.Primitives.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author Naoshi HIGUCHI
 */
public class ParserTest {
	public ParserTest() {
	}

	@BeforeAll
	public static void setUpClass() throws Exception {
	}

	@AfterAll
	public static void tearDownClass() throws Exception {
	}

	@BeforeEach
	public void setUp() {
	}

	@AfterEach
	public void tearDown() {
	}

	/**
	 * Test of parse method, of class AbstractParser.
	 */
	@Test
	public void testParse() {
		System.out.println("parse");
		String s;
		Parser instance;
		String expResult;
		List expResults;
		Result result;

		s = "123-456";
		instance = string("123");
		expResult = "123";
		result = instance.parse(s);
		assertEquals(expResult, result.value());

		s = "123-456";
		instance = seq(string("123"), string("-"), string("456"));
		expResults = flist("123", "-", "456");
		result = instance.parse(s);
		assertEquals(expResults, result.value());

		s = "-123";
		instance = or(string("+"), string("-"));
		expResult = "-";
		result = instance.parse(s);
		assertEquals(expResult, result.value());
		assertEquals(1, result.rest().asInt());

		s = "abc";
		instance = any();
		expResult = "a";
		result = instance.parse(s);
		assertEquals(expResult, result.value());
		assertEquals(1, result.rest().asInt());
	}
}
