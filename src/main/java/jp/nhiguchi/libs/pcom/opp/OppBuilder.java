package jp.nhiguchi.libs.pcom.opp;

import java.util.*;

import jp.nhiguchi.libs.flist.*;
import static jp.nhiguchi.libs.flist.FList.*;

import jp.nhiguchi.libs.tuple.*;
import static jp.nhiguchi.libs.tuple.Pair.*;

import jp.nhiguchi.libs.pcom.*;
import jp.nhiguchi.libs.pcom.opp.Operator.Fixity;
import static jp.nhiguchi.libs.pcom.opp.Operator.Fixity.*;

/**
 *
 * @author Naoshi HIGUCHI
 */
public class OppBuilder<T> {
	private final NavigableMap<Integer, FList<Operator<T>>> fMap = new TreeMap();
	private final HashSet<Pair<Parser<String>, Parser<String>>> fParens = new HashSet();
	private Parser<T> fOperandParser = null;

	public OppBuilder() {
	}

	private boolean isAmbiguous(FList<Operator<T>> column) {
		Set<Fixity> fixes = new HashSet();
		for (Operator<T> op : column) {
			fixes.add(op.fix());
		}
		return (fixes.contains(YFX) && fixes.contains(XFY))
				|| (fixes.contains(YFX) && fixes.contains(YF))
				|| (fixes.contains(YFX) && fixes.contains(FY))
				|| (fixes.contains(XFY) && fixes.contains(YF))
				|| (fixes.contains(XFY) && fixes.contains(FY))
				|| (fixes.contains(FY) && fixes.contains(YF));
	}

	public OppBuilder add(Operator<T> op) {
		FList<Operator<T>> column = fMap.get(op.prec());
		column = (column == null) ? flist(op) : cons(op, column);
		if (isAmbiguous(column)) {
			throw new IllegalArgumentException("Ambiguous grammar.");
		}
		fMap.put(op.prec(), column);
		return this;
	}

	public OppBuilder addParentheses(Parser<String> lPar,
			Parser<String> rPar) {
		if (lPar == null || rPar == null) {
			throw new IllegalArgumentException();
		}
		Pair<Parser<String>, Parser<String>> ps = newPair(lPar, rPar);
		fParens.add(ps);
		return this;
	}

	public OppBuilder setOperandParser(Parser<T> p) {
		fOperandParser = p;
		return this;
	}

	public Parser<T> toParser() {
		return OppParsers.oppParser(
				OpTable.create(fMap), flist(fParens), fOperandParser);
	}
}
