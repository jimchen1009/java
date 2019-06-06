package com.ximuyi.demo.vmoption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VMOptions {
	
	private static final Logger logger = LoggerFactory.getLogger(VMOptions.class);

	public static void main(String[] args) throws InterruptedException {
		//recursion(0, 0);
		//recursion(new RcursionValue());
		//GC0();
		GC1();
	}

	/**
	 * 练习2
	 *
	 * 方法区是规范层面的东西，规定了这一个区域要存放哪些东西
	 * 永久区或者是metaspace是对方法区的不同实现，是实现层面的东西。
	 * JAVA8 metaspace 替代了 永久区
	 *
	 * jmap -heap pid 查看堆的分配各种参数
	 * jstat -gcutil pid GM的运行情况
	 *
	 * -XX: -DoEscapeAnalysis  （不）做逃逸分析（对象也就不能分配到栈上了）；
	 * -XX:-EliminateAllocations （不）做标量分析，-号表示取反，否定的意思；
	 * -XX:-UseTLAB （不）使用线程本地缓存
	 *
	 */
	private static void GC0(){
		List<Object> objects = new ArrayList<>();
		for(int i=0; i< 1000; i++){
			objects.add(new byte[1024*1024]);
		}
	}

	private static void GC1() throws InterruptedException {
		int size = 1024 * 1024;
		List<byte[]> valueIds = new ArrayList<>();
		for (int i = 0; i < 10000000; i++) {
			valueIds.add(new byte[size]);
			if (valueIds.size() == 1){
				valueIds.clear();
			}
			TimeUnit.MILLISECONDS.sleep(10);
		}
	}

	/**
	 * 练习1：
	 * 栈帧：局部变量表、操作数栈、帧数据区
	 * 每次调用函数都会生成对应的栈帧【线程栈空间+1栈帧】
	 * -Xss128K 指定最大的栈空间【变量多，一个栈帧数据量就大】
	 * @param value1
	 * @param value2
	 */
	private static void recursion(long value1, long value2){
		if (value1 >= 50){
			System.gc();
			return;
		}
		recursion(value1 + 1, value2 + 1);
	}
	private static void recursion(RcursionValue value) {
		if (value.value++ >= 50){
			value.bytes = null;
			System.gc();
			try {
				TimeUnit.SECONDS.sleep(10);
			}
			catch (Throwable throwable){
				logger.debug("", throwable);
			}
			return;
		}
		recursion(value);
	}

	private static class RcursionValue{
		//p就是为增加这个类的占用内存的大小
		public int value = 0;
		protected long p1, p2, p3, p4, p5, p6, p7, p8;
		protected long p9, p10, p11, p12, p13, p14, p15;
		protected long p16, p17, p18, p19, p20, p21, p22;
		public byte[] bytes = new byte[500 * 1026 * 2014];
	}

}
