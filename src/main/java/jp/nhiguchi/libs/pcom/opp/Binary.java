package jp.nhiguchi.libs.pcom.opp;

import jp.nhiguchi.libs.pcom.*;

@FunctionalInterface
public interface Binary<T> extends Map2<T, T, T> {
	/**
	 * @throws jp.nhiguchi.libs.pcom.MappingException
	 */
	@Override
	T map(T src1, T src2);
}
