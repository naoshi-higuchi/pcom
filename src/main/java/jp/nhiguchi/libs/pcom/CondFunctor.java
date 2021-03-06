package jp.nhiguchi.libs.pcom;

import jp.nhiguchi.libs.pcom.MappingException;
import jp.nhiguchi.libs.pcom.Parser;
import jp.nhiguchi.libs.pcom.Position;
import jp.nhiguchi.libs.pcom.Predicate;
import jp.nhiguchi.libs.pcom.Result;
import static jp.nhiguchi.libs.pcom.Parser.*;

/**
 *
 * @author naoshi
 */
final class CondFunctor<T> implements ParseFunctor<T> {
	private final Predicate<T> fPred;
	private final Parser<? extends T> fP;

	CondFunctor(Predicate<T> pred, Parser<? extends T> p) {
		fPred = pred;
		fP = p;
	}

	public Result<T> parse(Context c, Position p) {
		Result<? extends T> r = c.apply(fP, p);
		if (r.isFail()) return fail(c, p, r.error());

		try {
			if (fPred.eval(r.value()))
				return Result.success(r.value(), r.rest());

			return fail(c, p);
		} catch (MappingException e) {
			return fail(c, p);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof CondFunctor)) return false;

		CondFunctor rhs = (CondFunctor) obj;
		return fPred.equals(rhs.fPred) && fP.equals(rhs.fP);
	}

	@Override
	public int hashCode() {
		return fPred.hashCode() + fP.hashCode();
	}

	@Override
	public String toString() {
		return "cond(" + fPred.toString() + ", " + fP.toString() + ")";
	}
}
