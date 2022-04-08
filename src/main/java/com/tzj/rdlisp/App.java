package com.tzj.rdlisp;

import java.io.PrintWriter;
import jline.console.ConsoleReader;

public class App {
  private static final String PROMPT = "> ";

  public static void main(String[] args) {
    try (ConsoleReader console = new ConsoleReader()) {
      Reader reader;
      var out = new PrintWriter(console.getOutput());

      while (true) {
        try {
          console.setPrompt(App.PROMPT);
          reader = new Reader(new Lexer(console.readLine().trim()));
          for (var expr : reader.read()) {
            out.println(Evaluator.eval(Environment.getInitEnv(), expr));
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
