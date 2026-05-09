package jp.nhiguchi.libs.pcom.opp;

import jp.nhiguchi.libs.pcom.*;

/**
 * Represents a unary operator that transforms a single value of type {@code T} into another value of type {@code T}.
 *
 * @param <T> the type of the value
 */
@FunctionalInterface
public interface Unary<T> extends Map1<T, T> {
	/**
	 * Applies this unary operation to the given source value.
	 *
	 * @param src the source value
	 * @return the result of applying the unary operation
	 * @throws jp.nhiguchi.libs.pcom.MappingException if an error occurs during mapping
	 */
	@Override
	T map(T src);
}
