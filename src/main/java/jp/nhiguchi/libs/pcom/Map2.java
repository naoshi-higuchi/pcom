package jp.nhiguchi.libs.pcom;

@FunctionalInterface
public interface Map2<From1, From2, To> {
	/**
	 * @throws jp.nhiguchi.libs.pcom.MappingException when <code>src1</code>
	 * and <code>src2</code> can NOT be mapped to a value of <code>To</code>.
	 */
	To map(From1 src1, From2 src2);
}
