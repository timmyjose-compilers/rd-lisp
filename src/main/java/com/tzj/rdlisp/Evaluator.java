package com.tzj.rdlisp;

public class Evaluator {
  public static LispObject eval(final Environment env, LispObject obj) {
    return switch (obj) {
      case Integer num -> num;

      case Symbol sym -> {
        var symBinding = env.retrieveBinding(sym);
        if (symBinding == null) {
          System.out.println("here for " + sym);
          System.out.println(env);
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
              var symVal = Evaluator.eval(env, Util.car(Util.cdr(cons.cdr)));

              env.bindSymbol((Symbol) symName, symVal);
              yield symName;
            }

            case "LAMBDA" -> {
              var closureEnv = env.clone();
              var args = Util.car(cons.cdr);
              var body = Util.car(Util.cdr(cons.cdr));

              yield new LambdaExpression(closureEnv, args, body);
            }

            default -> {
              var binding = env.retrieveBinding(op);
              if (binding instanceof Function fn) {
                var args = Util.copyList(cons.cdr);

                var argPtr = args;
                while (argPtr != Util.nil) {
                  if (argPtr instanceof Cons pair) {
                    pair.car = Evaluator.eval(env, pair.car);
                    argPtr = pair.cdr;
                  }
                }

                yield fn.apply(args);
              } else {
                throw new Error(String.format("%s is not a function", op));
              }
            }
          };
        } else if (cons.car instanceof Cons maybeLambda) {
          if (Evaluator.eval(env, maybeLambda) instanceof LambdaExpression lambda) {
            yield lambda.apply(cons.cdr);
          } else {
            throw new Error(String.format("%s is not a lambda", cons.car));
          }
        } else {
          throw new Error(String.format("%s is not an operator", cons.car));
        }
      }

      default -> throw new Error(String.format("eval for %s is not supported", obj));
    };
  }
}
