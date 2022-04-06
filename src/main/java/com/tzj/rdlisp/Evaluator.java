package com.tzj.rdlisp;

public class Evaluator {
  public static LispObject eval(LispObject obj) {
    return switch (obj) {
      case Integer num -> num;

      case Symbol sym -> {
        if (!Environment.hasSymbol(sym.sym)) {
          throw new RuntimeException(String.format("%s is not bound", sym));
        }

        yield Environment.retrieveSymbol(sym.sym);
      }

      case Cons cons -> {
        if (cons.car instanceof Symbol op) {
          yield switch (op.sym) {
            case "QUOTE" -> {
              if (cons.cdr.isNil() || !Util.cdr(cons.cdr).isNil()) {
                throw new RuntimeException("invalid number of args to quote");
              }

              yield Util.car(cons.cdr);
            }

            case "DEF" -> {
              if (cons.cdr.isNil()
                  || Util.cdr(cons.cdr).isNil()
                  || !Util.cdr(Util.cdr(cons.cdr)).isNil()) {
                throw new RuntimeException("invalid number of arguments for def");
              }
              var symName = Util.car(cons.cdr);
              var symVal = Evaluator.eval(Util.car(Util.cdr(cons.cdr)));

              Environment.addSymbol(((Symbol) symName).sym, symVal);
              yield symName;
            }

            default -> Util.nil;
          };
        } else {
          throw new RuntimeException(String.format("%s is not an operator", cons));
        }
      }

      default -> throw new RuntimeException(String.format("eval for %s is not supported", obj));
    };
  }
}
