/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.nhiguchi.libs.pcom;

import org.junit.jupiter.api.*;

import static jp.nhiguchi.libs.pcom.Primitives.*;
import static jp.nhiguchi.libs.pcom.Recursions.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Naoshi HIGUCHI
 */
public class RecursionsTest {
	public RecursionsTest() {
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
		RecursionMark<String> m;
		Parser<String> instance;
		String expResult;
		Result<String> result;

		/*
		 * X <- 'x' X / 'x'
		 */
		s = "xxxxx";
		m = new RecursionMark<String>();
		instance = mark(m, or(Parsers.concat(seq(string("x"), recur(m))), string("x")));
		expResult = "xxxxx";
		result = instance.parse(s);
		assertEquals(expResult, result.value());

		/*
		 * Left Recursion.
		 * An example which this parser can NOT handle.
		 * 
		 * X <- X / 'x'
		 */
		s = "xxxxx";
		m = new RecursionMark<String>();
		instance = mark(m, or(recur(m), string("x")));
		try {
			result = instance.parse(s);
			fail();
		} catch (java.lang.StackOverflowError expected) {
		}

		/*
		 * An example which this parser can NOT handle.
		 *
		 * X <- 'x' X 'x' / 'x'
		 */
		s = "xxxxx";
		m = new RecursionMark<String>();
		instance = mark(m, or(
				Parsers.concat(seq(string("x"), recur(m), string("x"))),
				string("x")));
		expResult = "xxxxx";
		result = instance.parse(s);
		assertFalse(expResult.equals(result.value()));
	}
}
