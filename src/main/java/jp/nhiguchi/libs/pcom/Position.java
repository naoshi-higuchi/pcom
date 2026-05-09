package jp.nhiguchi.libs.pcom;

/**
 *
 * @author naoshi
 */
/**
 * Represents a specific position within a {@link Source} of characters.
 * It encapsulates the source and the current index, providing methods to
 * navigate and inspect the input from this point.
 */
public final class Position {
	private final Source fSrc;
	private final int fPos;

	private Position(Source src, int pos) {
		fSrc = src;
		fPos = pos;
	}

	static Position startOf(Source src) {
		return new Position(src, 0);
	}

	boolean startsWith(String prefix) {
		return fSrc.startsWith(prefix, fPos);
	}

	String head() {
		return fSrc.string(fPos, 1);
	}

	Position next(int run) {
		return new Position(fSrc, fPos + run);
	}

	/**
	 * Checks if this position is at the end of the source.
	 *
	 * @return {@code true} if at the end of the source, {@code false} otherwise
	 */
	public boolean isEnd() {
		return fSrc.isEnd(fPos);
	}

	/**
	 * Returns the integer representation of this position (the character index).
	 *
	 * @return the character index of this position
	 */
	public int asInt() {
		return fPos;
	}

	@Override
	public String toString() {
		return String.format("@%d", fPos);
	}
}
