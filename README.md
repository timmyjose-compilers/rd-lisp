# rd-lisp

A simple minimal Lisp interpreter, mainly for the purposes of learning how Lisps work in general.

## Build and Run

```
  $ mvn -q clean && mvn -q compile && mvn -q exec:java -Dexec.mainClass=com.tzj.rdlisp.App
```

## Demo

Basic language:

```
  $ mvn -q clean && mvn -q compile && mvn -q exec:java -Dexec.mainClass=com.tzj.rdlisp.App
  > 1
  1

  > (+ 1 2)
  3

  > (* (+ 1 2) (- 3 (/ 10 2))))
  unmatched parenthesis

  > (* (+ 1 2) (- 3 (/ 10 2)))
  -6

  > foo
  FOO is not bound

  > (def foo 100)
  FOO

  > foo
  100

  > (def bar (quote a b c))
  invalid number of args to quote

  > (def bar (quote a))
  BAR

  > bar
  A

  > (def baz (quote (a b c)))
  BAZ

  > baz
  (A B C)

  > (def foobar '(a b c))
  FOOBAR

  > foobar
  (A B C)

  > (def factorial (lambda (n) (if (eq? n 0) 1 (* n (factorial (- n 1))))))
  FACTORIAL

  > factorial
  <lambda>:<947222732>

  > +
  <builtin>:<+>

  > *
  <builtin>:<*>

  > (factorial 10)
  3628800

  > (factorial 5)
  120

  > (def even (lambda (n) (if (eq? n 0) t (odd (- n 1)))))
  EVEN

  > (def odd (lambda (n) (if (eq? n 0) nil (even (- n 1)))))
  ODD

  > (odd 100)
  NIL

  > (odd 99)
  T

  > (even 100)
  T

  > (even 99)
  NIL

  > (def make-adder (lambda (n) (lambda (m) (+ n m))))
  MAKE-ADDER

  > (def add-10 (make-adder 10))
  ADD-10

  > (add-10 1)
  11

  > add-10
  <lambda>:<996489737>
  
```


Macros:

TBD


## LICENCE

See [LICENSE.md](LICENSE.md)