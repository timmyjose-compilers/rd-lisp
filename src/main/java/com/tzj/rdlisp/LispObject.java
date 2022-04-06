package com.tzj.rdlisp;

abstract sealed class LispObject permits Nil, Integer, Symbol, Cons, Eof {
  protected boolean isNil() {
    return false;
  }

  protected boolean isCons() {
    return false;
  }
}

final class Nil extends LispObject {
  @Override
  public boolean isNil() {
    return true;
  }

  @Override
  public String toString() {
    return "NIL";
  }
}

final class Integer extends LispObject {
  final int integer;

  public Integer(int integer) {
    this.integer = integer;
  }

  @Override
  public String toString() {
    return String.valueOf(integer);
  }
}

final class Symbol extends LispObject {
  public String sym;

  public Symbol(final String sym) {
    this.sym = sym;
  }

  @Override
  public String toString() {
    return sym;
  }
}

final class Cons extends LispObject {
  public LispObject car;
  public LispObject cdr;

  public Cons(final LispObject car, final LispObject cdr) {
    this.car = car;
    this.cdr = cdr;
  }

  @Override
  public boolean isCons() {
    return true;
  }

  @Override
  public String toString() {
    var sb = new StringBuffer("(");
    sb.append(car.toString());
    var obj = cdr;

    while (obj != Util.nil) {
      if (obj instanceof Cons cons) {
        sb.append(" ");
        sb.append(cons.car.toString());
        obj = cons.cdr;
      } else {
        sb.append(" . ");
        sb.append(obj.toString());
        break;
      }
    }
    sb.append(")");
    return sb.toString();
  }
}

final class Eof extends LispObject {
  @Override
  public String toString() {
    return "EOF";
  }
}
