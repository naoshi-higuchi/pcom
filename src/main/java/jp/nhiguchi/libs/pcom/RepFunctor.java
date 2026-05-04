package jp.nhiguchi.libs.pcom;

import java.util.List;

import jp.nhiguchi.libs.flist.FList;
import static jp.nhiguchi.libs.flist.FList.*;

import static jp.nhiguchi.libs.pcom.Parser.*;

final class RepFunctor<T> implements ParseFunctor<List<T>> {
	private Parser<? extends T> fP;

	RepFunctor(Parser<? extends T> p) {
		fP = p;
	}

	@SuppressWarnings("unchecked")
	public Result<List<T>> parse(Context c, Position p) {
		FList<T> res = flist();
		Result<? extends T> r;
		Position rest = p;

		while (true) {
			r = c.apply(fP, rest);
			if (r.isFail()) break;

			res = cons(r.value(), res);
			rest = r.rest();
		}

		// safe: FList<T>.reverse() returns List<T> at runtime despite raw return type
		return Result.success((List<T>) res.reverse(), rest);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof RepFunctor<?> rhs)) return false;
		return fP.equals(rhs.fP);
	}

	@Override
	public int hashCode() {
		return fP.hashCode();
	}

	@Override
	public String toString() {
		return "rep(" + fP.toString() + ")";
	}
}
