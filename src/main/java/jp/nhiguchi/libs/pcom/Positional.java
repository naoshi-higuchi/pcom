package jp.nhiguchi.libs.pcom;

/**
 * Represents a function that maps a source value and its position to a result.
 * This is typically used in parsing contexts where the position of a parsed element is relevant.
 *
 * @param <T> the type of the source value
 * @param <P> the type of the result
 */
@FunctionalInterface
public interface Positional<T, P> {
	/**
	 * Maps the source value and its position to a result.
	 *
	 * @param src the source value
	 * @param pos the position of the source value
	 * @return the mapped result
	 * @throws jp.nhiguchi.libs.pcom.MappingException if an error occurs during mapping
	 */
	P map(T src, int pos);
}
