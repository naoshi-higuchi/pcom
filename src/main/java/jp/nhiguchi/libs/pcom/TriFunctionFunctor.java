package jp.nhiguchi.libs.pcom;

import static jp.nhiguchi.libs.pcom.Parser.*;

/**
 *
 * @author Naoshi HIGUCHI
 */
final class TriFunctionFunctor<From1, From2, From3, To> implements ParseFunctor<To> {
	private final TriFunction<From1, From2, From3, To> fM;
	private final Parser<? extends From1> fP1;
	private final Parser<? extends From2> fP2;
	private final Parser<? extends From3> fP3;

	TriFunctionFunctor(TriFunction<From1, From2, From3, To> m,
					   Parser<? extends From1> p1,
					   Parser<? extends From2> p2,
					   Parser<? extends From3> p3) {
		fM = m;
		fP1 = p1;
		fP2 = p2;
		fP3 = p3;
	}

	public Result<To> parse(Context c, Position p) {
		Result<? extends From1> r1 = c.apply(fP1, p);
		if (r1.isFail()) return fail(c, p, r1.error());

		Result<? extends From2> r2 = c.apply(fP2, r1.rest());
		if (r2.isFail()) return fail(c, p, r2.error());

		Result<? extends From3> r3 = c.apply(fP3, r2.rest());
		if (r3.isFail()) return fail(c, p, r3.error());

		try {
			To res = fM.apply(r1.value(), r2.value(), r3.value());
			return Result.success(res, r3.rest());
		} catch (MappingException e) {
			return fail(c, p);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof TriFunctionFunctor)) return false;

		TriFunctionFunctor rhs = (TriFunctionFunctor) obj;
		return fM.equals(rhs.fM)
				&& fP1.equals(rhs.fP1)
				&& fP2.equals(rhs.fP2)
				&& fP3.equals(rhs.fP3);
	}

	@Override
	public int hashCode() {
		return fM.hashCode()
				+ fP1.hashCode() + fP2.hashCode() + fP3.hashCode();
	}

	@Override
	public String toString() {
		return new StringBuilder("map(")
				.append(fM.toString())
				.append(", ")
				.append(fP1.toString())
				.append(", ")
				.append(fP2.toString())
				.append(", ")
				.append(fP3.toString())
				.append(")")
				.toString();
	}
}
