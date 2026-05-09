package jp.nhiguchi.libs.pcom;

import java.util.*;
import java.util.Set;

import jp.nhiguchi.libs.flist.*;
import static jp.nhiguchi.libs.flist.FList.*;

import static jp.nhiguchi.libs.pcom.Parser.*;

final class SeqFunctor<T> implements ParseFunctor<List<T>> {
	private final FList<Parser<? extends T>> fPs;

	SeqFunctor(List<? extends Parser<? extends T>> ps) {
		fPs = flist(ps);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Result<List<T>> parse(Context c, Position p) {
		FList<T> vs = flist();

		Position rest = p;
		for (Parser<? extends T> par : fPs) {
			Result<? extends T> r = c.apply(par, rest);
			if (r.isFail()) {
				return fail(c, p, r.error());
			}

			T value = r.value();
			vs = cons(value, vs);
			rest = r.rest();
		}

		// safe: FList<T>.reverse() returns List<T> at runtime despite raw return type
		return Result.success((List<T>) vs.reverse(), rest);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof SeqFunctor<?> rhs)) return false;
		return fPs.equals(rhs.fPs);
	}

	@Override
	public int hashCode() {
		return fPs.hashCode();
	}

	@Override
	public String toString() {
		String head = "seq(";
		StringBuilder sb = new StringBuilder(head);
		for (Parser<?> p : fPs) {
			if (sb.length() != head.length()) sb.append(", ");
			sb.append(p.toString());
		}
		sb.append(")");
		return sb.toString();
	}
}
