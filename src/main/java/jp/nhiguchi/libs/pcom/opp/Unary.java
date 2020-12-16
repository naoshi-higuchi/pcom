package jp.nhiguchi.libs.pcom.opp;

import jp.nhiguchi.libs.pcom.*;

/**
 *
 * @author naoshi
 */
public interface Unary<T> extends Map1<T, T> {
	/**
	 * @throws jp.nhiguchi.libs.pcom.MappingException
	 * @param src
	 * @return mapped value
	 */
	@Override
	public T map(T src);
}
