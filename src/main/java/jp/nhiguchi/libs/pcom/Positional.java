package jp.nhiguchi.libs.pcom;

@FunctionalInterface
public interface Positional<T, P> {
	/**
	 * @throws jp.nhiguchi.libs.pcom.MappingException
	 */
	P map(T src, int pos);
}
