package jp.nhiguchi.libs.pcom;

import java.util.Set;

import static jp.nhiguchi.libs.pcom.Parser.*;

final class NotFunctor implements ParseFunctor<Void> {
	private Parser<?> fP;

	NotFunctor(Parser<?> p) {
		fP = p;
	}

	public Result<Void> parse(Context c, Position p) {
		Result<?> r = c.apply(fP, p);
		if (r.isSuccess()) return fail(c, p);

		return Result.success(null, p);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof NotFunctor rhs)) return false;
		return fP.equals(rhs.fP);
	}

	@Override
	public int hashCode() {
		return fP.hashCode();
	}

	@Override
	public String toString() {
		return "not(" + fP.toString() + ")";
	}
}
