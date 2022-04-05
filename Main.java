import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
  private static final String PROMPT = "> ";

  public static void main(String[] args) {
    try (var in = new BufferedReader(new InputStreamReader(System.in))) {
      Reader reader;

      while (true) {
        System.out.print(PROMPT);
        reader = new Reader(new Lexer(in.readLine().trim()));
        for (var expr : reader.read()) {
          System.out.println(Evaluator.eval(expr));
        }
      }

    } catch (Throwable err) {
      err.printStackTrace();
    }
  }
}

class Evaluator {
  public static LispObject eval(LispObject obj) {
    return switch (obj) {
      case Integer num -> num;

      case Symbol sym -> {
        if (!Environment.hasSymbol(sym.sym)) {
          throw new RuntimeException(String.format("%s is not bound", sym));
        }

        yield Environment.retrieveSymbol(sym.sym);
      }

      case Cons cons -> {
        if (cons.car instanceof Symbol op) {
          yield switch (op.sym) {
            case "QUOTE" -> {
              if (cons.cdr.isNil() || !Util.cdr(cons.cdr).isNil()) {
                throw new RuntimeException("invalid number of args to quote");
              }

              yield Util.car(cons.cdr);
            }

            case "DEF" -> {
              if (cons.cdr.isNil()
                  || Util.cdr(cons.cdr).isNil()
                  || !Util.cdr(Util.cdr(cons.cdr)).isNil()) {
                throw new RuntimeException("invalid number of arguments for def");
              }
              var symName = Util.car(cons.cdr);
              var symVal = Evaluator.eval(Util.car(Util.cdr(cons.cdr)));

              Environment.addSymbol(((Symbol) symName).sym, symVal);
              yield symName;
            }

            default -> Util.nil;
          };
        } else {
          throw new RuntimeException(String.format("%s is not an operator", cons));
        }
      }

      default -> throw new RuntimeException(String.format("eval for %s is not supported", obj));
    };
  }
}

abstract sealed class LispObject permits Nil, Integer, Symbol, Cons, Eof {
  protected boolean isNil() {
    return false;
  }

  protected boolean isCons() {
    return false;
  }
}

final class Nil extends LispObject {
  @Override
  public boolean isNil() {
    return true;
  }

  @Override
  public String toString() {
    return "NIL";
  }
}

final class Integer extends LispObject {
  final int integer;

  public Integer(int integer) {
    this.integer = integer;
  }

  @Override
  public String toString() {
    return String.valueOf(integer);
  }
}

final class Symbol extends LispObject {
  public String sym;

  public Symbol(final String sym) {
    this.sym = sym;
  }

  @Override
  public String toString() {
    return sym;
  }
}

final class Cons extends LispObject {
  public LispObject car;
  public LispObject cdr;

  public Cons(final LispObject car, final LispObject cdr) {
    this.car = car;
    this.cdr = cdr;
  }

  @Override
  public boolean isCons() {
    return true;
  }

  @Override
  public String toString() {
    var sb = new StringBuffer("(");
    sb.append(car.toString());
    var obj = cdr;

    while (obj != Util.nil) {
      if (obj instanceof Cons cons) {
        sb.append(" ");
        sb.append(cons.car.toString());
        obj = cons.cdr;
      } else {
        sb.append(" . ");
        sb.append(obj.toString());
        break;
      }
    }
    sb.append(")");
    return sb.toString();
  }
}

final class Eof extends LispObject {
  @Override
  public String toString() {
    return "EOF";
  }
}

class Environment {
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

class Util {
  public static LispObject makeInteger(String intStr) {
    try {
      return new Integer(java.lang.Integer.parseInt(intStr));
    } catch (NumberFormatException ex) {
      throw new RuntimeException(String.format("%s is not an integer", intStr));
    }
  }

  public static LispObject makeSymbol(String sym) {
    if (Environment.hasSymbol(sym)) {
      return Environment.retrieveSymbol(sym);
    }

    Environment.addSymbol(sym, new Symbol(sym.toUpperCase()));
    return Environment.retrieveSymbol(sym);
  }

  public static LispObject makeNil() {
    return nil;
  }

  public static LispObject makeCons(LispObject car, LispObject cdr) {
    return new Cons(car, cdr);
  }

  public static LispObject reverse(LispObject lst) {
    var obj = Util.nil;

    while (lst instanceof Cons cons) {
      var temp = cons.cdr;
      cons.cdr = obj;
      obj = cons;
      lst = temp;
    }

    return obj;
  }

  public static LispObject car(LispObject obj) {
    if (obj instanceof Cons cons) {
      return cons.car;
    }
    return Util.nil;
  }

  public static LispObject cdr(LispObject obj) {
    if (obj instanceof Cons cons) {
      return cons.cdr;
    }
    return Util.nil;
  }

  public static final LispObject nil = new Nil();
  public static final LispObject constEof = new Eof();
}

class Reader {
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
    var form = Util.makeCons(readForm(), Util.nil);
    while (currTok.kind() != TokenType.Eof && currTok.kind() != TokenType.RightParen) {
      form = Util.makeCons(readForm(), form);
    }

    if (currTok.kind() == TokenType.Eof) {
      throw new RuntimeException("unmatched right parenthesis");
    }
    advance();

    form = Util.reverse(form);

    return form;
  }

  private LispObject readForm() {
    return switch (currTok.kind()) {
      case Eof -> Util.constEof;

      case Integer -> {
        var integer = Util.makeInteger(currTok.spelling());
        advance();
        yield integer;
      }

      case Quote -> {
        advance();
        var form = readForm();
        yield Util.makeCons(Util.makeSymbol("quote"), Util.makeCons(form, Util.nil));
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

      case RightParen -> throw new RuntimeException("unmatched parenthesis");

      default -> throw new RuntimeException(String.format("invalid token: %s", currTok));
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

class Lexer {
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
      throw new RuntimeException("skipped past the end of the input");
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
          'z' -> {
        while (Character.isLetterOrDigit(currChar())) {
          eatIt();
        }

        yield TokenType.Symbol;
      }

      case '\u0000' -> TokenType.Eof;

      default -> throw new RuntimeException(
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
      throw new RuntimeException("[Lexer] idx out of range");
    }

    return tokens.get(currIdx++);
  }
}

record Token(TokenType kind, String spelling) {}

enum TokenType {
  Eof,
  Integer,
  LeftParen,
  QuasiQuote,
  Quote,
  RightParen,
  Symbol,
  Unquote,
  UnquoteSplice;
}