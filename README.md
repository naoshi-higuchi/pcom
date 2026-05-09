# pcom

A parser combinator library for Java implementing Parsing Expression Grammars (PEG).

## Requirements

- Java 26
- Maven 3.x

### Local dependencies (install to local Maven repository before building)

| Artifact | Version |
|---|---|
| `jp.nhiguchi.libs:tuple` | 0.2 |
| `jp.nhiguchi.libs:flist` | 0.3 |

## Build

```bash
mvn compile      # compile
mvn test         # run all tests
mvn package      # produce jar
```

## Quick start

```java
import jp.nhiguchi.libs.pcom.*;
import static jp.nhiguchi.libs.pcom.Parsers.*;

// Match a literal string
Parser<String> hello = string("hello");
Result<String> r = hello.parse("hello world");
// r.isSuccess() == true
// r.value()     == "hello"
// r.rest().asInt() == 5   (position after the match)

// Match an integer
Parser<String> digits = expr("[0-9]+");
Parser<Integer> integer = map(Integer::parseInt, digits);
integer.parse("42rest").value();  // 42

// Sequence: parse "key=value"
Parser<String> kv = map(
    (String k, String eq, String v) -> k + eq + v,
    expr("[a-z]+"), string("="), expr("[a-z]+"));
kv.parse("foo=bar").value();  // "foo=bar"
```

## Core concepts

### `Parser<T>`

`Parser<T>` is the central type. It is immutable and value-typed (two parsers are equal if their structure is equal). Internally it uses packrat memoization, so each `(parser, position)` pair is evaluated at most once per `parse()` call.

```java
Result<T> parse(String str)
Result<T> parse(Readable r)
Result<T> parse(Position pos)
```

### `Result<T>`

A discriminated union of success or failure.

```java
result.isSuccess()        // true on success
result.isFail()           // true on failure

// On success:
result.value()            // T — the parsed value
result.rest()             // Position — points just past the consumed input
result.rest().asInt()     // int offset
result.rest().isEnd()     // true when input is fully consumed

// On failure:
result.error()            // Result.Error<T>
result.error().position() // Position where the parse failed
result.error().parser()   // Parser that failed
result.error().causes()   // List<Error<?>> — nested cause chain, or null
```

Calling `value()` or `rest()` on a failed result, or `error()` on a successful result, throws `UnsupportedOperationException`.

## `Parsers` — combinator reference

All combinators are static methods on `Parsers`. The typical usage is a static import:

```java
import static jp.nhiguchi.libs.pcom.Parsers.*;
```

### Primitives

| Method | Description |
|---|---|
| `string(str)` | Matches the literal string `str`. |
| `any()` | Matches any single character. |
| `or(p1, p2, …)` | Ordered choice — tries each parser left to right, returns the first success. |
| `seq(p1, p2, …)` | Sequence — all parsers must match in order; returns `List<T>`. |
| `rep(p)` | Repetition — matches `p` zero or more times; always succeeds. |
| `rep1(p)` | Repetition — matches `p` one or more times; fails on zero matches. |
| `opt(p)` | Optional — returns `p`'s value on success, or `null` without consuming input. |
| `and(p)` | Positive lookahead — succeeds without consuming if `p` would succeed. |
| `not(p)` | Negative lookahead — succeeds without consuming if `p` would fail. |

### Mapping and transformation

| Method | Description |
|---|---|
| `map(Map1 fn, p)` | Apply a single-argument function to `p`'s result. |
| `map(Map2 fn, p1, p2)` | Parse `p1` then `p2`, apply a two-argument function. |
| `map(Map3 fn, p1, p2, p3)` | Parse three parsers in sequence, apply a three-argument function. |
| `cond(pred, p)` | Succeed only when `p` succeeds and `pred.eval(value)` returns true. |
| `pos(positional, p)` | Like `map`, but also passes the start position (int offset) to the function. |

`Map1`, `Map2`, `Map3`, `Predicate`, and `Positional` are all `@FunctionalInterface` — accept lambdas or method references. Throwing `MappingException` from any of them causes the parser to fail at that position.

### Utility combinators

| Method | Description |
|---|---|
| `precededBy(pre, p)` | Match `pre` then `p`; return `p`'s value only. |
| `followedBy(p, post)` | Match `p` then `post`; return `p`'s value only. |
| `body(pre, p, post)` | Match `pre`, `p`, `post`; return `p`'s value only. |
| `pair(p1, p2)` | Match `p1` then `p2`; return a `Pair<T, U>`. |
| `trim(trimming, p)` | Match `opt(trimming)`, `p`, `opt(trimming)`; return `p`'s value. |
| `concat(p1, p2, …)` | Match string parsers in sequence; return their concatenation as a single `String`. |
| `concat(listParser)` | Concatenate the `List<String>` produced by a parser into a single `String`. |
| `except(ex, p)` | Succeed only when `ex` would fail, then apply `p` (equivalent to `not(ex)` followed by `p`). |
| `sepBy(p, sep)` | Zero or more `p`, separated by `sep`; always succeeds. |
| `sepBy1(p, sep)` | One or more `p`, separated by `sep`; fails on zero matches. |

### PEG expression shorthand

```java
Parser<String> digits = expr("[0-9]+");
Parser<String> ident  = expr("[a-zA-Z_][a-zA-Z0-9_]*");
Parser<String> ws     = expr("[ \t\n]*");
```

`expr(String)` parses a PEG expression string into a `Parser<String>` at build time. Throws `IllegalArgumentException` if the expression is invalid.

Supported syntax:

| Syntax | Meaning |
|---|---|
| `"abc"` | Literal string |
| `.` | Any character |
| `[a-z0-9]` | Character class |
| `e?` | Optional |
| `e*` | Zero or more |
| `e+` | One or more |
| `e1 e2` | Sequence |
| `e1 / e2` | Ordered choice |
| `&e` | Positive lookahead |
| `!e` | Negative lookahead |
| `(e)` | Grouping |

### Recursive grammars

Use `mark`/`recur` to write right-recursive or mutually recursive rules. Left recursion is not supported and causes a `StackOverflowError`.

```java
// X <- 'x' X / 'x'   (right recursive)
RecursionMark<String> m = new RecursionMark<>();
Parser<String> xs = mark(m,
    or(concat(seq(string("x"), recur(m))),
       string("x")));

xs.parse("xxxxx").value();  // "xxxxx"
```

## Operator precedence parsing (`opp`)

The `jp.nhiguchi.libs.pcom.opp` package provides Prolog-style precedence climbing for expression languages.

```java
import jp.nhiguchi.libs.pcom.opp.*;
import static jp.nhiguchi.libs.pcom.Parsers.*;

Parser<String> ws  = expr("[ \t]*");
Parser<Integer> num = map(Integer::parseInt, trim(ws, expr("[0-9]+")));

// Infix left-associative  (precedence 700)
Operator<Integer> add = Operator.infixL(700, (a, b) -> a + b, trim(ws, string("+")));
Operator<Integer> sub = Operator.infixL(700, (a, b) -> a - b, trim(ws, string("-")));
// Infix left-associative  (precedence 800)
Operator<Integer> mul = Operator.infixL(800, (a, b) -> a * b, trim(ws, string("*")));
Operator<Integer> div = Operator.infixL(800, (a, b) -> a / b, trim(ws, string("/")));
// Infix right-associative (precedence 1100)
Operator<Integer> pow = Operator.infixR(1100, (a, b) -> (int) Math.pow(a, b), trim(ws, string("**")));
// Prefix (precedence 1000)
Operator<Integer> neg = Operator.prefix(1000, a -> -a, trim(ws, string("-")));
// Postfix (precedence 1200)
Operator<Integer> fac = Operator.postfix(1200, a -> {
    int r = 1; for (int i = 1; i <= a; i++) r *= i; return r;
}, trim(ws, string("!")));

OppBuilder<Integer> builder = new OppBuilder<>();
builder.setOperandParser(num);
builder.add(add); builder.add(sub);
builder.add(mul); builder.add(div);
builder.add(pow); builder.add(neg); builder.add(fac);
builder.addParentheses(trim(ws, string("(")), trim(ws, string(")")));

Parser<Integer> expr = builder.toParser();

expr.parse("3 ** 3 ** 2").value();       // 19683  (right-associative: 3^(3^2))
expr.parse("-6! / 2 + 3 * 3").value();  // -171
```

### Operator fixity types

| Factory method | Fixity | Associativity |
|---|---|---|
| `Operator.infixL(prec, fn, p)` | Binary infix | Left |
| `Operator.infixR(prec, fn, p)` | Binary infix | Right |
| `Operator.infix(prec, fn, p)` | Binary infix | None |
| `Operator.prefix(prec, fn, p)` | Unary prefix | Right |
| `Operator.prefixR(prec, fn, p)` | Unary prefix | Right (non-associative) |
| `Operator.postfix(prec, fn, p)` | Unary postfix | None |
| `Operator.postfixL(prec, fn, p)` | Unary postfix | Left |

Adding two operators at the same precedence level with conflicting associativity (e.g., `YFX` and `XFY`) throws `IllegalArgumentException` at build time.

## License

MIT License — see [LICENSE](LICENSE).
