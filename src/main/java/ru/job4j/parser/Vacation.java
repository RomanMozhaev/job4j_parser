package ru.job4j.parser;

public class Vacation {
    private String name;
    private String text;
    private String date;
    private String link;

    public Vacation(String name, String text, String date, String link) {
        this.name = name;
        this.text = text;
        this.date = date;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    public String getLink() {
        return link;
    }
}
