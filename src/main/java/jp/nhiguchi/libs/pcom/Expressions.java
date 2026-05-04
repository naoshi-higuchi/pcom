package jp.nhiguchi.libs.pcom;

import java.util.List;

import static jp.nhiguchi.libs.pcom.Parsers.*;

import static jp.nhiguchi.libs.flist.FList.*;
import jp.nhiguchi.libs.tuple.*;

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
		return map(src -> {
			if (src.length() != 1) throw new MappingException();
			return src.charAt(0);
		}, chStr());
	}

	private static Parser<String> chStr() {
		Parser<String> backSlash = string("\\");
		return precededBy(opt(backSlash), any());
	}

	private static Parser<String> range(final char from, final char to) {
		return map(src -> src.toString(), cond(src -> from <= src && src <= to, ch()));
	}

	static Parser<Parser<String>> range() {
		var cs = pair(ch(), precededBy(string("-"), ch()));
		return or(
			map(src -> range(src.get1st(), src.get2nd()), cs),
			map(src -> range(src, src), ch())
		);
	}

	static Parser<Parser<String>> charClass() {
		var osq = string("[");
		var csq = string("]");
		var rs = rep(except(csq, range()));
		return followedBy(
				body(osq, map(src -> or(src), rs), csq),
				spacing());
	}

	static Parser<Parser<String>> literal() {
		var sq = string("'");
		var dq = string("\"");

		var nonSQs = concat(rep(except(sq, chStr())));
		var nonDQs = concat(rep(except(dq, chStr())));

		var sQuoted = body(sq, nonSQs, sq);
		var dQuoted = body(dq, nonDQs, dq);

		var p = followedBy(or(sQuoted, dQuoted), spacing());

		return map(src -> string(src), p);
	}

	static Parser<Parser<String>> anyChar() {
		return map(src -> any(), dot());
	}

	private static final RecursionMark<Parser<String>> EXPR_MARK = new RecursionMark<>();

	static Parser<Parser<String>> primary() {
		return or(
				body(open(), recur(EXPR_MARK), close()),
				literal(), charClass(), anyChar());
	}

	static Parser<Parser<String>> suffix() {
		var q = followedBy(primary(), question());
		var s = followedBy(primary(), star());
		var p = followedBy(primary(), plus());

		return or(
			map(src -> opt(src), q),
			map(src -> concat(rep(src)), s),
			map(src -> concat(rep1(src)), p),
			primary()
		);
	}

	static Parser<Parser<String>> prefix() {
		var andP = precededBy(amp(), suffix());
		var notP = precededBy(excl(), suffix());

		return or(
			map(src -> precededBy(and(src), string("")), andP),
			map(src -> precededBy(not(src), string("")), notP),
			suffix()
		);
	}

	static Parser<Parser<String>> sequence() {
		var p = rep(prefix());
		return map(src -> concat(src), p);
	}

	private static final Parser<Parser<String>> EXPR;

	static {
		var tail = rep(precededBy(slash(), sequence()));
		var p = map(
			(Parser<String> src1, List<Parser<String>> src2) -> flist(src2).prepend(src1),
			sequence(), tail);
		EXPR = mark(EXPR_MARK, map(src -> or(src), p));
	}

	static Parser<Parser<String>> expression() {
		return EXPR;
	}
}
