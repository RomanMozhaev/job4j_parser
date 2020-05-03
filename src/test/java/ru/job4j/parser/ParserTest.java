package ru.job4j.parser;

import org.junit.Test;
import java.util.Calendar;
import java.util.Map;

import static org.junit.Assert.*;

public class ParserTest {

    @Test
    public void whenParserThenVacationIs() {
        Calendar date = Calendar.getInstance();
        int year = date.get(Calendar.YEAR);
        date.set(year, Calendar.JANUARY, 1, 0, 1, 1);
        String url = "https://www.sql.ru/forum/job/1";
        Parser parser = new Parser(date.getTimeInMillis());
        parser.parse(url);
        Map<String, Vacation> map = parser.getVacations();
        assertFalse(map.isEmpty());
    }

}