package com.github.Dementor0383.parser.model;

import java.util.List;

public record Test(String sectionType, String classname, String name, String time, String failureMessage, String type, List<String> failureLines) implements TestSection {
}
