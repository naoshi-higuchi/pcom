# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

`pcom` is a parser combinator library for Java implementing Parsing Expression Grammars (PEG). It is a Maven library (not an application), targeting Java 26 with local dependencies on `jp.nhiguchi.libs:tuple:0.2` and `jp.nhiguchi.libs:flist:0.3`.

## Build and Test Commands

```bash
# Build
mvn compile

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=ParserTest

# Run a single test method
mvn test -Dtest=ParserTest#testParse

# Package
mvn package
```

## Architecture

### Core parsing model

- **`Parser<T>`** — the central class. Immutable, value-typed (equality is structural). Takes a `Source`/`String`/`Readable` and returns `Result<T>`. Internally memoizes results by `(Parser, Position)` pairs to implement packrat parsing.
- **`Result<T>`** — a discriminated union: either `Success(value, restPosition)` or `Fail(error)`. Call `isSuccess()`/`isFail()` before accessing `value()` or `error()`.
- **`Position`** — an index into a `Source`. Immutable cursor; `rest().asInt()` gives the integer offset.
- **`Source`** — wraps a `String` or `Readable` for positional access.

### Public API surface

**`Parsers`** (static factory façade — this is the primary entry point for users):
- Primitives: `string(str)`, `any()`, `or(...)`, `seq(...)`, `rep(p)`, `rep1(p)`, `opt(p)`, `and(p)`, `not(p)`
- Mapping: `map(Map1, p)`, `map(Map2, p1, p2)`, `map(Map3, p1, p2, p3)`, `cond(pred, p)`, `pos(positional, p)`
- Combinators: `precededBy`, `followedBy`, `body`, `pair`, `trim`, `concat`, `except`, `sepBy`, `sepBy1`
- Recursion: `mark(RecursionMark, p)` + `recur(RecursionMark)` for left/right recursive grammars
- Expression shorthand: `expr(String)` — parses a PEG expression string into a `Parser<String>`

**`Map1<From, To>`**, **`Map2<...>`**, **`Map3<...>`** — `@FunctionalInterface` types for transforming parsed values; accept lambdas or method references.

**`Predicate<T>`** — functional interface used with `cond()`.

**`Positional<T, P>`** — functional interface used with `pos()` to attach source position to a parsed value.

### Internal implementation

- **`Primitives`** — package-private; implements the primitive combinators as `ParseFunctor` instances (`StringFunctor`, `AnyFunctor`, `OrFunctor`, `SeqFunctor`, `RepFunctor`, `OptFunctor`, `AndFunctor`, `NotFunctor`).
- **`Maps`** — package-private; implements the `map`/`cond`/`pos` combinators.
- **`Recursions`** — package-private; implements `mark`/`recur` via `RecursionMark<T>` (holds an `AtomicReference` to the marked parser) and `LazyFunctor`.
- **`Expressions`** — package-private; implements `Parsers.expr(String)` by building a PEG parser for PEG expressions using the library itself (self-hosting).

### Operator-precedence parsing (`opp` subpackage)

`jp.nhiguchi.libs.pcom.opp` adds Prolog-style operator precedence parsing:
- **`Operator<T>`** — an operator with fixity (`XFX`, `XFY`, `YFX`, `FX`, `FY`, `XF`, `YF`), precedence level, and a mapping function (`Binary<T>` or `Unary<T>`).
- **`OppBuilder<T>`** — fluent builder: `add(Operator)`, `addParentheses(lPar, rPar)`, `setOperandParser(p)`, then `toParser()`.
- **`OppParsers`** — package-private; recursive descent over an `OpTable` to build the precedence-climbing parser.
- Ambiguity is detected at `OppBuilder.add()` time (e.g., mixing `YFX` and `XFY` at the same precedence level throws `IllegalArgumentException`).
