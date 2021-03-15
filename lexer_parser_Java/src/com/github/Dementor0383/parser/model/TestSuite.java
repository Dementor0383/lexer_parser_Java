package com.github.Dementor0383.parser.model;

public record TestSuite(String sectionType, int tests, int failures, String name, String time, int errors,
                        int skipped) implements TestSection {
}
