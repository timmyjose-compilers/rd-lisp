package com.tzj.rdlisp;

public class Evaluator {
  public static LispObject eval(final Environment env, LispObject obj) {
    return switch (obj) {
      case Nil nil -> nil;

      case Integer num -> num;

      case Symbol sym -> {
        var symBinding = env.retrieveBinding(sym);
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

              if (Util.car(cons.cdr) instanceof Symbol sym) {
                var symVal = Evaluator.eval(env, Util.car(Util.cdr(cons.cdr)));
                env.bindSymbol(sym, symVal);

                // since the binding in the current environment was done after the
                // env for the closure was already passed in
                if (symVal instanceof LambdaExpression lambda) {
                  lambda.env.bindSymbol(sym, symVal);
                }

                yield sym;
              } else {
                throw new Error(
                    String.format(
                        "`def` expects a symbol to bind, but got %s", Util.car(cons.cdr)));
              }
            }

            case "LAMBDA" -> {
              var closureEnv = env.clone();
              var args = Util.car(cons.cdr);
              var body = Util.car(Util.cdr(cons.cdr));

              yield new LambdaExpression(closureEnv, args, body);
            }

            case "IF" -> {
              var argsLen = Util.consLength(cons.cdr);
              if (argsLen != 2 && argsLen != 3) {
                throw new Error(
                    String.format(
                        "incorrect number of arguments for `if` - expected between 2 and 3, got"
                            + " %d",
                        argsLen));
              }

              var cond = Util.car(cons.cdr);
              var true_expr = Util.car(Util.cdr(cons.cdr));

              if (argsLen == 2) {
                yield Evaluator.eval(env, cond).isTrue()
                    ? Evaluator.eval(env, true_expr)
                    : Util.nil;
              } else {
                var false_expr = Util.car(Util.cdr(Util.cdr(cons.cdr)));
                yield Evaluator.eval(env, cond).isTrue()
                    ? Evaluator.eval(env, true_expr)
                    : Evaluator.eval(env, false_expr);
              }
            }

            default -> {
              var binding = env.retrieveBinding(op);
              if (binding == null) {
                // if not found in the current scope, try in the global env
                binding = Environment.getInitEnv().retrieveBinding(op);
              }

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
