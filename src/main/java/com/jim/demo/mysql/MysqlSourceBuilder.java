package com.jim.demo.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.Properties;

/***
 *
 * java.sql.SQLException: Unable to load authentication plugin 'caching_sha2_password'.
 * 	at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:545) ~[mysql-connector-java-6.0.5.jar:6.0.5]
 * 	at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:513) ~[mysql-connector-java-6.0.5.jar:6.0.5]
 * 	at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:505) ~[mysql-connector-java-6.0.5.jar:6.0.5]
 * 	at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:479) ~[mysql-connector-java-6.0.5.jar:6.0.5]
 * 	at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:489) ~[mysql-connector-java-6.0.5.jar:6.0.5]
 * 	at com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping.translateException(SQLExceptionsMapping.java:78) ~[mysql-connector-java-6.0.5.jar:6.0.5]
 * 	at com.mysql.cj.jdbc.ConnectionImpl.createNewIO(ConnectionImpl.java:1606) ~[mysql-connector-java-6.0.5.jar:6.0.5]
 *
 * 	相关文档
 * https://dev.mysql.com/doc/refman/8.0/en/caching-sha2-pluggable-authentication.html
 *
 *
 *
 *
 */
public class MysqlSourceBuilder {

	private final Properties properties;
	private final String driver;
	private final String url;
	private final String username;
	private final String password;

	public MysqlSourceBuilder(Properties properties) {

		this.properties = new Properties();
		this.driver = properties.getProperty("jdbc.driverClassName");
		String url = properties.getProperty("jdbc.url");
		int index = url.indexOf("?");
		if (index != -1){
			this.url = url.substring(0, index);
			String[] strings = url.substring(index + 1).split("&");
			for (String string : strings) {
				String[] property = string.split("=");
				this.properties.put("dataSource."+ property[0], property[1]);
			}
		}
		else {
			this.url = url;
		}
		this.username = properties.getProperty("jdbc.username");
		this.password = properties.getProperty("jdbc.password");

		this.properties.setProperty("driverClassName", this.driver);
		this.properties.setProperty("jdbcUrl", this.url);
		this.properties.setProperty("username", this.username);
		this.properties.setProperty("password", this.password);
	}

	public HikariDataSource build(int connectionCount){
		/***
		 * this is because mysql jdbc driver set the default value of "useSessionStatus" to false.
		 * each time driver need to check isReadOnly status of target database,
		 * will send a "select @@session.tx_read_only" to server, set "useSessionStatus" to true will using connection object local state.
		 */
		setDefaultProperty("autoCommit", String.valueOf(true));
		// c3p0的idleTimeout单位是秒，而HikariCP的单位是毫秒，为了统一单位，这里需要转成毫秒。
		setDefaultProperty("connectionTimeout", "5000");
		setDefaultProperty("minimumIdle", String.valueOf(connectionCount));
		setDefaultProperty("maximumPoolSize",String.valueOf(connectionCount));

		HikariConfig hikariConfig = new HikariConfig(properties);
		return new HikariDataSource(hikariConfig);
	}


	public void setDataSourceProperty(String name, String value){
		properties.setProperty(name, value);
	}

	private void setDefaultProperty(String name, String value){
		if (properties.containsKey(name)) {
			return;
		}
		properties.setProperty(name, value);
	}
}
