package jp.nhiguchi.libs.pcom.opp;

import java.util.*;
import java.util.function.Function;

import jp.nhiguchi.libs.flist.*;
import static jp.nhiguchi.libs.flist.FList.*;

import jp.nhiguchi.libs.tuple.*;

import jp.nhiguchi.libs.pcom.*;
import static jp.nhiguchi.libs.pcom.Parsers.*;

import static jp.nhiguchi.libs.pcom.opp.OppOperator.Fixity.*;

/**
 *
 * @author Naoshi HIGUCHI
 */
final class OppParsers {
	private OppParsers() {
	}

	static <T> Parser<T> oppParser(
			OppOperatorTable<T> opTbl,
			FList<Pair<Parser<String>, Parser<String>>> parens,
			Parser<T> operandParser) {
		RecursionMark<T> mark = new RecursionMark();
		Parser<T> par = parenthesized(parens, recurse(mark));
		Parser<T> term = or(par, operandParser);
		return mark(mark, compose(opTbl, term));
	}

	private static <T> Parser<T> parenthesized(
			FList<Pair<Parser<String>, Parser<String>>> parens,
			Parser<T> expr) {
		FList<Parser<T>> ps = flist();

		for (Pair<Parser<String>, Parser<String>> p : parens) {
			ps = cons(body(p.get1st(), expr, p.get2nd()), ps);
		}

		return or(ps.reverse());
	}

	private static <T> Parser<OppOperator<T>> pOps(List<OppOperator<T>> ops) {
		FList<Parser<OppOperator<T>>> ps = flist();
		for (OppOperator<T> op : ops) {
			ps = cons(op.parser(), ps);
		}
		return or(ps);
	}

	private static <T> FList<OppOperator<T>> yfxs(List<OppOperator<T>> ops) {
		FList<OppOperator<T>> res = flist();
		for (OppOperator<T> op : ops) {
			if (op.fix() == YFX) res = cons(op, res);
		}
		return res;
	}

	private static <T> Parser<T> yfx(List<OppOperator<T>> ops, Parser<T> operand) {
		Parser<OppOperator<T>> pops = pOps(ops);
		Parser<List<Pair<OppOperator<T>, T>>> tail = rep(pair(pops, operand));
		Parser<Pair<T, List<Pair<OppOperator<T>, T>>>> p = pair(operand, tail);
		Function<Pair<T, List<Pair<OppOperator<T>, T>>>, T> applyL = x -> {
			T res = x.get1st();
			for (Pair<OppOperator<T>, T> te : x.get2nd()) {
				OppOperator<T> op = te.get1st();
				T opr = te.get2nd();
				res = op.binaryOperator().apply(res, opr);
			}
			return res;
		};

		return map(applyL, p);
	}

	private static <T> FList<OppOperator<T>> yfs(List<OppOperator<T>> ops) {
		FList<OppOperator<T>> res = flist();
		for (OppOperator<T> op : ops) {
			if (op.fix() == YF) res = cons(op, res);
		}
		return res;
	}

	private static <T> Parser<T> yf(List<OppOperator<T>> ops, Parser<T> operand) {
		Parser<OppOperator<T>> pops = pOps(ops);
		Parser<List<OppOperator<T>>> tail = rep(pops);
		Parser<Pair<T, List<OppOperator<T>>>> p = pair(operand, tail);
		Function<Pair<T, List<OppOperator<T>>>, T> applyL = x -> {
			T res = x.get1st();
			for (OppOperator<T> op : x.get2nd()) {
				res = op.unaryOperator().apply(res);
			}
			return res;
		};

		return map(applyL, p);
	}

	private static <T> Function<Pair<T, T>, T> bin(final OppOperator<T> op) {
		return x -> op.binaryOperator().apply(
				x.get1st(), x.get2nd());
	}

	private static <T> Parser<T> binary(
			final Parser<T> left,
			final OppOperator<T> op,
			final Parser<T> right) {
		Parser<Pair<T, T>> p = pair(followedBy(left, op.parser()), right);

		return map(bin(op), p);
	}

	private static <T> Function<T, T> uni(final OppOperator<T> op) {
		return x -> op.unaryOperator().apply(x);
	}

	private static <T> Parser<T> prefix(
			final OppOperator<T> op,
			final Parser<T> operand) {
		Parser<T> p = precededBy(op.parser(), operand);

		return map(uni(op), p);
	}

	private static <T> Parser<T> postfix(
			final Parser<T> operand,
			final OppOperator<T> op) {
		Parser<T> p = followedBy(operand, op.parser());

		return map(uni(op), p);
	}

	private static <T> Parser<T> compose(
			OppOperatorTable<T> rest, Parser<T> term) {
		if (rest.isEmpty()) return term;

		Parser<T> prec = compose(rest.tail(), term); // more preceding.
		FList<OppOperator<T>> column = rest.head();

		RecursionMark<T> mark = new RecursionMark<>();
		Parser<T> preceq = recurse(mark);

		FList<OppOperator<T>> yfxs = yfxs(column);
		FList<OppOperator<T>> yfs = yfs(column);
		FList<Parser<T>> ps = flist();
		for (OppOperator<T> op : column) {
			Parser<T> p;
			switch (op.fix()) {
				case XFX:
					p = binary(prec, op, prec);
					break;
				case XFY:
					p = binary(prec, op, preceq);
					break;
				case YFX:
					p = yfx(yfxs, prec);
					break;
				case FX:
					p = prefix(op, prec);
					break;
				case FY:
					p = prefix(op, preceq);
					break;
				case XF:
					p = postfix(prec, op);
					break;
				case YF:
					p = yf(yfs, prec);
					break;
				default:
					throw new RuntimeException();
			}
			ps = cons(p, ps);
		}

		return mark(mark, or(or(ps), prec));
	}
}
