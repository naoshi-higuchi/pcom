package jp.nhiguchi.libs.pcom;

import static jp.nhiguchi.libs.pcom.Parser.*;

/**
 *
 * @author Naoshi HIGUCHI
 */
final class LazyFunctor<T> implements ParseFunctor<T> {
	private final RecursionMark<T> fMark;

	LazyFunctor(RecursionMark<T> mark) {
		fMark = mark;
	}

	public Result<T> parse(Context c, Position p) {
		Result<? extends T> r = c.apply(fMark.get(), p);
		if (r.isSuccess())
			return Result.success(r.value(), r.rest());

		return fail(c, p, r.error());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof LazyFunctor)) return false;

		LazyFunctor rhs = (LazyFunctor) obj;
		return fMark.equals(rhs.fMark);
	}

	@Override
	public int hashCode() {
		return fMark.hashCode();
	}

	@Override
	public String toString() {
		return "recur(" + fMark.toString() + ")";
	}
}
