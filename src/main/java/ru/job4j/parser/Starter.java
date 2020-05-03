package ru.job4j.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.newJob;

public class Starter {
    private static final Logger LOG = LoggerFactory.getLogger(Starter.class.getName());
    public void start(String prop) {
        Config config = new Config(prop);
        config.init();
        try {
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            Scheduler scheduler = schedulerFactory.getScheduler();
            JobDetail job = newJob(ParserJob.class)
                    .withIdentity("ParserJob", "group1")
                    .usingJobData("url", config.get("url"))
                    .usingJobData("username", config.get("username"))
                    .usingJobData("password", config.get("password"))
                    .usingJobData("website", config.get("website"))
                    .build();
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("trigger1", "group1")
                    .withSchedule(CronScheduleBuilder.cronSchedule(config.get("cron.time")))
                    .forJob("ParserJob", "group1")
                    .build();
            scheduler.scheduleJob(job, trigger);
            scheduler.start();
//            scheduler.shutdown(true);
        } catch (SchedulerException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        if (args.length != 0) {
            String prop = args[0];
            Starter starter = new Starter();
            starter.start(prop);
        } else {
            System.out.println("Please try again to enter file with properties");
        }

    }
}
