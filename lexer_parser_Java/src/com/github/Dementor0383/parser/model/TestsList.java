package com.github.Dementor0383.parser.model;

import java.util.List;

public record TestsList(List<TestSection> testCases) implements TestSection {
}
