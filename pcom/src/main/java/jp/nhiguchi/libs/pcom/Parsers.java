package jp.nhiguchi.libs.pcom;

import java.util.List;

import jp.nhiguchi.libs.tuple.Pair;
import static jp.nhiguchi.libs.flist.FList.*;

/**
 *
 * @author naoshi
 */
public final class Parsers {
	private Parsers() {
	}

	private static void checkNotNull(List<?> objs) {
		if (objs == null) throw nullArgEx();
		for (Object obj : objs) {
			if (obj == null) throw nullArgEx();
		}
	}

	private static void checkNotNull(Object... objs) {
		if (objs == null) throw nullArgEx();
		for (Object obj : objs) {
			if (obj == null) throw nullArgEx();
		}
	}

	private static RuntimeException nullArgEx() {
		return new IllegalArgumentException("null argument");
	}

	public static Parser<String> string(String str) {
		return Primitives.string(str);
	}

	public static <T> Parser<T> or(List<? extends Parser<? extends T>> ps) {
		return Primitives.or(ps);
	}

	public static <T> Parser<T> or(Parser<? extends T>... ps) {
		return Primitives.or(ps);
	}

	public static <T> Parser<List<T>> seq(List<? extends Parser<? extends T>> ps) {
		return Primitives.seq(ps);
	}

	public static <T> Parser<List<T>> seq(Parser<? extends T>... ps) {
		return Primitives.seq(ps);
	}

	public static Parser<String> any() {
		return Primitives.any();
	}

	public static Parser<Void> and(Parser<?> p) {
		return Primitives.and(p);
	}

	public static Parser<Void> not(Parser<?> p) {
		return Primitives.not(p);
	}

	public static <T> Parser<T> opt(Parser<? extends T> p) {
		return Primitives.opt(p);
	}

	public static <T> Parser<List<T>> rep(Parser<? extends T> p) {
		return Primitives.rep(p);
	}

	public static <T> Parser<List<T>> rep1(Parser<? extends T> p) {
		Map2<T, List<? extends T>, List<T>> conc = new Map2<T, List<? extends T>, List<T>>() {
			public List<T> map(T src1, List<? extends T> src2) {
				return flist(src2).prepend(src1);
			}
		};

		return map(conc, p, rep(p));
	}

	public static <T> Parser<T> mark(RecursionMark<T> m, Parser<? extends T> p) {
		return Recursions.mark(m, p);
	}

	public static <T> Parser<T> recur(RecursionMark<T> m) {
		return Recursions.recur(m);
	}

	public static <From, To> Parser<To> map(
			Map1<From, To> m, Parser<? extends From> p) {
		return Maps.map(m, p);
	}

	public static <From1, From2, To> Parser<To> map(
			Map2<From1, From2, To> m,
			Parser<? extends From1> p1,
			Parser<? extends From2> p2) {
		return Maps.map(m, p1, p2);
	}

	public static <From1, From2, From3, To> Parser<To> map(
			Map3<From1, From2, From3, To> m,
			Parser<? extends From1> p1,
			Parser<? extends From2> p2,
			Parser<? extends From3> p3) {
		return Maps.map(m, p1, p2, p3);
	}

	public static <T> Parser<T> cond(
			Predicate<T> pred, Parser<? extends T> p) {
		return Maps.cond(pred, p);
	}

	public static <T, P> Parser<P> pos(
			Positional<T, P> positional, Parser<? extends T> p) {
		return Maps.pos(positional, p);
	}

	public static <T> Parser<T> precededBy(
			Parser<?> preceder, Parser<? extends T> p) {
		checkNotNull(preceder, p);

		Map2<Object, T, T> drop1st = new Map2<Object, T, T>() {
			public T map(Object ignore, T v) {
				return v;
			}
		};

		return map(drop1st, preceder, p);
	}

	public static <T> Parser<T> followedBy(
			Parser<? extends T> p, Parser<?> follower) {
		checkNotNull(p, follower);

		Map2<T, Object, T> drop2nd = new Map2<T, Object, T>() {
			public T map(T v, Object ignore) {
				return v;
			}
		};

		return map(drop2nd, p, follower);
	}

	public static <T> Parser<T> body(
			Parser<?> preceder, Parser<? extends T> p, Parser<?> follower) {
		checkNotNull(preceder, p, follower);

		Map3<Object, T, Object, T> take2nd = new Map3<Object, T, Object, T>() {
			public T map(Object ignore1, T v, Object ignore2) {
				return v;
			}
		};

		return map(take2nd, preceder, p, follower);
	}

	public static <T, U> Parser<Pair<T, U>> pair(Parser<? extends T> p1, Parser<? extends U> p2) {
		checkNotNull(p1, p2);

		Map2<T, U, Pair<T, U>> mkPair = new Map2<T, U, Pair<T, U>>() {
			public Pair<T, U> map(T e1, U e2) {
				return Pair.newPair(e1, e2);
			}
		};

		return map(mkPair, p1, p2);
	}

	public static Parser<String> trim(Parser<String> trimming, Parser<String> p) {
		checkNotNull(trimming, p);

		Parser<String> t = opt(trimming);
		return body(t, p, t);
	}

	public static Parser<String> concat(Parser<String>... ps) {
		return concat(flist(ps));
	}

	public static Parser<String> concat(Parser<? extends List<String>> p) {
		checkNotNull(p);

		Map1<List<String>, String> conc = new Map1<List<String>, String>() {
			public String map(List<String> strs) {
				StringBuilder sb = new StringBuilder();
				for (String str : strs) {
					sb.append(str);
				}
				return sb.toString();
			}
		};

		return map(conc, p);
	}

	public static Parser<String> concat(List<? extends Parser<String>> ps) {
		checkNotNull(ps);

		Parser<List<String>> p = seq(ps);
		return concat(p);
	}

	public static <T> Parser<T> except(Parser<?> ex, Parser<T> p) {
		return precededBy(not(ex), p);
	}

	public static Parser<String> expr(String expr) {
		checkNotNull(expr);

		Result<Parser<String>> r = Expressions.expression().parse(expr);
		if (r.isFail())
			throw new IllegalArgumentException(r.error().toString());
		if (!r.rest().isEnd()) {
			int i = r.rest().asInt();
			throw new IllegalArgumentException(
					"\"" + expr.substring(0, i) + "\" | \""
					+ expr.substring(i) + "\"");
		}

		return r.value();
	}
}
