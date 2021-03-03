package com.github.Dementor0383;

import com.github.Dementor0383.parser.model.*;

import java.io.*;
import java.util.List;

public class FileWorkerJava {

    private static void exists(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
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
                    if (line.equals("  <properties>") || !check){
                        check = checkProperty(line);
                        continue;
                    }
                    else {
                        sb.append(line);
                        sb.append("\n");
                    }
                }
            } finally {
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
                outFile.createNewFile();
            }
            PrintWriter out = new PrintWriter(outFile.getAbsoluteFile());
            try{
                while (listPosition < sizeOfPartTest){
                    list = partTest.get(listPosition);
                    if (marker == true) {
                        marker = false;
                       TestSuite part = (TestSuite)list;
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
