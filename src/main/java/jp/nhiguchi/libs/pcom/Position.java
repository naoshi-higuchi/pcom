package jp.nhiguchi.libs.pcom;

/**
 *
 * @author Naoshi HIGUCHI
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

	public boolean isEnd() {
		return fSrc.isEnd(fPos);
	}

	public int asInt() {
		return fPos;
	}

	@Override
	public String toString() {
		return String.format("@%d", fPos);
	}
}
