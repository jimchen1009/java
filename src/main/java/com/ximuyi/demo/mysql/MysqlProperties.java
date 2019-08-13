package com.ximuyi.demo.mysql;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.ibatis.io.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class MysqlProperties {

	private static final String CMD = "INSERT INTO t (id, name, value) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)";

	private static HikariDataSource DataSource;

	public static void main(String[] args) throws IOException, SQLException, InterruptedException {
		Properties properties = new Properties();
		InputStream stream = Resources.getResourceAsStream("mysql.properties");
		properties.load(stream);
		DataSource = new MysqlSourceBuilder(properties).build(10);
		scenario2();
	}

	private static void scenario0() throws SQLException, InterruptedException {
		for (int i = 0; i < 10; i++) {
			TimeUnit.SECONDS.sleep(5);
			executeOne(DataSource);
		}
		/**
		 * 参数：
		 * useLocalSessionState=false
		 *
		 * 2019-08-13T08:32:12.396458Z	   54 Query	select @@session.tx_read_only
		 * 2019-08-13T08:32:12.396458Z	   54 Query	INSERT INTO t (id, name, value) VALUES (0, 'property', 1565685132) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T08:32:17.425458Z	   54 Query	select @@session.tx_read_only
		 * 2019-08-13T08:32:17.426458Z	   54 Query	INSERT INTO t (id, name, value) VALUES (0, 'property', 1565685137) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T08:32:22.450458Z	   54 Query	select @@session.tx_read_only
		 * 2019-08-13T08:32:22.450458Z	   54 Query	INSERT INTO t (id, name, value) VALUES (0, 'property', 1565685142) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 *
		 *
		 * 问题：
		 * 每次执行都会有 Query	select @@session.tx_read_only
		 * 解决：
		 * useLocalSessionState=true
		 */
	}

	private static void scenario1() throws SQLException, InterruptedException {
		for (int i = 0; i < 10; i++) {
			TimeUnit.SECONDS.sleep(5);
			executeOne(DataSource);
		}
		/**
		 * 参数：
		 * useServerPrepStmts=true
		 * cachePrepStmts=true
		 *
		 * 2019-08-13T09:06:23.993458Z	   94 Prepare	INSERT INTO t (id, name, value) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:06:23.999458Z	   94 Execute	INSERT INTO t (id, name, value) VALUES (0, 'property', 1565687183) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:06:29.033458Z	   94 Execute	INSERT INTO t (id, name, value) VALUES (0, 'property', 1565687189) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:06:34.042458Z	   94 Execute	INSERT INTO t (id, name, value) VALUES (0, 'property', 1565687194) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:06:39.706458Z	   94 Execute	INSERT INTO t (id, name, value) VALUES (0, 'property', 1565687199) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)

		 * 参数：
		 * useServerPrepStmts=true
		 * cachePrepStmts=false
		 * 2019-08-13T09:10:46.045458Z	  104 Prepare	INSERT INTO t (id, name, value) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:10:46.049458Z	  104 Execute	INSERT INTO t (id, name, value) VALUES (0, 'property', 1565687446) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:10:46.083458Z	  104 Close stmt
		 * 2019-08-13T09:10:51.096458Z	  104 Prepare	INSERT INTO t (id, name, value) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:10:51.097458Z	  104 Execute	INSERT INTO t (id, name, value) VALUES (0, 'property', 1565687451) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:10:51.166458Z	  104 Close stmt
		 * 2019-08-13T09:10:56.167458Z	  104 Prepare	INSERT INTO t (id, name, value) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:10:56.168458Z	  104 Execute	INSERT INTO t (id, name, value) VALUES (0, 'property', 1565687456) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:10:56.428458Z	  104 Close stmt
		 */
	}

	private static void scenario2() throws SQLException, InterruptedException {
		TimeUnit.SECONDS.sleep(5);
		for (int i = 0; i < 10; i++) {
			TimeUnit.SECONDS.sleep(2);
			executeBatch(DataSource);
		}
		/***
		 * 参数：
		 * useServerPrepStmts=true
		 * rewriteBatchedStatements=true
		 * cachePrepStmts=false
		 *
		 * 2019-08-13T09:21:54.796458Z	  154 Prepare	INSERT INTO t (id, name, value) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:21:54.811458Z	  154 Prepare	INSERT INTO t (id, name, value) VALUES (?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?)  ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:21:54.812458Z	  154 Execute	INSERT INTO t (id, name, value) VALUES (0, 'property-0', 1565688114) ,(1, 'property-1', 1565688114) ,(2, 'property-2', 1565688114) ,(3, 'property-3', 1565688114) ,(4, 'property-4', 1565688114) ,(5, 'property-5', 1565688114)  ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:21:54.831458Z	  154 Close stmt
		 * 2019-08-13T09:21:54.831458Z	  154 Close stmt
		 * 2019-08-13T09:21:56.844458Z	  154 Prepare	INSERT INTO t (id, name, value) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:21:56.846458Z	  154 Prepare	INSERT INTO t (id, name, value) VALUES (?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?)  ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:21:56.846458Z	  154 Execute	INSERT INTO t (id, name, value) VALUES (0, 'property-0', 1565688116) ,(1, 'property-1', 1565688116) ,(2, 'property-2', 1565688116) ,(3, 'property-3', 1565688116) ,(4, 'property-4', 1565688116) ,(5, 'property-5', 1565688116) ,(6, 'property-6', 1565688116) ,(7, 'property-7', 1565688116)  ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:21:56.861458Z	  154 Close stmt
		 * 2019-08-13T09:21:56.862458Z	  154 Close stmt
		 * 2019-08-13T09:21:58.862458Z	  154 Prepare	INSERT INTO t (id, name, value) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:21:58.864458Z	  154 Prepare	INSERT INTO t (id, name, value) VALUES (?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?)  ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:21:58.864458Z	  154 Execute	INSERT INTO t (id, name, value) VALUES (0, 'property-0', 1565688118) ,(1, 'property-1', 1565688118) ,(2, 'property-2', 1565688118) ,(3, 'property-3', 1565688118) ,(4, 'property-4', 1565688118)  ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:21:58.873458Z	  154 Close stmt
		 * 2019-08-13T09:21:58.873458Z	  154 Close stmt
		 *
		 * 参数：
		 * useServerPrepStmts=true
		 * rewriteBatchedStatements=true
		 * cachePrepStmts=true
		 *
		 * 2019-08-13T09:36:25.612458Z	  164 Prepare	INSERT INTO t (id, name, value) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:36:25.630458Z	  164 Prepare	INSERT INTO t (id, name, value) VALUES (?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?)  ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:36:25.631458Z	  164 Execute	INSERT INTO t (id, name, value) VALUES (0, 'property-0', 1565688985) ,(1, 'property-1', 1565688985) ,(2, 'property-2', 1565688985) ,(3, 'property-3', 1565688985) ,(4, 'property-4', 1565688985) ,(5, 'property-5', 1565688985) ,(6, 'property-6', 1565688985)  ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:36:27.824458Z	  164 Prepare	INSERT INTO t (id, name, value) VALUES (?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?)  ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:36:27.825458Z	  164 Execute	INSERT INTO t (id, name, value) VALUES (0, 'property-0', 1565688987) ,(1, 'property-1', 1565688987) ,(2, 'property-2', 1565688987) ,(3, 'property-3', 1565688987) ,(4, 'property-4', 1565688987) ,(5, 'property-5', 1565688987) ,(6, 'property-6', 1565688987) ,(7, 'property-7', 1565688987) ,(8, 'property-8', 1565688987) ,(9, 'property-9', 1565688987)  ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:36:30.119458Z	  164 Prepare	INSERT INTO t (id, name, value) VALUES (?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?)  ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:36:30.119458Z	  164 Execute	INSERT INTO t (id, name, value) VALUES (0, 'property-0', 1565688990) ,(1, 'property-1', 1565688990) ,(2, 'property-2', 1565688990) ,(3, 'property-3', 1565688990) ,(4, 'property-4', 1565688990) ,(5, 'property-5', 1565688990)  ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:36:32.134458Z	  164 Prepare	INSERT INTO t (id, name, value) VALUES (?, ?, ?) ,(?, ?, ?) ,(?, ?, ?)  ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:36:32.134458Z	  164 Execute	INSERT INTO t (id, name, value) VALUES (0, 'property-0', 1565688992) ,(1, 'property-1', 1565688992) ,(2, 'property-2', 1565688992)  ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:36:34.147458Z	  164 Execute	INSERT INTO t (id, name, value) VALUES (0, 'property-0', 1565688994) ,(1, 'property-1', 1565688994) ,(2, 'property-2', 1565688994) ,(3, 'property-3', 1565688994) ,(4, 'property-4', 1565688994) ,(5, 'property-5', 1565688994) ,(6, 'property-6', 1565688994) ,(7, 'property-7', 1565688994) ,(8, 'property-8', 1565688994) ,(9, 'property-9', 1565688994)  ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:36:36.168458Z	  164 Execute	INSERT INTO t (id, name, value) VALUES (0, 'property-0', 1565688996) ,(1, 'property-1', 1565688996) ,(2, 'property-2', 1565688996) ,(3, 'property-3', 1565688996) ,(4, 'property-4', 1565688996) ,(5, 'property-5', 1565688996) ,(6, 'property-6', 1565688996)  ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:36:38.189458Z	  164 Prepare	INSERT INTO t (id, name, value) VALUES (?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?)  ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:36:38.189458Z	  164 Execute	INSERT INTO t (id, name, value) VALUES (0, 'property-0', 1565688998) ,(1, 'property-1', 1565688998) ,(2, 'property-2', 1565688998) ,(3, 'property-3', 1565688998) ,(4, 'property-4', 1565688998) ,(5, 'property-5', 1565688998) ,(6, 'property-6', 1565688998) ,(7, 'property-7', 1565688998) ,(8, 'property-8', 1565688998)  ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:36:40.211458Z	  164 Prepare	INSERT INTO t (id, name, value) VALUES (?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?)  ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:36:40.214458Z	  164 Execute	INSERT INTO t (id, name, value) VALUES (0, 'property-0', 1565689000) ,(1, 'property-1', 1565689000) ,(2, 'property-2', 1565689000) ,(3, 'property-3', 1565689000) ,(4, 'property-4', 1565689000) ,(5, 'property-5', 1565689000) ,(6, 'property-6', 1565689000) ,(7, 'property-7', 1565689000)  ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:36:42.290458Z	  164 Execute	INSERT INTO t (id, name, value) VALUES (0, 'property-0', 1565689002) ,(1, 'property-1', 1565689002) ,(2, 'property-2', 1565689002) ,(3, 'property-3', 1565689002) ,(4, 'property-4', 1565689002) ,(5, 'property-5', 1565689002) ,(6, 'property-6', 1565689002) ,(7, 'property-7', 1565689002) ,(8, 'property-8', 1565689002)  ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:36:44.314458Z	  164 Prepare	INSERT INTO t (id, name, value) VALUES (?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?) ,(?, ?, ?)  ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:36:44.314458Z	  164 Execute	INSERT INTO t (id, name, value) VALUES (0, 'property-0', 1565689004) ,(1, 'property-1', 1565689004) ,(2, 'property-2', 1565689004) ,(3, 'property-3', 1565689004) ,(4, 'property-4', 1565689004)  ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 *
		 *
		 * 参数：
		 * useServerPrepStmts=true
		 * rewriteBatchedStatements=false  [关闭批量]
		 * cachePrepStmts=true
		 *
		 *2019-08-13T09:38:17.717458Z	  174 Prepare	INSERT INTO t (id, name, value) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:38:17.730458Z	  174 Execute	INSERT INTO t (id, name, value) VALUES (0, 'property-0', 1565689097) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:38:17.747458Z	  174 Execute	INSERT INTO t (id, name, value) VALUES (1, 'property-1', 1565689097) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:38:17.758458Z	  174 Execute	INSERT INTO t (id, name, value) VALUES (2, 'property-2', 1565689097) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:38:17.770458Z	  174 Execute	INSERT INTO t (id, name, value) VALUES (3, 'property-3', 1565689097) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:38:17.786458Z	  174 Execute	INSERT INTO t (id, name, value) VALUES (4, 'property-4', 1565689097) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:38:17.794458Z	  174 Execute	INSERT INTO t (id, name, value) VALUES (5, 'property-5', 1565689097) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:38:17.807458Z	  174 Execute	INSERT INTO t (id, name, value) VALUES (6, 'property-6', 1565689097) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:38:17.819458Z	  174 Execute	INSERT INTO t (id, name, value) VALUES (7, 'property-7', 1565689097) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:38:17.832458Z	  174 Execute	INSERT INTO t (id, name, value) VALUES (8, 'property-8', 1565689097) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:38:19.894458Z	  174 Execute	INSERT INTO t (id, name, value) VALUES (0, 'property-0', 1565689099) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:38:19.913458Z	  174 Execute	INSERT INTO t (id, name, value) VALUES (1, 'property-1', 1565689099) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:38:21.926458Z	  174 Execute	INSERT INTO t (id, name, value) VALUES (0, 'property-0', 1565689101) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:38:22.239458Z	  174 Execute	INSERT INTO t (id, name, value) VALUES (1, 'property-1', 1565689101) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 * 2019-08-13T09:38:22.266458Z	  174 Execute	INSERT INTO t (id, name, value) VALUES (2, 'property-2', 1565689101) ON DUPLICATE KEY UPDATE name=VALUES(name), value=VALUES(value)
		 **/
	}

	private static void executeOne(HikariDataSource dataSource) throws SQLException {
		try (Connection connection = dataSource.getConnection()){
			executeOne(connection);
		}
	}

	private static void executeBatch(HikariDataSource dataSource) throws SQLException {
		try (Connection connection = dataSource.getConnection()){
			executeBatch(connection);
		}
	}

	private static void executeOne(Connection connection) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement(CMD)) {
			String name = "property";
			int value = (int)(System.currentTimeMillis() / 1000);
			statement.setInt(1, 0);
			statement.setString(2, name);
			statement.setInt(3, value);
			statement.execute();
		}
	}

	private static void executeBatch(Connection connection) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement(CMD)) {
			int count = RandomUtils.nextInt(10) + 1;
			for (int i = 0; i < count; i++) {
				String name = "property-"+i;
				int value = (int)(System.currentTimeMillis() / 1000);
				statement.setInt(1, i);
				statement.setString(2, name);
				statement.setInt(3, value);
				statement.addBatch();
			}
			int[] executeBatch = statement.executeBatch();
		}
	}
}
