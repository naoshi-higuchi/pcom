package jp.nhiguchi.libs.pcom;

import java.util.Objects;
import java.util.function.Function;

/**
 * Represents a function that accepts three arguments and produces a result.
 * This is three-arity specialization of {@link java.util.function.Function}
 * @author Naoshi HIGUCHI
 */
@FunctionalInterface
public interface TriFunction<From1, From2, From3, To> {
	/**
	 * @throws jp.nhiguchi.libs.pcom.MappingException
	 * when <code>src1</code>, <code>src2</code> and <code>src3</code> can NOT
	 * be mapped to a value of <code>To</code>.
	 * @param src1
	 * @param src2
	 * @param src3
	 * @return mapped (converted) value.
	 */
	To apply(From1 src1, From2 src2, From3 src3);

	default <V> TriFunction<From1, From2, From3, V> andThen(Function<? super To,? extends V> after) {
		Objects.requireNonNull(after);
		return (From1 src1, From2 src2, From3 src3) -> after.apply(apply(src1, src2, src3));
	}
}
