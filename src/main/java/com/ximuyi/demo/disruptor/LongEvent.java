package com.ximuyi.demo.disruptor;

import com.google.common.base.Joiner;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class LongEvent {

	private long value;
	private Set<String> names = new ConcurrentSkipListSet<>();

	public void setValue(long value) {
		this.value = value;
	}

	public long getValue() {
		return value;
	}

	public void finish(String name){
		names.add(name);
	}

	@Override
	public String toString() {
		Joiner joiner = Joiner.on(", ").skipNulls();
		return "{" +
				"value=" + value +
				", names=" + joiner.join(names)+
				'}';
	}
}
