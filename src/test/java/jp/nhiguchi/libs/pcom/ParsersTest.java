/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.nhiguchi.libs.pcom;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.Random;

import static jp.nhiguchi.libs.pcom.Parsers.*;

/**
 *
 * @author Naoshi HIGUCHI
 */
public class ParsersTest {
	public ParsersTest() {
	}

	@BeforeAll
	public static void setUpClass() throws Exception {
	}

	@AfterAll
	public static void tearDownClass() throws Exception {
	}

	@BeforeEach
	public void setUp() {
	}

	@AfterEach
	public void tearDown() {
	}

	private static RecursionMark<String> newMark() {
		return new RecursionMark<String>();
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