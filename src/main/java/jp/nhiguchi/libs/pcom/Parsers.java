package jp.nhiguchi.libs.pcom;

import java.util.List;

import jp.nhiguchi.libs.tuple.Pair;
import static jp.nhiguchi.libs.flist.FList.*;

/**
 * Provides a collection of factory methods for creating common parser combinators.
 * This class is the primary entry point for constructing parsers using the PCOM library.
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

	/**
	 * Creates a parser that matches a literal string.
	 *
	 * @param str the string to match
	 * @return a parser that matches the given string
	 */
	public static Parser<String> string(String str) {
		return Primitives.string(str);
	}

	/**
	 * Creates a parser that tries to match one of the given parsers.
	 * It attempts each parser in the order they are provided and returns the result of the first successful parse.
	 *
	 * @param <T> the type of the value produced by the parsers
	 * @param ps a list of parsers to try
	 * @return a parser that succeeds if any of the given parsers succeed
	 */
	public static <T> Parser<T> or(List<? extends Parser<? extends T>> ps) {
		return Primitives.or(ps);
	}

	/**
	 * Creates a parser that tries to match one of the given parsers.
	 * It attempts each parser in the order they are provided and returns the result of the first successful parse.
	 *
	 * @param <T> the type of the value produced by the parsers
	 * @param ps an array of parsers to try
	 * @return a parser that succeeds if any of the given parsers succeed
	 */
	public static <T> Parser<T> or(Parser<? extends T>... ps) {
		return Primitives.or(ps);
	}

	/**
	 * Creates a parser that matches a sequence of parsers.
	 * If all parsers in the sequence succeed, it returns a list of their results.
	 *
	 * @param <T> the common supertype of the values produced by the parsers in the sequence
	 * @param ps a list of parsers to match in sequence
	 * @return a parser that returns a list of results from the sequential parsers
	 */
	public static <T> Parser<List<T>> seq(List<? extends Parser<? extends T>> ps) {
		return Primitives.seq(ps);
	}

	/**
	 * Creates a parser that matches a sequence of parsers.
	 * If all parsers in the sequence succeed, it returns a list of their results.
	 *
	 * @param <T> the common supertype of the values produced by the parsers in the sequence
	 * @param ps an array of parsers to match in sequence
	 * @return a parser that returns a list of results from the sequential parsers
	 */
	public static <T> Parser<List<T>> seq(Parser<? extends T>... ps) {
		return Primitives.seq(ps);
	}

	/**
	 * Creates a parser that matches any single character.
	 *
	 * @return a parser that matches any single character
	 */
	public static Parser<String> any() {
		return Primitives.any();
	}

	/**
	 * Creates a parser that performs a positive lookahead.
	 * It succeeds if the given parser {@code p} succeeds, but consumes no input.
	 *
	 * @param p the parser to look ahead with
	 * @return a parser that performs a positive lookahead
	 */
	public static Parser<Void> and(Parser<?> p) {
		return Primitives.and(p);
	}

	/**
	 * Creates a parser that performs a negative lookahead.
	 * It succeeds if the given parser {@code p} fails, but consumes no input.
	 *
	 * @param p the parser to look ahead with
	 * @return a parser that performs a negative lookahead
	 */
	public static Parser<Void> not(Parser<?> p) {
		return Primitives.not(p);
	}

	/**
	 * Creates a parser that optionally matches the given parser.
	 * It always succeeds, returning {@code null} if the parser {@code p} fails.
	 *
	 * @param <T> the type of the value produced by the parser
	 * @param p the parser to optionally match
	 * @return a parser that optionally matches {@code p}
	 */
	public static <T> Parser<T> opt(Parser<? extends T> p) {
		return Primitives.opt(p);
	}

	/**
	 * Creates a parser that repeatedly matches the given parser zero or more times.
	 * It returns a list of all successful matches.
	 *
	 * @param <T> the type of the value produced by the parser
	 * @param p the parser to repeat
	 * @return a parser that repeatedly matches {@code p} zero or more times
	 */
	public static <T> Parser<List<T>> rep(Parser<? extends T> p) {
		return Primitives.rep(p);
	}

	/**
	 * Creates a parser that repeatedly matches the given parser one or more times.
	 * It returns a list of all successful matches.
	 *
	 * @param <T> the type of the value produced by the parser
	 * @param p the parser to repeat
	 * @return a parser that repeatedly matches {@code p} one or more times
	 */
	public static <T> Parser<List<T>> rep1(Parser<? extends T> p) {
		return map((T src1, List<T> src2) -> flist(src2).prepend(src1), p, rep(p));
	}

	/**
	 * Marks a parser as a point for recursion. This is used in conjunction with {@link #recur(RecursionMark)}
	 * to define recursive grammar rules.
	 *
	 * @param <T> the type of the value produced by the parser
	 * @param m the recursion mark
	 * @param p the parser to mark
	 * @return the marked parser
	 */
	public static <T> Parser<T> mark(RecursionMark<T> m, Parser<? extends T> p) {
		return Recursions.mark(m, p);
	}

	/**
	 * Creates a parser that refers to a {@link RecursionMark}. This allows for defining recursive grammars.
	 *
	 * @param <T> the type of the value produced by the parser
	 * @param m the recursion mark
	 * @return a parser that refers to the given recursion mark
	 */
	public static <T> Parser<T> recur(RecursionMark<T> m) {
		return Recursions.recur(m);
	}

	/**
	 * Creates a parser that transforms the result of another parser using a {@link Map1} function.
	 *
	 * @param <From> the type of the value produced by the source parser
	 * @param <To> the type of the value produced by this parser after transformation
	 * @param m the mapping function
	 * @param p the source parser
	 * @return a parser that applies the mapping function to the result of {@code p}
	 */
	public static <From, To> Parser<To> map(
			Map1<From, To> m, Parser<? extends From> p) {
		return Maps.map(m, p);
	}

	/**
	 * Creates a parser that transforms the results of two other parsers using a {@link Map2} function.
	 *
	 * @param <From1> the type of the value produced by the first source parser
	 * @param <From2> the type of the value produced by the second source parser
	 * @param <To> the type of the value produced by this parser after transformation
	 * @param m the mapping function
	 * @param p1 the first source parser
	 * @param p2 the second source parser
	 * @return a parser that applies the mapping function to the results of {@code p1} and {@code p2}
	 */
	public static <From1, From2, To> Parser<To> map(
			Map2<From1, From2, To> m,
			Parser<? extends From1> p1,
			Parser<? extends From2> p2) {
		return Maps.map(m, p1, p2);
	}

	/**
	 * Creates a parser that transforms the results of three other parsers using a {@link Map3} function.
	 *
	 * @param <From1> the type of the value produced by the first source parser
	 * @param <From2> the type of the value produced by the second source parser
	 * @param <From3> the type of the value produced by the third source parser
	 * @param <To> the type of the value produced by this parser after transformation
	 * @param m the mapping function
	 * @param p1 the first source parser
	 * @param p2 the second source parser
	 * @param p3 the third source parser
	 * @return a parser that applies the mapping function to the results of {@code p1}, {@code p2}, and {@code p3}
	 */
	public static <From1, From2, From3, To> Parser<To> map(
			Map3<From1, From2, From3, To> m,
			Parser<? extends From1> p1,
			Parser<? extends From2> p2,
			Parser<? extends From3> p3) {
		return Maps.map(m, p1, p2, p3);
	}

	/**
	 * Creates a parser that applies a predicate to the result of another parser.
	 * The parser succeeds only if the underlying parser succeeds and its result satisfies the predicate.
	 *
	 * @param <T> the type of the value produced by the parser
	 * @param pred the predicate to apply
	 * @param p the parser whose result will be tested
	 * @return a parser that applies a predicate to the result of {@code p}
	 */
	public static <T> Parser<T> cond(
			Predicate<T> pred, Parser<? extends T> p) {
		return Maps.cond(pred, p);
	}

	/**
	 * Creates a parser that maps the result of another parser along with its starting position.
	 *
	 * @param <T> the type of the value produced by the source parser
	 * @param <P> the type of the value produced by this parser after positional mapping
	 * @param positional the positional mapping function
	 * @param p the source parser
	 * @return a parser that applies the positional mapping function to the result of {@code p}
	 */
	public static <T, P> Parser<P> pos(
			Positional<T, P> positional, Parser<? extends T> p) {
		return Maps.pos(positional, p);
	}

	/**
	 * Creates a parser that matches a {@code preceder} parser followed by a {@code p} parser,
	 * returning the result of {@code p} and discarding the result of {@code preceder}.
	 *
	 * @param <T> the type of the value produced by the main parser {@code p}
	 * @param preceder the parser that must precede {@code p}
	 * @param p the main parser whose result is returned
	 * @return a parser that matches {@code preceder} then {@code p}, returning {@code p}'s result
	 */
	public static <T> Parser<T> precededBy(
			Parser<?> preceder, Parser<? extends T> p) {
		checkNotNull(preceder, p);
		return map((Object ignore, T v) -> v, preceder, p);
	}

	/**
	 * Creates a parser that matches a {@code p} parser followed by a {@code follower} parser,
	 * returning the result of {@code p} and discarding the result of {@code follower}.
	 *
	 * @param <T> the type of the value produced by the main parser {@code p}
	 * @param p the main parser whose result is returned
	 * @param follower the parser that must follow {@code p}
	 * @return a parser that matches {@code p} then {@code follower}, returning {@code p}'s result
	 */
	public static <T> Parser<T> followedBy(
			Parser<? extends T> p, Parser<?> follower) {
		checkNotNull(p, follower);
		return map((T v, Object ignore) -> v, p, follower);
	}

	/**
	 * Creates a parser that matches a {@code preceder}, then a {@code p} parser, then a {@code follower},
	 * returning the result of {@code p} and discarding the results of {@code preceder} and {@code follower}.
	 *
	 * @param <T> the type of the value produced by the main parser {@code p}
	 * @param preceder the parser that must precede {@code p}
	 * @param p the main parser whose result is returned
	 * @param follower the parser that must follow {@code p}
	 * @return a parser that matches {@code preceder}, then {@code p}, then {@code follower}, returning {@code p}'s result
	 */
	public static <T> Parser<T> body(
			Parser<?> preceder, Parser<? extends T> p, Parser<?> follower) {
		checkNotNull(preceder, p, follower);
		return map((Object i1, T v, Object i2) -> v, preceder, p, follower);
	}

	/**
	 * Creates a parser that matches two parsers sequentially and returns their results as a {@link Pair}.
	 *
	 * @param <T> the type of the value produced by the first parser
	 * @param <U> the type of the value produced by the second parser
	 * @param p1 the first parser
	 * @param p2 the second parser
	 * @return a parser that returns a {@link Pair} of the results from {@code p1} and {@code p2}
	 */
	public static <T, U> Parser<Pair<T, U>> pair(Parser<? extends T> p1, Parser<? extends U> p2) {
		checkNotNull(p1, p2);
		return map((T e1, U e2) -> Pair.newPair(e1, e2), p1, p2);
	}

	/**
	 * Creates a parser that matches a given parser {@code p} surrounded by optional {@code trimming} parsers.
	 * The results of the trimming parsers are discarded.
	 *
	 * @param trimming the parser for the optional leading and trailing elements (e.g., whitespace)
	 * @param p the main parser whose result is returned
	 * @return a parser that matches {@code p} surrounded by optional {@code trimming}
	 */
	public static Parser<String> trim(Parser<String> trimming, Parser<String> p) {
		checkNotNull(trimming, p);

		Parser<String> t = opt(trimming);
		return body(t, p, t);
	}

	/**
	 * Creates a parser that concatenates the string results of multiple parsers.
	 *
	 * @param ps an array of parsers that produce {@code String} results
	 * @return a parser that concatenates the results of the given parsers
	 */
	public static Parser<String> concat(Parser<String>... ps) {
		return concat(flist(ps));
	}

	/**
	 * Creates a parser that concatenates a list of strings produced by another parser.
	 *
	 * @param p a parser that produces a list of strings
	 * @return a parser that concatenates the list of strings produced by {@code p}
	 */
	public static Parser<String> concat(Parser<? extends List<String>> p) {
		checkNotNull(p);
		return map((List<String> strs) -> String.join("", strs), p);
	}

	/**
	 * Creates a parser that concatenates the string results of a list of parsers.
	 *
	 * @param ps a list of parsers that produce {@code String} results
	 * @return a parser that concatenates the results of the given parsers
	 */
	public static Parser<String> concat(List<? extends Parser<String>> ps) {
		checkNotNull(ps);

		Parser<List<String>> p = seq(ps);
		return concat(p);
	}

	/**
	 * Creates a parser that matches parser {@code p} only if parser {@code ex} does not match at the current position.
	 * This is equivalent to {@code precededBy(not(ex), p)}.
	 *
	 * @param <T> the type of the value produced by the main parser {@code p}
	 * @param ex the parser that must not match
	 * @param p the main parser to match
	 * @return a parser that matches {@code p} only if {@code ex} does not match
	 */
	public static <T> Parser<T> except(Parser<?> ex, Parser<T> p) {
		return precededBy(not(ex), p);
	}

	/**
	 * Creates a parser that matches zero or more occurrences of parser {@code p}, separated by parser {@code sep}.
	 *
	 * @param <T> the type of the value produced by parser {@code p}
	 * @param p the parser for the elements
	 * @param sep the parser for the separator
	 * @return a parser that matches zero or more {@code p}s separated by {@code sep}
	 */
	public static <T> Parser<List<T>> sepBy(
			Parser<? extends T> p, Parser<?> sep) {
		if (p == null || sep == null) throw nullArgEx();
		Map1<Void, List<T>> toEmpty = v -> flist();
		Parser<List<T>> empty = map(toEmpty, not(p));

		return or(empty, sepBy1(p, sep));
	}

	/**
	 * Creates a parser that matches one or more occurrences of parser {@code p}, separated by parser {@code sep}.
	 *
	 * @param <T> the type of the value produced by parser {@code p}
	 * @param p the parser for the elements
	 * @param sep the parser for the separator
	 * @return a parser that matches one or more {@code p}s separated by {@code sep}
	 */
	public static <T> Parser<List<T>> sepBy1(
			Parser<? extends T> p, Parser<?> sep) {
		if (p == null || sep == null) throw nullArgEx();

		Parser<T> q = precededBy(sep, p);
		Parser<List<T>> qs = rep(q);

		return map((T v1, List<T> v2) -> cons(v1, flist(v2)), p, qs);
	}

	/**
	 * Parses a string expression into a {@code Parser<String>}.
	 * This method uses an internal grammar to construct a parser from a string representation.
	 *
	 * @param expr the string expression to parse
	 * @return a {@code Parser<String>} built from the expression
	 * @throws IllegalArgumentException if the expression is invalid or cannot be parsed
	 */
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
