package jp.nhiguchi.libs.pcom;

import static jp.nhiguchi.libs.pcom.Parser.*;

/**
 *
 * @author Naoshi HIGUCHI
 */
final class PosFunctor<T, P> implements ParseFunctor<P> {
	private final Positional<T, P> fPositional;
	private final Parser<? extends T> fP;

	PosFunctor(Positional<T, P> positional, Parser<? extends T> p) {
		fPositional = positional;
		fP = p;
	}

	public Result<P> parse(Context c, Position p) {
		Result<? extends T> r = c.apply(fP, p);
		if (r.isFail()) return fail(c, p, r.error());

		try {
			P pval = fPositional.map(r.value(), p.asInt());
			return Result.success(pval, r.rest());
		} catch (MappingException e) {
			return fail(c, p);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof PosFunctor)) return false;

		PosFunctor rhs = (PosFunctor) obj;
		return fPositional.equals(rhs.fPositional) && fP.equals(rhs.fP);
	}

	@Override
	public int hashCode() {
		return fPositional.hashCode() + fP.hashCode();
	}

	@Override
	public String toString() {
		return "pos(" + fPositional.toString() + ", " + fP.toString() + ")";
	}
}
