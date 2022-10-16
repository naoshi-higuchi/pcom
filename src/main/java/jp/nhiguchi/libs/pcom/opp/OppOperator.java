package jp.nhiguchi.libs.pcom.opp;

import jp.nhiguchi.libs.pcom.*;

import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static jp.nhiguchi.libs.pcom.opp.OppOperator.Fixity.*;

import static jp.nhiguchi.libs.pcom.Parsers.*;

/**
 *
 * @author Naoshi HIGUCHI
 */
public final class OppOperator<T> {
	static enum Fixity {
		/**
		 * Infix, non-associative.
		 */
		XFX,
		/**
		 * Infix, right-associative.
		 */
		XFY,
		/**
		 * Infix, left-associative.
		 */
		YFX,
		/**
		 * Prefix, non-associative.
		 */
		FX,
		/**
		 * Prefix, associative.
		 */
		FY,
		/**
		 * Postfix, non-associative.
		 */
		XF,
		/**
		 * Postfix, associative.
		 */
		YF
	}
	private final int fPrec;
	private final Fixity fFix;
	private final UnaryOperator<T> fUnary;
	private final BinaryOperator<T> fBinary;
	private final Parser<OppOperator<T>> fP;

	private OppOperator(
			int prec, Fixity fix,
			UnaryOperator<T> unary, BinaryOperator<T> binary, Parser<?> p) {
		fPrec = prec;
		fFix = fix;
		fUnary = unary;
		fBinary = binary;
		fP = opp(this, p);
	}

	private static <T> Parser<OppOperator<T>> opp(
			final OppOperator<T> op, Parser<?> p) {
		Function<Void, OppOperator<T>> retOp = v -> op;

		return map(retOp, followedBy(and(p), p));
	}

	private static <T> OppOperator<T> unary(
			int prec, Fixity fix, UnaryOperator<T> unary, Parser<?> p) {
		return new OppOperator(prec, fix, unary, null, p);
	}

	private static <T> OppOperator<T> binary(
			int prec, Fixity fix, BinaryOperator<T> binary, Parser<?> p) {
		return new OppOperator(prec, fix, null, binary, p);
	}

	/**
	 * Infix, non-associative.
	 */
	public static <T> OppOperator<T> infix(int precedence, BinaryOperator<T> map, Parser<?> p) {
		return binary(precedence, XFX, map, p);
	}

	/**
	 * Infix, right-associative.
	 */
	public static <T> OppOperator<T> infixR(int precedence, BinaryOperator<T> map, Parser<?> p) {
		return binary(precedence, XFY, map, p);
	}

	/**
	 * Infix, left-associative.
	 */
	public static <T> OppOperator<T> infixL(int precedence, BinaryOperator<T> map, Parser<?> p) {
		return binary(precedence, YFX, map, p);
	}

	/**
	 * Prefix, non-associative.
	 */
	public static <T> OppOperator<T> prefix(int precedence, UnaryOperator<T> map, Parser<?> p) {
		return unary(precedence, FX, map, p);
	}

	/**
	 * Prefix, associative.
	 */
	public static <T> OppOperator<T> prefixR(int precedence, UnaryOperator<T> map, Parser<?> p) {
		return unary(precedence, FY, map, p);
	}

	/**
	 * Postfix, non-associative.
	 */	public static <T> OppOperator<T> postfix(int precedence, UnaryOperator<T> map, Parser<?> p) {
		return unary(precedence, XF, map, p);
	}

	/**
	 * Postfix, associative.
	 */
	public static <T> OppOperator<T> postfixL(int prec, UnaryOperator<T> map, Parser<?> p) {
		return unary(prec, YF, map, p);
	}

	int prec() {
		return fPrec;
	}

	Fixity fix() {
		return fFix;
	}

	Parser<OppOperator<T>> parser() {
		return fP;
	}

	UnaryOperator<T> unaryOperator() {
		return fUnary;
	}

	BinaryOperator<T> binaryOperator() {
		return fBinary;
	}
}
