package jp.nhiguchi.libs.pcom.opp;

import java.util.*;

import jp.nhiguchi.libs.flist.*;

/**
 *
 * @author Naoshi HIGUCHI
 */
final class OpTable<T> {
	private final NavigableMap<Integer, FList<Operator<T>>> fMap;

	private OpTable(NavigableMap<Integer, FList<Operator<T>>> map) {
		fMap = map;
	}

	static <T> OpTable<T> create(
			NavigableMap<Integer, FList<Operator<T>>> map) {
		NavigableMap<Integer, FList<Operator<T>>> defensiveCopy = new TreeMap();
		defensiveCopy.putAll(map);
		return new OpTable(defensiveCopy);
	}

	FList<Operator<T>> head() {
		return fMap.firstEntry().getValue();
	}

	OpTable<T> tail() {
		Integer first = fMap.firstKey();
		return new OpTable(fMap.tailMap(first, false));
	}

	boolean isEmpty() {
		return fMap.isEmpty();
	}
}
