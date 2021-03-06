package com.github.Dementor0383;

import com.github.Dementor0383.lexer.Lexer;
import com.github.Dementor0383.lexer.Token;
import com.github.Dementor0383.lexer.TokenType;
import com.github.Dementor0383.parser.Parser;
import com.github.Dementor0383.parser.model.*;
import com.github.Dementor0383.FileWorkerJava;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*
CR:
 you need parser test to determine if your model is initialized correctly after lexer and parser worked.
 For example:
    public void testSuiteTag() {
        List<Token> tokens = createTokens("""
                <testsuite tests="3" failures="1" name="SomeTest" time="0.008">
                </testsuite>
                """);
        TestSuite testSuite = getTestSuite(tokens)
        Assert.assertEquals(testSuite.name(), "SomeTest");
        Assert.assertEquals(testSuite.nSuccess(), 3);
        Assert.assertEquals(testSuite.nFailure(), 1);
        Assert.assertEquals(testSuite.time(), "0.008");
    }
 here we check that for empty test suite fields are initialized correctly.
 also you might need methods that check : what will happen for test suite with one test case?
 what will happen for testsuite inside testsuite? what will happen for testsuite with testcase with failure inside? and so on
 */
public class ParserTest {
    private String rightLine = """
             <?xml version=\"1.0\" encoding=\"UTF-8\"?>
                <testsuite name=\"rspec\" tests=\"2967\" failures=\"2\" errors=\"0\" time=\"611.546160\">
                <testcase classname="SomeTest" name="doSmth" time="0"/>
                <testcase classname="SomeTest" name="doOther" time="0"/>
             </testsuite>
            """;
    private String lineOpenAngle = """
             <?xml version=\"1.0\" encoding=\"UTF-8\"?>
                <testsuite name=\"rspec\" tests=\"2967\" failures=\"2\" errors=\"0\" time=\"611.546160\">
                <testcase classname="SomeTest" name="doSmth" time="0"/>
                testcase classname="SomeTest" name="doOther" time="0"/>
             </testsuite>
            """;
    private String lineCloseAngle = """
             <?xml version=\"1.0\" encoding=\"UTF-8\"?>
                <testsuite name=\"rspec\" tests=\"2967\" failures=\"2\" errors=\"0\" time=\"611.546160\">
                <testcase classname="SomeTest" name="doSmth" time="0"/>
                <testcase classname="SomeTest" name="doOther" time="0"/>
             </testsuite
            """;
    private String lineSlash = """
             <?xml version=\"1.0\" encoding=\"UTF-8\"?>
                <testsuite name=\"rspec\" tests=\"2967\" failures=\"2\" errors=\"0\" time=\"611.546160\">
                <testcase classname="SomeTest" name="doSmth" time="0"/>
                <testcase classname="SomeTest" name="doOther" time="0"/>
             <testsuite>
            """;

    BufferedReader brRight = new BufferedReader(new StringReader(rightLine));
    Lexer actualLexer = new Lexer(brRight);
    List<Token> tokensRight = actualLexer.scan();
    Parser parserRight = new Parser(tokensRight);
    List<TestSection> partTestRight = parserRight.parse();

    @Test
    public void testOpenAngleBraceFail(){
        BufferedReader br = new BufferedReader(new StringReader(lineOpenAngle));
        Lexer lexer = new Lexer(br);
        List<Token> tokens = lexer.scan();
        Parser parser = new Parser(tokens);
        List<TestSection> partTest = parser.parse();

        Assert.assertEquals(partTest, partTestRight);
    }

    @Test
    public void testCloseAngleBraceFail(){
        BufferedReader br = new BufferedReader(new StringReader(lineCloseAngle));
        Lexer lexer = new Lexer(br);
        List<Token> tokens = lexer.scan();
        Parser parser = new Parser(tokens);
        List<TestSection> partTest = parser.parse();

        Assert.assertEquals(partTest, partTestRight);
    }

    @Test
    public void testSlashFail(){
        BufferedReader br = new BufferedReader(new StringReader(lineSlash));
        Lexer lexer = new Lexer(br);
        List<Token> tokens = lexer.scan();
        Parser parser = new Parser(tokens);
        List<TestSection> partTest = parser.parse();

        Assert.assertEquals(partTest, partTestRight);
    }
}
