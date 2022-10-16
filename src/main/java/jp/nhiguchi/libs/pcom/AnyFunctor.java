package jp.nhiguchi.libs.pcom;

import static jp.nhiguchi.libs.pcom.Parser.*;

/**
 *
 * @author Naoshi HIGUCHI
 */
final class AnyFunctor implements ParseFunctor<String> {
	private static AnyFunctor fSingleton = new AnyFunctor();

	private AnyFunctor() {
	}

	static AnyFunctor getInstance() {
		return fSingleton;
	}

	public Result<String> parse(Context c, Position p) {
		String val = p.head();
		if (val == null) return fail(c, p);

		return Result.success(val, p.next(1));
	}

	@Override
	public String toString() {
		return "any()";
	}
}
