package jp.nhiguchi.libs.pcom;

import jp.nhiguchi.libs.pcom.CondFunctor;
import static jp.nhiguchi.libs.pcom.Parser.*;

/**
 *
 * @author naoshi
 */
final class Maps {
	static <From, To> Parser<To> map(
			Map1<From, To> m, Parser<? extends From> p) {
		return parser(new Map1Functor(m, p));
	}

	static <From1, From2, To> Parser<To> map(
			Map2<From1, From2, To> m,
			Parser<? extends From1> p1, Parser<? extends From2> p2) {
		return parser(new Map2Functor(m, p1, p2));
	}

	static <From1, From2, From3, To> Parser<To> map(
			Map3<From1, From2, From3, To> m,
			Parser<? extends From1> p1,
			Parser<? extends From2> p2,
			Parser<? extends From3> p3) {
		return parser(new Map3Functor(m, p1, p2, p3));
	}

	static <T> Parser<T> cond(Predicate<T> pred, Parser<? extends T> p) {
		return parser(new CondFunctor(pred, p));
	}

	static <T, P> Parser<P> pos(
			Positional<T, P> positional, Parser<? extends T> p) {
		return parser(new PosFunctor(positional, p));
	}
}
