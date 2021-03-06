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
        // CR: your program should be able to work without output file
        // CR: also it should show helpful 'usage' message when user provided too many / not enough args
        String inputFileName = args[0];
        String outputFile = args[1];
        FileWorkerJava fileRead = null;
        FileWorkerJava fileWrite = null;
        // CR: please fix warnings, e.g. here you can access static method via FileWorkerJava.read
        BufferedReader br = new BufferedReader(new StringReader(fileRead.read(inputFileName)));
        Lexer lexer = new Lexer(br);
        List<Token> tokens = lexer.scan();
        Parser parser = new Parser(tokens);
        List<TestSection> partTest = parser.parse();
        // CR: if something wrong with file you'll find out it only after parser finished, so it might take a long time
        // CR: it's better to open it before starting lexing and parsing
        fileWrite.write(outputFile, partTest);
    }
}
