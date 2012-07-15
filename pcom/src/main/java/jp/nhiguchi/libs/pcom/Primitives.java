package jp.nhiguchi.libs.pcom;

import java.util.List;

import static jp.nhiguchi.libs.flist.FList.*;

import static jp.nhiguchi.libs.pcom.Parser.*;

/**
 *
 * @author naoshi
 */
final class Primitives {
	private Primitives() {
	}

	static Parser<String> string(String str) {
		return parser(new StringFunctor(str));
	}

	static <T> Parser<T> or(List<? extends Parser<? extends T>> ps) {
		return parser(new OrFunctor(ps));
	}

	static <T> Parser<T> or(Parser<? extends T>... ps) {
		return or(flist(ps));
	}

	static <T> Parser<List<T>> seq(List<? extends Parser<? extends T>> ps) {
		return parser(new SeqFunctor(ps));
	}

	static <T> Parser<List<T>> seq(Parser<? extends T>... ps) {
		return seq(flist(ps));
	}

	static Parser<String> any() {
		return parser(AnyFunctor.getInstance());
	}

	static Parser<Void> and(Parser<?> p) {
		return parser(new AndFunctor(p));
	}

	static Parser<Void> not(Parser<?> p) {
		return parser(new NotFunctor(p));
	}

	static <T> Parser<T> opt(Parser<? extends T> p) {
		return parser(new OptFunctor(p));
	}

	static <T> Parser<List<T>> rep(Parser<? extends T> p) {
		return parser(new RepFunctor(p));
	}
}
