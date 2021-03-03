package com.github.Dementor0383;

import com.github.Dementor0383.lexer.Lexer;
import com.github.Dementor0383.lexer.Token;
import com.github.Dementor0383.parser.Parser;
import com.github.Dementor0383.parser.model.TestSection;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;


public class Main {

    public static void main(String[] args) throws IOException {
        String inputFileName = args[0];
        String outputFile = args[1];
        FileWorkerJava fileRead = null;
        FileWorkerJava fileWrite = null;
        BufferedReader br = new BufferedReader(new StringReader(fileRead.read(inputFileName)));
        Lexer lexer = new Lexer(br);
        List<Token> tokens = lexer.scan();
        Parser parser = new Parser(tokens);
        List<TestSection> partTest = parser.parse();
        fileWrite.write(outputFile, partTest);
    }
}
