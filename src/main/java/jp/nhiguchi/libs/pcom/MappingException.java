package jp.nhiguchi.libs.pcom;

/**
 *
 * @author naoshi
 */
/**
 * Signals that an error occurred during a mapping operation within the parsing process.
 * This exception is typically thrown by {@code Map1}, {@code Map2}, {@code Map3},
 * {@code Predicate}, or {@code Positional} implementations when they cannot
 * successfully transform or evaluate their inputs.
 */
public final class MappingException extends RuntimeException {
	/**
	 * Constructs a new {@code MappingException} with no detail message.
	 */
	public MappingException() {
	}
}
