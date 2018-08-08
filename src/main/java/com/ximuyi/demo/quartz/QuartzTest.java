package com.ximuyi.demo.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

/**
 * Created by chenjingjun on 2018-03-20.
 */
public class QuartzTest {
    public static void main(String[] args) throws SchedulerException, InterruptedException {
        // Grab the Scheduler instance from the Factory
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        JobDetail job = JobBuilder.newJob(HelloJob.class).withIdentity("job1", "group1").build();
        // Trigger the job to run now, and then repeat every 40 seconds
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "group1").startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(1).repeatForever())
                .build();
        // Tell quartz to schedule the job using our trigger
        scheduler.scheduleJob(job, trigger);

        // and start it off
        scheduler.start();

        Thread.sleep(6000);
        scheduler.shutdown();
        System.out.println("ok..");
    }

    public static class HelloJob implements Job{

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.err.println("Hello!  HelloJob is executing."+ new Date() );
        }
    }
}
