package jp.nhiguchi.libs.pcom;

/**
 * Represents a predicate (boolean-valued function) of one argument.
 *
 * @param <T> the type of the input to the predicate
 */
@FunctionalInterface
public interface Predicate<T> {
	/**
	 * Evaluates this predicate on the given argument.
	 *
	 * @param val the input argument
	 * @return {@code true} if the input argument matches the predicate, otherwise {@code false}
	 */
	boolean eval(T val);
}
