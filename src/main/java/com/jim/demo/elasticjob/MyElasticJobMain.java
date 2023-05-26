package com.jim.demo.elasticjob;

import com.jim.common.Log4jUtil;
import com.jim.common.ResourceUtil;
import com.jim.demo.mysql.MysqlSourceBuilder;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.error.handler.wechat.WechatPropertiesConstants;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.OneOffJobBootstrap;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.lite.internal.snapshot.SnapshotService;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;
import org.apache.shardingsphere.elasticjob.tracing.api.TracingConfiguration;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

public class MyElasticJobMain {

	private static final CoordinatorRegistryCenter register = createRegistryCenter();

	public static void main(String args[]) throws IOException {
		Log4jUtil.initializeV2();
		new SnapshotService(register, 9888).listen();
		schedule();
	}


	private static void schedule() throws IOException {
		// 调度基于 class 类型的作业
		JobConfiguration configuration = createJobConfiguration("job1", builder -> builder.cron("0/5 * * * * ?"));
		Properties properties = ResourceUtil.getResourceAsProperties("demo.properties");
		HikariDataSource dataSource = new MysqlSourceBuilder(properties).build(2);
		configuration.getExtraConfigurations().add(new TracingConfiguration<>("RDB", dataSource));
		ScheduleJobBootstrap bootstrap = new ScheduleJobBootstrap(register, new MyElasticSimpleJobJob(), configuration);
		bootstrap.schedule();
	}

	private static void execute(){
		// 调度基于 class 类型的作业
		JobConfiguration configuration = createJobConfiguration("job2", null);
		OneOffJobBootstrap bootstrap = new OneOffJobBootstrap(register, new MyElasticSimpleJobJob(), configuration);
		bootstrap.execute();
	}

	private static CoordinatorRegistryCenter createRegistryCenter() {
		ZookeeperConfiguration configuration = new ZookeeperConfiguration("localhost:2181", "elastic-job-demo");
		CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(configuration);
		regCenter.init();
		return regCenter;
	}

	private static JobConfiguration createJobConfiguration(String jobName, Consumer<JobConfiguration.Builder> consumer) {
		JobConfiguration.Builder builder = JobConfiguration.newBuilder(jobName, 3).shardingItemParameters("0=Beijing,1=Shanghai,2=Guangzhou");
		if (consumer != null) {
			consumer.accept(builder);
		}
		builder.jobErrorHandlerType("WECHAT");
		builder.setProperty(WechatPropertiesConstants.WEBHOOK, "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=3b26de32-5b08-496e-9d6c-9e9214065f77");
		return builder.build();
	}
}
