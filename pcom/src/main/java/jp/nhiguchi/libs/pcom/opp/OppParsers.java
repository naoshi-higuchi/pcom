package jp.nhiguchi.libs.pcom.opp;

import java.util.*;

import jp.nhiguchi.libs.flist.*;
import static jp.nhiguchi.libs.flist.FList.*;

import jp.nhiguchi.libs.tuple.*;

import jp.nhiguchi.libs.pcom.*;
import static jp.nhiguchi.libs.pcom.Parsers.*;

import static jp.nhiguchi.libs.pcom.opp.Operator.Fixity.*;

/**
 *
 * @author Naoshi HIGUCHI
 */
final class OppParsers {
	private OppParsers() {
	}

	static <T> Parser<T> oppParser(
			OpTable<T> opTbl,
			FList<Pair<Parser<String>, Parser<String>>> parens,
			Parser<T> operandParser) {
		RecursionMark<T> mark = new RecursionMark();
		Parser<T> par = parened(parens, recur(mark));
		Parser<T> term = or(par, operandParser);
		return mark(mark, compose(opTbl, term));
	}

	private static <T> Parser<T> parened(
			FList<Pair<Parser<String>, Parser<String>>> parens,
			Parser<T> expr) {
		FList<Parser<T>> ps = flist();

		for (Pair<Parser<String>, Parser<String>> p : parens) {
			ps = cons(body(p.get1st(), expr, p.get2nd()), ps);
		}

		return or(ps.reverse());
	}

	private static <T> Parser<Operator<T>> pOps(List<Operator<T>> ops) {
		FList<Parser<Operator<T>>> ps = flist();
		for (Operator<T> op : ops) {
			ps = cons(op.parser(), ps);
		}
		return or(ps);
	}

	private static <T> FList<Operator<T>> yfxs(List<Operator<T>> ops) {
		FList<Operator<T>> res = flist();
		for (Operator<T> op : ops) {
			if (op.fix() == YFX) res = cons(op, res);
		}
		return res;
	}

	private static <T> Parser<T> yfx(List<Operator<T>> ops, Parser<T> operand) {
		Parser<Operator<T>> pops = pOps(ops);
		Parser<List<Pair<Operator<T>, T>>> tail = rep(pair(pops, operand));
		Parser<Pair<T, List<Pair<Operator<T>, T>>>> p = pair(operand, tail);
		Map1<Pair<T, List<Pair<Operator<T>, T>>>, T> applyL = new Map1<Pair<T, List<Pair<Operator<T>, T>>>, T>() {
			public T map(
					Pair<T, List<Pair<Operator<T>, T>>> x) {
				T res = x.get1st();
				for (Pair<Operator<T>, T> te : x.get2nd()) {
					Operator<T> op = te.get1st();
					T opr = te.get2nd();
					res = op.bmap().map(res, opr);
				}
				return res;
			}
		};

		return map(applyL, p);
	}

	private static <T> FList<Operator<T>> yfs(List<Operator<T>> ops) {
		FList<Operator<T>> res = flist();
		for (Operator<T> op : ops) {
			if (op.fix() == YF) res = cons(op, res);
		}
		return res;
	}

	private static <T> Parser<T> yf(List<Operator<T>> ops, Parser<T> operand) {
		Parser<Operator<T>> pops = pOps(ops);
		Parser<List<Operator<T>>> tail = rep(pops);
		Parser<Pair<T, List<Operator<T>>>> p = pair(operand, tail);
		Map1<Pair<T, List<Operator<T>>>, T> applyL = new Map1<Pair<T, List<Operator<T>>>, T>() {
			public T map(Pair<T, List<Operator<T>>> x) {
				T res = x.get1st();
				for (Operator<T> op : x.get2nd()) {
					res = op.umap().map(res);
				}
				return res;
			}
		};

		return map(applyL, p);
	}

	private static <T> Map1<Pair<T, T>, T> bin(final Operator<T> op) {
		return new Map1<Pair<T, T>, T>() {
			public T map(Pair<T, T> x) {
				return op.bmap().map(
						x.get1st(), x.get2nd());
			}
		};
	}

	private static <T> Parser<T> binary(
			final Parser<T> left,
			final Operator<T> op,
			final Parser<T> right) {
		Parser<Pair<T, T>> p = pair(followedBy(left, op.parser()), right);

		return map(bin(op), p);
	}

	private static <T> Map1<T, T> uni(final Operator<T> op) {
		return new Map1<T, T>() {
			public T map(T x) {
				return op.umap().map(x);
			}
		};
	}

	private static <T> Parser<T> prefix(
			final Operator<T> op,
			final Parser<T> operand) {
		Parser<T> p = precededBy(op.parser(), operand);

		return map(uni(op), p);
	}

	private static <T> Parser<T> postfix(
			final Parser<T> operand,
			final Operator<T> op) {
		Parser<T> p = followedBy(operand, op.parser());

		return map(uni(op), p);
	}

	private static <T> Parser<T> compose(
			OpTable<T> rest, Parser<T> term) {
		if (rest.isEmpty()) return term;

		Parser<T> prec = compose(rest.tail(), term); // more preceding.
		FList<Operator<T>> column = rest.head();

		RecursionMark<T> mark = new RecursionMark<T>();
		Parser<T> preceq = recur(mark);

		FList<Operator<T>> yfxs = yfxs(column);
		FList<Operator<T>> yfs = yfs(column);
		FList<Parser<T>> ps = flist();
		for (Operator<T> op : column) {
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
