package jp.nhiguchi.libs.pcom;

import static jp.nhiguchi.libs.pcom.Parser.*;

/**
 *
 * @author Naoshi HIGUCHI
 */
final class StringFunctor implements ParseFunctor<String> {
	private final String fStr;

	StringFunctor(String str) {
		assert (str != null);
		fStr = str;
	}

	public Result<String> parse(Context c, Position p) {
		if (!p.startsWith(fStr)) {
			return fail(c, p);
		}

		Position next = p.next(fStr.length());
		return Result.success(fStr, next);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof StringFunctor)) return false;

		StringFunctor rhs = (StringFunctor) obj;
		return fStr.equals(rhs.fStr);
	}

	@Override
	public int hashCode() {
		return fStr.hashCode();
	}

	@Override
	public String toString() {
		return "string(\"" + fStr + "\")";
	}
}
