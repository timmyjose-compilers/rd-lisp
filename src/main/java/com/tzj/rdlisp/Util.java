package com.tzj.rdlisp;

public class Util {
  public static LispObject makeInteger(String intStr) {
    try {
      return new Integer(java.lang.Integer.parseInt(intStr));
    } catch (NumberFormatException ex) {
      throw new RuntimeException(String.format("%s is not an integer", intStr));
    }
  }

  public static Symbol makeSymbol(String symStr) {
    var sym = Environment.retrieveSymbol(symStr);

    if (sym == null) {
      Environment.addSymbol(symStr);
      return Environment.retrieveSymbol(symStr);
    } else {
      return sym;
    }
  }

  public static LispObject makeNil() {
    return nil;
  }

  public static LispObject makeCons(LispObject car, LispObject cdr) {
    return new Cons(car, cdr);
  }

  public static LispObject reverse(LispObject lst) {
    var obj = Util.nil;

    while (lst instanceof Cons cons) {
      var temp = cons.cdr;
      cons.cdr = obj;
      obj = cons;
      lst = temp;
    }

    return obj;
  }

  public static LispObject car(LispObject obj) {
    if (obj instanceof Cons cons) {
      return cons.car;
    }
    return Util.nil;
  }

  public static LispObject cdr(LispObject obj) {
    if (obj instanceof Cons cons) {
      return cons.cdr;
    }
    return Util.nil;
  }

  public static LispObject copyList(LispObject obj) {
    LispObject copy = Util.nil;

    while (obj != Util.nil) {
      if (obj instanceof Cons cons) {
        copy = Util.makeCons(cons.car, copy);
        obj = cons.cdr;
      }
    }

    return Util.reverse(copy);
  }

  public static final LispObject nil = new Nil();
  public static final LispObject eof = new Eof();
}
