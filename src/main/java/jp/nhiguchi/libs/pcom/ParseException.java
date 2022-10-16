package jp.nhiguchi.libs.pcom;

/**
 *
 * @author Naoshi HIGUCHI
 */
public final class ParseException extends RuntimeException {
	private final int fPos;

	ParseException(Position pos, Throwable cause) {
		super(cause);
		fPos = pos.asInt();
	}

	ParseException(Position pos, String msg) {
		super(msg);
		fPos = pos.asInt();
	}

	ParseException(Position pos, String message, Throwable cause) {
		super(message, cause);
		fPos = pos.asInt();
	}

	public int position() {
		return fPos;
	}
}
