package jp.nhiguchi.libs.pcom;

import java.io.StringReader;
import java.util.List;

import jp.nhiguchi.libs.tuple.Pair;
import org.junit.jupiter.api.*;

import static jp.nhiguchi.libs.flist.FList.*;
import static jp.nhiguchi.libs.pcom.Parsers.*;
import static org.junit.jupiter.api.Assertions.*;

public class ParsersTest {

	// --- rep ---

	@Test
	public void testRepZeroMatches() {
		Result<List<String>> r = rep(string("a")).parse("bbb");
		assertTrue(r.isSuccess());
		assertEquals(flist(), r.value());
		assertEquals(0, r.rest().asInt());
	}

	@Test
	public void testRepOneMatch() {
		Result<List<String>> r = rep(string("a")).parse("abbb");
		assertTrue(r.isSuccess());
		assertEquals(flist("a"), r.value());
		assertEquals(1, r.rest().asInt());
	}

	@Test
	public void testRepMultipleMatches() {
		Result<List<String>> r = rep(string("a")).parse("aaab");
		assertTrue(r.isSuccess());
		assertEquals(flist("a", "a", "a"), r.value());
		assertEquals(3, r.rest().asInt());
	}

	@Test
	public void testRepFullConsumption() {
		Result<List<String>> r = rep(string("a")).parse("aaa");
		assertTrue(r.isSuccess());
		assertEquals(flist("a", "a", "a"), r.value());
		assertTrue(r.rest().isEnd());
	}

	// --- rep1 ---

	@Test
	public void testRep1OneMatch() {
		Result<List<String>> r = rep1(string("a")).parse("abbb");
		assertTrue(r.isSuccess());
		assertEquals(flist("a"), r.value());
	}

	@Test
	public void testRep1MultipleMatches() {
		Result<List<String>> r = rep1(string("a")).parse("aaab");
		assertTrue(r.isSuccess());
		assertEquals(flist("a", "a", "a"), r.value());
	}

	@Test
	public void testRep1FailsOnZeroMatches() {
		assertTrue(rep1(string("a")).parse("bbb").isFail());
	}

	// --- opt ---

	@Test
	public void testOptPresent() {
		Result<?> r = opt(string("a")).parse("abc");
		assertTrue(r.isSuccess());
		assertEquals("a", r.value());
		assertEquals(1, r.rest().asInt());
	}

	@Test
	public void testOptAbsent() {
		Result<?> r = opt(string("a")).parse("xyz");
		assertTrue(r.isSuccess());
		assertNull(r.value());
		assertEquals(0, r.rest().asInt());
	}

	// --- and ---

	@Test
	public void testAndSuccessDoesNotConsume() {
		Result<Void> r = and(string("a")).parse("abc");
		assertTrue(r.isSuccess());
		assertEquals(0, r.rest().asInt());
	}

	@Test
	public void testAndFailsWhenLookaheadFails() {
		assertTrue(and(string("a")).parse("xyz").isFail());
	}

	// --- not ---

	@Test
	public void testNotSucceedsAndDoesNotConsume() {
		Result<Void> r = not(string("a")).parse("xyz");
		assertTrue(r.isSuccess());
		assertEquals(0, r.rest().asInt());
	}

	@Test
	public void testNotFailsWhenParserSucceeds() {
		assertTrue(not(string("a")).parse("abc").isFail());
	}

	// --- map(Map1) ---

	@Test
	public void testMap1() {
		Parser<Integer> p = map(String::length, string("hello"));

		Result<Integer> r = p.parse("hello");
		assertTrue(r.isSuccess());
		assertEquals(5, r.value());
	}

	@Test
	public void testMap1FailsWhenUnderlyingFails() {
		assertTrue(map(String::length, string("hello")).parse("world").isFail());
	}

	@Test
	public void testMap1MappingExceptionCausesFailure() {
		Map1<String, Integer> throwing = s -> { throw new MappingException(); };
		assertTrue(map(throwing, string("x")).parse("x").isFail());
	}

	// --- map(Map2) ---

	@Test
	public void testMap2() {
		Parser<String> p = map((String a, String b) -> a + b, string("foo"), string("bar"));

		Result<String> r = p.parse("foobar");
		assertTrue(r.isSuccess());
		assertEquals("foobar", r.value());
	}

	@Test
	public void testMap2FailsWhenSecondFails() {
		assertTrue(map((String a, String b) -> a + b, string("foo"), string("bar")).parse("foobaz").isFail());
	}

	@Test
	public void testMap2MappingExceptionCausesFailure() {
		Map2<String, String, String> throwing = (a, b) -> { throw new MappingException(); };
		assertTrue(map(throwing, string("a"), string("b")).parse("ab").isFail());
	}

	// --- map(Map3) ---

	@Test
	public void testMap3() {
		Parser<String> p = map(
				(String a, String b, String c) -> a + b + c,
				string("a"), string("b"), string("c"));

		Result<String> r = p.parse("abc");
		assertTrue(r.isSuccess());
		assertEquals("abc", r.value());
	}

	@Test
	public void testMap3FailsWhenThirdFails() {
		Parser<String> p = map(
				(String a, String b, String c) -> a + b + c,
				string("a"), string("b"), string("c"));
		assertTrue(p.parse("abx").isFail());
	}

	@Test
	public void testMap3MappingExceptionCausesFailure() {
		Map3<String, String, String, String> throwing = (a, b, c) -> { throw new MappingException(); };
		assertTrue(map(throwing, string("a"), string("b"), string("c")).parse("abc").isFail());
	}

	// --- cond ---

	@Test
	public void testCondPassingPredicate() {
		Parser<String> digit = cond(s -> Character.isDigit(s.charAt(0)), any());

		Result<String> r = digit.parse("5abc");
		assertTrue(r.isSuccess());
		assertEquals("5", r.value());
	}

	@Test
	public void testCondFailingPredicate() {
		Parser<String> digit = cond(s -> Character.isDigit(s.charAt(0)), any());
		assertTrue(digit.parse("abc").isFail());
	}

	@Test
	public void testCondMappingExceptionCausesFailure() {
		Predicate<String> throwing = s -> { throw new MappingException(); };
		assertTrue(cond(throwing, any()).parse("x").isFail());
	}

	// --- pos ---

	@Test
	public void testPosAtStart() {
		Parser<String> p = pos((v, position) -> v + "@" + position, string("hello"));

		Result<String> r = p.parse("hello");
		assertTrue(r.isSuccess());
		assertEquals("hello@0", r.value());
	}

	@Test
	public void testPosMidInput() {
		Parser<String> p = map(
				(String ignored, String v) -> v,
				string(">>"),
				pos((v, position) -> v + "@" + position, string("x")));

		Result<String> r = p.parse(">>x");
		assertTrue(r.isSuccess());
		assertEquals("x@2", r.value());
	}

	@Test
	public void testPosMappingExceptionCausesFailure() {
		Positional<String, String> throwing = (v, position) -> { throw new MappingException(); };
		assertTrue(pos(throwing, string("x")).parse("x").isFail());
	}

	// --- precededBy ---

	@Test
	public void testPrecededBy() {
		Parser<String> p = precededBy(string("("), string("val"));

		Result<String> r = p.parse("(val");
		assertTrue(r.isSuccess());
		assertEquals("val", r.value());
	}

	@Test
	public void testPrecededByFailsWithoutLeader() {
		assertTrue(precededBy(string("("), string("val")).parse("val").isFail());
	}

	@Test
	public void testPrecededByNullArguments() {
		assertThrows(IllegalArgumentException.class, () -> precededBy(null, string("x")));
		assertThrows(IllegalArgumentException.class, () -> precededBy(string("x"), null));
	}

	// --- followedBy ---

	@Test
	public void testFollowedBy() {
		Parser<String> p = followedBy(string("val"), string(")"));

		Result<String> r = p.parse("val)");
		assertTrue(r.isSuccess());
		assertEquals("val", r.value());
	}

	@Test
	public void testFollowedByFailsWithoutTrailer() {
		assertTrue(followedBy(string("val"), string(")")).parse("val(").isFail());
	}

	@Test
	public void testFollowedByNullArguments() {
		assertThrows(IllegalArgumentException.class, () -> followedBy(null, string("x")));
		assertThrows(IllegalArgumentException.class, () -> followedBy(string("x"), null));
	}

	// --- body ---

	@Test
	public void testBody() {
		Parser<String> p = body(string("["), string("inner"), string("]"));

		Result<String> r = p.parse("[inner]");
		assertTrue(r.isSuccess());
		assertEquals("inner", r.value());
	}

	@Test
	public void testBodyFailsWithMismatchedClose() {
		assertTrue(body(string("["), string("inner"), string("]")).parse("[inner)").isFail());
	}

	@Test
	public void testBodyNullArguments() {
		assertThrows(IllegalArgumentException.class, () -> body(null, string("x"), string("x")));
		assertThrows(IllegalArgumentException.class, () -> body(string("x"), null, string("x")));
		assertThrows(IllegalArgumentException.class, () -> body(string("x"), string("x"), null));
	}

	// --- pair ---

	@Test
	public void testPair() {
		Parser<Pair<String, String>> p = pair(string("key"), string(":value"));

		Result<Pair<String, String>> r = p.parse("key:value");
		assertTrue(r.isSuccess());
		assertEquals("key", r.value().get1st());
		assertEquals(":value", r.value().get2nd());
	}

	@Test
	public void testPairFailsWhenSecondMissing() {
		assertTrue(pair(string("key"), string(":value")).parse("key=value").isFail());
	}

	@Test
	public void testPairNullArguments() {
		assertThrows(IllegalArgumentException.class, () -> pair(null, string("x")));
		assertThrows(IllegalArgumentException.class, () -> pair(string("x"), null));
	}

	// --- trim ---

	@Test
	public void testTrimNoSurrounding() {
		assertEquals("hello", trim(string(" "), string("hello")).parse("hello").value());
	}

	@Test
	public void testTrimLeadingSpace() {
		assertEquals("hello", trim(string(" "), string("hello")).parse(" hello").value());
	}

	@Test
	public void testTrimTrailingSpace() {
		assertEquals("hello", trim(string(" "), string("hello")).parse("hello ").value());
	}

	@Test
	public void testTrimBothSides() {
		assertEquals("hello", trim(string(" "), string("hello")).parse(" hello ").value());
	}

	@Test
	public void testTrimNullArguments() {
		assertThrows(IllegalArgumentException.class, () -> trim(null, string("x")));
		assertThrows(IllegalArgumentException.class, () -> trim(string(" "), null));
	}

	// --- concat ---

	@Test
	public void testConcatVarargs() {
		Parser<String> p = concat(string("foo"), string("bar"), string("baz"));

		Result<String> r = p.parse("foobarbaz");
		assertTrue(r.isSuccess());
		assertEquals("foobarbaz", r.value());
	}

	@Test
	public void testConcatFromListParser() {
		Parser<String> p = concat(rep(string("a")));

		assertEquals("aaa", p.parse("aaa").value());
		assertEquals("", p.parse("bbb").value());
	}

	@Test
	public void testConcatNullArgument() {
		assertThrows(IllegalArgumentException.class, () -> concat((Parser<List<String>>) null));
	}

	// --- except ---

	@Test
	public void testExceptAllowsNonExcluded() {
		Parser<String> notQuote = except(string("\""), any());

		Result<String> r = notQuote.parse("a");
		assertTrue(r.isSuccess());
		assertEquals("a", r.value());
	}

	@Test
	public void testExceptBlocksExcluded() {
		assertTrue(except(string("\""), any()).parse("\"").isFail());
	}

	// --- sepBy ---

	@Test
	public void testSepByZeroElements() {
		Result<List<String>> r = sepBy(string("a"), string(",")).parse("b");
		assertTrue(r.isSuccess());
		assertEquals(flist(), r.value());
	}

	@Test
	public void testSepByOneElement() {
		Result<List<String>> r = sepBy(string("a"), string(",")).parse("a");
		assertTrue(r.isSuccess());
		assertEquals(flist("a"), r.value());
	}

	@Test
	public void testSepByMultipleElements() {
		Result<List<String>> r = sepBy(string("a"), string(",")).parse("a,a,a");
		assertTrue(r.isSuccess());
		assertEquals(flist("a", "a", "a"), r.value());
	}

	@Test
	public void testSepByNullArguments() {
		assertThrows(IllegalArgumentException.class, () -> sepBy(null, string(",")));
		assertThrows(IllegalArgumentException.class, () -> sepBy(string("a"), null));
	}

	// --- sepBy1 ---

	@Test
	public void testSepBy1OneElement() {
		Result<List<String>> r = sepBy1(string("a"), string(",")).parse("a");
		assertTrue(r.isSuccess());
		assertEquals(flist("a"), r.value());
	}

	@Test
	public void testSepBy1MultipleElements() {
		Result<List<String>> r = sepBy1(string("a"), string(",")).parse("a,a,a");
		assertTrue(r.isSuccess());
		assertEquals(flist("a", "a", "a"), r.value());
	}

	@Test
	public void testSepBy1FailsOnZeroElements() {
		assertTrue(sepBy1(string("a"), string(",")).parse("b").isFail());
	}

	@Test
	public void testSepBy1NullArguments() {
		assertThrows(IllegalArgumentException.class, () -> sepBy1(null, string(",")));
		assertThrows(IllegalArgumentException.class, () -> sepBy1(string("a"), null));
	}

	// --- parse(Readable) ---

	@Test
	public void testParseReadableSuccess() {
		Result<String> r = string("hello").parse(new StringReader("hello"));
		assertTrue(r.isSuccess());
		assertEquals("hello", r.value());
	}

	@Test
	public void testParseReadableFailure() {
		assertTrue(string("hello").parse(new StringReader("world")).isFail());
	}

	// --- Result failure paths ---

	@Test
	public void testResultFailAccessors() {
		Result<?> fail = string("x").parse("y");

		assertTrue(fail.isFail());
		assertFalse(fail.isSuccess());
		assertNotNull(fail.error());
		assertThrows(UnsupportedOperationException.class, fail::value);
		assertThrows(UnsupportedOperationException.class, fail::rest);
	}

	@Test
	public void testResultSuccessErrorAccessorThrows() {
		Result<?> ok = string("x").parse("x");

		assertTrue(ok.isSuccess());
		assertThrows(UnsupportedOperationException.class, ok::error);
	}

	@Test
	public void testResultErrorPosition() {
		Result<?> fail = string("abc").parse("xyz");

		assertTrue(fail.isFail());
		assertEquals(0, fail.error().position().asInt());
	}

	@Test
	public void testResultErrorParser() {
		Parser<String> p = string("abc");
		Result<?> fail = p.parse("xyz");

		assertSame(p, fail.error().parser());
	}
}
