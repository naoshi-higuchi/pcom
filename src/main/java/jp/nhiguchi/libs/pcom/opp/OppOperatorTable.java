package jp.nhiguchi.libs.pcom.opp;

import java.util.*;

import jp.nhiguchi.libs.flist.*;

/**
 *
 * @author Naoshi HIGUCHI
 */
final class OppOperatorTable<T> {
	private final NavigableMap<Integer, FList<OppOperator<T>>> fMap;

	private OppOperatorTable(NavigableMap<Integer, FList<OppOperator<T>>> map) {
		fMap = map;
	}

	static <T> OppOperatorTable<T> create(
			NavigableMap<Integer, FList<OppOperator<T>>> map) {
		NavigableMap<Integer, FList<OppOperator<T>>> defensiveCopy = new TreeMap();
		defensiveCopy.putAll(map);
		return new OppOperatorTable(defensiveCopy);
	}

	FList<OppOperator<T>> head() {
		return fMap.firstEntry().getValue();
	}

	OppOperatorTable<T> tail() {
		Integer first = fMap.firstKey();
		return new OppOperatorTable(fMap.tailMap(first, false));
	}

	boolean isEmpty() {
		return fMap.isEmpty();
	}
}
