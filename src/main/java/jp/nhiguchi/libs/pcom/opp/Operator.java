package jp.nhiguchi.libs.pcom.opp;

import jp.nhiguchi.libs.pcom.*;

import static jp.nhiguchi.libs.pcom.opp.Operator.Fixity.*;

import static jp.nhiguchi.libs.pcom.Parsers.*;

/**
 *
 * @author Naoshi HIGUCHI
 */
public final class Operator<T> {
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
		Map1<Void, Operator<T>> retOp = new Map1<Void, Operator<T>>() {
			public Operator<T> map(Void v) {
				return op;
			}
		};

		return map(retOp, followedBy(and(p), p));
	}

	private static <T> Operator<T> unary(
			int prec, Fixity fix, Unary<T> unary, Parser<?> p) {
		return new Operator(prec, fix, unary, null, p);
	}

	private static <T> Operator<T> binary(
			int prec, Fixity fix, Binary<T> binary, Parser<?> p) {
		return new Operator(prec, fix, null, binary, p);
	}

	public static <T> Operator<T> infix(int prec, Binary map, Parser<?> p) {
		return binary(prec, XFX, map, p);
	}

	public static <T> Operator<T> infixR(int prec, Binary map, Parser<?> p) {
		return binary(prec, XFY, map, p);
	}

	public static <T> Operator<T> infixL(int prec, Binary map, Parser<?> p) {
		return binary(prec, YFX, map, p);
	}

	public static <T> Operator<T> prefix(int prec, Unary map, Parser<?> p) {
		return unary(prec, FX, map, p);
	}

	public static <T> Operator<T> prefixR(int prec, Unary map, Parser<?> p) {
		return unary(prec, FY, map, p);
	}

	public static <T> Operator<T> postfix(int prec, Unary map, Parser<?> p) {
		return unary(prec, XF, map, p);
	}

	public static <T> Operator<T> postfixL(int prec, Unary map, Parser<?> p) {
		return unary(prec, YF, map, p);
	}

	int prec() {
		return fPrec;
	}

	Fixity fix() {
		return fFix;
	}

	Parser<Operator<T>> parser() {
		return fP;
	}

	Unary<T> umap() {
		return fUnary;
	}

	Binary<T> bmap() {
		return fBinary;
	}
}
