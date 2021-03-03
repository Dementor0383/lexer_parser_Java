package com.github.Dementor0383.lexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Lexer {
    private final BufferedReader br;
    private final List<Token> tokens = new ArrayList<>();

    public Lexer(BufferedReader br) {
        this.br = br;
    }
    public List<Token> scan() {
        String line;
        int nLine = 1;
        boolean counter = false;
        while ((line = readLine()) != null) {
            if (counter == false){
                counter = true;
                continue;
            }
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                if (Character.isWhitespace(c)) continue;
                if (Character.isLetter(c)) {
                    i = scanIdentifier(line, i, nLine);
                    continue;
                }
                if (Character.isDigit(c)) {
                    i = scanNumber(line, i, nLine);
                    continue;
                }
                TokenType tokenType = switch (c) {
                    case '(' -> TokenType.OP_BRACE;
                    case ')' -> TokenType.CL_BRACE;
                    case '<' -> TokenType.OP_ANGLE_BRACE;
                    case '>' -> TokenType.CL_ANGLE_BRACE;
                    case '.' -> TokenType.DOT;
                    case '!' -> TokenType.EXCLAMATION_MARK;
                    case ':' -> TokenType.COLON;
                    case '"' -> TokenType.QUOTATION_MARKS;
                    case '?' -> TokenType.QUESTION_MARKS;
                    case '/' -> TokenType.SLASH;
                    case '$' -> TokenType.DOLLAR;
                    case '=' -> TokenType.EQUAL;
                    default -> null;
                };

                if (tokenType == null) {
                    throw new IllegalStateException(String.format("Unexpected char '%s' at line %d", c, nLine));
                }

                tokens.add(new Token(c, nLine, tokenType));
            }
            nLine++;
            tokens.add(new Token(null, nLine, TokenType.EOL));
        }
        tokens.add(new Token(null, nLine, TokenType.EOF));
        return tokens;
    }

    private int scanIdentifier(String line, int i, int nLine) {
        return scanSequence(line, i, nLine, Character::isLetter, TokenType.WORD);
    }

    private int scanNumber(String line, int i, int nLine) {
        return scanSequence(line, i, nLine, Character::isDigit, TokenType.NUMBER);
    }

    private int scanSequence(String line, int i, int nLine, Predicate<Character> charPredicate, TokenType tokenType) {
        StringBuilder sb = new StringBuilder();
        while (i < line.length()) {
            char c = line.charAt(i);//в строке line берется i символ
            if (!charPredicate.test(c)) break;//если символ c не соответсвует проверке на isLetter или isDigit, то цикл прерывается
            sb.append(c);
            i++;
        }
        String sbLine = sb.toString();
        if (sbLine.equals("testsuite")) tokenType = TokenType.TEST_SUITE;
        else if (sbLine.equals("at")) tokenType = TokenType.AT;
        else if (sbLine.equals("failure")) tokenType = TokenType.FAILURE;
        else if (sbLine.equals("testcase")) tokenType = TokenType.TESTCASE;
        else if (sbLine.equals("skipped")) tokenType = TokenType.SKIPPED;

        tokens.add(new Token(sb.toString(), nLine, tokenType));
        return i - 1;
    }

    private String readLine() {
        try {
            return br.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

