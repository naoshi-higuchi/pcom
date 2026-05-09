package jp.nhiguchi.libs.pcom.opp;

import jp.nhiguchi.libs.pcom.*;
import org.junit.jupiter.api.*;

import static jp.nhiguchi.libs.pcom.Parsers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OppBuilderTest {

	@Test
	public void testAmbiguousGrammar() {
		// YFX and XFY
		assertThrows(IllegalArgumentException.class, () -> {
			OppBuilder<Integer> builder = new OppBuilder<>();
			builder.add(Operator.infixL(100, (x, y) -> x + y, string("+")));
			builder.add(Operator.infixR(100, (x, y) -> x + y, string("-")));
		});

		// YFX and YF
		assertThrows(IllegalArgumentException.class, () -> {
			OppBuilder<Integer> builder = new OppBuilder<>();
			builder.add(Operator.infixL(100, (x, y) -> x + y, string("+")));
			builder.add(Operator.postfixL(100, x -> x, string("++")));
		});

		// YFX and FY
		assertThrows(IllegalArgumentException.class, () -> {
			OppBuilder<Integer> builder = new OppBuilder<>();
			builder.add(Operator.infixL(100, (x, y) -> x + y, string("+")));
			builder.add(Operator.prefixR(100, x -> x, string("++")));
		});

		// XFY and YF
		assertThrows(IllegalArgumentException.class, () -> {
			OppBuilder<Integer> builder = new OppBuilder<>();
			builder.add(Operator.infixR(100, (x, y) -> x + y, string("+")));
			builder.add(Operator.postfixL(100, x -> x, string("++")));
		});

		// XFY and FY
		assertThrows(IllegalArgumentException.class, () -> {
			OppBuilder<Integer> builder = new OppBuilder<>();
			builder.add(Operator.infixR(100, (x, y) -> x + y, string("+")));
			builder.add(Operator.prefixR(100, x -> x, string("++")));
		});

		// FY and YF
		assertThrows(IllegalArgumentException.class, () -> {
			OppBuilder<Integer> builder = new OppBuilder<>();
			builder.add(Operator.prefixR(100, x -> x, string("++")));
			builder.add(Operator.postfixL(100, x -> x, string("--")));
		});
	}

	@Test
	public void testToParser() {
		System.out.println("toParser");
		Parser<String> ws = expr("[ \t]*");
		Parser<Integer> num = map((String s) -> Integer.parseInt(s), trim(ws, expr("[0-9]+")));

		Operator<Integer> addOp  = Operator.infixL(700,  (x1, x2) -> x1 + x2, trim(ws, string("+")));
		Operator<Integer> subOp  = Operator.infixL(700,  (x1, x2) -> x1 - x2, trim(ws, string("-")));
		Operator<Integer> multOp = Operator.infixL(800,  (x1, x2) -> x1 * x2, trim(ws, string("*")));
		Operator<Integer> divOp  = Operator.infixL(800,  (x1, x2) -> x1 / x2, trim(ws, string("/")));
		Operator<Integer> incrOp = Operator.postfixL(900, x -> x + 1, trim(ws, string(".incr")));
		Operator<Integer> plusOp = Operator.prefix(1000,  x -> x,  trim(ws, string("+")));
		Operator<Integer> minusOp = Operator.prefix(1000, x -> -x, trim(ws, string("-")));
		Operator<Integer> factOp = Operator.postfix(1200, x -> {
			int res = 1;
			for (int i = 1; i <= x; ++i) res *= i;
			return res;
		}, trim(ws, string("!")));
		Operator<Integer> powOp = Operator.infixR(1100, (x1, x2) -> {
			int res = 1;
			for (int i = 1; i <= x2; ++i) res *= x1;
			return res;
		}, trim(ws, string("**")));

		Parser<String> lpar = trim(ws, string("("));
		Parser<String> rpar = trim(ws, string(")"));
		Parser<String> lbr  = trim(ws, string("{"));
		Parser<String> rbr  = trim(ws, string("}"));
		Parser<String> lsq  = trim(ws, string("["));
		Parser<String> rsq  = trim(ws, string("]"));

		OppBuilder<Integer> builder;
		Parser<Integer> p;
		Result<Integer> pr;

		{
			builder = new OppBuilder<>();
			builder.setOperandParser(num);
			p = builder.toParser();

			pr = p.parse("1rest");
			assertTrue(pr.isSuccess());
			assertEquals(Integer.valueOf(1), pr.value());
			assertEquals("1".length(), pr.rest().asInt());
		}

		{
			builder = new OppBuilder<>();
			builder.setOperandParser(num);
			builder.add(addOp);
			p = builder.toParser();

			pr = p.parse("1 + 2rest");
			assertTrue(pr.isSuccess());
			assertEquals(Integer.valueOf(3), pr.value());
			assertEquals("1 + 2".length(), pr.rest().asInt());
		}

		{
			builder = new OppBuilder<>();
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
			builder = new OppBuilder<>();
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
			builder = new OppBuilder<>();
			builder.setOperandParser(num);
			builder.add(addOp); builder.add(subOp);
			builder.add(multOp); builder.add(divOp);
			builder.add(plusOp); builder.add(minusOp);
			p = builder.toParser();

			pr = p.parse("-6 / 2 - 2 + 3 * 3 - 4 / +2rest"); // -3 -2 + 9 - 2
			assertTrue(pr.isSuccess());
			assertEquals(Integer.valueOf(2), pr.value());
			assertEquals("-6 / 2 - 2 + 3 * 3 - 4 / +2".length(), pr.rest().asInt());
		}

		{
			builder = new OppBuilder<>();
			builder.setOperandParser(num);
			builder.add(addOp); builder.add(subOp);
			builder.add(multOp); builder.add(divOp);
			builder.add(plusOp); builder.add(minusOp);
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
			builder = new OppBuilder<>();
			builder.setOperandParser(num);
			builder.add(addOp); builder.add(subOp);
			builder.add(multOp); builder.add(divOp);
			builder.add(plusOp); builder.add(minusOp);
			builder.add(factOp); builder.add(powOp);
			p = builder.toParser();

			pr = p.parse("3 ** 3 ** 2rest"); // 3 ** 9
			assertTrue(pr.isSuccess());
			assertEquals(Integer.valueOf(19683), pr.value());
			assertEquals("3 ** 3 ** 2".length(), pr.rest().asInt());
		}

		{
			builder = new OppBuilder<>();
			builder.setOperandParser(num);
			builder.add(addOp); builder.add(subOp);
			builder.add(multOp); builder.add(divOp);
			builder.add(plusOp); builder.add(minusOp);
			builder.add(factOp); builder.add(powOp);
			builder.addParentheses(lpar, rpar);
			p = builder.toParser();

			pr = p.parse("(3 ** 3) ** 2rest"); // 27 ** 2
			assertTrue(pr.isSuccess());
			assertEquals(Integer.valueOf(729), pr.value());
			assertEquals("(3 ** 3) ** 2".length(), pr.rest().asInt());
		}

		{
			builder = new OppBuilder<>();
			builder.setOperandParser(num);
			builder.add(addOp); builder.add(subOp);
			builder.add(multOp); builder.add(divOp);
			builder.add(plusOp); builder.add(minusOp);
			builder.add(factOp); builder.add(powOp);
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
