package jp.nhiguchi.libs.pcom;

import static jp.nhiguchi.libs.pcom.Parser.*;

/**
 *
 * @author naoshi
 */
final class AndFunctor implements ParseFunctor<Void> {
	private Parser<?> fP;

	AndFunctor(Parser<?> p) {
		fP = p;
	}

	public Result<Void> parse(Context c, Position p) {
		Result<?> r = c.apply(fP, p);
		if (r.isFail()) return fail(c, p, r.error());

		return Result.success(null, p);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof AndFunctor)) return false;

		AndFunctor rhs = (AndFunctor) obj;
		return fP.equals(rhs.fP);
	}

	@Override
	public int hashCode() {
		return fP.hashCode();
	}

	@Override
	public String toString() {
		return "and(" + fP.toString() + ")";
	}
}
