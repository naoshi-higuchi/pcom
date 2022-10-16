/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.nhiguchi.libs.pcom.opp;

import jp.nhiguchi.libs.pcom.*;
import org.junit.jupiter.api.*;

import static jp.nhiguchi.libs.pcom.Parsers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author naoshi
 */
public class OppBuilderTest {
	public OppBuilderTest() {
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
	 * Test of toParser method, of class OppBuilder.
	 */
	@Test
	public void testToParser() {
		System.out.println("toParser");
		Parser<String> ws = expr("[ \t]*");
		Map1<String, Integer> toInt = new Map1<String, Integer>() {
			public Integer map(String s) {
				return Integer.parseInt(s);
			}
		};
		Parser<Integer> num = map(toInt, trim(ws, expr("[0-9]+")));

		Binary<Integer> add = new Binary<Integer>() {
			public Integer map(Integer x1, Integer x2) {
				return x1 + x2;
			}
		};
		Operator<Integer> addOp = Operator.infixL(700, add, trim(ws, string("+")));

		Binary<Integer> sub = new Binary<Integer>() {
			public Integer map(Integer x1, Integer x2) {
				return x1 - x2;
			}
		};
		Operator<Integer> subOp = Operator.infixL(700, sub, trim(ws, string("-")));

		Binary<Integer> mult = new Binary<Integer>() {
			public Integer map(Integer x1, Integer x2) {
				return x1 * x2;
			}
		};
		Operator<Integer> multOp = Operator.infixL(800, mult, trim(ws, string("*")));

		Binary<Integer> div = new Binary<Integer>() {
			public Integer map(Integer x1, Integer x2) {
				return x1 / x2;
			}
		};
		Operator<Integer> divOp = Operator.infixL(800, div, trim(ws, string("/")));

		Unary<Integer> incr = new Unary<Integer>() {
			public Integer map(Integer x) {
				return ++x;
			}
		};
		Operator<Integer> incrOp = Operator.postfixL(900, incr, trim(ws, string(".incr")));

		Unary<Integer> plus = new Unary<Integer>() {
			public Integer map(Integer x) {
				return x;
			}
		};
		Operator<Integer> plusOp = Operator.prefix(1000, plus, trim(ws, string("+")));

		Unary<Integer> minus = new Unary<Integer>() {
			public Integer map(Integer x) {
				return -x;
			}
		};
		Operator<Integer> minusOp = Operator.prefix(1000, minus, trim(ws, string("-")));

		Unary<Integer> fact = new Unary<Integer>() {
			public Integer map(Integer x) {
				int res = 1;
				for (int i = 1; i <= x; ++i)
					res *= i;
				return res;
			}
		};
		Operator<Integer> factOp = Operator.postfix(1200, fact, trim(ws, string("!")));

		Binary<Integer> pow = new Binary<Integer>() {
			public Integer map(Integer x1, Integer x2) {
				int res = 1;
				for (int i = 1; i <= x2; ++i)
					res *= x1;
				return res;
			}
		};
		Operator<Integer> powOp = Operator.infixR(1100, pow, trim(ws, string("**")));

		Parser<String> lpar = trim(ws, string("("));
		Parser<String> rpar = trim(ws, string(")"));
		Parser<String> lbr = trim(ws, string("{"));
		Parser<String> rbr = trim(ws, string("}"));
		Parser<String> lsq = trim(ws, string("["));
		Parser<String> rsq = trim(ws, string("]"));

		OppBuilder<Integer> builder;
		Parser<Integer> p;
		Result<Integer> pr;

		{
			builder = new OppBuilder();
			builder.setOperandParser(num);
			p = builder.toParser();

			pr = p.parse("1rest");
			assertTrue(pr.isSuccess());
			assertEquals(Integer.valueOf(1), pr.value());
			assertEquals("1".length(), pr.rest().asInt());
		}

		{
			builder = new OppBuilder();
			builder.setOperandParser(num);
			builder.add(addOp);
			p = builder.toParser();

			pr = p.parse("1 + 2rest");
			assertTrue(pr.isSuccess());
			assertEquals(Integer.valueOf(3), pr.value());
			assertEquals("1 + 2".length(), pr.rest().asInt());
		}

		{
			builder = new OppBuilder();
			builder.setOperandParser(num);
			builder.add(addOp);
			builder.add(subOp);
			p = builder.toParser();

			pr = p.parse("2 - 1rest");
			assertTrue(pr.isSuccess());
			assertEquals(Integer.valueOf(1), pr.value());
			assertEquals("2 - 1".length(), pr.rest().asInt());

			pr = p.parse("1 - 2 - 1rest");
			assertTrue(pr.isSuccess());
			assertEquals(Integer.valueOf(-2), pr.value());
			assertEquals("1 - 2 - 1".length(), pr.rest().asInt());

			pr = p.parse("1 - 2 + 1rest");
			assertTrue(pr.isSuccess());
			assertEquals(Integer.valueOf(0), pr.value());
			assertEquals("1 - 2 + 1".length(), pr.rest().asInt());
		}

		{
			builder = new OppBuilder();
			builder.setOperandParser(num);
			builder.add(addOp);
			builder.add(subOp);
			p = builder.toParser();

			pr = p.parse("2 - 1rest");
			assertTrue(pr.isSuccess());
			assertEquals(Integer.valueOf(1), pr.value());
			assertEquals("2 - 1".length(), pr.rest().asInt());

			pr = p.parse("1 - 2 - 1rest");
			assertTrue(pr.isSuccess());
			assertEquals(Integer.valueOf(-2), pr.value());
			assertEquals("1 - 2 - 1".length(), pr.rest().asInt());

			pr = p.parse("1 - 2 + 1rest");
			assertTrue(pr.isSuccess());
			assertEquals(Integer.valueOf(0), pr.value());
			assertEquals("1 - 2 + 1".length(), pr.rest().asInt());
		}

		{
			builder = new OppBuilder();
			builder.setOperandParser(num);
			builder.add(addOp);
			builder.add(subOp);
			builder.add(incrOp);
			p = builder.toParser();

			pr = p.parse("1.incrrest");
			assertTrue(pr.isSuccess());
			assertEquals(Integer.valueOf(2), pr.value());
			assertEquals("1.incr".length(), pr.rest().asInt());

			pr = p.parse("1.incr.incr.incrrest");
			assertTrue(pr.isSuccess());
			assertEquals(Integer.valueOf(4), pr.value());
			assertEquals("1.incr.incr.incr".length(), pr.rest().asInt());

			pr = p.parse("5 - 1.incr.incr.incr + 2rest");
			assertTrue(pr.isSuccess());
			assertEquals(Integer.valueOf(3), pr.value());
			assertEquals("5 - 1.incr.incr.incr + 2".length(), pr.rest().asInt());
		}

		{
			builder = new OppBuilder();
			builder.setOperandParser(num);
			builder.add(addOp);
			builder.add(subOp);
			builder.add(multOp);
			builder.add(divOp);
			builder.add(plusOp);
			builder.add(minusOp);
			p = builder.toParser();

			pr = p.parse("-6 / 2 - 2 + 3 * 3 - 4 / +2rest"); // -3 -2 + 9 - 2
			assertTrue(pr.isSuccess());
			assertEquals(Integer.valueOf(2), pr.value());
			assertEquals("-6 / 2 - 2 + 3 * 3 - 4 / +2".length(), pr.rest().asInt());
		}

		{
			builder = new OppBuilder();
			builder.setOperandParser(num);
			builder.add(addOp);
			builder.add(subOp);
			builder.add(multOp);
			builder.add(divOp);
			builder.add(plusOp);
			builder.add(minusOp);
			builder.add(factOp);
			p = builder.toParser();

			pr = p.parse("-6! / 2 - 2 + 3 * 3 - 4! / +2rest"); // -360 - 2 + 9 - 12
			assertTrue(pr.isSuccess());
			assertEquals(Integer.valueOf(-365), pr.value());
			assertEquals("-6! / 2 - 2 + 3 * 3 - 4! / +2".length(), pr.rest().asInt());

			pr = p.parse("-3!rest");
			assertTrue(pr.isSuccess());
			assertEquals(Integer.valueOf(-6), pr.value());
			assertEquals("-3!".length(), pr.rest().asInt());
			pr = p.parse("-3!!rest"); // ! is not associative.
			assertTrue(pr.isSuccess());
			assertEquals("-3!".length(), pr.rest().asInt());
		}

		{
			builder = new OppBuilder();
			builder.setOperandParser(num);
			builder.add(addOp);
			builder.add(subOp);
			builder.add(multOp);
			builder.add(divOp);
			builder.add(plusOp);
			builder.add(minusOp);
			builder.add(factOp);
			builder.add(powOp);
			p = builder.toParser();

			pr = p.parse("3 ** 3 ** 2rest"); // 3 ** 9
			assertTrue(pr.isSuccess());
			assertEquals(Integer.valueOf(19683), pr.value());
			assertEquals("3 ** 3 ** 2".length(), pr.rest().asInt());
		}

		{
			builder = new OppBuilder();
			builder.setOperandParser(num);
			builder.add(addOp);
			builder.add(subOp);
			builder.add(multOp);
			builder.add(divOp);
			builder.add(plusOp);
			builder.add(minusOp);
			builder.add(factOp);
			builder.add(powOp);
			builder.addParentheses(lpar, rpar);
			p = builder.toParser();

			pr = p.parse("(3 ** 3) ** 2rest"); // 27 ** 2
			assertTrue(pr.isSuccess());
			assertEquals(Integer.valueOf(729), pr.value());
			assertEquals("(3 ** 3) ** 2".length(), pr.rest().asInt());
		}

		{
			builder = new OppBuilder();
			builder.setOperandParser(num);
			builder.add(addOp);
			builder.add(subOp);
			builder.add(multOp);
			builder.add(divOp);
			builder.add(plusOp);
			builder.add(minusOp);
			builder.add(factOp);
			builder.add(powOp);
			builder.addParentheses(lpar, rpar);
			builder.addParentheses(lbr, rbr);
			builder.addParentheses(lsq, rsq);
			p = builder.toParser();

			pr = p.parse("[(1 + 2) * 3 - {(4 - 2) + 5}] + 2rest"); // [9 - {2 + 5}] + 2
			assertTrue(pr.isSuccess());
			assertEquals(Integer.valueOf(4), pr.value());
			assertEquals("[(1 + 2) * 3 - {(4 - 2) + 5}] + 2".length(), pr.rest().asInt());

			pr = p.parse("{(1 - 2} + 3)rest");
			assertTrue(pr.isFail());
		}
	}
}
