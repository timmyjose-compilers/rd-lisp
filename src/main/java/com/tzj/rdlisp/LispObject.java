package com.tzj.rdlisp;

abstract sealed class LispObject permits Nil, Integer, Symbol, Cons, Eof, BuiltinFunction {
  protected boolean isNil() {
    return false;
  }

  protected boolean isCons() {
    return false;
  }
}

/// Built-in functions

abstract sealed class BuiltinFunction extends LispObject
    permits ConsFunction, CarFunction, CdrFunction {
  public abstract LispObject apply(LispObject args);
}

final class ConsFunction extends BuiltinFunction {
  @Override
  public LispObject apply(LispObject args) {
    if (Util.cdr(args).isNil() || !Util.cdr(Util.cdr(args)).isNil()) {
      throw new Error("invalid number of arguments for `cons`");
    }

    return Util.makeCons(Util.car(args), Util.cdr(args));
  }

  @Override
  public String toString() {
    return "<builtin>:<cons>";
  }
}

final class CarFunction extends BuiltinFunction {
  @Override
  public LispObject apply(LispObject args) {
    args = Util.car(args);

    if (args == Util.nil) {
      return Util.nil;
    } else if (args instanceof Cons cons) {
      return cons.car;
    } else {
      throw new Error(String.format("Cannot take the car of %s", args));
    }
  }

  @Override
  public String toString() {
    return "<builtin>:<car>";
  }
}

final class CdrFunction extends BuiltinFunction {
  @Override
  public LispObject apply(LispObject args) {
    args = Util.car(args);

    if (args == Util.nil) {
      return Util.nil;
    } else if (args instanceof Cons cons) {
      return cons.cdr;
    } else {
      throw new Error(String.format("Cannot take the car of %s", args));
    }
  }

  @Override
  public String toString() {
    return "<builtin>:<cdr>";
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
    this.sym = sym.toUpperCase();
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
