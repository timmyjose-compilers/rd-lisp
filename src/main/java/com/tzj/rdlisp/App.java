package com.tzj.rdlisp;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import jline.console.ConsoleReader;

public class App {
  private static final String STDLIB_PATH = "stdlib.lisp";

  private static final String PROMPT = "> ";

  public static void main(String[] args) {
    try (ConsoleReader console = new ConsoleReader()) {
      setupStdLib();

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

  private static void setupStdLib() throws IOException {
    var reader = new Reader(new Lexer(readStdLib()));
    for (var expr : reader.read()) {
      Evaluator.eval(Environment.getInitEnv(), expr);
    }

    System.out.println("Loaded up the standard library");
  }

  private static String readStdLib() throws IOException {
    return Files.readString(Path.of(STDLIB_PATH));
  }
}
