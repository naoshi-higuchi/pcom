package jp.nhiguchi.libs.pcom;

import java.util.List;
import java.util.Objects;

import static jp.nhiguchi.libs.flist.FList.*;

/**
 *
 * @author Naoshi HIGUCHI
 */
public final class Result<T> {
	public static final class Error<T> {
		private final Parser<T> fParser;
		private final Position fPos;
		private final List<Error> fCauses;

		private Error(Parser<T> p, Position pos, List<Error> causes) {
			fParser = p;
			fPos = pos;
			fCauses = causes;
		}

		public Parser<T> parser() {
			return fParser;
		}

		public Position position() {
			return fPos;
		}

		public List<Error> causes() {
			return fCauses;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			if (obj == this) return true;
			if (!(obj instanceof Error)) return false;

			Error rhs = (Error) obj;
			return Objects.equals(fParser, rhs.fParser)
					&& Objects.equals(fPos, rhs.fPos)
					&& Objects.equals(fCauses, rhs.fCauses);
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(fParser)
					+ Objects.hashCode(fPos)
					+ Objects.hashCode(fCauses);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("error(");
			sb.append(fParser.toString());
			sb.append(", @");
			sb.append(fPos.asInt());
			if (fCauses != null) {
				sb.append(", ");
				sb.append(fCauses.toString());
			}
			sb.append(")");
			return sb.toString();
		}
	}
	private final T fValue;
	private final Position fRest;
	private final Error fError;

	private Result(T value, Position rest, Error error) {
		fValue = value;
		fRest = rest;
		fError = error;
	}

	public T value() {
		if (isFail()) throw new UnsupportedOperationException();
		return fValue;
	}

	public Position rest() {
		if (isFail()) throw new UnsupportedOperationException();
		return fRest;
	}

	public boolean isSuccess() {
		return fError == null;
	}

	public boolean isFail() {
		return fError != null;
	}

	public Error error() {
		if (isSuccess()) throw new UnsupportedOperationException();
		return fError;
	}

	static <T> Result<T> success(T value, Position next) {
		return new Result(value, next, null);
	}

	static <T> Result<T> fail(Parser p, Position pos, List<Error> causes) {
		if (p == null || pos == null || causes == null)
			throw new NullPointerException();

		return new Result(null, null, new Error(p, pos, flist(causes)));
	}

	static <T> Result<T> fail(Parser p, Position pos, Error cause) {
		if (p == null || pos == null || cause == null)
			throw new NullPointerException();

		return new Result(null, null, new Error(p, pos, flist(cause)));
	}

	static <T> Result<T> fail(Parser p, Position pos) {
		if (p == null || pos == null)
			throw new NullPointerException();

		return new Result(null, null, new Error(p, pos, null));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof Result)) return false;

		Result rhs = (Result) obj;
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
