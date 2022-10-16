package jp.nhiguchi.libs.pcom;

import static jp.nhiguchi.libs.pcom.Parser.*;

/**
 *
 * @author Naoshi HIGUCHI
 */
final class Recursions {
	private Recursions() {
	}

	static <T> Parser<T> mark(RecursionMark<T> m, Parser<? extends T> p) {
		Parser<T> par = parser(new MarkFunctor(m, p));
		m.init(par);
		return par;
	}

	static <T> Parser<T> recur(RecursionMark<T> m) {
		return parser(new LazyFunctor(m));
	}
}
