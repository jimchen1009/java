package com.ximuyi.demo.disruptor;

import com.google.common.base.Joiner;
import org.apache.mina.util.ConcurrentHashSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class LongEvent {

	private long value;
	private Set<String> names = new ConcurrentHashSet<>();

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
