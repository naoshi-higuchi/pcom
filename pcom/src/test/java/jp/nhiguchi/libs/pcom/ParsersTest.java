/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.nhiguchi.libs.pcom;

import org.junit.Ignore;
import java.util.Random;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import static jp.nhiguchi.libs.pcom.Parsers.*;

/**
 *
 * @author naoshi
 */
public class ParsersTest {
	public ParsersTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
	}

	@After
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