package jp.nhiguchi.libs.pcom.opp;

import jp.nhiguchi.libs.pcom.*;

/**
 *
 * @author naoshi
 */
public interface Binary<T> extends Map2<T, T, T> {
	/**
	 * @throws jp.nhiguchi.libs.pcom.MappingException
	 * @param src1
	 * @param src2
	 * @return mapped value.
	 */
	@Override
	public T map(T src1, T src2);
}
