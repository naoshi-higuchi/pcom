package jp.nhiguchi.libs.pcom;

@FunctionalInterface
public interface Map1<From, To> {
	/**
	 * @throws jp.nhiguchi.libs.pcom.MappingException when <code>src</code> can
	 * NOT be mapped to a value of <code>To</code>.
	 */
	To map(From src);
}
