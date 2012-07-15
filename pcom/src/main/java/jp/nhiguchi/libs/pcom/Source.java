package jp.nhiguchi.libs.pcom;

import java.io.*;
import java.nio.*;

/**
 *
 * @author naoshi
 */
class Source {
	private String fStr = "";
	private boolean fIsEOS;
	private final Readable fReadable;
	private static final int BUFSIZE = 2048;
	private final CharBuffer fBuf = CharBuffer.allocate(BUFSIZE);

	private Source(String str) {
		fStr = str;
		fIsEOS = true;
		fReadable = null;
	}

	private Source(Readable r) {
		fIsEOS = false;
		fReadable = r;
	}

	static Source source(String str) {
		return new Source(str);
	}

	static Source source(Readable r) {
		return new Source(r);
	}

	private int remaining(int pos) {
		return fStr.length() - pos;
	}

	private String readAtLeast(int n) {
		int len = 0;
		StringBuilder sb = new StringBuilder();

		while (len < n) {
			try {
				fBuf.clear();
				len += fReadable.read(fBuf);
				if (len == -1) break;

				sb.append(fBuf.toString());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		if (len < n) return null;

		return sb.toString();
	}

	private int prepare(int pos, int len) {
		int r = remaining(pos);
		if (r >= len) return len;

		if (fIsEOS) return 0;

		String str = readAtLeast(len - r);
		if (str == null) return r;

		fStr = fStr + str;
		if (str.length() > len - r) return len;

		return r + str.length();
	}

	String string(int pos, int len) {
		int n = prepare(pos, len);
		if (n != len) return null;

		return fStr.substring(pos, pos + len);
	}

	boolean startsWith(String prefix, int offset) {
		int len = prefix.length();
		if (prepare(offset, len) < len) return false;

		return fStr.startsWith(prefix, offset);
	}

	boolean isEOS() {
		return fIsEOS;
	}

	boolean isEnd(int pos) {
		return prepare(pos, 1) == 0 && fStr.length() == pos;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof Source)) return false;

		Source rhs = (Source) obj;
		if (fReadable != null) {
			// identity.
			return fReadable == rhs.fReadable;
		}
		return fStr.equals(rhs.fStr);
	}

	@Override
	public int hashCode() {
		if (fReadable != null)
			return System.identityHashCode(fReadable);

		return fStr.hashCode();
	}
}
