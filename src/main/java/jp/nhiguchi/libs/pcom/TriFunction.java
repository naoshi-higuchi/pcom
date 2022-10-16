package jp.nhiguchi.libs.pcom;

/**
 *
 * @author Naoshi HIGUCHI
 */
@FunctionalInterface
public interface TriFunction<From1, From2, From3, To> {
	/**
	 * @throws jp.nhiguchi.libs.pcom.MappingException
	 * when <code>src1</code>, <code>src2</code> and <code>src3</code> can NOT
	 * be mapped to a value of <code>To</code>.
	 * @param src1
	 * @param src2
	 * @param src3
	 * @return mapped (converted) value.
	 */
	To apply(From1 src1, From2 src2, From3 src3);
}
