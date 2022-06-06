package com.jim.demo.elasticjob;

import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyElasticSimpleJobJob implements SimpleJob {

	private static final Logger logger = LoggerFactory.getLogger(MyElasticSimpleJobJob.class);

	@Override
	public void execute(ShardingContext context) {
		switch (context.getShardingItem()) {
			case 0:
				logger.debug("execute 0");
				break;
			case 1:
				logger.debug("execute 1");
				break;
			case 2:
				logger.debug("execute 2");
				break;
			default:
				logger.debug("execute null");
		}
	}
}
