package com.github.Dementor0383.parser;

import com.github.Dementor0383.lexer.Token;
import com.github.Dementor0383.lexer.TokenType;
import com.github.Dementor0383.parser.model.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private int pos;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<TestSection> parse(){
        List<TestSection> partTest = new ArrayList<>();
        while (peek().tokenType() != TokenType.EOF){
            TestSection test = parseAngle();
            if (test == null) break;
            partTest.add(test);
            if (peek().tokenType() != TokenType.EOF) pos++;
        }
        return partTest;
    }

    private TestSection parseAngle(){
        TestSection partTest = null;
        if (tokens.get(pos).tokenType() != TokenType.OP_ANGLE_BRACE){
            throw error (String.format("No open angle brace at line %d!", tokens.get(pos).line()));
        }
        if (peek().tokenType() == TokenType.OP_ANGLE_BRACE){
            pos++;
            if (peek().tokenType() == TokenType.TEST_SUITE) {
                pos++;
                partTest = parseTestSuite();
                return partTest;
            }
            else if (peek().tokenType() == TokenType.TESTCASE){
                pos++;
                List<TestSection> testCases = new ArrayList<>();
                while (peek().tokenType() != TokenType.TEST_SUITE){
                    testCases.add(parseTestCaseSection());
                    Token token = update();
                    while ((token.tokenType() != TokenType.TESTCASE) ) {
                        if (peek().tokenType() == TokenType.TEST_SUITE){
                            if (tokens.get(pos - 1).tokenType() != TokenType.SLASH){
                                throw error (String.format("No slash before close testsuite at line %d!", tokens.get(pos - 1).line()));
                            }
                            if (tokens.get(pos - 2).tokenType() != TokenType.OP_ANGLE_BRACE){
                                throw error (String.format("No open angle brace at line %d!", tokens.get(pos - 2).line()));
                            }
                            break;
                        }
                         pos++;
                        token = update();
                    }
                    }
                pos++;
                if (tokens.get(pos).tokenType() != TokenType.CL_ANGLE_BRACE){
                    throw error (String.format("No close angle brace at line %d!", tokens.get(pos).line()));
                }
                pos++;
                return new TestsList(testCases);
            }
        }
        return partTest;
    }

    private TestSection parseTestCaseSection(){
        Token token = advance();
        TokenType tokenType = token.tokenType();
        TestSection partTest = null;
        String className = "";
        String name = "";
        String time = "";
        while (peek().tokenType() != TokenType.EOL) {
            if (token.value().equals("classname")){
                while (peek().tokenType() != TokenType.WORD) token = advance();
                token = advance();
                className = token.value();
                token = advance();
                continue;
            }
            if (token.value().equals("name")){
                while (peek().tokenType() != TokenType.WORD) token = advance();
                token = advance();
                name = token.value();
                token = advance();
                continue;
            }
            if (token.value().equals("time")){
                while (peek().tokenType() != TokenType.NUMBER) token = advance();
                while (peek().tokenType() != TokenType.QUOTATION_MARKS) {
                    token = advance();
                    time = time + token.value();
                }
                pos++;
                continue;
            }
            token = advance();
        }
        pos++;
        token = advance();
        if (token.tokenType() != TokenType.OP_ANGLE_BRACE) {
            throw error (String.format("No open angle brace at line %d!", token.line()));
        }
        token = advance();
        if (token.tokenType() == TokenType.FAILURE) {
            partTest = parseFailTest(className, name, time);
            while (token.tokenType() != TokenType.TESTCASE) token = advanceNotEOL();
            token = advance();
            return partTest;
        }
        else if (token.tokenType() == TokenType.SKIPPED){
            while (token.tokenType() != TokenType.TESTCASE) token = advanceNotEOL();
            if (token.tokenType() == TokenType.TESTCASE){
                if (tokens.get(pos - 1).tokenType() != TokenType.OP_ANGLE_BRACE){
                    throw error (String.format("No slash before close testsuite at line %d!", tokens.get(pos - 1).line()));
                }
            }
            token = advance();
            return new Test("skipped", className, name, time, null, null, null);
        }
        if (token.tokenType() == TokenType.TEST_SUITE){
            if (tokens.get(pos - 1).tokenType() != TokenType.SLASH){
                throw error (String.format("No slash before close testsuite at line %d!", tokens.get(pos - 1).line()));
            }
            if (tokens.get(pos - 2).tokenType() != TokenType.OP_ANGLE_BRACE){
                throw error (String.format("No open angle brace at line %d!", tokens.get(pos - 2).line()));
            }
        }
        if (token.tokenType() == TokenType.TESTCASE){
            if (tokens.get(pos - 2).tokenType() != TokenType.OP_ANGLE_BRACE){
                throw error (String.format("No slash before close testsuite at line %d!", tokens.get(pos - 1).line()));
            }
        }
        return new Test("passed", className, name, time, null, null, null);
    }

    private TestSection parseFailTest(String className, String name, String time){
        Token token = advance();
        TokenType tokenType = token.tokenType();
        TestSection partTest = null;
        String failureMessage = "";
        String type = "";
        List<String> failureLines = new ArrayList<>();
        while (peek().tokenType() != TokenType.FAILURE) {
            if (peek().tokenType() == TokenType.EOL) {
                pos++;
                token = advance();
            }
            if (token.value().equals("message")){
                while (peek().tokenType() != TokenType.WORD) token = advance();
                while (peek().tokenType() != TokenType.QUOTATION_MARKS) {
                    token = advance();
                    if (token.tokenType() == TokenType.WORD) failureMessage = failureMessage + " ";
                    failureMessage = failureMessage + token.value();
                }
                token = advance();
                continue;
            }
            if (token.value().equals("type")){
                while (peek().tokenType() != TokenType.WORD) token = advance();
                while (peek().tokenType() != TokenType.QUOTATION_MARKS) {
                    token = advance();
                    type = type + token.value();
                }
                token = advance();
                failureLines.add(type + ": " + failureMessage);
                continue;
            }
            if (token.value().equals("at")){
                String failureLine = token.value() + " ";
                while (peek().tokenType() != TokenType.EOL) {
                    token = advance();
                    failureLine = failureLine + token.value();
                }
                pos++;
                failureLines.add(failureLine);
                token = advance();
                continue;
            }
            token = advance();
        }
        if (tokens.get(pos - 2).tokenType() != TokenType.OP_ANGLE_BRACE) {
            throw error (String.format("No open angle brace before FAILURE at line %d!", token.line()));
        }
        if (tokens.get(pos + 1).tokenType() != TokenType.CL_ANGLE_BRACE) {
            throw error (String.format("No close angle brace before FAILURE at line %d!", token.line()));
        }
        if (token.tokenType() != TokenType.SLASH) {
            throw error (String.format("No slash before close FAILURE at line %d!", token.line()));
                }
        return new Test("failure", className, name, time, failureMessage, type, failureLines);
    }

    private TestSection parseTestSuite(){
        Token token = advance();
        TokenType tokenType = token.tokenType();
        int tests = 0;
        int failures = 0;
        String name = "";
        String time = "";
        int errors = 0;
        int skipped = 0;
        while (peek().tokenType() != TokenType.EOL) {
            if (token.value().equals("tests")){
                while (peek().tokenType() != TokenType.NUMBER) token = advance();
                token = advance();
                tests = Integer.parseInt(token.value());
                token = advance();
                continue;
            }
            if (token.value().equals("failures")){
                while (peek().tokenType() != TokenType.NUMBER) token = advance();
                token = advance();
                failures = Integer.parseInt(token.value());
                token = advance();
                continue;
            }
            if (token.value().equals("name")){
                while (peek().tokenType() != TokenType.WORD) token = advance();
                token = advance();
                name = token.value();
                token = advance();
                continue;
            }
            if (token.value().equals("time")){
                while (peek().tokenType() != TokenType.NUMBER) token = advance();
                while (peek().tokenType() != TokenType.QUOTATION_MARKS) {
                    token = advance();
                    time = time + token.value();
                }
                token = advance();
                continue;
            }
            if (token.value().equals("errors")){
                while (peek().tokenType() != TokenType.NUMBER) token = advance();
                token = advance();
                errors = Integer.parseInt(token.value());
                token = advance();
                continue;
            }
            if (token.value().equals("skipped")){
                while (peek().tokenType() != TokenType.NUMBER) token = advance();
                token = advance();
                skipped = Integer.parseInt(token.value());
                pos++;
                if (peek().tokenType() != TokenType.CL_ANGLE_BRACE) {
                    throw error (String.format("No close angle brace at line %d!", token.line()));
                }
                continue;
            }
            token = advance();
        }
        return new TestSuite("testsuite", tests, failures, name, time, errors, skipped);
    }

    private static RuntimeException error(String message){
        return new IllegalStateException(message);
    }

    private Token advance() {
        Token token = tokens.get(pos);
        if (token.tokenType() != TokenType.EOL) pos++;
        return token;
    }

    private Token advanceNotEOL() {
        Token token = tokens.get(pos);
        pos++;
        return token;
    }

    private Token update() {
        Token token = tokens.get(pos - 1);
        return token;
    }

    private Token peek() {
        return tokens.get(pos);
    }
}
