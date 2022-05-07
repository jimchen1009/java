package com.jim.demo.mybatis.config;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@MappedTypes(String.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class StringTypeHandler implements TypeHandler<String> {

    private static final Logger logger = LoggerFactory.getLogger(StringTypeHandler.class);

    @Override
    public void setParameter(PreparedStatement preparedStatement, int i, String s, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, s);
        logger.info(i + ":" + s);
    }

    @Override
    public String getResult(ResultSet resultSet, String s) throws SQLException {
        return resultSet.getString(s);
    }

    @Override
    public String getResult(ResultSet resultSet, int i) throws SQLException {
        return resultSet.getString(i);
    }

    @Override
    public String getResult(CallableStatement callableStatement, int i) throws SQLException {
        return callableStatement.getString(i);
    }
}
