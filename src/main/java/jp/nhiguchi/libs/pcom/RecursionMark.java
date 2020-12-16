package jp.nhiguchi.libs.pcom;

import java.util.concurrent.atomic.*;

/**
 *
 * @author Naoshi HIGUCHI
 */
public final class RecursionMark<T> {
	private AtomicReference<Parser<? extends T>> fMark = new AtomicReference(null);

	public RecursionMark() {
	}

	void init(Parser<? extends T> p) {
		if (!fMark.compareAndSet(null, p))
			throw new IllegalStateException();
	}

	Parser<? extends T> get() {
		return fMark.get();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
