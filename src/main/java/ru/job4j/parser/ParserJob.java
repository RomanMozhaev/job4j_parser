package ru.job4j.parser;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import java.util.Calendar;

public class ParserJob implements Job {

    public void execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String url = dataMap.getString("url");
        String username = dataMap.getString("username");
        String password = dataMap.getString("password");
        String website = dataMap.getString("website");
        SQLManager sqlManager = new SQLManager(url, username, password);
        sqlManager.init();
        ParserInterface parser = new Parser(sqlManager.getLastTime());
        Calendar now = Calendar.getInstance();
        boolean result;
        int i = 1;
        String link;
        do {
            link = website + "/" + i;
            result = parser.parse(link);
            i++;
        } while (result);
        sqlManager.addNewVacations(parser.getVacations());
        sqlManager.writeLastTime(now.getTimeInMillis());
    }
}
