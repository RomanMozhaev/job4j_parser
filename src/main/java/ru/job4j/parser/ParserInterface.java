package ru.job4j.parser;

import java.util.Map;

public interface ParserInterface {
    boolean parse(String url);
    Map<String, Vacation> getVacations();
}
