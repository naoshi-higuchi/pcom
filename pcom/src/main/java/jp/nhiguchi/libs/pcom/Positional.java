package jp.nhiguchi.libs.pcom;

/**
 *
 * @author naoshi
 */
public interface Positional<T, P> {
	/**
	 * @throws jp.nhiguchi.libs.pcom.MappingException
	 * @param src
	 * @param pos
	 * @return mapped value
	 */
	P map(T src, int pos);
}
