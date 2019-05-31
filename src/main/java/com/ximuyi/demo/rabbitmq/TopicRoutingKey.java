package com.ximuyi.demo.rabbitmq;

import org.apache.commons.lang3.RandomUtils;

public class TopicRoutingKey {
	private static String[] Colours = new String[]{"red", "yellow", "blue", "gray", "*"};
	private static String[] Stationery = new String[]{"pen", "pencil", "sharpener", "*"};

	public static String randomOne(boolean permitWildcard){
		int i = RandomUtils.nextInt(0, permitWildcard ? Colours.length : Colours.length - 1);
		int j = RandomUtils.nextInt(0, permitWildcard ? Stationery.length : Stationery.length - 1);
		return Colours[i] + "." + Stationery[j];
	}
}
