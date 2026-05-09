package jp.nhiguchi.libs.pcom;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Collections;
import java.util.HashSet;

import static jp.nhiguchi.libs.flist.FList.*;

public final class Result<T> {
	/**
	 * Represents an error that occurred during parsing.
	 *
	 * @param <T> the type of the value that the parser was trying to produce
	 */
	public static final class Error<T> {
		private final Parser<T> fParser;
		private final Position fPos;
		private final List<Error<?>> fCauses;
		private final Set<String> fExpectedTokens;

		private Error(Parser<T> p, Position pos, List<Error<?>> causes, Set<String> expectedTokens) {
			fParser = p;
			fPos = pos;
			fCauses = causes;
			fExpectedTokens = Collections.unmodifiableSet(new HashSet<>(expectedTokens));
		}

		/**
		 * Returns the parser that caused this error.
		 *
		 * @return the parser that caused this error
		 */
		public Parser<T> parser() {
			return fParser;
		}

		/**
		 * Returns the position in the input where the error occurred.
		 *
		 * @return the position where the error occurred
		 */
		public Position position() {
			return fPos;
		}

		/**
		 * Returns a list of underlying causes for this error, if any.
		 *
		 * @return a list of underlying causes
		 */
		public List<Error<?>> causes() {
			return fCauses;
		}

		/**
		 * Returns a set of tokens that were expected at the error position.
		 *
		 * @return a set of expected tokens
		 */
		public Set<String> expectedTokens() {
			return fExpectedTokens;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			if (obj == this) return true;
			if (!(obj instanceof Error<?> rhs)) return false;
			return Objects.equals(fParser, rhs.fParser)
					&& Objects.equals(fPos, rhs.fPos)
					&& Objects.equals(fCauses, rhs.fCauses)
					&& Objects.equals(fExpectedTokens, rhs.fExpectedTokens);
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(fParser)
					+ Objects.hashCode(fPos)
					+ Objects.hashCode(fCauses)
					+ Objects.hashCode(fExpectedTokens);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("error(");
			sb.append(fParser.toString());
			sb.append(", @");
			sb.append(fPos.asInt());
			if (fCauses != null) {
				sb.append(", causes=");
				sb.append(fCauses.toString());
			}
			if (!fExpectedTokens.isEmpty()) {
				sb.append(", expected=");
				sb.append(fExpectedTokens.toString());
			}
			sb.append(")");
			return sb.toString();
		}
	}

	private final T fValue;
	private final Position fRest;
	private final Error<T> fError;

	private Result(T value, Position rest, Error<T> error) {
		fValue = value;
		fRest = rest;
		fError = error;
	}

	/**
	 * Returns the parsed value if the result is a success.
	 *
	 * @return the parsed value
	 * @throws UnsupportedOperationException if the result is a failure
	 */
	public T value() {
		if (isFail()) throw new UnsupportedOperationException();
		return fValue;
	}

	/**
	 * Returns the remaining position in the input if the result is a success.
	 *
	 * @return the remaining position
	 * @throws UnsupportedOperationException if the result is a failure
	 */
	public Position rest() {
		if (isFail()) throw new UnsupportedOperationException();
		return fRest;
	}

	/**
	 * Checks if this result represents a successful parse.
	 *
	 * @return {@code true} if the parse was successful, {@code false} otherwise
	 */
	public boolean isSuccess() {
		return fError == null;
	}

	/**
	 * Checks if this result represents a failed parse.
	 *
	 * @return {@code true} if the parse failed, {@code false} otherwise
	 */
	public boolean isFail() {
		return fError != null;
	}

	/**
	 * Returns the error details if the result is a failure.
	 *
	 * @return the error details
	 * @throws UnsupportedOperationException if the result is a success
	 */
	public Error<T> error() {
		if (isSuccess()) throw new UnsupportedOperationException();
		return fError;
	}

	static <T> Result<T> success(T value, Position next) {
		return new Result<>(value, next, null);
	}

	static <T> Result<T> fail(Parser<?> p, Position pos, List<Error<?>> causes, Set<String> expectedTokens) {
		if (p == null || pos == null || causes == null || expectedTokens == null)
			throw new NullPointerException();
		// p is Parser<?> but Error<T> stores Parser<T>; safe by erasure at runtime
		return new Result<>(null, null, new Error<>((Parser<T>) p, pos, flist(causes), expectedTokens));
	}

	static <T> Result<T> fail(Parser<?> p, Position pos, Error<?> cause, Set<String> expectedTokens) {
		if (p == null || pos == null || cause == null || expectedTokens == null)
			throw new NullPointerException();
		return new Result<>(null, null, new Error<>((Parser<T>) p, pos, flist((Error<T>) cause), expectedTokens));
	}

	static <T> Result<T> fail(Parser<?> p, Position pos, Set<String> expectedTokens) {
		if (p == null || pos == null || expectedTokens == null)
			throw new NullPointerException();
		return new Result<>(null, null, new Error<>((Parser<T>) p, pos, null, expectedTokens));
	}

	@SuppressWarnings("unchecked")
	static <T> Result<T> fail(Parser<?> p, Position pos, List<Error<?>> causes) {
		if (p == null || pos == null || causes == null)
			throw new NullPointerException();
		// p is Parser<?> but Error<T> stores Parser<T>; safe by erasure at runtime
		return new Result<>(null, null, new Error<>((Parser<T>) p, pos, flist(causes), Collections.emptySet()));
	}

	@SuppressWarnings("unchecked")
	static <T> Result<T> fail(Parser<?> p, Position pos, Error<?> cause) {
		if (p == null || pos == null || cause == null)
			throw new NullPointerException();
		return new Result<>(null, null, new Error<>((Parser<T>) p, pos, flist((Error<T>) cause), Collections.emptySet()));
	}

	@SuppressWarnings("unchecked")
	static <T> Result<T> fail(Parser<?> p, Position pos) {
		if (p == null || pos == null)
			throw new NullPointerException();
		return new Result<>(null, null, new Error<>((Parser<T>) p, pos, null, Collections.emptySet()));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof Result<?> rhs)) return false;
		return Objects.equals(fValue, rhs.fValue)
				&& Objects.equals(fRest, rhs.fRest)
				&& Objects.equals(fError, rhs.fError);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(fValue)
				+ Objects.hashCode(fRest)
				+ Objects.hashCode(fError);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (isSuccess()) {
			sb.append("Success(");
			sb.append(fValue.toString());
			sb.append(", @");
			sb.append(fRest.asInt());
			sb.append(")");
		} else {
			sb.append("Fail(");
			sb.append(fError.toString());
			sb.append(")");
		}
		return sb.toString();
	}
}
