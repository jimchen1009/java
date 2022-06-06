package com.jim.demo.elasticjob;

import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.dataflow.job.DataflowJob;

import java.util.Collections;
import java.util.List;

public class MyElasticDataflowJob implements DataflowJob<Integer> {

	@Override
	public List<Integer> fetchData(ShardingContext shardingContext) {
		switch (shardingContext.getShardingItem()) {
			case 0:
				return Collections.singletonList(0);
			case 1:
				return Collections.singletonList(1);
			case 2:
				return Collections.singletonList(2);
			// case n: ...
		}
		return null;
	}

	@Override
	public void processData(ShardingContext shardingContext, List<Integer> data) {

	}
}
