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
    var sym = Environment.getInitEnv().retrieveSymbol(symStr);

    if (sym == null) {
      Environment.getInitEnv().addSymbol(symStr);
      return Environment.getInitEnv().retrieveSymbol(symStr);
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
    throw new Error(String.format("%s is not a pair", obj));
  }

  public static LispObject cdr(LispObject obj) {
    if (obj instanceof Cons cons) {
      return cons.cdr;
    }
    throw new Error(String.format("%s is not a pair", obj));
  }

  public static LispObject copyList(LispObject obj) {
    LispObject copy = Util.nil;

    while (obj != Util.nil) {
      if (obj instanceof Cons cons) {
        copy = makeCons(cons.car, copy);
        obj = cons.cdr;
      }
    }

    return reverse(copy);
  }

  public static int consLength(LispObject obj) {
    int len = 0;
    var cons = obj;

    try {
      while (!cons.isNil()) {
        len++;
        cons = Util.cdr(cons);
      }
    } catch (Error err) {
    }

    return len;
  }

  public static final LispObject nil = new Nil();
  public static final LispObject eof = new Eof();
  public static final LispObject t = new True();
}
