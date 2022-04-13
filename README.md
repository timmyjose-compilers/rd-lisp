# rd-lisp

A simple minimal Lisp interpreter, mainly for the purposes of learning how Lisps work in general.

## Build and Run

```
  $ mvn -q clean && mvn -q compile && mvn -q exec:java
```

## Demo

Basic language:

```
  $ mvn -q clean && mvn -q compile && mvn -q exec:java
  Loaded up the standard library  
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

With the `defun` special form:

```
  $ mvn -q clean && mvn -q compile && mvn -q exec:java
  Loaded up the standard library

  > (defun even? (n) (if (eq? n 0) t (odd? (- n 1))))
  <function>:<EVEN?><74767163>

  > (defun odd? (n) (if (eq? n 0) nil (even? (- n 1))))
  <function>:<ODD?><827083818>

  > (filter even? '(1 2 3 4 5))
  (2 4)

  > (filter odd? '(1 2 3 4 5))
  (1 3 5)
```

With macros:

```
  $ mvn -q clean && mvn -q compile && mvn -q exec:java
  Loaded up the standard library

  > (defmacro ignore (x) (cons 'quote (cons x nil)))
  <macro>:<IGNORE><52818027>

  > (ignore (+ 1 2))
  (+ 1 2)

  > (ignore foo)
  FOO

  > foo
  FOO is not bound

  > (defmacro when (cond body) (cons 'if (cons cond (cons body nil))))
  <macro>:<WHEN><578205442>

  > (when (eq? 1 1) 100)
  100

  > (when (eq? 1 0) 100)
  NIL
```

With variadic functions:

```
  $ mvn -q clean && mvn -q -T8 compile && mvn -q exec:java
  Loaded up the standard library

  > (defun add (x y &rest nums) (foldl + 0 (cons x (cons y nums))))
  <function>:<ADD><74767163>

  > (add 1)
  incorrect number of arguments in lambda - expected 2, but got 1

  > (add 1 2)

  3
  > (add 1 2 3)

  6
  > (add 1 2 3 4 5 6 7 8 9 10)
  55

  > (defun foo (&rest args) args)
  <function>:<FOO><827083818>

  > (foo 1 2 3 4 5)
  (1 2 3 4 5)

  > (defun bar (x y &rest args &rest args-again) nil)
  can have at most one &rest param declaration, but got 2

  > (defun bar (&rest args x) x)
  cannot define &rest after required positional params
```

With quasiquoted macros:

```
  $ mvn -q clean && mvn -q -T8 compile && mvn -q exec:java
  Loaded up the standard library

  > `(+ 1 ,(+ 2 3))
  (+ 1 5)

  > (def l '(3 4 5))
  L

  > l
  (3 4 5)

  > `(1 2 ,@l)
  (1 2 3 4 5)

  > (defmacro ignore (x) `(quote ,x))
  <macro>:<IGNORE><188316699>

  > (ignore 100)
  100

  > (ignore foo)
  FOO

  > foo
  FOO is not bound

  > (defmacro when (condition body) `(if ,condition ,body))
  <macro>:<WHEN><1626537335>

  > (when (eq? 1 0) 'yes)
  NIL

  > (when (eq? 1 1) 'yes)
  YES

  > (def x '(a b c))
  X

  > `(x ,x ,@x)
  (X (A B C) A B C)

  > `(x ,x ,@x foo ,(cadr x) bar ,(cdr x) baz ,@(cdr x))
  (X (A B C) A B C FOO B BAR (B C) BAZ B C)
```

## LICENCE

See [LICENSE.md](LICENSE.md)