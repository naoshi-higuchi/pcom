package jp.nhiguchi.libs.pcom;

@FunctionalInterface
public interface Map3<From1, From2, From3, To> {
	/**
	 * @throws jp.nhiguchi.libs.pcom.MappingException when the inputs can NOT be
	 * mapped to a value of <code>To</code>.
	 */
	To map(From1 src1, From2 src2, From3 src3);
}
