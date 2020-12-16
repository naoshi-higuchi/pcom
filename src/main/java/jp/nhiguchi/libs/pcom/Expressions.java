package jp.nhiguchi.libs.pcom;

import java.util.List;

import static jp.nhiguchi.libs.pcom.Parsers.*;

import static jp.nhiguchi.libs.flist.FList.*;
import jp.nhiguchi.libs.tuple.*;

/**
 *
 * @author naoshi
 */
final class Expressions {
	private static Parser<String> eol() {
		return or(string("\r\n"), string("\n"), string("\r"));
	}

	private static Parser<String> space() {
		return or(string(" "), string("\t"), eol());
	}

	private static Parser<String> comment() {
		Parser<String> ns = string("#");
		Parser<String> nonEOL = except(eol(), any());
		Parser<String> nonEOLs = concat(rep(nonEOL));

		return concat(seq(ns, nonEOLs, eol()));
	}

	private static Parser<String> spacing() {
		return concat(rep(or(space(), comment())));
	}

	private static Parser<String> spacing(String str) {
		return followedBy(string(str), spacing());
	}

	private static Parser<String> slash() {
		return spacing("/");
	}

	private static Parser<String> amp() {
		return spacing("&");
	}

	private static Parser<String> excl() {
		return spacing("!");
	}

	private static Parser<String> question() {
		return spacing("?");
	}

	private static Parser<String> star() {
		return spacing("*");
	}

	private static Parser<String> plus() {
		return spacing("+");
	}

	private static Parser<String> open() {
		return spacing("(");
	}

	private static Parser<String> close() {
		return spacing(")");
	}

	private static Parser<String> dot() {
		return spacing(".");
	}

	private static Parser<Character> ch() {
		Map1<String, Character> toChar = new Map1<String, Character>() {
			public Character map(String src) {
				if (src.length() != 1) throw new MappingException();
				return src.charAt(0);
			}
		};

		return map(toChar, chStr());
	}

	private static Parser<String> chStr() {
		Parser<String> backSlash = string("\\");
		return precededBy(opt(backSlash), any());
	}

	private static Parser<String> range(final char from, final char to) {
		Predicate<Character> inRange = new Predicate<Character>() {
			public boolean eval(Character src) {
				return from <= src && src <= to;
			}
		};

		Map1<Character, String> toStr = new Map1<Character, String>() {
			public String map(Character src) {
				return src.toString();
			}
		};

		return map(toStr, cond(inRange, ch()));
	}

	static Parser<Parser<String>> range() {
		Parser<Pair<Character, Character>> cs = pair(ch(), precededBy(string("-"), ch()));

		Map1<Pair<Character, Character>, Parser<String>> mcs;
		mcs = new Map1<Pair<Character, Character>, Parser<String>>() {
			public Parser<String> map(Pair<Character, Character> src) {
				return range(src.get1st(), src.get2nd());
			}
		};

		Parser<Character> c = ch();
		Map1<Character, Parser<String>> mc;
		mc = new Map1<Character, Parser<String>>() {
			public Parser<String> map(Character src) {
				return range(src, src);
			}
		};

		return or(map(mcs, cs), map(mc, c));
	}

	static Parser<Parser<String>> charClass() {
		Parser<String> osq = string("[");
		Parser<String> csq = string("]");

		Parser<List<Parser<String>>> rs = rep(except(csq, range()));
		Map1<List<Parser<String>>, Parser<String>> m;
		m = new Map1<List<Parser<String>>, Parser<String>>() {
			public Parser<String> map(List<Parser<String>> src) {
				return or(src);
			}
		};

		return followedBy(
				body(osq, map(m, rs), csq),
				spacing());
	}

	static Parser<Parser<String>> literal() {
		Parser<String> sq = string("'");
		Parser<String> dq = string("\"");

		Parser<String> nonSQs = concat(rep(except(sq, chStr())));
		Parser<String> nonDQs = concat(rep(except(dq, chStr())));

		Parser<String> sQuoted = body(sq, nonSQs, sq);
		Parser<String> dQuoted = body(dq, nonDQs, dq);

		Parser<String> p = followedBy(or(sQuoted, dQuoted), spacing());

		Map1<String, Parser<String>> m = new Map1<String, Parser<String>>() {
			public Parser<String> map(String src) {
				return string(src);
			}
		};

		return map(m, p);
	}

	static Parser<Parser<String>> anyChar() {
		Map1<String, Parser<String>> m = new Map1<String, Parser<String>>() {
			public Parser<String> map(String src) {
				return any();
			}
		};

		return map(m, dot());
	}
	private static final RecursionMark<Parser<String>> EXPR_MARK = new RecursionMark<Parser<String>>();

	static Parser<Parser<String>> primary() {
		return or(
				body(open(), recur(EXPR_MARK), close()),
				literal(), charClass(), anyChar());
	}

	static Parser<Parser<String>> suffix() {
		Parser<Parser<String>> q = followedBy(primary(), question());
		Parser<Parser<String>> s = followedBy(primary(), star());
		Parser<Parser<String>> p = followedBy(primary(), plus());

		Map1<Parser<String>, Parser<String>> mq = new Map1<Parser<String>, Parser<String>>() {
			public Parser<String> map(Parser<String> src) {
				return opt(src);
			}
		};

		Map1<Parser<String>, Parser<String>> ms = new Map1<Parser<String>, Parser<String>>() {
			public Parser<String> map(Parser<String> src) {
				return concat(rep(src));
			}
		};

		Map1<Parser<String>, Parser<String>> mp = new Map1<Parser<String>, Parser<String>>() {
			public Parser<String> map(Parser<String> src) {
				return concat(rep1(src));
			}
		};

		return or(map(mq, q), map(ms, s), map(mp, p), primary());
	}

	static Parser<Parser<String>> prefix() {
		Parser<Parser<String>> andP = precededBy(amp(), suffix());
		Parser<Parser<String>> notP = precededBy(excl(), suffix());

		Map1<Parser<String>, Parser<String>> mAnd = new Map1<Parser<String>, Parser<String>>() {
			public Parser<String> map(Parser<String> src) {
				return precededBy(and(src), string(""));
			}
		};

		Map1<Parser<String>, Parser<String>> mNot = new Map1<Parser<String>, Parser<String>>() {
			public Parser<String> map(Parser<String> src) {
				return precededBy(not(src), string(""));
			}
		};

		return or(map(mAnd, andP), map(mNot, notP), suffix());
	}

	static Parser<Parser<String>> sequence() {
		Parser<List<Parser<String>>> p = rep(prefix());
		Map1<List<Parser<String>>, Parser<String>> m = new Map1<List<Parser<String>>, Parser<String>>() {
			public Parser<String> map(List<Parser<String>> src) {
				return concat(src);
			}
		};

		return map(m, p);
	}
	private static final Parser<Parser<String>> EXPR;

	static {
		Parser<List<Parser<String>>> tail = rep(precededBy(slash(), sequence()));
		Map2<Parser<String>, List<Parser<String>>, List<Parser<String>>> conc;
		conc = new Map2<Parser<String>, List<Parser<String>>, List<Parser<String>>>() {
			public List<Parser<String>> map(Parser<String> src1, List<Parser<String>> src2) {
				return flist(src2).prepend(src1);
			}
		};

		Parser<List<Parser<String>>> p = map(conc, sequence(), tail);
		Map1<List<Parser<String>>, Parser<String>> mOR = new Map1<List<Parser<String>>, Parser<String>>() {
			public Parser<String> map(List<Parser<String>> src) {
				return or(src);
			}
		};

		EXPR = mark(EXPR_MARK, map(mOR, p));

	}

	static Parser<Parser<String>> expression() {
		return EXPR;
	}
}
