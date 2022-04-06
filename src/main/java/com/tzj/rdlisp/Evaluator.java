package com.tzj.rdlisp;

public class Evaluator {
  public static LispObject eval(LispObject obj) {
    return switch (obj) {
      case Integer num -> num;

      case Symbol sym -> {
        var symBinding = Environment.retrieveBinding(sym);
        if (symBinding == null) {
          throw new Error(String.format("%s is not bound", sym));
        }

        yield symBinding;
      }

      case Cons cons -> {
        if (cons.car instanceof Symbol op) {
          yield switch (op.sym) {
            case "QUOTE" -> {
              if (cons.cdr.isNil() || !Util.cdr(cons.cdr).isNil()) {
                throw new Error("invalid number of args to quote");
              }

              yield Util.car(cons.cdr);
            }

            case "DEF" -> {
              if (cons.cdr.isNil()
                  || Util.cdr(cons.cdr).isNil()
                  || !Util.cdr(Util.cdr(cons.cdr)).isNil()) {
                throw new Error("invalid number of arguments for def");
              }
              var symName = Util.car(cons.cdr);
              var symVal = Evaluator.eval(Util.car(Util.cdr(cons.cdr)));

              Environment.bindSymbol((Symbol) symName, symVal);
              yield symName;
            }

            default -> {
              var binding = Environment.retrieveBinding(op);
              if (binding instanceof BuiltinFunction fn) {
                var args = Util.copyList(cons.cdr);

                var argsPtr = args;
                while (argsPtr != Util.nil) {
                  if (argsPtr instanceof Cons pair) {
                    pair.car = Evaluator.eval(pair.car);
                    argsPtr = pair.cdr;
                  }
                }

                yield fn.apply(args);
              } else {
                throw new Error(String.format("%s is not a builtin function", op));
              }
            }
          };
        } else {
          throw new Error(String.format("%s is not an operator", cons.car));
        }
      }

      default -> throw new Error(String.format("eval for %s is not supported", obj));
    };
  }
}
