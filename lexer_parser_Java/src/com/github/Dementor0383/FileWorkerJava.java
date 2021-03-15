package com.github.Dementor0383;

import com.github.Dementor0383.parser.model.*;

import java.io.*;
import java.util.List;

public class FileWorkerJava {

    public static String read(String fileName) {
        StringBuilder sb = new StringBuilder();
        File file = new File(fileName);
        try {
            BufferedReader input = new BufferedReader(new FileReader(file.getAbsoluteFile()));
            try {
                String line;
                while ((line = input.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
            } finally {
                try {
                    input.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public static void write(List<TestSection> partTest) {
        int sizeOfPartTest = partTest.size();
        int listPosition = 0;
        TestSection list;
        boolean marker = true;
        while (listPosition < sizeOfPartTest) {
            list = partTest.get(listPosition);
            if (marker) {
                marker = false;
                TestSuite part = (TestSuite) list;
                printTestSuite(part);
            } else {
                marker = true;
                TestsList tests = (TestsList) list;
                printTestCases(tests);
            }
            listPosition++;
        }
    }

    private static void printTestSuite(TestSuite part) {
        System.out.println("TestSuite name is " + part.name());
        System.out.println("Quantity of tests: " + part.tests());
        System.out.println("Quantity of failures tests: " + part.failures());
        System.out.println("Quantity of errors: " + part.errors());
        System.out.println("Quantity of skipped tests: " + part.skipped());
        System.out.println("Time: " + part.time() + "sec");
        System.out.println("____________TestCases___________");
    }

    private static void printTestCases(TestsList tests) {
        int counterOfLocalList = 0;
        int localListSize = tests.testCases().size();
        while (counterOfLocalList < localListSize) {
            Test test = (Test) tests.testCases().get(counterOfLocalList);
            switch (test.sectionType()) {
                case "passed" -> {
                    System.out.println("Test " + test.name() + " PASSED (" + test.time() + " sec)");
                    counterOfLocalList++;
                }
                case "failure" -> {
                    System.out.println("Test " + test.name() + " FAILED (" + test.time() + " sec)");
                    for (int i = 0; i < test.failureLines().size(); i++) {
                        System.out.println(test.failureLines().get(i));
                    }
                    counterOfLocalList++;
                }
                case "skipped" -> {
                    System.out.println("Test " + test.name() + " SKIPPED (" + test.time() + " sec)");
                    counterOfLocalList++;
                }
            }
        }
        System.out.println("____________TestCasesEND___________");
    }
}

