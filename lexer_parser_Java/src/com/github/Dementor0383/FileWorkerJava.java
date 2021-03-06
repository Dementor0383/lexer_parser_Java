package com.github.Dementor0383;

import com.github.Dementor0383.parser.model.*;

import java.io.*;
import java.util.List;

public class FileWorkerJava {

    private static void exists(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        // CR: 1. you still can fail since exists doesn't check type of file, so it might be a folder
        // CR: 2. when you construct FileReader it checks weather file exists or not, so you're doing same work twice
        if (!file.exists()) {
            throw new FileNotFoundException(file.getName());
        }
    }

    public static String read(String fileName) throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();

        exists(fileName);
        boolean check = true;
        File file = new File(fileName);

        try {
            BufferedReader input = new BufferedReader(new FileReader(file.getAbsoluteFile()));
            try {
                String line;
                while ((line = input.readLine()) != null) {
                    // CR: it's bad idea just to skip properties, there might be some unclosed tags and so on.
                    // CR: i think it's better to handle them as one of the model classes but do not include in report
                    if (line.equals("  <properties>") || !check){
                        check = checkProperty(line);
                        continue;
                    }
                    else {
                        // CR: purpose of BufferedReader is to read file by chunks, so it won't eat all of your memory
                        // CR: here you store all file in memory, so it's kinda defeats the purpose
                        sb.append(line);
                        sb.append("\n");
                    }
                }
            } finally {
                // CR: you don't use that right, also in modern java it's better to use https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
                input.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    private static boolean checkProperty(String line){
        if (line.equals("  </properties>")) return true;
        else return false;
    }

    public static void write(String outputNameFile, List<TestSection> partTest) throws IOException {
        int sizeOfPartTest = partTest.size();
        int listPosition = 0;
        TestSection list = null;
        boolean marker = true;
        File outFile = new File(outputNameFile);
        try {
            if (!outFile.exists()){
                // CR: it's created automatically
                outFile.createNewFile();
            }
            PrintWriter out = new PrintWriter(outFile.getAbsoluteFile());
            try{
                // CR: please rewrite as two methods e.g.: printTestSuite and printTestCases (you might also have printTestCase)
                // CR: there's no reason to merge this two cases into one
                while (listPosition < sizeOfPartTest){
                    list = partTest.get(listPosition);
                    if (marker == true) {
                        marker = false;
                       TestSuite part = (TestSuite)list;
                       // CR: there's println method
                        out.print("TestSuite name is " + part.name() + "\n");
                        out.print("Quantity of tests: " + part.tests() + "\n");
                        out.print("Quantity of failures tests: " + part.failures() + "\n");
                        out.print("Quantity of errors: " + part.errors() + "\n");
                        out.print("Quantity of skipped tests: " + part.skipped() + "\n");
                        out.print("Time: " + part.time() + "sec \n");
                        out.print("____________TestCases___________\n");
                        listPosition++;
                        continue;
                    }
                    if (marker == false) {
                        marker = true;
                        int counterOfLocalList = 0;
                        TestsList tests = (TestsList)list;
                        int localListSize = tests.testCases().size();
                        while(counterOfLocalList < localListSize) {
                            Test test = (Test)tests.testCases().get(counterOfLocalList);
                            if (test.sectionType() == "passed") {
                                out.print("Test " + test.name() + " PASSED (" + test.time() + " sec)\n");
                                counterOfLocalList++;
                                continue;
                            }
                            else if (test.sectionType() == "failure"){
                                out.print("Test " + test.name() + " FAILED (" + test.time() + " sec)\n");
                                for (int i = 0; i < test.failureLines().size(); i++){
                                    out.print(test.failureLines().get(i) + "\n");
                                }
                                counterOfLocalList++;
                                continue;
                            }
                            else if (test.sectionType() == "skipped"){
                                out.print("Test " + test.name() + " SKIPPED (" + test.time() + " sec)\n");
                                counterOfLocalList++;
                                continue;
                            }
                        }
                        out.print("____________TestCasesEND___________\n");
                        listPosition++;
                        continue;
                    }
                }
            }finally {
                out.close();
            }
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }


}
