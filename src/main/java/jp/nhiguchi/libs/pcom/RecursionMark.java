package jp.nhiguchi.libs.pcom;

import java.util.concurrent.atomic.*;

/**
 * Represents a marker for recursive parsers, allowing for the definition of mutually recursive grammar rules.
 * This class is used internally to break cycles in grammar definitions and enable memoization for recursive calls.
 *
 * @param <T> the type of the value produced by the parser associated with this mark
 */
public final class RecursionMark<T> {
	private AtomicReference<Parser<? extends T>> fMark = new AtomicReference<>(null);

	/**
	 * Constructs a new {@code RecursionMark}.
	 */
	public RecursionMark() {
	}

	void init(Parser<? extends T> p) {
		if (!fMark.compareAndSet(null, p))
			throw new IllegalStateException();
	}

	Parser<? extends T> get() {
		return fMark.get();
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 * The default implementation of this method in {@code Object} is used,
	 * which means two {@code RecursionMark} instances are only equal if they are the exact same object.
	 *
	 * @param obj the reference object with which to compare.
	 * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	/**
	 * Returns a hash code value for the object.
	 * The default implementation of this method in {@code Object} is used.
	 *
	 * @return a hash code value for this object.
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
