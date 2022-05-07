package com.jim.demo.kafka;

import com.jim.common.Log4jUtil;
import com.jim.common.ResourceUtil;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/***
 * 1.
 * SERVER BUG:
 *
 * ERROR Error while deleting segments for Jim-2 in dir D:\tmp\kafka-logs (kafka.server.LogDirFailureChannel)
 * java.nio.file.FileSystemException: D:\tmp\kafka-logs\Jim-2\00000000000000000000.index -> D:\tmp\kafka-logs\Jim-2\00000000000000000000.index.deleted: The process cannot access the file because it is being used by another process.
 *
 *         at sun.nio.fs.WindowsException.translateToIOException(Unknown Source)
 *         at sun.nio.fs.WindowsException.rethrowAsIOException(Unknown Source)
 *         at sun.nio.fs.WindowsFileCopy.move(Unknown Source)
 *         at sun.nio.fs.WindowsFileSystemProvider.move(Unknown Source)
 *         at java.nio.file.Files.move(Unknown Source)
 *         at org.apache.kafka.common.utils.Utils.atomicMoveWithFallback(Utils.java:917)
 *         at kafka.log.AbstractIndex.renameTo(AbstractIndex.scala:211)
 *         at kafka.log.LazyIndex$IndexValue.renameTo(LazyIndex.scala:155)
 *         at kafka.log.LazyIndex.$anonfun$renameTo$1(LazyIndex.scala:79)
 *         at kafka.log.LazyIndex.renameTo(LazyIndex.scala:79)
 *         at kafka.log.LogSegment.changeFileSuffixes(LogSegment.scala:496)
 *         at kafka.log.Log.$anonfun$deleteSegmentFiles$1(Log.scala:2279)
 *         at kafka.log.Log.$anonfun$deleteSegmentFiles$1$adapted(Log.scala:2279)
 *         at scala.collection.immutable.List.foreach(List.scala:333)
 *         at kafka.log.Log.deleteSegmentFiles(Log.scala:2279)
 *         at kafka.log.Log.removeAndDeleteSegments(Log.scala:2263)
 *         at kafka.log.Log.$anonfun$deleteSegments$2(Log.scala:1753)
 *         at kafka.log.Log.deleteSegments(Log.scala:2387)
 *         at kafka.log.Log.deleteRetentionMsBreachedSegments(Log.scala:1737)
 *         at kafka.log.Log.deleteOldSegments(Log.scala:1806)
 *         at kafka.log.LogManager.$anonfun$cleanupLogs$3(LogManager.scala:1074)
 *         at kafka.log.LogManager.$anonfun$cleanupLogs$3$adapted(LogManager.scala:1071)
 *         at scala.collection.immutable.List.foreach(List.scala:333)
 *         at kafka.log.LogManager.cleanupLogs(LogManager.scala:1071)
 *         at kafka.log.LogManager.$anonfun$startup$2(LogManager.scala:409)
 *         at kafka.utils.KafkaScheduler.$anonfun$schedule$2(KafkaScheduler.scala:114)
 *         at java.util.concurrent.Executors$RunnableAdapter.call(Unknown Source)
 *         at java.util.concurrent.FutureTask.runAndReset(Unknown Source)
 *         at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.access$301(Unknown Source)
 *         at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(Unknown Source)
 *         at java.util.concurrent.ThreadPoolExecutor.runWorker(Unknown Source)
 *         at java.util.concurrent.ThreadPoolExecutor$Worker.run(Unknown Source)
 *         at java.lang.Thread.run(Unknown Source)
 *         Suppressed: java.nio.file.FileSystemException: D:\tmp\kafka-logs\Jim-2\00000000000000000000.index -> D:\tmp\kafka-logs\Jim-2\00000000000000000000.index.deleted: The process cannot access the file because it is being used by another process.
 *
 *                 at sun.nio.fs.WindowsException.translateToIOException(Unknown Source)
 *                 at sun.nio.fs.WindowsException.rethrowAsIOException(Unknown Source)
 *                 at sun.nio.fs.WindowsFileCopy.move(Unknown Source)
 *                 at sun.nio.fs.WindowsFileSystemProvider.move(Unknown Source)
 *                 at java.nio.file.Files.move(Unknown Source)
 *                 at org.apache.kafka.common.utils.Utils.atomicMoveWithFallback(Utils.java:914)
 *                 ... 27 more
 *
 *
 * https://issues.apache.org/jira/browse/KAFKA-1194
 * https://issues.apache.org/jira/browse/KAFKA-2170
 * @param <K>
 * @param <V>
 */
public class MyKafkaProducer<K, V> {

	private final String topic;
	private final Producer<K, V> producer;

	public MyKafkaProducer(String topic, Properties properties) {
		this.topic = topic;
		this.producer = new KafkaProducer<>(properties);
	}

	public void send(K key, V value){
		producer.send(new MyProducerRecord<>(topic, key, value));
	}

	public void close(){
		producer.close();
	}


	/***
	 *
	 * 1.
	 * 两个线程: IO调度线程Sender 与 业务发送消息线程
	 * 使用类作为媒介 RecordAccumulator, 并发相关的逻辑在这里类中处理
	 *
	 * 2. RecordAccumulator.BufferPool 限制业务发送数据大小【目测只有这限制】, 固然限制了消息内存大小了
	 *
	 * 3. IO线程Sender检测内存耗尽会立刻发送数据, Sender.guaranteeMessageOrder保证顺序结果类似于同步IO
	 *
	 * 4.
	 *
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		Log4jUtil.initializeV2();
		Properties properties = ResourceUtil.getResourceAsProperties("kafka/producer.properties");
		MyKafkaProducer<String, String> kafkaProducer = new MyKafkaProducer<>("JimV2", properties);
		for (int i = 0; i < 1000; i++) {
			kafkaProducer.send(Integer.toString(i), Integer.toString(i));
			TimeUnit.SECONDS.sleep(30);
		}
		kafkaProducer.close();
	}


}
