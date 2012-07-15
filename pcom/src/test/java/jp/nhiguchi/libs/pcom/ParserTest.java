/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.nhiguchi.libs.pcom;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;
import static jp.nhiguchi.libs.flist.FList.*;

import static jp.nhiguchi.libs.pcom.Primitives.*;

/**
 *
 * @author naoshi
 */
public class ParserTest {
	public ParserTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
	}

	@After
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
