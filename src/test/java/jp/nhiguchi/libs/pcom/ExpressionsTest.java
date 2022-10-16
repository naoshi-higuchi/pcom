/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.nhiguchi.libs.pcom;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author Naoshi HIGUCHI
 */
public class ExpressionsTest {
	public ExpressionsTest() {
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
	 * Test of range method, of class Expressions.
	 */
	@Test
	public void testRange() {
		System.out.println("range");
		Parser<Parser<String>> result = Expressions.range();
		Parser<String> p;

		p = result.parse("a-c").value();
		assertTrue(p.parse("a").isSuccess());
		assertTrue(p.parse("b").isSuccess());
		assertTrue(p.parse("c").isSuccess());
		assertTrue(p.parse("d").isFail());
	}

	/**
	 * Test of charClass method, of class Expressions.
	 */
	@Test
	public void testCharClass() {
		System.out.println("charClass");
		Parser<Parser<String>> result = Expressions.charClass();
		Parser<String> p;

		p = result.parse("[a-zA-Z]").value();
		assertTrue(p.parse("a").isSuccess());
		assertTrue(p.parse("m").isSuccess());
		assertTrue(p.parse("z").isSuccess());
		assertTrue(p.parse("A").isSuccess());
		assertTrue(p.parse("M").isSuccess());
		assertTrue(p.parse("Z").isSuccess());
		assertTrue(p.parse("0").isFail());

		p = result.parse("[-]").value();
		assertTrue(p.parse("-").isSuccess());
	}

	/**
	 * Test of literal method, of class Expressions.
	 */
	@Test
	public void testLiteral() {
		System.out.println("literal");
		Parser<Parser<String>> result = Expressions.literal();
		Parser<String> p;

		p = result.parse("'foo'").value();
		assertTrue(p.parse("foo").isSuccess());
		assertTrue(p.parse("foobar").isSuccess());
		assertTrue(p.parse("bar").isFail());

		p = result.parse("'\\\\'").value();
		assertTrue(p.parse("\\").isSuccess());
	}

	/**
	 * Test of anyChar method, of class Expressions.
	 */
	@Test
	public void testAnyChar() {
		System.out.println("anyChar");
		Parser<Parser<String>> result = Expressions.anyChar();
		Parser<String> p;

		p = result.parse(".").value();
		assertTrue(p.parse("a").isSuccess());
		assertTrue(p.parse("0").isSuccess());
		assertTrue(p.parse("@").isSuccess());
		assertTrue(p.parse("'").isSuccess());
		assertTrue(p.parse(".").isSuccess());
	}

	/**
	 * Test of primary method, of class Expressions.
	 */
	@Test
	public void testPrimary() {
		System.out.println("primary()");
		Parser<Parser<String>> result = Expressions.primary();
		Parser<String> p;

		p = result.parse("'foo'").value();
		assertTrue(p.parse("foo").isSuccess());
		assertTrue(p.parse("bar").isFail());

		p = result.parse("[a-z]").value();
		assertTrue(p.parse("a").isSuccess());
		assertTrue(p.parse("A").isFail());

		p = result.parse(".").value();
		assertTrue(p.parse("@").isSuccess());
	}

	/**
	 * Test of suffix method, of class Expressions.
	 */
	@Test
	public void testSuffix() {
		System.out.println("suffix()");
		Parser<Parser<String>> result = Expressions.suffix();
		Parser<String> p;

		p = result.parse("'foo'").value();
		assertTrue(p.parse("foo").isSuccess());
		assertTrue(p.parse("bar").isFail());

		p = result.parse("'foo'?").value();
		assertTrue(p.parse("foo").isSuccess());
		assertTrue(p.parse("").isSuccess());

		p = result.parse("'foo'*").value();
		assertTrue(p.parse("foo").isSuccess());
		assertTrue(p.parse("foofoo").isSuccess());
		assertTrue(p.parse("").isSuccess());

		p = result.parse("'foo'+").value();
		assertTrue(p.parse("foo").isSuccess());
		assertTrue(p.parse("foofoo").isSuccess());
		assertTrue(p.parse("").isFail());
	}

	/**
	 * Test of prefix method, of class Expressions.
	 */
	@Test
	public void testPrefix() {
		System.out.println("prefix()");
		Parser<Parser<String>> result = Expressions.prefix();
		Parser<String> p;
		Result<String> r;

		p = result.parse("'foo'").value();
		assertTrue(p.parse("foo").isSuccess());
		assertTrue(p.parse("bar").isFail());

		p = result.parse("&'foo'").value();
		r = p.parse("foo");
		assertTrue(r.isSuccess());
		assertEquals(0, r.rest().asInt());

		p = result.parse("!'foo'").value();
		r = p.parse("foo");
		assertTrue(r.isFail());
		r = p.parse("bar");
		assertTrue(r.isSuccess());
		assertEquals(0, r.rest().asInt());
	}

	/**
	 * Test of sequence method, of class Expressions.
	 */
	@Test
	public void testSequence() {
		System.out.println("sequence()");
		Parser<Parser<String>> result = Expressions.sequence();
		Parser<String> p;

		p = result.parse("'foo' 'bar'").value();
		assertTrue(p.parse("foo").isFail());
		assertTrue(p.parse("bar").isFail());
		assertTrue(p.parse("foobar").isSuccess());

		p = result.parse("!'foo' 'fo' .+").value();
		assertTrue(p.parse("foo").isFail());
		assertTrue(p.parse("fog").isSuccess());
		assertTrue(p.parse("foggy").isSuccess());
	}

	/**
	 * Test of expression method, of class Expressions.
	 */
	@Test
	public void testExpression() {
		System.out.println("expression()");
		Parser<Parser<String>> result = Expressions.expression();
		Parser<String> p;

		p = result.parse("'foo' / 'bar'").value();
		assertTrue(result.parse("'foo' / 'bar'").rest().isEnd());
		assertTrue(p.parse("foo").isSuccess());
		assertTrue(p.parse("bar").isSuccess());

		p = result.parse("'foo'? / 'bar'+").value();
		assertTrue(p.parse("foo").isSuccess());
		assertTrue(p.parse("foo").isSuccess());
		assertTrue(p.parse("").isSuccess());
		assertTrue(p.parse("bar").isSuccess());
		assertTrue(p.parse("barbar").isSuccess());
	}
}
