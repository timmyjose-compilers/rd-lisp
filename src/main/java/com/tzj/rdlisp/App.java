package com.tzj.rdlisp;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class App {
  private static final String PROMPT = "> ";

  public static void main(String[] args) {
    try (var in = new BufferedReader(new InputStreamReader(System.in))) {
      Reader reader;

      while (true) {
        try {
          System.out.print(PROMPT);
          reader = new Reader(new Lexer(in.readLine().trim()));
          for (var expr : reader.read()) {
            System.out.println(Evaluator.eval(expr));
          }
        } catch (Error err) {
          System.out.println(err);
        }
      }
    } catch (Throwable err) {
      err.printStackTrace();
    }
  }
}
