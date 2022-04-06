package com.tzj.rdlisp;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
  private String src;
  private int currPos;
  private List<Token> tokens;
  private int currIdx;
  private StringBuffer currBuf;

  public Lexer(final String src) {
    this.src = src + '\u0000';
    this.tokens = new ArrayList<>();
    this.currPos = 0;
    this.currIdx = 0;
    scanAll();
  }

  private char currChar() {
    return src.charAt(currPos);
  }

  private void skipWhitespace() {
    switch (currChar()) {
      case ';' -> {
        while (currChar() != '\u0000' && currChar() != '\n') {
          skipIt();
        }

        if (currChar() == '\n') {
          skipIt();
        }
      }

      case ' ', '\t', '\n' -> {
        while (currChar() != '\u0000' && Character.isWhitespace(currChar())) {
          skipIt();
        }
      }
    }
  }

  private void skipIt() {
    currPos++;

    if (currPos >= src.length()) {
      throw new Error("skipped past the end of the input");
    }
  }

  private void eatIt() {
    currBuf.append(currChar());
    currPos++;
  }

  private TokenType scanToken() {
    return switch (currChar()) {
      case '(' -> {
        eatIt();
        yield TokenType.LeftParen;
      }

      case ')' -> {
        eatIt();
        yield TokenType.RightParen;
      }

      case '\'' -> {
        eatIt();
        yield TokenType.Quote;
      }

      case '`' -> {
        eatIt();
        yield TokenType.QuasiQuote;
      }

      case '.' -> {
        eatIt();
        yield TokenType.Dot;
      }

      case ',' -> {
        eatIt();
        if (currChar() == '@') {
          eatIt();
          yield TokenType.UnquoteSplice;
        } else {
          yield TokenType.Unquote;
        }
      }

      case '+' -> {
        eatIt();
        yield TokenType.Symbol;
      }

      case '-' -> {
        eatIt();
        yield TokenType.Symbol;
      }

      case '*' -> {
        eatIt();
        yield TokenType.Symbol;
      }

      case '/' -> {
        eatIt();
        yield TokenType.Symbol;
      }

      case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
        eatIt();
        while (Character.isDigit(currChar())) {
          eatIt();
        }

        yield TokenType.Integer;
      }

      case 'a',
          'b',
          'c',
          'd',
          'e',
          'f',
          'g',
          'h',
          'i',
          'j',
          'k',
          'l',
          'm',
          'n',
          'o',
          'p',
          'q',
          'r',
          's',
          't',
          'u',
          'v',
          'w',
          'x',
          'y',
          'z',
          'A',
          'B',
          'C',
          'D',
          'E',
          'F',
          'G',
          'I',
          'J',
          'K',
          'L',
          'M',
          'N',
          'O',
          'P',
          'Q',
          'R',
          'S',
          'T',
          'U',
          'V',
          'W',
          'Y',
          'Z' -> {
        while (Character.isLetterOrDigit(currChar())
            || currChar() == '-'
            || currChar() == '>'
            || currChar() == '$') {

          eatIt();
        }

        yield TokenType.Symbol;
      }

      case '\u0000' -> TokenType.Eof;

      default -> throw new Error(
          String.format("invalid character %c (code: %d)\n", currChar(), (int) currChar()));
    };
  }

  private void scanAll() {
    while (true) {
      while (currChar() == ';' || Character.isWhitespace(currChar())) {
        skipWhitespace();
      }

      currBuf = new StringBuffer();
      var currKind = scanToken();
      tokens.add(new Token(currKind, currBuf.toString()));

      if (currKind == TokenType.Eof) {
        break;
      }
    }
  }

  public Token nextToken() {
    if (currIdx >= tokens.size()) {
      throw new Error("[Lexer] idx out of range");
    }

    return tokens.get(currIdx++);
  }
}
