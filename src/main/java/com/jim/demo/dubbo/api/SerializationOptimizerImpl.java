package com.jim.demo.dubbo.api;

import org.apache.dubbo.common.serialize.support.SerializationOptimizer;

import java.util.Collection;
import java.util.Collections;

public class SerializationOptimizerImpl implements SerializationOptimizer {
	@Override
	public Collection<Class> getSerializableClasses() {
		return Collections.emptyList();
	}
}
