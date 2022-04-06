package com.tzj.rdlisp;

import java.util.HashMap;
import java.util.Map;

public class Environment {
  private static final Map<String, LispObject> symbolTable = new HashMap<>();

  public static void addSymbol(String symStr, LispObject obj) {
    symbolTable.put(symStr, obj);
  }

  public static boolean hasSymbol(String symStr) {
    return symbolTable.containsKey(symStr);
  }

  public static LispObject retrieveSymbol(String symStr) {
    return symbolTable.get(symStr);
  }
}
