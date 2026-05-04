package jp.nhiguchi.libs.pcom;

import java.util.List;

import jp.nhiguchi.libs.tuple.Pair;
import static jp.nhiguchi.libs.flist.FList.*;

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
		return map((T src1, List<T> src2) -> flist(src2).prepend(src1), p, rep(p));
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
		return map((Object ignore, T v) -> v, preceder, p);
	}

	public static <T> Parser<T> followedBy(
			Parser<? extends T> p, Parser<?> follower) {
		checkNotNull(p, follower);
		return map((T v, Object ignore) -> v, p, follower);
	}

	public static <T> Parser<T> body(
			Parser<?> preceder, Parser<? extends T> p, Parser<?> follower) {
		checkNotNull(preceder, p, follower);
		return map((Object i1, T v, Object i2) -> v, preceder, p, follower);
	}

	public static <T, U> Parser<Pair<T, U>> pair(Parser<? extends T> p1, Parser<? extends U> p2) {
		checkNotNull(p1, p2);
		return map((T e1, U e2) -> Pair.newPair(e1, e2), p1, p2);
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
		return map((List<String> strs) -> String.join("", strs), p);
	}

	public static Parser<String> concat(List<? extends Parser<String>> ps) {
		checkNotNull(ps);

		Parser<List<String>> p = seq(ps);
		return concat(p);
	}

	public static <T> Parser<T> except(Parser<?> ex, Parser<T> p) {
		return precededBy(not(ex), p);
	}

	public static <T> Parser<List<T>> sepBy(
			Parser<? extends T> p, Parser<?> sep) {
		if (p == null || sep == null) throw nullArgEx();
		Map1<Void, List<T>> toEmpty = v -> flist();
		Parser<List<T>> empty = map(toEmpty, not(p));

		return or(empty, sepBy1(p, sep));
	}

	public static <T> Parser<List<T>> sepBy1(
			Parser<? extends T> p, Parser<?> sep) {
		if (p == null || sep == null) throw nullArgEx();

		Parser<T> q = precededBy(sep, p);
		Parser<List<T>> qs = rep(q);

		return map((T v1, List<T> v2) -> cons(v1, flist(v2)), p, qs);
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
