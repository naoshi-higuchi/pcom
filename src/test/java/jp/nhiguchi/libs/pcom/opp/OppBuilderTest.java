/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.nhiguchi.libs.pcom.opp;

import jp.nhiguchi.libs.pcom.*;
import org.junit.jupiter.api.*;

import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static jp.nhiguchi.libs.pcom.Parsers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author Naoshi HIGUCHI
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
		Function<String, Integer> toInt = s -> Integer.parseInt(s);
		Parser<Integer> num = map(toInt, trim(ws, expr("[0-9]+")));

		BinaryOperator<Integer> add = (x1, x2) -> x1 + x2;
		OppOperator<Integer> addOp = OppOperator.infixL(700, add, trim(ws, string("+")));

		BinaryOperator<Integer> sub = (x1, x2) -> x1 - x2;
		OppOperator<Integer> subOp = OppOperator.infixL(700, sub, trim(ws, string("-")));

		BinaryOperator<Integer> mult = (x1, x2) -> x1 * x2;
		OppOperator<Integer> multOp = OppOperator.infixL(800, mult, trim(ws, string("*")));

		BinaryOperator<Integer> div = (x1, x2) -> x1 / x2;
		OppOperator<Integer> divOp = OppOperator.infixL(800, div, trim(ws, string("/")));

		UnaryOperator<Integer> incr = x -> ++x;
		OppOperator<Integer> incrOp = OppOperator.postfixL(900, incr, trim(ws, string(".incr")));

		UnaryOperator<Integer> plus = x -> x;
		OppOperator<Integer> plusOp = OppOperator.prefix(1000, plus, trim(ws, string("+")));

		UnaryOperator<Integer> minus = x -> -x;
		OppOperator<Integer> minusOp = OppOperator.prefix(1000, minus, trim(ws, string("-")));

		UnaryOperator<Integer> fact = x -> {
			int res = 1;
			for (int i = 1; i <= x; ++i)
				res *= i;
			return res;
		};
		OppOperator<Integer> factOp = OppOperator.postfix(1200, fact, trim(ws, string("!")));

		BinaryOperator<Integer> pow = (x1, x2) -> {
			int res = 1;
			for (int i = 1; i <= x2; ++i)
				res *= x1;
			return res;
		};
		OppOperator<Integer> powOp = OppOperator.infixR(1100, pow, trim(ws, string("**")));

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
