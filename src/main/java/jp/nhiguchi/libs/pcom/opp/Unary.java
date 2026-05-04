package jp.nhiguchi.libs.pcom.opp;

import jp.nhiguchi.libs.pcom.*;

@FunctionalInterface
public interface Unary<T> extends Map1<T, T> {
	/**
	 * @throws jp.nhiguchi.libs.pcom.MappingException
	 */
	@Override
	T map(T src);
}
