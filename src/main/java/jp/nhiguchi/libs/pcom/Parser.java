package jp.nhiguchi.libs.pcom;

import java.util.*;

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
		return Result.fail(c.fOwner, p, err);
	}

	static <T> Result<T> fail(Context c, Position p) {
		return Result.fail(c.fOwner, p);
	}

	static <T> Result<T> fail(Context c, Position p, List<Result.Error<?>> errors) {
		return Result.fail(c.fOwner, p, errors);
	}

	Result<T> parse(Source s) {
		Arg arg = new Arg(Position.startOf(s), new Memo());
		return doParse(arg);
	}

	public Result<T> parse(String str) {
		return parse(Source.source(str));
	}

	public Result<T> parse(Readable r) {
		return parse(Source.source(r));
	}

	public Result<T> parse(Position pos) {
		Arg arg = new Arg(pos, new Memo());
		return doParse(arg);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof Parser<?> rhs)) return false;
		return fFunctor.equals(rhs.fFunctor);
	}

	@Override
	public int hashCode() {
		return fFunctor.hashCode();
	}

	@Override
	public String toString() {
		return fFunctor.toString();
	}
}
