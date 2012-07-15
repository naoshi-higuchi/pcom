package jp.nhiguchi.libs.pcom;

/**
 *
 * @author naoshi
 */
public interface Map2<From1, From2, To> {
	/**
	 * @throws jp.nhiguchi.libs.pcom.MappingException when <code>src1</code>
	 * and <code>src2</code> can NOT be mapped to a value of <code>To</code>.
	 * @param src1
	 * @param src2
	 * @return mapped (converted) value.
	 */
	To map(From1 src1, From2 src2);
}
