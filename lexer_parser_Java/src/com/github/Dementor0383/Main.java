package com.github.Dementor0383;

import com.github.Dementor0383.lexer.Lexer;
import com.github.Dementor0383.lexer.Token;
import com.github.Dementor0383.parser.Parser;
import com.github.Dementor0383.parser.model.TestSection;

import java.io.*;
import java.util.List;


public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length > 2) {
            throw error("Too many program arguments\n");
        }
        if (args.length < 1) {
            throw error("Not enough program arguments\n");
        }
        String inputFileName = args[0];
        String outputFile = null;
        if (args.length == 2) outputFile = args[1];
        PrintStream out = null;
        if (outputFile != null) {
            out = new PrintStream(new FileOutputStream(outputFile));
            System.setOut(out);
        }
        BufferedReader br = new BufferedReader(new StringReader(FileWorkerJava.read(inputFileName)));
        Lexer lexer = new Lexer(br);
        List<Token> tokens = lexer.scan();
        Parser parser = new Parser(tokens);
        List<TestSection> partTest = parser.parse();
        FileWorkerJava.write(partTest);
        if (outputFile != null) out.close();
    }

    private static RuntimeException error(String message) {
        return new IllegalStateException(message);
    }
}
