package com.tzj.rdlisp;

import java.util.Objects;

abstract sealed class LispObject permits Nil, True, Integer, Symbol, Cons, Eof, Function {
  protected boolean isNil() {
    return false;
  }

  protected boolean isTrue() {
    return true;
  }

  protected boolean isCons() {
    return false;
  }
}

abstract sealed class Function extends LispObject
    permits LambdaExpression, BuiltinFunction, MacroFunction {
  public abstract LispObject apply(LispObject args);
}

final class LambdaExpression extends Function {
  public Environment env;
  public LispObject params;
  public LispObject body;

  public LambdaExpression(final Environment env, final LispObject params, final LispObject body) {
    this.env = env;
    this.params = params;
    this.body = body;
  }

  @Override
  public LispObject apply(LispObject args) {
    // spawn a new environment ("frame") for this closure's execution.
    var env = this.env.clone();
    var paramCount = 0;
    var paramPtr = params;
    while (paramPtr != Util.nil) {
      paramCount++;
      paramPtr = Util.cdr(paramPtr);
    }

    var argCount = 0;
    var argPtr = args;
    while (argPtr != Util.nil) {
      argCount++;
      argPtr = Util.cdr(argPtr);
    }

    if (paramCount != argCount) {
      throw new Error(
          String.format(
              "incorrect number of arguments in lambda - expected %d, but got %d",
              paramCount, argCount));
    }

    // bindIngs for all the params
    var runParams = Util.copyList(params);
    var runArgs = Util.copyList(args);

    while (runParams != Util.nil) {
      var param = (Symbol) Util.car(runParams);
      env.addSymbol(param.sym);
      env.bindSymbol(env.retrieveSymbol(param.sym), Util.car(runArgs));

      runParams = Util.cdr(runParams);
      runArgs = Util.cdr(runArgs);
    }

    return Evaluator.eval(env, body);
  }

  @Override
  public String toString() {
    return String.format("<lambda>:<%d>", this.hashCode());
  }
}

/// Built-in functions

abstract sealed class BuiltinFunction extends Function
    permits ConsFunction,
        CarFunction,
        CdrFunction,
        AddFunction,
        SubFunction,
        MulFunction,
        DivFunction,
        EqFunction,
        LessThanFunction {}

final class ConsFunction extends BuiltinFunction {
  @Override
  public LispObject apply(LispObject args) {
    var argsLen = Util.consLength(args);

    if (argsLen != 2) {
      throw new Error(
          String.format("invalid number of arguments for `cons` - expected 2, got %d", argsLen));
    }

    return Util.makeCons(Util.car(args), Util.car(Util.cdr(args)));
  }

  @Override
  public String toString() {
    return "<builtin>:<cons>";
  }
}

final class CarFunction extends BuiltinFunction {
  @Override
  public LispObject apply(LispObject args) {
    var argsLen = Util.consLength(args);
    args = Util.car(args);

    if (argsLen != 1) {
      throw new Error(
          String.format("invalid number of arguments for `car` - expected 1, got %d", argsLen));
    }

    if (args.equals(Util.nil)) {
      return Util.nil;
    } else if (args instanceof Cons cons) {
      return cons.car;
    } else {
      throw new Error(String.format("Cannot take the car of %s - not a Cons cell", args));
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
    var argsLen = Util.consLength(args);
    args = Util.car(args);

    if (argsLen != 1) {
      throw new Error(
          String.format("invalid number of arguments for `cdr` - expected 1, got %d", argsLen));
    }

    if (args.equals(Util.nil)) {
      return Util.nil;
    } else if (args instanceof Cons cons) {
      return cons.cdr;
    } else {
      throw new Error(String.format("Cannot take the cdr of %s - not a Cons cell", args));
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
  public boolean isTrue() {
    return false;
  }

  @Override
  public String toString() {
    return "NIL";
  }

  @Override
  public int hashCode() {
    return Objects.hash(this);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Nil) {
      return true;
    }
    return false;
  }
}

final class True extends LispObject {
  @Override
  public String toString() {
    return "T";
  }
}

final class AddFunction extends BuiltinFunction {
  @Override
  public LispObject apply(LispObject args) {
    var res = 0;

    while (args != Util.nil) {
      if (Util.car(args) instanceof Integer n) {
        res += n.integer;
      } else {
        throw new Error(String.format("\"%s\" passed to `+` is not a number", Util.car(args)));
      }
      args = Util.cdr(args);
    }

    return new Integer(res);
  }

  @Override
  public String toString() {
    return "<builtin>:<+>";
  }
}

final class SubFunction extends BuiltinFunction {
  @Override
  public LispObject apply(LispObject args) {
    var res = 0;

    if (Util.car(args) instanceof Integer m) {
      res = m.integer;
      args = Util.cdr(args);
    } else {
      throw new Error(String.format("\"%s\" passed to `-` is not a number", Util.car(args)));
    }

    while (args != Util.nil) {
      if (Util.car(args) instanceof Integer n) {
        res -= n.integer;
      } else {
        throw new Error(String.format("\"%s\" passed to `-` is not a number", Util.car(args)));
      }
      args = Util.cdr(args);
    }

    return new Integer(res);
  }

  @Override
  public String toString() {
    return "<builtin>:<->";
  }
}

final class MulFunction extends BuiltinFunction {
  @Override
  public LispObject apply(LispObject args) {
    var res = 1;

    while (args != Util.nil) {
      if (Util.car(args) instanceof Integer n) {
        res *= n.integer;
      } else {
        throw new Error(String.format("\"%s\" passed to `*` is not a number", Util.car(args)));
      }
      args = Util.cdr(args);
    }

    return new Integer(res);
  }

  @Override
  public String toString() {
    return "<builtin>:<*>";
  }
}

final class DivFunction extends BuiltinFunction {
  @Override
  public LispObject apply(LispObject args) {
    var res = 0;
    if (Util.car(args) instanceof Integer m) {
      res = m.integer;
      args = Util.cdr(args);
    } else {
      throw new Error(String.format("\"%s\" passed to `/` is not a number", Util.car(args)));
    }

    while (args != Util.nil) {
      if (Util.car(args) instanceof Integer n) {
        res /= n.integer;
      } else {
        throw new Error(String.format("\"%s\" passed to `/` is not a number", Util.car(args)));
      }
      args = Util.cdr(args);
    }

    return new Integer(res);
  }

  @Override
  public String toString() {
    return "<builtin>:</>";
  }
}

final class EqFunction extends BuiltinFunction {
  @Override
  public LispObject apply(LispObject args) {
    var argsLen = Util.consLength(args);
    if (argsLen != 2) {
      throw new Error(
          String.format("incorrect number of arguments for `eq?` - expected 2, got %d", argsLen));
    }

    var firstArg = Util.car(args);
    var secondArg = Util.car(Util.cdr(args));

    return firstArg.equals(secondArg) ? Util.t : Util.nil;
  }

  @Override
  public String toString() {
    return "<builtin>:<eq?>";
  }
}

final class LessThanFunction extends BuiltinFunction {
  @Override
  public LispObject apply(LispObject args) {
    var argsLen = Util.consLength(args);
    if (argsLen != 2) {
      throw new Error(
          String.format("incorrect numnber of arguments for `<` - expected 2, got %d", argsLen));
    }

    if (Util.car(args) instanceof Integer first) {
      if (Util.car(Util.cdr(args)) instanceof Integer second) {
        return switch (first.compareTo(second)) {
          case -1 -> Util.t;
          default -> Util.nil;
        };
      } else {
        throw new Error("second argument to `<` is not a number");
      }
    } else {
      throw new Error("first argument to `<` is not a number");
    }
  }

  @Override
  public String toString() {
    return "<builtin>:<<>";
  }
}

final class MacroFunction extends Function {
  public Symbol name;
  private LambdaExpression lambda;

  public MacroFunction(
      final Environment env, final Symbol name, final LispObject args, final LispObject body) {
    this.name = name;
    this.lambda = new LambdaExpression(env, args, body);
  }

  @Override
  public LispObject apply(LispObject args) {
    return lambda.apply(args);
  }

  @Override
  public String toString() {
    return String.format("<macro>:<%s><%d>", name, this.hashCode());
  }
}

final class Integer extends LispObject implements Comparable<Integer> {
  final int integer;

  public Integer(int integer) {
    this.integer = integer;
  }

  @Override
  public String toString() {
    return String.valueOf(integer);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Integer other) {
      return this.integer == other.integer;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this);
  }

  @Override
  public int compareTo(Integer other) {
    if (this.integer < other.integer) {
      return -1;
    } else if (this.integer == other.integer) {
      return 0;
    } else {
      return 1;
    }
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

  @Override
  public int hashCode() {
    return Objects.hash(this.sym);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Symbol other) {
      return this.sym.equalsIgnoreCase(other.sym);
    }
    return false;
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

  @Override
  public int hashCode() {
    return Objects.hash(this);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Cons other) {
      return this.car.equals(other.car) && this.cdr.equals(other.cdr);
    }
    return false;
  }
}

final class Eof extends LispObject {
  @Override
  public String toString() {
    return "EOF";
  }
}
