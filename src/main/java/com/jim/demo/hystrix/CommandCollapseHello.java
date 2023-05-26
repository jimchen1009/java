package com.jim.demo.hystrix;

import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCollapserKey;
import com.netflix.hystrix.HystrixCollapserProperties;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author chenjingjun
 * @date 2023/5/25 14:55:39
 */
public class CommandCollapseHello extends HystrixCollapser<List<String>, String, Integer> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommandCollapseHello.class);


	private final int requestId;

	public CommandCollapseHello(int requestId) {
		super(Setter.withCollapserKey(HystrixCollapserKey.Factory.asKey("GetValue"))
				.andCollapserPropertiesDefaults(HystrixCollapserProperties.Setter().withTimerDelayInMilliseconds(100)));
		this.requestId = requestId;
	}

	@Override
	public Integer getRequestArgument() {
		return requestId;
	}

	@Override
	protected HystrixCommand<List<String>> createCommand(Collection<CollapsedRequest<String, Integer>> collapsedRequests) {
		return new BatchCommand(collapsedRequests);
	}

	@Override
	protected void mapResponseToRequests(List<String> batchResponse, Collection<CollapsedRequest<String, Integer>> collapsedRequests) {
		int count = 0;
		for (CollapsedRequest<String, Integer> request : collapsedRequests) {
			request.setResponse(batchResponse.get(count++));
		}
	}


	private static final class BatchCommand extends HystrixCommand<List<String>> {
		private final Collection<CollapsedRequest<String, Integer>> requests;

		private BatchCommand(Collection<CollapsedRequest<String, Integer>> requests) {
			super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
					.andCommandKey(HystrixCommandKey.Factory.asKey("GetValueForKey")));
			this.requests = requests;
		}

		@Override
		protected List<String> run() {
			ArrayList<String> response = new ArrayList<>();
			for (CollapsedRequest<String, Integer> request : requests) {
				// artificial response for each argument received in the batch
				response.add("Hello Num. " + request.getArgument());
			}
			LOGGER.debug("response: {}", response);
			return response;
		}
	}

	public static class UnitTest {
		@Test
		public void testCollapse() throws Exception {
			HystrixRequestContext context = HystrixRequestContext.initializeContext();
			try {
				Future<String> f1 = new CommandCollapseHello(1).queue();
				Future<String> f2 = new CommandCollapseHello(2).queue();
				Future<String> f3 = new CommandCollapseHello(3).queue();
				Future<String> f4 = new CommandCollapseHello(4).queue();
				LOGGER.debug("{}", f1.get());
				LOGGER.debug("{}", f2.get());
				LOGGER.debug("{}", f3.get());
				LOGGER.debug("{}", f4.get());
			} finally {
				context.shutdown();
			}
		}

	}
}
