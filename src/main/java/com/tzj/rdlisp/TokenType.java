package com.tzj.rdlisp;

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
