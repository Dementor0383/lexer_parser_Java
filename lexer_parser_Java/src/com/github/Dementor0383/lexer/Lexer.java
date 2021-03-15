package com.github.Dementor0383.lexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

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
            if (!counter) {
                if (line.equals(" ")){
                    throw error("Bad xml line\n");
                }
                checkXml(line);
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
                TokenType tokenType = switch (c) {
                    case '<' -> TokenType.OP_ANGLE_BRACE;
                    case '>' -> TokenType.CL_ANGLE_BRACE;
                    case '"' -> TokenType.QUOTATION_MARKS;
                    case '?' -> TokenType.QUESTION_MARKS;
                    case '/' -> TokenType.SLASH;
                    default -> null;
                };
                if (tokenType == TokenType.QUOTATION_MARKS) {
                    tokens.add(new Token(c, nLine, TokenType.QUOTATION_MARKS));
                    StringBuilder sb = new StringBuilder();
                    i++;
                    if (i >= line.length()) {
                        line = readLine();
                        i = 0;
                    }
                    c = line.charAt(i);//в строке line берется i символ
                    while (c != '"') {
                        sb.append(c);
                        i++;
                        if (i >= line.length()) {
                            line = readLine();
                            i = 0;
                        }
                        c = line.charAt(i);//в строке line берется i символ
                    }
                    tokens.add(new Token(sb.toString(), nLine, TokenType.IDENTIFIER));
                    tokens.add(new Token(c, nLine, TokenType.QUOTATION_MARKS));
                    continue;
                }
                if (tokenType == null){
                    tokens.add(new Token(c, nLine, TokenType.IDENTIFIER));
                    continue;
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
        return scanSequence(line, i, nLine);
    }

    private int scanSequence(String line, int i, int nLine) {
        TokenType tokenType = TokenType.WORD;
        StringBuilder sb = new StringBuilder();
        while (i < line.length()) {
            char c = line.charAt(i);//в строке line берется i символ
            if (c == '=' || c == ' ' || c == '>')
                break;//если символ c не соответсвует проверке на isLetter или isDigit, то цикл прерывается
            sb.append(c);
            i++;
        }
        String sbLine = sb.toString();
        switch (sbLine) {
            case "testsuite" -> tokenType = TokenType.TEST_SUITE;
            case "at" -> tokenType = TokenType.AT;
            case "failure" -> tokenType = TokenType.FAILURE;
            case "testcase" -> tokenType = TokenType.TESTCASE;
            case "skipped" -> tokenType = TokenType.SKIPPED;
            case "property" -> tokenType = TokenType.PROPERTY;
            case "properties" -> tokenType = TokenType.PROPERTIES;
        }

        tokens.add(new Token(sb.toString(), nLine, tokenType));
        return i - 1;
    }

    //<?xml version="1.0" encoding="UTF-8" ?>
    private void checkXml(String line) {
        int i = 1;
        char c = line.charAt(i);
        StringBuilder word = new StringBuilder();
        if (c != '?') {
            throw error("No ? mark at xml line\n");
        }
        for (i = 2; i < line.length(); i++){
            c = line.charAt(i);
            if(c == ' ') break;
            word.append(c);
        }
        if (!word.toString().equals("xml")) {
            throw error("No xml at xml line\n");
        }
        word = new StringBuilder();
        for (i = 6; i < line.length(); i++){
            c = line.charAt(i);
            if(c == ' ' || c == '=') break;
            word.append(c);
        }
        if (!word.toString().equals("version")) {
            throw error("No version at xml line\n");
        }
        word = new StringBuilder();
        for (i = 20; i < line.length(); i++){
            c = line.charAt(i);
            if(c == ' ' || c == '=') break;
            word.append(c);
        }
        if (!word.toString().equals("encoding")) {
            throw error("No encoding at xml line\n");
        }
        while (c != '>'){
            i++;
            c = line.charAt(i);
        }
        c = line.charAt(i-1);
        if (c != '?') {
            throw error("No ? mark at xml line\n");
        }
    }

    private static RuntimeException error(String message) {
        return new IllegalStateException(message);
    }

    private String readLine() {
        try {
            return br.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

