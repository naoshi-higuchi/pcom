package jp.nhiguchi.libs.pcom;

/**
 * Represents a function that accepts two arguments and produces a result.
 *
 * @param <From1> the type of the first argument
 * @param <From2> the type of the second argument
 * @param <To> the type of the result of the function
 */
@FunctionalInterface
public interface Map2<From1, From2, To> {
	/**
	 * Applies this function to the given arguments.
	 *
	 * @param src1 the first function argument
	 * @param src2 the second function argument
	 * @return the function result
	 * @throws jp.nhiguchi.libs.pcom.MappingException when {@code src1}
	 * and {@code src2} can NOT be mapped to a value of {@code To}.
	 */
	To map(From1 src1, From2 src2);
}
