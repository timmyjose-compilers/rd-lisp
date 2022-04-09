package com.tzj.rdlisp;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Environment {
  private final Map<Symbol, LispObject> bindings = new HashMap<>();
  private final Map<String, Symbol> symbols = new HashMap<>();

  private static final Environment initEnv;

  static {
    // initial environment
    initEnv = new Environment();

    initEnv.addSymbol("nil");
    initEnv.addSymbol("t");
    initEnv.addSymbol("cons");
    initEnv.addSymbol("car");
    initEnv.addSymbol("cdr");
    initEnv.addSymbol("+");
    initEnv.addSymbol("-");
    initEnv.addSymbol("*");
    initEnv.addSymbol("/");
    initEnv.addSymbol("eq?");
    initEnv.addSymbol("<");
    initEnv.addSymbol("apply");
    initEnv.addSymbol("pair?");

    initEnv.bindSymbol(initEnv.retrieveSymbol("nil"), new Nil());
    initEnv.bindSymbol(initEnv.retrieveSymbol("t"), new True());
    initEnv.bindSymbol(initEnv.retrieveSymbol("cons"), new ConsFunction());
    initEnv.bindSymbol(initEnv.retrieveSymbol("car"), new CarFunction());
    initEnv.bindSymbol(initEnv.retrieveSymbol("cdr"), new CdrFunction());
    initEnv.bindSymbol(initEnv.retrieveSymbol("+"), new AddFunction());
    initEnv.bindSymbol(initEnv.retrieveSymbol("-"), new SubFunction());
    initEnv.bindSymbol(initEnv.retrieveSymbol("*"), new MulFunction());
    initEnv.bindSymbol(initEnv.retrieveSymbol("/"), new DivFunction());
    initEnv.bindSymbol(initEnv.retrieveSymbol("eq?"), new EqFunction());
    initEnv.bindSymbol(initEnv.retrieveSymbol("<"), new LessThanFunction());
    initEnv.bindSymbol(initEnv.retrieveSymbol("apply"), new ApplyFunction());
    initEnv.bindSymbol(initEnv.retrieveSymbol("pair?"), new PairFunction());
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
    return initEnv;
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
