package jp.nhiguchi.libs.pcom;

/**
 *
 * @author naoshi
 */
/**
 * Signals that an error occurred during parsing.
 * This exception provides information about the position in the input where the error occurred.
 */
public final class ParseException extends RuntimeException {
	private final int fPos;

	/**
	 * Constructs a new {@code ParseException} with the specified cause and position.
	 *
	 * @param pos the position in the input where the error occurred
	 * @param cause the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method).
	 *              (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
	 */
	ParseException(Position pos, Throwable cause) {
		super(cause);
		fPos = pos.asInt();
	}

	/**
	 * Constructs a new {@code ParseException} with the specified detail message and position.
	 *
	 * @param pos the position in the input where the error occurred
	 * @param msg the detail message. The detail message is saved for later retrieval by the {@link Throwable#getMessage()} method.
	 */
	ParseException(Position pos, String msg) {
		super(msg);
		fPos = pos.asInt();
	}

	/**
	 * Constructs a new {@code ParseException} with the specified detail message, cause, and position.
	 *
	 * @param pos the position in the input where the error occurred
	 * @param message the detail message (see {@link Throwable#getMessage()}).
	 * @param cause the cause (see {@link Throwable#getCause()}).
	 */
	ParseException(Position pos, String message, Throwable cause) {
		super(message, cause);
		fPos = pos.asInt();
	}

	/**
	 * Returns the character position in the input where this exception occurred.
	 *
	 * @return the character position
	 */
	public int position() {
		return fPos;
	}
}
