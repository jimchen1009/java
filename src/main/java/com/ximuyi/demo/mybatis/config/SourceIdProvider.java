package com.ximuyi.demo.mybatis.config;

import org.apache.ibatis.mapping.DatabaseIdProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class SourceIdProvider implements DatabaseIdProvider {

    private Properties properties;
    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String getDatabaseId(DataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();
        try {
            String string = connection.getMetaData().getDatabaseProductName();
            return properties.get(string).toString();
        }
        finally {
            connection.close();
        }
    }
}
