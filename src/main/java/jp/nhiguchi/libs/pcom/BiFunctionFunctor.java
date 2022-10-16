package jp.nhiguchi.libs.pcom;

import java.util.function.BiFunction;

import static jp.nhiguchi.libs.pcom.Parser.*;

/**
 *
 * @author naoshi
 */
final class BiFunctionFunctor<From1, From2, To> implements ParseFunctor<To> {
	private final BiFunction<From1, From2, To> fM;
	private final Parser<? extends From1> fP1;
	private final Parser<? extends From2> fP2;

	BiFunctionFunctor(BiFunction<From1, From2, To> m,
					  Parser<? extends From1> p1, Parser<? extends From2> p2) {
		fM = m;
		fP1 = p1;
		fP2 = p2;
	}

	public Result<To> parse(Context c, Position p) {
		Result<? extends From1> r1 = c.apply(fP1, p);
		if (r1.isFail()) return fail(c, p, r1.error());

		Result<? extends From2> r2 = c.apply(fP2, r1.rest());
		if (r2.isFail()) return fail(c, p, r2.error());

		try {
			To res = fM.apply(r1.value(), r2.value());
			return Result.success(res, r2.rest());
		} catch (MappingException e) {
			return fail(c, p);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof BiFunctionFunctor)) return false;

		BiFunctionFunctor rhs = (BiFunctionFunctor) obj;
		return fM.equals(rhs.fM)
				&& fP1.equals(rhs.fP1)
				&& fP2.equals(rhs.fP2);
	}

	@Override
	public int hashCode() {
		return fM.hashCode() + fP1.hashCode() + fP2.hashCode();
	}

	@Override
	public String toString() {
		return new StringBuilder("map(")
				.append(fM.toString())
				.append(", ")
				.append(fP1.toString())
				.append(", ")
				.append(fP2.toString())
				.append(")")
				.toString();
	}
}
