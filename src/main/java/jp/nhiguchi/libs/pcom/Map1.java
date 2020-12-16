package jp.nhiguchi.libs.pcom;

/**
 *
 * @author naoshi
 */
public interface Map1<From, To> {
	/**
	 * @throws jp.nhiguchi.libs.pcom.MappingException when <code>src</code> can
	 * NOT be mapped to a value of <code>To</code>.
	 * @param src
	 * @return mapped (converted) value.
	 */
	To map(From src);
}
