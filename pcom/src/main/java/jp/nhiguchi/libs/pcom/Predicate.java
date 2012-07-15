package jp.nhiguchi.libs.pcom;

/**
 *
 * @author naoshi
 */
public interface Predicate<T> {
	boolean eval(T val);
}
