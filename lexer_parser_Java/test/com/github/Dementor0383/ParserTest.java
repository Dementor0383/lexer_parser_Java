package com.github.Dementor0383;

import com.github.Dementor0383.lexer.Lexer;
import com.github.Dementor0383.lexer.Token;
import com.github.Dementor0383.parser.Parser;
import com.github.Dementor0383.parser.model.TestSection;
import com.github.Dementor0383.parser.model.TestSuite;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

public class ParserTest {

    @Test
    public void testEmptyTestSuite() {
        String emptyTestSuite = """ 
                <?xml version="1.0" encoding="UTF-8" ?>
                <testsuite tests="3" failures="1" name="SomeTest" time="0.008">
                 </testsuite>            
                 """;
        BufferedReader br = new BufferedReader(new StringReader(emptyTestSuite));
        Lexer lexer = new Lexer(br);
        List<Token> tokens = lexer.scan();
        Parser parser = new Parser(tokens);
        List<TestSection> partTest = parser.parse();
        TestSection list = partTest.get(0);
        TestSuite part = (TestSuite) list;
        Assert.assertEquals(part.failures(), 1);
        Assert.assertEquals(part.tests(), 3);
        Assert.assertEquals(part.time(), "0.008");
        Assert.assertEquals(part.name(), "SomeTest");
    }

    @Test(expected = IllegalStateException.class)
    public void testOpenAngleBraceFail() {
        String lineOpenAngle = """
                <?xml version="1.0" encoding="UTF-8" ?>
                <testsuite name="rspec" tests="2967" failures="2" errors="0" time="611.546160">
                    <testcase classname="SomeTest" name="doSmth" time="0"/>
                    testcase classname="SomeTest" name="doOther" time="0"/>
                 </testsuite>
                """;
        BufferedReader br = new BufferedReader(new StringReader(lineOpenAngle));
        Lexer lexer = new Lexer(br);
        List<Token> tokens = lexer.scan();
        Parser parser = new Parser(tokens);
        List<TestSection> partTest = parser.parse();
    }

    @Test(expected = IllegalStateException.class)
    public void testDoubleTestSuite() {
        String doubleTestSuite = """
                <?xml version="1.0" encoding="UTF-8" ?>
                         <testsuite tests="1" failures="0" name="OtherTest" time="0" errors="0" skipped="0">
                                            <testsuite tests="1" failures="0" name="OtherTest" time="0" errors="0" skipped="0">
                                             <testcase classname="OtherTest" name="testEquals" time="0"/>
                                             </testsuite>
                                             </testsuite>   
                """;
        BufferedReader br = new BufferedReader(new StringReader(doubleTestSuite));
        Lexer lexer = new Lexer(br);
        List<Token> tokens = lexer.scan();
        Parser parser = new Parser(tokens);
        List<TestSection> partTest = parser.parse();
    }

    @Test(expected = IllegalStateException.class)
    public void testCloseAngleBraceFail() {
        String lineCloseAngle = """
                <?xml version="1.0" encoding="UTF-8" ?>
                <testsuite name="rspec" tests="2967" failures="2" errors="0" time="611.546160">
                    <testcase classname="SomeTest" name="doSmth" time="0"/>
                    <testcase classname="SomeTest" name="doOther" time="0"/>
                 </testsuite
                """;
        BufferedReader br = new BufferedReader(new StringReader(lineCloseAngle));
        Lexer lexer = new Lexer(br);
        List<Token> tokens = lexer.scan();
        Parser parser = new Parser(tokens);
        List<TestSection> partTest = parser.parse();
    }

    @Test(expected = IllegalStateException.class)
    public void testSlashFail() {
        String lineSlash = """
                <?xml version="1.0" encoding="UTF-8" ?>
                <testsuite name="rspec" tests="2967" failures="2" errors="0" time="611.546160">
                    <testcase classname="SomeTest" name="doSmth" time="0"/>
                    <testcase classname="SomeTest" name="doOther" time="0"/>
                 <testsuite>
                """;
        BufferedReader br = new BufferedReader(new StringReader(lineSlash));
        Lexer lexer = new Lexer(br);
        List<Token> tokens = lexer.scan();
        Parser parser = new Parser(tokens);
        List<TestSection> partTest = parser.parse();
    }

}
