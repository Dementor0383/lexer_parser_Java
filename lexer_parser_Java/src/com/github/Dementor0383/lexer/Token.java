package com.github.Dementor0383.lexer;

public record Token(String value, int line, TokenType tokenType) {

public Token(char c, int line, TokenType tokenType) {
        this(String.valueOf(c), line, tokenType);
}
}