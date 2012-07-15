package jp.nhiguchi.libs.pcom;

import java.util.*;

/**
 *
 * @author naoshi
 */
public final class Parser<T> {
	private final ParseFunctor<T> fFunctor;

	private Parser(ParseFunctor<T> functor) {
		fFunctor = functor;
	}

	static <T> Parser<T> parser(ParseFunctor<T> functor) {
		return new Parser(functor);
	}

	static final class Context {
		private final Parser fOwner;
		private final Memo fMemo;

		private Context(Parser owner, Memo memo) {
			fOwner = owner;
			fMemo = memo;
		}

		final <S> Result<S> apply(Parser<S> p, Position pos) {
			Arg a = new Arg(pos, fMemo);
			Ret<S> r = p.doParse(a);
			return r.fResult;
		}
	}

	static interface ParseFunctor<T> {
		Result<T> parse(Context c, Position p);
	}

	private static final class Cache<E> {
		private final Map<Position, Map<Parser, E>> fMap = new HashMap();

		private <T> void put(Parser<T> p, Position pos, E value) {
			Map<Parser, E> map = fMap.get(pos);
			if (map == null) {
				map = new HashMap();
				fMap.put(pos, map);
			}
			map.put(p, value);
		}

		private <T> E get(Parser<T> p, Position pos) {
			Map<Parser, E> map = fMap.get(pos);
			if (map == null) return null;

			return (E) map.get(p);
		}
	}

	private static final class Memo {
		private final Cache<Result> fCache = new Cache();

		private <T> void put(Parser<T> p, Position pos, Result value) {
			fCache.put(p, pos, value);
		}

		private <T> Result<T> get(Parser<T> p, Position pos) {
			return (Result<T>) fCache.get(p, pos);
		}
	}

	private static final class Arg {
		private final Position fPos;
		private final Memo fMemo;

		private Arg(Position pos, Memo memo) {
			fPos = pos;
			fMemo = memo;
		}
	}

	private static final class Ret<T> {
		private final Result<T> fResult;

		private Ret(Result result) {
			fResult = result;
		}
	}

	private Ret<T> getCache(Arg arg) {
		Position pos = arg.fPos;

		Result<T> m = arg.fMemo.get(this, pos);
		if (m != null)
			return new Ret(m);

		return null;
	}

	private Ret<T> doParse(Arg arg) {
		Ret<T> ret = getCache(arg);
		if (ret != null) return ret;

		Position pos = arg.fPos;

		Context c = new Context(this, arg.fMemo);

		Result<T> r = fFunctor.parse(c, pos);
		arg.fMemo.put(this, pos, r);

		return new Ret(r);
	}

	static <T> Result<T> fail(Context c, Position p, Result.Error err) {
		return Result.fail(c.fOwner, p, err);
	}

	static <T> Result<T> fail(Context c, Position p) {
		return Result.fail(c.fOwner, p);
	}

	static <T> Result<T> fail(Context c, Position p, List<Result.Error> errors) {
		return Result.fail(c.fOwner, p, errors);
	}

	Result<T> parse(Source s) {
		Arg arg = new Arg(Position.startOf(s), new Memo());

		Ret<T> r = doParse(arg);
		return r.fResult;
	}

	public Result<T> parse(String str) {
		return parse(Source.source(str));
	}

	public Result<T> parse(Readable r) {
		return parse(Source.source(r));
	}

	public Result<T> parse(Position pos) {
		Arg arg = new Arg(pos, new Memo());

		Ret<T> r = doParse(arg);
		return r.fResult;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof Parser)) return false;

		Parser rhs = (Parser) obj;
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
