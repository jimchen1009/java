package com.ximuyi.demo.classloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Launcher;

public class ClassLoaderMain {

	private static final Logger logger = LoggerFactory.getLogger(ClassLoaderMain.class);

	public static void main(String[] args){
		ClassLoader appLoader = Launcher.getLauncher().getClassLoader();
		ClassLoader lauchLoader = Launcher.class.getClassLoader();
		/***
		 * Bootstrap ClassLoader是由C/C++编写的，它本身是虚拟机的一部分，所以它并不是一个JAVA类，也就是无法在java代码中获取它的引用，
		 * JVM启动时通过Bootstrap类加载器加载rt.jar等核心jar包中的class文件，之前的int.class,String.class都是由它加载。
		 * 然后呢，我们前面已经分析了，JVM初始化sun.misc.Launcher并创建Extension ClassLoader和AppClassLoader实例。
		 * 并将ExtClassLoader设置为AppClassLoader的父加载器。
		 */
		logger.debug("appLoader:{} ", appLoader.getClass().getName());
		logger.debug("lauchLoader:{} ", lauchLoader == null ? null: lauchLoader.getClass().getName());
	}
}
