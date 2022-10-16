package jp.nhiguchi.libs.pcom;

import java.util.function.Function;

import static jp.nhiguchi.libs.pcom.Parser.*;

/**
 *
 * @author naoshi
 */
class FunctionFunctor<From, To> implements ParseFunctor<To> {
	private final Function<From, To> fM;
	private final Parser<? extends From> fP;

	FunctionFunctor(Function<From, To> m, Parser<? extends From> p) {
		fM = m;
		fP = p;
	}

	public Result<To> parse(Context c, Position p) {
		Result<? extends From> r = c.apply(fP, p);
		if (r.isFail()) return fail(c, p, r.error());

		try {
			To res = fM.apply(r.value());
			return Result.success(res, r.rest());
		} catch (MappingException e) {
			return fail(c, p);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof FunctionFunctor)) return false;

		FunctionFunctor rhs = (FunctionFunctor) obj;
		return fM.equals(rhs.fM) && fP.equals(rhs.fP);
	}

	@Override
	public int hashCode() {
		return fM.hashCode() + fP.hashCode();
	}

	@Override
	public String toString() {
		return "map(" + fM.toString() + ", " + fP.toString() + ")";
	}
}
