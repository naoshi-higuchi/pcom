package jp.nhiguchi.libs.pcom;

import java.util.function.BiFunction;
import java.util.function.Function;

import static jp.nhiguchi.libs.pcom.Parser.*;

/**
 *
 * @author naoshi
 */
final class Maps {
	static <From, To> Parser<To> map(
			Function<From, To> m, Parser<? extends From> p) {
		return parser(new FunctionFunctor(m, p));
	}

	static <From1, From2, To> Parser<To> map(
			BiFunction<From1, From2, To> m,
			Parser<? extends From1> p1, Parser<? extends From2> p2) {
		return parser(new BiFunctionFunctor(m, p1, p2));
	}

	static <From1, From2, From3, To> Parser<To> map(
			TriFunction<From1, From2, From3, To> m,
			Parser<? extends From1> p1,
			Parser<? extends From2> p2,
			Parser<? extends From3> p3) {
		return parser(new TriFunctionFunctor(m, p1, p2, p3));
	}

	static <T> Parser<T> cond(Predicate<T> pred, Parser<? extends T> p) {
		return parser(new CondFunctor(pred, p));
	}

	static <T, P> Parser<P> pos(
			Positional<T, P> positional, Parser<? extends T> p) {
		return parser(new PosFunctor(positional, p));
	}
}
