package com.tzj.rdlisp;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Environment {
  private final Map<Symbol, LispObject> bindings = new HashMap<>();
  private final Map<String, Symbol> symbols = new HashMap<>();

  private static final Environment initialEnv;

  static {
    // initial environment
    initialEnv = new Environment();

    initialEnv.addSymbol("cons");
    initialEnv.addSymbol("car");
    initialEnv.addSymbol("cdr");
    initialEnv.addSymbol("+");
    initialEnv.addSymbol("-");
    initialEnv.addSymbol("*");
    initialEnv.addSymbol("/");

    initialEnv.bindSymbol(initialEnv.retrieveSymbol("cons"), new ConsFunction());
    initialEnv.bindSymbol(initialEnv.retrieveSymbol("car"), new CarFunction());
    initialEnv.bindSymbol(initialEnv.retrieveSymbol("cdr"), new CdrFunction());
    initialEnv.bindSymbol(initialEnv.retrieveSymbol("+"), new AddFunction());
    initialEnv.bindSymbol(initialEnv.retrieveSymbol("-"), new SubFunction());
    initialEnv.bindSymbol(initialEnv.retrieveSymbol("*"), new MulFunction());
    initialEnv.bindSymbol(initialEnv.retrieveSymbol("/"), new DivFunction());
  }

  public void addSymbol(String symStr) {
    symbols.put(symStr.toUpperCase(), new Symbol(symStr.toUpperCase()));
  }

  public Symbol retrieveSymbol(String symStr) {
    return symbols.get(symStr.toUpperCase());
  }

  public void bindSymbol(Symbol sym, LispObject obj) {
    bindings.put(sym, obj);
  }

  public LispObject retrieveBinding(Symbol sym) {
    return bindings.get(sym);
  }

  public Set<Map.Entry<Symbol, LispObject>> bindings() {
    return bindings.entrySet();
  }

  public Environment clone() {
    Environment newEnv = new Environment();

    for (var binding : bindings()) {
      var sym = binding.getKey();
      var val = binding.getValue();

      newEnv.addSymbol(sym.sym);
      newEnv.bindSymbol(newEnv.retrieveSymbol(sym.sym), val);
    }

    return newEnv;
  }

  public static Environment getInitEnv() {
    return initialEnv;
  }

  @Override
  public String toString() {
    var sb = new StringBuffer();
    for (var binding : bindings.entrySet()) {
      sb.append(binding.getKey())
          .append("(hash: ")
          .append(binding.getKey().hashCode())
          .append(")")
          .append("=>")
          .append(binding.getValue())
          .append("\n");
    }

    return sb.toString();
  }
}
