package jp.nhiguchi.libs.pcom;

import java.util.*;

import jp.nhiguchi.libs.flist.FList;
import static jp.nhiguchi.libs.flist.FList.*;

import static jp.nhiguchi.libs.pcom.Parser.*;

final class OrFunctor<T> implements ParseFunctor<T> {
	private final FList<Parser<? extends T>> fPs;

	OrFunctor(List<? extends Parser<? extends T>> ps) {
		fPs = flist(ps);
	}

	@SuppressWarnings("unchecked")
	public Result<T> parse(Context c, Position p) {
		FList<Result.Error<?>> causes = flist();

		for (Parser<? extends T> par : fPs) {
			Result<? extends T> r = c.apply(par, p);
			if (r.isSuccess()) {
				// safe: Result<? extends T> is-a Result<T> by covariance at runtime
				return (Result<T>) r;
			}
			Result.Error<?> err = r.error();
			causes = cons(err, causes);
		}

		return fail(c, p, causes.reverse());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof OrFunctor<?> rhs)) return false;
		return fPs.equals(rhs.fPs);
	}

	@Override
	public int hashCode() {
		return fPs.hashCode();
	}

	@Override
	public String toString() {
		String head = "or(";
		StringBuilder sb = new StringBuilder(head);
		for (Parser<?> p : fPs) {
			if (sb.length() != head.length()) sb.append(", ");
			sb.append(p.toString());
		}
		sb.append(")");
		return sb.toString();
	}
}
