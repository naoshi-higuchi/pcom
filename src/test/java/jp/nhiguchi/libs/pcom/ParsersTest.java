package jp.nhiguchi.libs.pcom;

import java.util.Random;

import static jp.nhiguchi.libs.pcom.Parsers.*;

public class ParsersTest {

	private static RecursionMark<String> newMark() {
		return new RecursionMark<>();
	}

	private static String randomAB(int len) {
		Random r = new Random(System.currentTimeMillis());

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; ++i) {
			sb.append(r.nextInt() % 2 == 0 ? 'a' : 'b');
		}
		return sb.toString();
	}
}
