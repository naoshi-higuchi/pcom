package jp.nhiguchi.libs.pcom;

import static jp.nhiguchi.libs.pcom.Parser.*;

/**
 *
 * @author naoshi
 */
final class OptFunctor<T> implements ParseFunctor<T> {
	private Parser<? extends T> fP;

	OptFunctor(Parser<? extends T> p) {
		fP = p;
	}

	public Result<T> parse(Context c, Position p) {
		Result<? extends T> r = c.apply(fP, p);
		if (r.isFail()) return Result.success(null, p);

		return Result.success(r.value(), r.rest());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof OptFunctor)) return false;

		OptFunctor rhs = (OptFunctor) obj;
		return fP.equals(rhs.fP);
	}

	@Override
	public int hashCode() {
		return fP.hashCode();
	}

	@Override
	public String toString() {
		return "opt(" + fP.toString() + ")";
	}
}
