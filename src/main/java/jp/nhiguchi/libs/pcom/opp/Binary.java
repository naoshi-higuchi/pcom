package jp.nhiguchi.libs.pcom.opp;

import jp.nhiguchi.libs.pcom.*;

/**
 * Represents a binary operator that combines two values of type {@code T} into a single value of type {@code T}.
 *
 * @param <T> the type of the values
 */
@FunctionalInterface
public interface Binary<T> extends Map2<T, T, T> {
	/**
	 * Applies this binary operation to the given source values.
	 *
	 * @param src1 the first source value
	 * @param src2 the second source value
	 * @return the result of applying the binary operation
	 * @throws jp.nhiguchi.libs.pcom.MappingException if an error occurs during mapping
	 */
	@Override
	T map(T src1, T src2);
}
