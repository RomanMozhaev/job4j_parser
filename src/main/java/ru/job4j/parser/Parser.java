package ru.job4j.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class Parser implements ParserInterface {
    private Map<String, Vacation> vacations = new LinkedHashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(Parser.class.getName());
    private long lastDate;
    private Calendar calDate;

    public Parser(long lastDate) {
        this.lastDate = lastDate;
    }

    public Map<String, Vacation> getVacations() {
        return vacations;
    }

    /**
     * the method make the connection to the web-site and returns the document for parsing
     * @param url
     * @return the document
     */
    private Document getConnection(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return doc;
    }

    /**
     * the method puts the vacation, if it fits to the parameters, to the map
     * @param url
     * @return true if the recorded was added
     */
    public boolean parse(String url) {
        boolean result = true;
        String name;
        String date;
        String link;
        String text;
        Document doc = getConnection(url);
        if (doc != null) {
            Elements allElements = doc.select("tr");
            for (Element aElement : allElements) {
                Elements eName = aElement.getElementsByTag("a");
                List<String> nameList = eName.eachText();
                if (!nameList.isEmpty()) {
                    name = nameList.get(0);
                    if (checkName(name)) {
                        Elements eDate = aElement.getElementsByClass("altCol");
                        link = eName.attr("href");
                        List<String> dateList = eDate.eachText();
                        if (!dateList.isEmpty()) {
                            date = dateList.get(dateList.size() - 1);
                            if (checkDate(date)) {
                                text = getDescription(link);
                                this.vacations.put(name, new Vacation(name, text, date, link));
                            } else {
                                result = false;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * the method extracts the description of the vacation
     * @param url
     * @return the string with the description
     */
    private String getDescription(String url) {
        String text = "";
        Document doc = getConnection(url);
        if (doc != null) {
            Elements allElements = doc.getElementsByClass("msgBody");
            if (allElements != null) {
                text = allElements.get(1).text();
            }
        }
        return text;
    }

    /**
     * the method checks the name of vacation. it must contains Java and doesn't contain Script
     * @param name
     * @return true if the name is okay
     */
    private boolean checkName(String name) {
        boolean result = false;
        name = name.toLowerCase();
        if (name.contains("java") && !name.contains("script")) {
            result = true;
        }
        return result;
    }

    /**
     * the method receives the string with date and time and compares it to the date of the last program starting.
     * @param date
     * @return true if after the last program start
     */
    private boolean checkDate(String date) {
        boolean result = false;
        boolean modified = false;
        int length = date.length();
        Calendar lastDateCal = Calendar.getInstance();
        lastDateCal.setTimeInMillis(this.lastDate);
        this.calDate = Calendar.getInstance();
        if (length == 12) {  //yesterday
            readTime(date, 10, 12, 7, 9);
            this.calDate.add(Calendar.DATE, -1);
            modified = true;
        }
        if (length == 14) {   // today
            readTime(date, 12, 14, 9, 11);
            modified = true;
        }
        if (length == 15) {  // 1 -9 day of month
            readTime(date, 13, 15, 10, 12);
            readDate(date, 0, 1, 2, 5, 6, 8);
            modified = true;
        }
        if (length == 16) { // others days of month
            readTime(date, 14, 16, 11, 13);
            readDate(date, 0, 2, 3, 6, 7, 9);
            modified = true;
        }
        if (modified && (this.calDate.compareTo(lastDateCal) >= 0)) {
            result = true;
        }
        return result;
    }

    /**
     * the method detects minutes and hours and set them to this.calDate
     * @param date
     * @param minStart
     * @param minEnd
     * @param hourStart
     * @param hourEnd
     */
    private void readTime(String date, int minStart, int minEnd, int hourStart, int hourEnd) {
        int min = Integer.parseInt(date.substring(minStart, minEnd));
        int hour = Integer.parseInt(date.substring(hourStart, hourEnd));
        this.calDate.set(Calendar.HOUR_OF_DAY, hour);
        this.calDate.set(Calendar.MINUTE, min);
    }

    /** the method based on the start and end numbers detects day, month and year and set them to this.calDate
     *
     * @param date - the string with date
     * @param dayStart
     * @param dayEnd
     * @param monthStart
     * @param monthEnd
     * @param yearStart
     * @param yearEnd
     */
    private void readDate(String date, int dayStart, int dayEnd, int monthStart, int monthEnd, int yearStart, int yearEnd) {
        int day = Integer.parseInt(date.substring(dayStart, dayEnd));
        int month = getMonthNum(date.substring(monthStart, monthEnd));
        int year = Integer.parseInt(date.substring(yearStart, yearEnd)) + 2000;
        this.calDate.set(Calendar.DATE, day);
        this.calDate.set(Calendar.MONTH, month);
        this.calDate.set(Calendar.YEAR, year);
    }

    /**
     * the method compares the Strings and returns the number of month
     * @param month - the string with tree letters in Russian
     * @return the month number
     */
    private int getMonthNum(String month) {
        int numMonth = -1;
        switch (month) {
            case "янв":
                numMonth = 0;
                break;
            case "фев":
                numMonth = 1;
                break;
            case "мар":
                numMonth = 2;
                break;
            case "апр":
                numMonth = 3;
                break;
            case "май":
                numMonth = 4;
                break;
            case "июн":
                numMonth = 5;
                break;
            case "июл":
                numMonth = 6;
                break;
            case "авг":
                numMonth = 7;
                break;
            case "сен":
                numMonth = 8;
                break;
            case "окт":
                numMonth = 9;
                break;
            case "ноя":
                numMonth = 10;
                break;
            case "дек":
                numMonth = 11;
                break;
            default:
                break;
        }
        return numMonth;
    }

}
