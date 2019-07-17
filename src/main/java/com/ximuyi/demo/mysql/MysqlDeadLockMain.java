package com.ximuyi.demo.mysql;

import com.ximuyi.common.PoolThreadFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/***
 * mysql的配置~
 * #禁止开启死锁检测
 * innodb_deadlock_detect=true
 * #死锁超时时间
 * innodb_lock_wait_timeout=30
 */
public class MysqlDeadLockMain {

	private static final Logger logger = LoggerFactory.getLogger(MysqlDeadLockMain.class);

    public static void main(String[] args) throws Throwable {
        Properties properties = new Properties();
        InputStream stream = Resources.getResourceAsStream("demo.properties");
        properties.load(stream);

	    String driver = properties.getProperty("jdbc.driverClassName");
        String url = properties.getProperty("jdbc.url");
        String username = properties.getProperty("jdbc.username");
        String password = properties.getProperty("jdbc.password");
	    properties.clear();

	    int connectionCount = 2;

	    properties.setProperty("driverClassName", driver);
	    properties.setProperty("jdbcUrl", url);
	    properties.setProperty("username", username);
	    properties.setProperty("password", password);
	    properties.setProperty("autoCommit", String.valueOf(true));
	    // c3p0的idleTimeout单位是秒，而HikariCP的单位是毫秒，为了统一单位，这里需要转成毫秒。
	    properties.setProperty("connectionTimeout", "5000");
	    properties.setProperty("minimumIdle", String.valueOf(connectionCount));
	    properties.setProperty("maximumPoolSize",String.valueOf(connectionCount));
	    // sql批量执行优化，有sql不能带;的副作用，所以只能选择性使用
	    properties.setProperty("dataSource.useServerPrepStmts", "false");
	    properties.setProperty("dataSource.rewriteBatchedStatements", "true");

	    HikariConfig hikariConfig = new HikariConfig(properties);
	    HikariDataSource dataSource = new HikariDataSource(hikariConfig);

	    PoolThreadFactory factory = new PoolThreadFactory("mysql", false);
	    CountDownLatch downLatch = new CountDownLatch(connectionCount);
	    for (int i = 0; i < connectionCount; i++) {
		    Runnable runnable = newCmdInsertRunner(dataSource::getConnection, 2000, i % 2 == 0);
		    factory.newThread(loopRunnable(100000000, runnable, downLatch)).start();
	    }
	    downLatch.await();
	    while(true){
		    logger.info("\n\n===================================尝试获取Connection再次执行===================================");
		    List<Connection> connectionList = new ArrayList<>(connectionCount);
		    for (int i = 0; i < connectionCount; i++) {
		    	connectionList.add(dataSource.getConnection());
		    }
		    for (int i = 0; i < connectionList.size(); i++) {
		    	int index = i;
			    newCmdInsertRunner(()-> connectionList.get(index), 100,i % 2 == 0).run();
		    }
		    TimeUnit.SECONDS.sleep(5);
	    }
    }

    private static Runnable loopRunnable(final int count ,Runnable runnable, CountDownLatch downLatch){
    	return ()->{
		    int current = 0;
		    while(current < count){
		    	try {
				    runnable.run();
			    }
			    catch (Throwable throwable){
			    }
			    current++;
		    }
		    if (downLatch != null){
			    downLatch.countDown();
		    }
	    };
    }

    private static Runnable newCmdInsertRunner(ConnectionSupplier supplier, int count, boolean ascendingOrder){
	    return ()->{
		    int current = 0;
		    Connection connection = null;
		    try {
			    connection = supplier.get();
			    while(current < count){
				    cmdInsert(connection, ascendingOrder);
				    current++;
			    }
		    }catch (Exception e) {
			    BatchUpdateException batchUpdateException = recursiveFindException(e, BatchUpdateException.class);
			    if (batchUpdateException != null) {
				    logger.error("updateCounts:{}", batchUpdateException.getUpdateCounts());
			    }
			    else {
				    //logger.error("", e);
			    }
		    } finally {
			    //logger.info("连接[{}]执行{}", connection.toString(), current == count? "成功" : "失败");
			    closeConnection(connection);
		    }
	    };
    }

    private static void closeConnection(Connection connection){
    	if (connection == null){
    		return;
	    }
	    try {
		    connection.close();
	    } catch (SQLException e) {
	    }
    }

    private static void cmdInsert(Connection connection, boolean ascendingOrder) throws SQLException {
	    String cmd = "INSERT INTO t (id, name, value) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)";
	    int max = 10;
	    try (PreparedStatement statement = connection.prepareStatement(cmd)) {
		    for (int i = 0; i < max; i++) {
			    int id = ascendingOrder ? i : (max - i - 1);
			    String name = "name-"+i;
			    int value = (int)(System.currentTimeMillis() / 1000);
			    statement.setInt(1, id);
			    statement.setString(2, name);
			    statement.setInt(3, value);
			    statement.addBatch();
		    }
		    int[] executeBatch = statement.executeBatch();
	    }
    }

	private static BatchUpdateException recursiveFindException(Throwable e, Class<BatchUpdateException> expected) {
		if (e == null) {
			return null;
		}
		if (expected.isInstance(e)) {
			return expected.cast(e);
		}
		return recursiveFindException(e.getCause(), expected);
	}

	@FunctionalInterface
	private interface ConnectionSupplier{
		Connection get() throws Exception;
	}
}
