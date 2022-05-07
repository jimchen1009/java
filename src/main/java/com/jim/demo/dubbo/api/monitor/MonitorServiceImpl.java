package com.jim.demo.dubbo.api.monitor;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.monitor.MonitorService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MonitorServiceImpl implements MonitorService {

	private Map<String, AtomicLong> url2Counters = new ConcurrentHashMap<>();

	@Override
	public void collect(URL statistics) {
		String string = statistics.toIdentityString();
		AtomicLong atomicLong = url2Counters.computeIfAbsent(string, key -> new AtomicLong(0));
		atomicLong.incrementAndGet();
	}

	@Override
	public List<URL> lookup(URL query) {
		List<URL> urlList = new ArrayList<>();
		for (Map.Entry<String, AtomicLong> entry : url2Counters.entrySet()) {
			URL url = URL.valueOf(entry.getKey());
			urlList.add(url.addParameter("collect.count", entry.getValue().get()));
		}
		return urlList;
	}
}
