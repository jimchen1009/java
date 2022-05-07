package com.jim.demo.quartz;

import org.apache.commons.lang3.RandomUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.calendar.HolidayCalendar;
import org.quartz.impl.matchers.KeyMatcher;
import org.quartz.listeners.JobListenerSupport;
import org.quartz.listeners.TriggerListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by chenjingjun on 2018-03-20.
 */
public class QuartzTest {

	private static final Logger logger = LoggerFactory.getLogger(QuartzTest.class);

	private static final String JOB = "job";
	private static final String GROUP = "group";
	private static final String TRIGGER = "trigger";
	private static final String HOLIDAYS = "holidays";

    public static void main(String[] args) throws SchedulerException, InterruptedException {

        // Grab the Scheduler instance from the Factory
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

	    try {
		    HolidayCalendar holidayCalendar = new HolidayCalendar();
		    //holidayCalendar.addExcludedDate( new Date());
		    scheduler.addCalendar(HOLIDAYS, holidayCalendar, false,false);
	    }
	    catch (ObjectAlreadyExistsException ignored){
	    	logger.warn("Unexpectedly found existing scheduler", ignored);
	    }

	    for (int i = 0; i < 1; i++) {
		    JobDetail jobDetail = randomJobDetail(i);
		    Trigger trigger = randomTrigger(i);
		    try {
			    scheduler.scheduleJob(jobDetail, trigger);
		    }
		    catch (ObjectAlreadyExistsException ignored){
		    	//如果需要设置的触发器 存在 恢复策略，就不要替换掉了~
			    //scheduler.rescheduleJob(trigger.getKey(), trigger);
			    logger.warn("Unexpectedly found existing trigger", ignored);
		    }
	    }
	    scheduler.getListenerManager().addJobListener(new HelloJobListener("listener"), KeyMatcher.keyEquals(JobKey.jobKey(JOB, GROUP)));
	    scheduler.getListenerManager().addTriggerListener(new HelloTriggerListener("listener"), KeyMatcher.keyEquals(TriggerKey.triggerKey(TRIGGER, GROUP)));

        // and start it off
        scheduler.start();

        Thread.sleep(60000000);
        scheduler.shutdown();
        System.out.println("ok..");
    }

    private static JobDetail randomJobDetail( int index){
	    return JobBuilder.newJob(HelloJob.class)
			    .withIdentity(JOB + index, GROUP)
			    .usingJobData("jobSays", "Hello World!")
			    .usingJobData("floatValue", 3.141f)
			    .build();
    }

    private static Trigger randomTrigger(int index){
	    int interval = RandomUtils.nextInt(5, 10);
	    SimpleScheduleBuilder builder = SimpleScheduleBuilder.simpleSchedule()
			    .withMisfireHandlingInstructionNowWithExistingCount()
			    .withIntervalInSeconds(interval)
			    .repeatForever();
	    return TriggerBuilder.newTrigger()
			    .withIdentity(TRIGGER + index, GROUP)
			    .startAt(getDate(Calendar.MINUTE, 1))
			    .endAt(getDate(Calendar.MINUTE, RandomUtils.nextInt(20, 30)))
			    .modifiedByCalendar(HOLIDAYS)
			    .withSchedule(builder)
			    .build();
    }

    private static Date getDate(int field, int amount){
	    Calendar calendar = Calendar.getInstance();
	    calendar.add(field, amount);
	    return calendar.getTime();
    }

	/**
	 * If you use the @PersistJobDataAfterExecution annotation,
	 * you should strongly consider also using the @DisallowConcurrentExecution annotation,
	 * in order to avoid possible confusion (race conditions) of what data was left stored when two instances of the same job (JobDetail) executed concurrently.
	 */
	@PersistJobDataAfterExecution
	@DisallowConcurrentExecution
	public static class HelloJob implements Job{

	    private String jobSays;
	    private float floatValue;

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
	        JobKey key = context.getJobDetail().getKey();
	        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
	        dataMap.put("floatValue", floatValue + 1);
	        System.err.println("Instance " + key + " of DumbJob says: " + jobSays + ", and val is: " + floatValue);
        }

	    public void setJobSays(String jobSays) {
		    this.jobSays = jobSays;
	    }

	    public void setFloatValue(float floatValue) {
		    this.floatValue = floatValue;
	    }
    }

    public static class HelloJobListener extends JobListenerSupport {

	    private final String name;

	    public HelloJobListener(String name) {
		    this.name = name;
	    }

	    @Override
	    public String getName() {
		    return name;
	    }
    }

    public static class HelloTriggerListener extends TriggerListenerSupport {

		private final String name;

	    public HelloTriggerListener(String name) {
		    this.name = name;
	    }

	    @Override
	    public String getName() {
		    return name;
	    }
    }
}
