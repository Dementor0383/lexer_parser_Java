package com.github.Dementor0383;

import com.github.Dementor0383.lexer.Lexer;
import com.github.Dementor0383.lexer.Token;
import com.github.Dementor0383.lexer.TokenType;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LexerTest {

    @Test
    public void testToken(){
        String line = """
                <?xml version=\"1.0\" encoding=\"UTF-8\"?>
                <testsuite name=\"rspec\" tests=\"2967\" failures=\"2\" errors=\"0\" time=\"611.546160\">
                """;
        BufferedReader br = new BufferedReader(new StringReader(line));
        Lexer lexer = new Lexer(br);
        List<Token> tokens = lexer.scan();
        List<Token> actual = new ArrayList<>();
        // CR: 1. i think it's enough to check types of tokens
        // CR: 2. you can make it more readable using Arrays.asList, also take a look at Assert.assertThat and org.hamcrest.core.Is.is
        // CR: 3. your test should be helpful for you, they should check different inputs that you might get and that might fail
        // CR:    e.g. what will happen for empty input? what will happen if we have one valid tag? one tag with one attribute? and so on
        actual.add(new Token('<', 1, TokenType.OP_ANGLE_BRACE));
        actual.add(new Token("testsuite", 1, TokenType.TEST_SUITE));
        actual.add(new Token("name", 1, TokenType.WORD));
        actual.add(new Token('=', 1, TokenType.EQUAL));
        actual.add(new Token('"', 1, TokenType.QUOTATION_MARKS));
        actual.add(new Token("rspec", 1, TokenType.WORD));
        actual.add(new Token('"', 1, TokenType.QUOTATION_MARKS));
        actual.add(new Token("tests", 1, TokenType.WORD));
        actual.add(new Token('=', 1, TokenType.EQUAL));
        actual.add(new Token('"', 1, TokenType.QUOTATION_MARKS));
        actual.add(new Token("2967", 1, TokenType.NUMBER));
        actual.add(new Token('"', 1, TokenType.QUOTATION_MARKS));
        actual.add(new Token("failures", 1, TokenType.WORD));
        actual.add(new Token('=', 1, TokenType.EQUAL));
        actual.add(new Token('"', 1, TokenType.QUOTATION_MARKS));
        actual.add(new Token("2", 1, TokenType.NUMBER));
        actual.add(new Token('"', 1, TokenType.QUOTATION_MARKS));
        actual.add(new Token("errors", 1, TokenType.WORD));
        actual.add(new Token('=', 1, TokenType.EQUAL));
        actual.add(new Token('"', 1, TokenType.QUOTATION_MARKS));
        actual.add(new Token("0", 1, TokenType.NUMBER));
        actual.add(new Token('"', 1, TokenType.QUOTATION_MARKS));
        actual.add(new Token("time", 1, TokenType.WORD));
        actual.add(new Token('=', 1, TokenType.EQUAL));
        actual.add(new Token('"', 1, TokenType.QUOTATION_MARKS));
        actual.add(new Token("611", 1, TokenType.NUMBER));
        actual.add(new Token(".", 1, TokenType.DOT));
        actual.add(new Token("546160", 1, TokenType.NUMBER));
        actual.add(new Token('"', 1, TokenType.QUOTATION_MARKS));
        actual.add(new Token('>', 1, TokenType.CL_ANGLE_BRACE));
        actual.add(new Token(null, 2, TokenType.EOL));
        actual.add(new Token(null, 2, TokenType.EOF));

        Assert.assertEquals(tokens, actual);

    }

}
