package jp.nhiguchi.libs.pcom;

import java.util.*;
import java.util.Set;
import java.util.Collections;

/**
 * Represents a parser that attempts to match a sequence of characters from an input
 * source and transform it into a value of type {@code T}.
 * <p>
 * {@code Parser} instances are immutable and value-typed. Two parsers are considered
 * equal if their internal structure (the {@link ParseFunctor} they wrap) is equal.
 * <p>
 * This library implements Parsing Expression Grammars (PEG) and uses packrat memoization
 * to ensure that each {@code (parser, position)} pair is evaluated at most once
 * per {@code parse()} call, which guarantees linear time complexity for many grammars.
 *
 * @param <T> The type of the value produced by this parser upon successful parsing.
 */
public final class Parser<T> {
	private final ParseFunctor<T> fFunctor;

	private Parser(ParseFunctor<T> functor) {
		fFunctor = functor;
	}

	static <T> Parser<T> parser(ParseFunctor<T> functor) {
		return new Parser<>(functor);
	}

	static final class Context {
		private final Parser<?> fOwner;
		private final Memo fMemo;

		private Context(Parser<?> owner, Memo memo) {
			fOwner = owner;
			fMemo = memo;
		}

		final <S> Result<S> apply(Parser<S> p, Position pos) {
			Arg a = new Arg(pos, fMemo);
			return p.doParse(a);
		}
	}

	@FunctionalInterface
	interface ParseFunctor<T> {
		Result<T> parse(Context c, Position p);
	}

	private static final class Cache<E> {
		private final Map<Position, Map<Parser<?>, E>> fMap = new HashMap<>();

		private void put(Parser<?> p, Position pos, E value) {
			Map<Parser<?>, E> map = fMap.get(pos);
			if (map == null) {
				map = new HashMap<>();
				fMap.put(pos, map);
			}
			map.put(p, value);
		}

		private E get(Parser<?> p, Position pos) {
			Map<Parser<?>, E> map = fMap.get(pos);
			if (map == null) return null;
			return map.get(p);
		}
	}

	private static final class Memo {
		private final Cache<Result<?>> fCache = new Cache<>();

		private <T> void put(Parser<?> p, Position pos, Result<T> value) {
			fCache.put(p, pos, value);
		}

		@SuppressWarnings("unchecked")
		private <T> Result<T> get(Parser<?> p, Position pos) {
			// heterogeneous container: safe because we always put Result<T> under Parser<T>
			return (Result<T>) fCache.get(p, pos);
		}
	}

	private record Arg(Position pos, Memo memo) {}

	private Result<T> getCache(Arg arg) {
		return arg.memo().get(this, arg.pos());
	}

	private Result<T> doParse(Arg arg) {
		Result<T> ret = getCache(arg);
		if (ret != null) return ret;

		Position pos = arg.pos();
		Context c = new Context(this, arg.memo());

		Result<T> r = fFunctor.parse(c, pos);
		arg.memo().put(this, pos, r);

		return r;
	}

	static <T> Result<T> fail(Context c, Position p, Result.Error<?> err) {
		return Result.fail(c.fOwner, p, err, err.expectedTokens());
	}

	static <T> Result<T> fail(Context c, Position p) {
		return Result.fail(c.fOwner, p, Collections.emptySet());
	}

	static <T> Result<T> fail(Context c, Position p, List<Result.Error<?>> errors) {
		Set<String> expectedTokens = new HashSet<>();
		for (Result.Error<?> error : errors) {
			expectedTokens.addAll(error.expectedTokens());
		}
		return Result.fail(c.fOwner, p, errors, expectedTokens);
	}

	static <T> Result<T> fail(Context c, Position p, Set<String> expectedTokens) {
		return Result.fail(c.fOwner, p, expectedTokens);
	}

	Result<T> parse(Source s) {
		Arg arg = new Arg(Position.startOf(s), new Memo());
		return doParse(arg);
	}

	/**
	 * Attempts to parse the given string input using this parser.
	 *
	 * @param str The string input to parse.
	 * @return A {@link Result} indicating either success with the parsed value and
	 *         remaining input position, or failure with error details.
	 */
	public Result<T> parse(String str) {
		return parse(Source.source(str));
	}

	/**
	 * Attempts to parse the input from the given {@link Readable} source using this parser.
	 *
	 * @param r The {@link Readable} source to parse.
	 * @return A {@link Result} indicating either success with the parsed value and
	 *         remaining input position, or failure with error details.
	 */
	public Result<T> parse(Readable r) {
		return parse(Source.source(r));
	}

	/**
	 * Attempts to parse starting from a specific {@link Position} in an input source.
	 * This is useful for continuing a parse from a known point or for internal recursive calls.
	 *
	 * @param pos The starting {@link Position} in the input source.
	 * @return A {@link Result} indicating either success with the parsed value and
	 *         remaining input position, or failure with error details.
	 */
	public Result<T> parse(Position pos) {
		Arg arg = new Arg(pos, new Memo());
		return doParse(arg);
	}

	/**
	 * Compares this parser to the specified object. The result is {@code true} if and only if
	 * the argument is not {@code null} and is a {@code Parser} object that represents the same
	 * internal parsing logic (i.e., their {@link ParseFunctor}s are equal).
	 *
	 * @param obj The object to compare this {@code Parser} against.
	 * @return {@code true} if the given object represents a {@code Parser} equivalent to this parser,
	 *         {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof Parser<?> rhs)) return false;
		return fFunctor.equals(rhs.fFunctor);
	}

	/**
	 * Returns a hash code for this parser. The hash code is computed based on the
	 * hash code of its internal {@link ParseFunctor}.
	 *
	 * @return A hash code value for this object.
	 */
	@Override
	public int hashCode() {
		return fFunctor.hashCode();
	}

	/**
	 * Returns a string representation of this parser. The string representation
	 * is delegated to its internal {@link ParseFunctor}.
	 *
	 * @return A string representation of this object.
	 */
	@Override
	public String toString() {
		return fFunctor.toString();
	}
}
