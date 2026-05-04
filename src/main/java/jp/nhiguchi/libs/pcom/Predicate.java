package jp.nhiguchi.libs.pcom;

@FunctionalInterface
public interface Predicate<T> {
	boolean eval(T val);
}
