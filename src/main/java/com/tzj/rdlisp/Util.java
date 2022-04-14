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

    while (!(obj.equals(Util.nil))) {
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

  private static LispObject appendTwo(LispObject lst1, LispObject lst2) {
    if (lst1.isNil()) {
      return lst2;
    }

    return Util.makeCons(Util.car(lst1), appendTwo(Util.cdr(lst1), lst2));
  }

  public static LispObject append(LispObject... lsts) {
    var res = Util.nil;
    for (var lst : lsts) {
      res = appendTwo(res, lst);
    }

    return res;
  }

  public static final Symbol quote = new Symbol("quote");
  public static final Symbol quasiQuote = new Symbol("quasiquote");
  public static final Symbol unquote = new Symbol("unquote");
  public static final Symbol unquoteSplice = new Symbol("unquote-splice");
  public static final Symbol vararg = new Symbol("vararg");
  public static final LispObject nil = new Nil();
  public static final LispObject eof = new Eof();
  public static final LispObject t = new True();
}
