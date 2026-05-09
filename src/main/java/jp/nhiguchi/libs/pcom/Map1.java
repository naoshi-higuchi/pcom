package jp.nhiguchi.libs.pcom;

/**
 * Represents a function that accepts one argument and produces a result.
 *
 * @param <From> the type of the input to the function
 * @param <To> the type of the result of the function
 */
@FunctionalInterface
public interface Map1<From, To> {
	/**
	 * Applies this function to the given argument.
	 *
	 * @param src the function argument
	 * @return the function result
	 * @throws jp.nhiguchi.libs.pcom.MappingException when {@code src} can
	 * NOT be mapped to a value of {@code To}.
	 */
	To map(From src);
}
