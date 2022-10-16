package jp.nhiguchi.libs.pcom;

import java.util.*;

import jp.nhiguchi.libs.flist.FList;
import static jp.nhiguchi.libs.flist.FList.*;

import static jp.nhiguchi.libs.pcom.Parser.*;

/**
 *
 * @author Naoshi HIGUCHI
 */
final class OrFunctor<T> implements ParseFunctor<T> {
	private final FList<Parser<? extends T>> fPs;

	OrFunctor(List<Parser<? extends T>> ps) {
		fPs = flist(ps);
	}

	public Result<T> parse(Context c, Position p) {
		FList<Result.Error> causes = flist();

		for (Parser<? extends T> par : fPs) {
			Result<? extends T> r = c.apply(par, p);
			if (r.isSuccess()) {
				return (Result<T>) r;
			}
			causes = cons(r.error(), causes);
		}

		return fail(c, p, causes.reverse());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof OrFunctor)) return false;

		OrFunctor rhs = (OrFunctor) obj;
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
		for (Parser p : fPs) {
			if (sb.length() != head.length()) sb.append(", ");

			sb.append(p.toString());
		}

		sb.append(")");
		return sb.toString();
	}
}
