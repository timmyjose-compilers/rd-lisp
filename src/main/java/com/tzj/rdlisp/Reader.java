package com.tzj.rdlisp;

import java.util.ArrayList;
import java.util.List;

public class Reader {
  private Lexer lexer;
  private Token currTok;

  public Reader(final Lexer lexer) {
    this.lexer = lexer;
    this.currTok = lexer.nextToken();
  }

  private void advance() {
    currTok = lexer.nextToken();
  }

  private LispObject readList() {
    var form = Util.nil;
    while (currTok.kind() != TokenType.Eof && currTok.kind() != TokenType.RightParen) {
      if (currTok.kind() == TokenType.Dot) {
        advance();
        var cdr = readForm();
        if (currTok.kind() != TokenType.RightParen) {
          throw new Error("illegal dotted pair - more forms after cdr element");
        }
        advance();
        return Util.reverse(Util.makeCons(cdr, form));
      }
      form = Util.makeCons(readForm(), form);
    }

    if (currTok.kind() == TokenType.Eof) {
      throw new Error("unmatched right parenthesis");
    }
    advance();

    return Util.reverse(form);
  }

  private LispObject readForm() {
    return switch (currTok.kind()) {
      case Eof -> Util.eof;

      case Integer -> {
        var integer = Util.makeInteger(currTok.spelling());
        advance();
        yield integer;
      }

      case AndRest -> {
        advance();
        var form = readForm();
        if (!(form instanceof Symbol)) {
          throw new Error(
              String.format("&rest expects a symbol to bind params to, but got %s", form));
        }

        yield Util.makeCons(Util.vararg, form);
      }

      case Quote -> {
        advance();
        var form = readForm();
        yield Util.makeCons(Util.quote, Util.makeCons(form, Util.nil));
      }

      case Symbol -> {
        var sym = Util.makeSymbol(currTok.spelling());
        advance();
        yield sym;
      }

      case LeftParen -> {
        advance();
        yield readList();
      }

      case RightParen -> throw new Error("unmatched parenthesis");

      default -> throw new Error(String.format("invalid token: %s", currTok));
    };
  }

  public List<LispObject> read() {
    List<LispObject> forms = new ArrayList<>();
    while (true) {
      var form = readForm();
      if (form instanceof Eof) {
        break;
      }
      forms.add(form);
    }

    return forms;
  }
}
