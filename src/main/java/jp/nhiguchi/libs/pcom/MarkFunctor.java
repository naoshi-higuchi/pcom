package jp.nhiguchi.libs.pcom;

import static jp.nhiguchi.libs.pcom.Parser.*;

/**
 *
 * @author Naoshi HIGUCHI
 */
final class MarkFunctor<T> implements ParseFunctor<T> {
	private final Parser<T> fP;
	private final RecursionMark fMark;

	MarkFunctor(RecursionMark m, Parser<T> p) {
		fP = p;
		fMark = m;
	}

	public Result<T> parse(Context c, Position p) {
		Result<T> r = c.apply(fP, p);
		if (r.isSuccess()) return r;

		return fail(c, p, r.error());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof MarkFunctor)) return false;

		MarkFunctor rhs = (MarkFunctor) obj;
		return fMark.equals(rhs.fMark) && fP.equals(rhs.fP);
	}

	@Override
	public int hashCode() {
		return fMark.hashCode() + fP.hashCode();
	}

	@Override
	public String toString() {
		return "mark(" + fMark.toString() + ", " + fP.toString() + ")";
	}
}
