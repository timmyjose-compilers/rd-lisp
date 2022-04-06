package com.tzj.rdlisp;

import java.util.HashMap;
import java.util.Map;

public class Environment {
  private static final Map<Symbol, LispObject> bindings = new HashMap<>();
  private static final Map<String, Symbol> symbols = new HashMap<>();

  static {
    // initial environment
    Environment.addSymbol("cons");
    Environment.addSymbol("car");
    Environment.addSymbol("cdr");

    Environment.bindSymbol(Environment.retrieveSymbol("cons"), new ConsFunction());
    Environment.bindSymbol(Environment.retrieveSymbol("car"), new CarFunction());
    Environment.bindSymbol(Environment.retrieveSymbol("cdr"), new CdrFunction());
  }

  public static void addSymbol(String symStr) {
    symbols.put(symStr.toUpperCase(), new Symbol(symStr.toUpperCase()));
  }

  public static Symbol retrieveSymbol(String symStr) {
    return symbols.get(symStr.toUpperCase());
  }

  public static void bindSymbol(Symbol sym, LispObject obj) {
    bindings.put(sym, obj);
  }

  public static LispObject retrieveBinding(Symbol sym) {
    return bindings.get(sym);
  }
}
