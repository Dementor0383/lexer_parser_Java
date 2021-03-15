package com.github.Dementor0383;

import com.github.Dementor0383.lexer.Lexer;
import com.github.Dementor0383.lexer.Token;
import com.github.Dementor0383.lexer.TokenType;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class LexerTest {

    @Test
    public void testToken(){
        String line = """
                <?xml version="1.0" encoding="UTF-8" ?>
                <testsuite name="rspec" tests="2967" failures="2" errors="0" time="611.546160">
                """;
        BufferedReader br = new BufferedReader(new StringReader(line));
        Lexer lexer = new Lexer(br);
        List<Token> tokens = lexer.scan();
        List<Token> actual = new ArrayList<>();
        actual.add(new Token('<', 1, TokenType.OP_ANGLE_BRACE));
        actual.add(new Token("testsuite", 1, TokenType.TEST_SUITE));
        actual.add(new Token("name", 1, TokenType.WORD));
        actual.add(new Token('=', 1, TokenType.IDENTIFIER));
        actual.add(new Token('"', 1, TokenType.QUOTATION_MARKS));
        actual.add(new Token("rspec", 1, TokenType.IDENTIFIER));
        actual.add(new Token('"', 1, TokenType.QUOTATION_MARKS));
        actual.add(new Token("tests", 1, TokenType.WORD));
        actual.add(new Token('=', 1, TokenType.IDENTIFIER));
        actual.add(new Token('"', 1, TokenType.QUOTATION_MARKS));
        actual.add(new Token("2967", 1, TokenType.IDENTIFIER));
        actual.add(new Token('"', 1, TokenType.QUOTATION_MARKS));
        actual.add(new Token("failures", 1, TokenType.WORD));
        actual.add(new Token('=', 1, TokenType.IDENTIFIER));
        actual.add(new Token('"', 1, TokenType.QUOTATION_MARKS));
        actual.add(new Token("2", 1, TokenType.IDENTIFIER));
        actual.add(new Token('"', 1, TokenType.QUOTATION_MARKS));
        actual.add(new Token("errors", 1, TokenType.WORD));
        actual.add(new Token('=', 1, TokenType.IDENTIFIER));
        actual.add(new Token('"', 1, TokenType.QUOTATION_MARKS));
        actual.add(new Token("0", 1, TokenType.IDENTIFIER));
        actual.add(new Token('"', 1, TokenType.QUOTATION_MARKS));
        actual.add(new Token("time", 1, TokenType.WORD));
        actual.add(new Token('=', 1, TokenType.IDENTIFIER));
        actual.add(new Token('"', 1, TokenType.QUOTATION_MARKS));
        actual.add(new Token("611.546160", 1, TokenType.IDENTIFIER));
        actual.add(new Token('"', 1, TokenType.QUOTATION_MARKS));
        actual.add(new Token('>', 1, TokenType.CL_ANGLE_BRACE));
        actual.add(new Token(null, 2, TokenType.EOL));
        actual.add(new Token(null, 2, TokenType.EOF));

        Assert.assertEquals(tokens, actual);

    }

    @Test(expected = IllegalStateException.class)
    public void testQuotationMarkAtXMLLine(){
        String line = """
                <xml version="1.0" encoding="UTF-8" ?>
                <testsuite name="rspec" tests="2967" failures="2" errors="0" time="611.546160">
                """;
        BufferedReader br = new BufferedReader(new StringReader(line));
        Lexer lexer = new Lexer(br);
        List<Token> tokens = lexer.scan();
    }

    @Test(expected = IllegalStateException.class)
    public void testEncodingAtXMLLine(){
        String line = """
                <?xml version="1.0" ="UTF-8" ?>
                <testsuite name="rspec" tests="2967" failures="2" errors="0" time="611.546160">
                """;
        BufferedReader br = new BufferedReader(new StringReader(line));
        Lexer lexer = new Lexer(br);
        List<Token> tokens = lexer.scan();
    }

    @Test(expected = IllegalStateException.class)
    public void testEmptyFile(){
        String line = " ";
        BufferedReader br = new BufferedReader(new StringReader(line));
        Lexer lexer = new Lexer(br);
        List<Token> tokens = lexer.scan();
    }

}
