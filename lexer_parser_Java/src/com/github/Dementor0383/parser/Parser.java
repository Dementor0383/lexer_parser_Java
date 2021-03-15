package com.github.Dementor0383.parser;

import com.github.Dementor0383.lexer.Token;
import com.github.Dementor0383.lexer.TokenType;
import com.github.Dementor0383.parser.model.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private int pos;
    private boolean doubleTestSuiteFlag = false;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<TestSection> parse() {
        List<TestSection> partTest = new ArrayList<>();
        while (peek().tokenType() != TokenType.EOF) {
            TestSection test = parseAngle();
            if (test == null) {
                if (peek().tokenType() != TokenType.EOF) pos++;
                continue;
            }
            partTest.add(test);
            if (peek().tokenType() != TokenType.EOF) pos++;
        }
        return partTest;
    }

    private TestSection parseAngle() {
        TestSection partTest;
        if (tokens.get(pos).tokenType() != TokenType.OP_ANGLE_BRACE) {
            throw error(String.format("No open angle brace at line %d!", tokens.get(pos).line()));
        }
        if (peek().tokenType() == TokenType.OP_ANGLE_BRACE) {
            updatePos();
            if (tokens.get(pos).tokenType() == TokenType.SLASH && tokens.get(pos + 1).tokenType() == TokenType.TEST_SUITE) {
                updatePos();
                updatePos();
                if (tokens.get(pos).tokenType() != TokenType.CL_ANGLE_BRACE) {
                    throw error(String.format("No close angle brace at line %d!", tokens.get(pos).line()));
                }
                updatePos();
                return null;
            }
            if (peek().tokenType() == TokenType.TEST_SUITE) {
                if (doubleTestSuiteFlag) {
                    throw error("Bad structure, double test suite tag");
                }
                updatePos();
                partTest = parseTestSuite();
                doubleTestSuiteFlag = true;
                return partTest;
            } else if (peek().tokenType() == TokenType.TESTCASE) {
                updatePos();
                List<TestSection> testCases = new ArrayList<>();
                while (peek().tokenType() != TokenType.TEST_SUITE) {
                    testCases.add(parseTestCaseSection());
                    Token token = update();
                    while ((token.tokenType() != TokenType.TESTCASE)) {
                        if (peek().tokenType() == TokenType.TEST_SUITE) {
                            if (tokens.get(pos - 1).tokenType() != TokenType.SLASH) {
                                throw error(String.format("No slash before close testsuite at line %d!", tokens.get(pos - 1).line()));
                            }
                            if (tokens.get(pos - 2).tokenType() != TokenType.OP_ANGLE_BRACE) {
                                throw error(String.format("No open angle brace at line %d!", tokens.get(pos - 2).line()));
                            }
                            break;
                        }
                        if (tokens.get(pos + 1).tokenType() != TokenType.EOF) pos++;
                        token = update();
                    }
                }
                updatePos();
                if (tokens.get(pos).tokenType() != TokenType.CL_ANGLE_BRACE) {
                    throw error(String.format("No close angle brace at line %d!", tokens.get(pos).line()));
                }
                updatePos();
                doubleTestSuiteFlag = false;
                return new TestsList(testCases);
            } else if (peek().tokenType() == TokenType.PROPERTIES) {
                updatePos();
                if (peek().tokenType() != TokenType.CL_ANGLE_BRACE) {
                    throw error(String.format("No close angle brace at line %d!", tokens.get(pos).line()));
                }
                checkProperties();
            }
        }
        return null;
    }

    private void checkProperties() {
        Token token = advance();
        if (token.tokenType() != TokenType.CL_ANGLE_BRACE) {
            throw error(String.format("No close angle brace at line %d!", tokens.get(pos).line()));
        }
        token = advance();
        boolean propertyOpen = false;
        while (peek().tokenType() != TokenType.PROPERTIES) {
            while (token.tokenType() != TokenType.PROPERTY) {
                if (peek().tokenType() == TokenType.PROPERTIES) {
                    propertyOpen = false;
                    break;
                }
                if (token.tokenType() == TokenType.EOL) {
                    token = checkEOF();
                }
                if (token.tokenType() != TokenType.OP_ANGLE_BRACE) {
                    throw error(String.format("No open angle brace at line %d!", tokens.get(pos).line()));
                }
                token = advance();
                propertyOpen = true;
            }
            if (propertyOpen) {
                while (peek().tokenType() != TokenType.EOL) {
                    token = advance();
                }
                if (token.tokenType() != TokenType.CL_ANGLE_BRACE) {
                    throw error(String.format("No close angle brace at line %d!", tokens.get(pos).line()));
                }
                if (tokens.get(pos - 2).tokenType() != TokenType.SLASH)
                    throw error(String.format("No close angle brace at line %d!", tokens.get(pos).line()));
                updatePos();
                propertyOpen = false;
            }
            if (peek().tokenType() == TokenType.PROPERTIES) break;
            token = advance();
        }
        updatePos();
        if (peek().tokenType() != TokenType.CL_ANGLE_BRACE)
            throw error(String.format("No close angle brace at line %d!", tokens.get(pos).line()));
        updatePos();
    }

    private TestSection parseTestCaseSection() {
        Token token = advance();
        TestSection partTest;
        String className = "";
        String name = "";
        String time = "";
        boolean classNameFlag = false;
        boolean nameFlag = false;
        boolean timeFlag = false;
        while (peek().tokenType() != TokenType.EOL) {
            if (token.value().equals("classname")) {
                if (classNameFlag) {
                    throw error(String.format("Double classname filed at line%d", token.line()));
                }
                classNameFlag = true;
                while (peek().tokenType() != TokenType.QUOTATION_MARKS) updatePos();
                checkField();
                updatePos();
                token = advance();
                className = token.value();
                token = advance();
                continue;
            }
            if (token.value().equals("name")) {
                if (nameFlag) {
                    throw error(String.format("Double name filed at line%d", token.line()));
                }
                nameFlag = true;
                while (peek().tokenType() != TokenType.QUOTATION_MARKS) updatePos();
                checkField();
                updatePos();
                token = advance();
                name = token.value();
                token = advance();
                continue;
            }
            if (token.value().equals("time")) {
                if (timeFlag) {
                    throw error(String.format("Double time filed at line%d", token.line()));
                }
                timeFlag = true;
                while (peek().tokenType() != TokenType.QUOTATION_MARKS) updatePos();
                checkField();
                updatePos();
                token = advance();
                time = token.value();
                token = advance();
                continue;
            }
            token = advance();
        }

        token = checkEOF();
        if (token.tokenType() != TokenType.OP_ANGLE_BRACE) {
            throw error(String.format("No open angle brace at line %d!", token.line()));
        }

        token = advance();
        if (token.tokenType() == TokenType.FAILURE) {
            partTest = parseFailTest(className, name, time);
            while (token.tokenType() != TokenType.TESTCASE) token = checkEOF();
            updatePos();
            return partTest;
        } else if (token.tokenType() == TokenType.SKIPPED) {
            while (token.tokenType() != TokenType.TESTCASE) token = checkEOF();
            if (tokens.get(pos - 1).tokenType() != TokenType.OP_ANGLE_BRACE) {
                throw error(String.format("No slash before close testsuite at line %d!", tokens.get(pos - 1).line()));
            }
            updatePos();
            return new Test("skipped", className, name, time, null, null, null);
        }
        if (token.tokenType() == TokenType.TEST_SUITE) {
            if (tokens.get(pos - 1).tokenType() != TokenType.SLASH) {
                throw error(String.format("No slash before close testsuite at line %d!", tokens.get(pos - 1).line()));
            }
            if (tokens.get(pos - 2).tokenType() != TokenType.OP_ANGLE_BRACE) {
                throw error(String.format("No open angle brace at line %d!", tokens.get(pos - 2).line()));
            }
        }
        if (token.tokenType() == TokenType.TESTCASE) {
            if (tokens.get(pos - 2).tokenType() != TokenType.OP_ANGLE_BRACE) {
                throw error(String.format("No slash before close testsuite at line %d!", tokens.get(pos - 1).line()));
            }
        }
        return new Test("passed", className, name, time, null, null, null);

    }

    private TestSection parseFailTest(String className, String name, String time) {
        Token token = advance();
        String failureMessage = "";
        String type = "";
        boolean messageFlag = false;
        boolean typeFlag = false;
        List<String> failureLines = new ArrayList<>();
        while (peek().tokenType() != TokenType.FAILURE) {
            if (peek().tokenType() == TokenType.EOL) {
                token = checkEOF();
            }
            if (token.value().equals("message")) {
                if (messageFlag) {
                    throw error(String.format("Double message filed at line%d", token.line()));
                }
                messageFlag = true;
                while (peek().tokenType() != TokenType.QUOTATION_MARKS) updatePos();
                checkField();
                updatePos();
                token = advance();
                failureMessage = token.value();
                token = advance();
                continue;
            }
            if (token.value().equals("type")) {
                if (typeFlag) {
                    throw error(String.format("Double type filed at line%d", token.line()));
                }
                typeFlag = true;
                while (peek().tokenType() != TokenType.QUOTATION_MARKS) updatePos();
                checkField();
                updatePos();
                token = advance();
                type = token.value();
                token = advance();
                failureLines.add(type + ": " + failureMessage);
                continue;
            }
            if (token.value().equals("at")) {
                StringBuilder failureLine = new StringBuilder(token.value() + " ");
                while (peek().tokenType() != TokenType.EOL) {
                    token = advance();
                    failureLine.append(token.value());
                }
                failureLines.add(failureLine.toString());
                token = checkEOF();
                continue;
            }
            token = advance();
        }
        if (tokens.get(pos - 2).tokenType() != TokenType.OP_ANGLE_BRACE) {
            throw error(String.format("No open angle brace before FAILURE at line %d!", token.line()));
        }
        if (tokens.get(pos + 1).tokenType() != TokenType.CL_ANGLE_BRACE) {
            throw error(String.format("No close angle brace before FAILURE at line %d!", token.line()));
        }
        if (token.tokenType() != TokenType.SLASH) {
            throw error(String.format("No slash before close FAILURE at line %d!", token.line()));
        }
        return new Test("failure", className, name, time, failureMessage, type, failureLines);
    }

    private TestSection parseTestSuite() {
        Token token = advance();
        int tests = 0;
        int failures = 0;
        String name = "";
        String time = "";
        int errors = 0;
        int skipped = 0;
        boolean testFlag = false;
        boolean failuresFlag = false;
        boolean nameFlag = false;
        boolean timeFlag = false;
        boolean errorsFlag = false;
        boolean skippedFlag = false;
        while (peek().tokenType() != TokenType.EOL) {
            if (token.value().equals("tests")) {
                if (testFlag) {
                    throw error(String.format("Double test filed at line%d", token.line()));
                }
                testFlag = true;
                while (peek().tokenType() != TokenType.QUOTATION_MARKS) updatePos();
                checkField();
                updatePos();
                token = advance();
                tests = Integer.parseInt(token.value());
                token = advance();
                continue;
            }
            if (token.value().equals("failures")) {
                if (failuresFlag) {
                    throw error(String.format("Double failures filed at line%d", token.line()));
                }
                failuresFlag = true;
                while (peek().tokenType() != TokenType.QUOTATION_MARKS) updatePos();
                checkField();
                updatePos();
                token = advance();
                failures = Integer.parseInt(token.value());
                token = advance();
                continue;
            }
            if (token.value().equals("name")) {
                if (nameFlag) {
                    throw error(String.format("Double name filed at line%d", token.line()));
                }
                nameFlag = true;
                while (peek().tokenType() != TokenType.QUOTATION_MARKS) updatePos();
                checkField();
                updatePos();
                token = advance();
                name = token.value();
                token = advance();
                continue;
            }
            if (token.value().equals("time")) {
                if (timeFlag) {
                    throw error(String.format("Double time filed at line%d", token.line()));
                }
                timeFlag = true;
                while (peek().tokenType() != TokenType.QUOTATION_MARKS) updatePos();
                checkField();
                updatePos();
                token = advance();
                time = token.value();
                token = advance();
                continue;
            }
            if (token.value().equals("errors")) {
                if (errorsFlag) {
                    throw error(String.format("Double error filed at line%d", token.line()));
                }
                errorsFlag = true;
                while (peek().tokenType() != TokenType.QUOTATION_MARKS) updatePos();
                checkField();
                updatePos();
                token = advance();
                errors = Integer.parseInt(token.value());
                token = advance();
                continue;
            }
            if (token.value().equals("skipped")) {
                if (skippedFlag) {
                    throw error(String.format("Double skipped filed at line%d", token.line()));
                }
                skippedFlag = true;
                while (peek().tokenType() != TokenType.QUOTATION_MARKS) updatePos();
                checkField();
                updatePos();
                token = advance();
                skipped = Integer.parseInt(token.value());
                token = advance();
                continue;
            }
            token = advance();
        }
        if (token.tokenType() != TokenType.CL_ANGLE_BRACE) {
            throw error(String.format("No close angle brace at line %d!", token.line()));
        }
        return new TestSuite("testsuite", tests, failures, name, time, errors, skipped);
    }

    private static RuntimeException error(String message) {
        return new IllegalStateException(message);
    }

    private Token advance() {
        Token token = tokens.get(pos);
        if (token.tokenType() != TokenType.EOL) pos++;
        return token;
    }

    private Token update() {
        return tokens.get(pos - 1);
    }

    private void updatePos() {
        if (tokens.get(pos).tokenType() != TokenType.EOL) pos++;
    }

    private Token checkEOF() {
        Token token;
        if (tokens.get(pos).tokenType() != TokenType.EOF) {
            pos++;
            token = advance();
        } else {
            throw error(String.format("Error at line %d!", tokens.get(pos).line()));
        }
        return token;
    }

    private void checkField() {
        if (tokens.get(pos - 2).tokenType() != TokenType.WORD && tokens.get(pos - 2).tokenType() != TokenType.SKIPPED) {
            throw error(String.format("Error with field at line %d!", tokens.get(pos - 2).line()));
        }
    }

    private Token peek() {
        return tokens.get(pos);
    }
}
