package com.ximuyi.demo.mysql;

import org.apache.ibatis.io.Resources;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class MysqlMain {

    public static void main(String[] args) throws Throwable {
        Properties properties = new Properties();
        InputStream stream = Resources.getResourceAsStream("demo.properties");
        properties.load(stream);
        String driver = properties.getProperty("jdbc.driverClassName");
        String url = properties.getProperty("jdbc.url");
        String username = properties.getProperty("jdbc.username");
        String password = properties.getProperty("jdbc.password");

        Class.forName(driver);

        for (int i = 1; i < 5; i ++){
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                insert(connection, (int)(System.currentTimeMillis() / 1000), "abc" + i);
                TimeUnit.SECONDS.sleep(1);
            }
        }
    }

    private static void insert(Connection con, int arg1, String arg2) throws SQLException {
        String sql = "insert into t select ?,?";
        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setInt(1, arg1);
            statement.setString(2, arg2);
            statement.executeUpdate();
        }
    }
}
