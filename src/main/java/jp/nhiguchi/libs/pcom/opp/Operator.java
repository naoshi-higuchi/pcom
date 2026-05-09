package jp.nhiguchi.libs.pcom.opp;

import jp.nhiguchi.libs.pcom.*;

import static jp.nhiguchi.libs.pcom.opp.Operator.Fixity.*;

import static jp.nhiguchi.libs.pcom.Parsers.*;

/**
 * Represents an operator in an operator-precedence grammar.
 * Operators have a precedence, a fixity (infix, prefix, postfix, and associativity),
 * and associated mapping functions for unary or binary operations.
 *
 * @param <T> the type of the values that the operator acts upon
 */
public final class Operator<T> {
	/**
	 * Defines the fixity and associativity of an operator.
	 */
	enum Fixity {
		/** Infix, non-associative. */
		XFX,
		/** Infix, right-associative. */
		XFY,
		/** Infix, left-associative. */
		YFX,
		/** Prefix, non-associative. */
		FX,
		/** Prefix, associative. */
		FY,
		/** Postfix, non-associative. */
		XF,
		/** Postfix, associative. */
		YF
	}

	private final int fPrec;
	private final Fixity fFix;
	private final Unary<T> fUnary;
	private final Binary<T> fBinary;
	private final Parser<Operator<T>> fP;

	private Operator(
			int prec, Fixity fix,
			Unary<T> unary, Binary<T> binary, Parser<?> p) {
		fPrec = prec;
		fFix = fix;
		fUnary = unary;
		fBinary = binary;
		fP = opp(this, p);
	}

	private static <T> Parser<Operator<T>> opp(
			final Operator<T> op, Parser<?> p) {
		return map(v -> op, followedBy(and(p), p));
	}

	private static <T> Operator<T> unary(
			int prec, Fixity fix, Unary<T> unary, Parser<?> p) {
		return new Operator<>(prec, fix, unary, null, p);
	}

	private static <T> Operator<T> binary(
			int prec, Fixity fix, Binary<T> binary, Parser<?> p) {
		return new Operator<>(prec, fix, null, binary, p);
	}

	/**
	 * Creates an infix, non-associative operator (XFX).
	 *
	 * @param <T> the type of the values that the operator acts upon
	 * @param prec the precedence of the operator
	 * @param map the binary mapping function for this operator
	 * @param p the parser that recognizes this operator's symbol
	 * @return a new {@code Operator} instance
	 */
	public static <T> Operator<T> infix(int prec, Binary<T> map, Parser<?> p) {
		return binary(prec, XFX, map, p);
	}

	/**
	 * Creates an infix, right-associative operator (XFY).
	 *
	 * @param <T> the type of the values that the operator acts upon
	 * @param prec the precedence of the operator
	 * @param map the binary mapping function for this operator
	 * @param p the parser that recognizes this operator's symbol
	 * @return a new {@code Operator} instance
	 */
	public static <T> Operator<T> infixR(int prec, Binary<T> map, Parser<?> p) {
		return binary(prec, XFY, map, p);
	}

	/**
	 * Creates an infix, left-associative operator (YFX).
	 *
	 * @param <T> the type of the values that the operator acts upon
	 * @param prec the precedence of the operator
	 * @param map the binary mapping function for this operator
	 * @param p the parser that recognizes this operator's symbol
	 * @return a new {@code Operator} instance
	 */
	public static <T> Operator<T> infixL(int prec, Binary<T> map, Parser<?> p) {
		return binary(prec, YFX, map, p);
	}

	/**
	 * Creates a prefix, non-associative operator (FX).
	 *
	 * @param <T> the type of the values that the operator acts upon
	 * @param prec the precedence of the operator
	 * @param map the unary mapping function for this operator
	 * @param p the parser that recognizes this operator's symbol
	 * @return a new {@code Operator} instance
	 */
	public static <T> Operator<T> prefix(int prec, Unary<T> map, Parser<?> p) {
		return unary(prec, FX, map, p);
	}

	/**
	 * Creates a prefix, associative operator (FY).
	 *
	 * @param <T> the type of the values that the operator acts upon
	 * @param prec the precedence of the operator
	 * @param map the unary mapping function for this operator
	 * @param p the parser that recognizes this operator's symbol
	 * @return a new {@code Operator} instance
	 */
	public static <T> Operator<T> prefixR(int prec, Unary<T> map, Parser<?> p) {
		return unary(prec, FY, map, p);
	}

	/**
	 * Creates a postfix, non-associative operator (XF).
	 *
	 * @param <T> the type of the values that the operator acts upon
	 * @param prec the precedence of the operator
	 * @param map the unary mapping function for this operator
	 * @param p the parser that recognizes this operator's symbol
	 * @return a new {@code Operator} instance
	 */
	public static <T> Operator<T> postfix(int prec, Unary<T> map, Parser<?> p) {
		return unary(prec, XF, map, p);
	}

	/**
	 * Creates a postfix, associative operator (YF).
	 *
	 * @param <T> the type of the values that the operator acts upon
	 * @param prec the precedence of the operator
	 * @param map the unary mapping function for this operator
	 * @param p the parser that recognizes this operator's symbol
	 * @return a new {@code Operator} instance
	 */
	public static <T> Operator<T> postfixL(int prec, Unary<T> map, Parser<?> p) {
		return unary(prec, YF, map, p);
	}

	/**
	 * Returns the precedence of this operator.
	 *
	 * @return the precedence
	 */
	int prec() {
		return fPrec;
	}

	/**
	 * Returns the fixity of this operator.
	 *
	 * @return the fixity
	 */
	Fixity fix() {
		return fFix;
	}

	/**
	 * Returns a parser that recognizes this operator's symbol and returns this operator.
	 *
	 * @return a parser for this operator
	 */
	Parser<Operator<T>> parser() {
		return fP;
	}

	/**
	 * Returns the unary mapping function associated with this operator.
	 *
	 * @return the unary mapping function
	 */
	Unary<T> umap() {
		return fUnary;
	}

	/**
	 * Returns the binary mapping function associated with this operator.
	 *
	 * @return the binary mapping function
	 */
	Binary<T> bmap() {
		return fBinary;
	}
}
