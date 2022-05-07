package com.jim.demo.quartz;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.quartz.utils.ConnectionProvider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class HikariConnectionProvider implements ConnectionProvider {
	//JDBC驱动
	private String driver;
	//JDBC连接串
	private String URL;
	//数据库用户名
	private String user;
	//数据库用户密码
	private String password;
	//数据库最大连接数
	private int maxConnection;

	private HikariDataSource dataSource;


	@Override
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	@Override
	public void shutdown() throws SQLException {
		dataSource.close();
	}

	@Override
	public void initialize() throws SQLException {
		Properties hikariProperties = new Properties();
		hikariProperties.setProperty("driverClassName", driver);
		hikariProperties.setProperty("jdbcUrl", URL);
		hikariProperties.setProperty("username", user);
		hikariProperties.setProperty("password", password);
		HikariConfig hikariConfig = new HikariConfig(hikariProperties);
		hikariConfig.setMaximumPoolSize(1);
		dataSource = new HikariDataSource(hikariConfig);
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String URL) {
		this.URL = URL;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getMaxConnection() {
		return maxConnection;
	}

	public void setMaxConnection(int maxConnection) {
		this.maxConnection = maxConnection;
	}
}
