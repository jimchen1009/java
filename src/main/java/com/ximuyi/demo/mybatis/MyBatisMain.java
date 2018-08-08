package com.ximuyi.demo.mybatis;

import com.alibaba.fastjson.JSON;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.ximuyi.demo.mybatis.config.StringTypeHandler;
import com.ximuyi.demo.mybatis.mapper.OptimizeMapper;
import com.ximuyi.demo.mybatis.mapper.SupplierMapper;
import com.ximuyi.demo.mybatis.model.Optimize;
import com.ximuyi.demo.mybatis.model.Supplier;
import com.ximuyi.demo.mybatis.model.SupplierExample;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jodd.io.findfile.FindFile;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class MyBatisMain {

    public static void main(String[] args) throws IOException, PropertyVetoException {
        SqlSessionFactory factory = bulidCode("hikari");
//        SqlSessionFactory factory = bulidConfig();
//        optimize(factory);
        for (int i = 0; i < 10; i++) {
            session(i, factory);
        }
    }

    /**
     *
     * @param factory
     */
    private static void optimize(SqlSessionFactory factory){
        SqlSession session = factory.openSession();
        OptimizeMapper mapper = session.getMapper(OptimizeMapper.class);

        //insert
        List<Optimize> optimizeList = new ArrayList<>();
        long[] rangs = new long[]{1,4,7,8,5,2,3,6,9};
        for (long i  : rangs){
            i = i * 11;
            for (int j = 0; j < 50000; j++){
                optimizeList.add(
                        new Optimize.Builder().id(i)
                                .key1(j)
                                .key2(j)
                                .value("comment-" + i + "-" + j)
                                .build()
                );
            }
            mapper.batchInsert(optimizeList);
            session.commit();
            optimizeList.clear();
        }
    }

    private static void session(long i,SqlSessionFactory factory){
        // 获取到 SqlSession
        SqlSession session1 = factory.openSession();
        SupplierMapper mapper1 = session1.getMapper(SupplierMapper.class);

        //insert
        List<Supplier> supplierList = new ArrayList<>();
        supplierList.add(
                new Supplier.Builder().id(i)
                        .name("name-" + i)
                        .address("address-" + i)
                        .comment("comment-" + i)
                        .phone("10-000" + i)
                        .build()
        );
        try {
            mapper1.batchInsert(supplierList);
            //select
            supplierList = mapper1.selectByExample(new SupplierExample().limit(2,6));
            supplierList.forEach(supplier -> System.out.println(JSON.toJSON(supplier)));
            Supplier supplier = mapper1.selectOneByExample(new SupplierExample().createCriteria().andIdNotEqualTo(1L).example());
            System.out.println(JSON.toJSON(supplier));
            //delete
//            session1.commit();
            //如果 不执行 session1.commit(); 下面的函数就会一直阻塞超时报错
//            mapper1.deleteByExample(new SupplierExample());
            session1.commit();
        }
        catch (Throwable throwable){
            throwable.printStackTrace();
            session1.rollback();
        }
        finally {
            session1.close();
        }
    }

    private static SqlSessionFactory bulidConfig() throws IOException {
        InputStream stream = Resources.getResourceAsStream("myBatisConfig.xml");
        // 创建 SqlSessionFactory
        return new SqlSessionFactoryBuilder().build(stream);
    }

    private static SqlSessionFactory bulidCode(String dbDataSource) throws IOException, PropertyVetoException {
        Properties properties = new Properties();
        InputStream stream = Resources.getResourceAsStream("jdbc.properties");
        properties.load(stream);
        String driver = properties.getProperty("jdbc.driverClassName");
        String url = properties.getProperty("jdbc.url");
        String username = properties.getProperty("jdbc.username");
        String password = properties.getProperty("jdbc.password");
        DataSource dataSource = null;
        if (dbDataSource.equals("c3p0")){
            ComboPooledDataSource c3p0DataSource = new ComboPooledDataSource();
            c3p0DataSource.setDriverClass(driver);
            c3p0DataSource.setJdbcUrl(url);
            c3p0DataSource.setUser(username);
            c3p0DataSource.setPassword(password);
            c3p0DataSource.setMinPoolSize(5);
            c3p0DataSource.setAcquireIncrement(5);
            c3p0DataSource.setMaxPoolSize(10);
            dataSource = c3p0DataSource;
        }
        else if (dbDataSource.equals("hikari")){
            Properties hikariProperties = new Properties();
            hikariProperties.setProperty("driverClassName", driver);
            hikariProperties.setProperty("jdbcUrl", url);
            hikariProperties.setProperty("username", username);
            hikariProperties.setProperty("password", password);
            HikariConfig hikariConfig = new HikariConfig(hikariProperties);
            hikariConfig.setMaximumPoolSize(1);
            dataSource = new HikariDataSource(hikariConfig);
        }

        JdbcTransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.getTypeHandlerRegistry().register(StringTypeHandler.class);
        configuration.getTypeAliasRegistry().registerAlias(Supplier.class);
        configuration.addMapper(SupplierMapper.class);

        FindFile file = new FindFile().searchPath("D:\\demo\\java\\src\\main\\java\\com\\ximuyi\\demo\\mybatis\\db");
        Iterator<File> iterator = file.iterator();
        File next = null;
        while (iterator.hasNext()) {
            next = iterator.next();
            String URI = next.toURI().toString();
            Map<String, XNode> sqlFragments = configuration.getSqlFragments();
            try {
                new XMLMapperBuilder(new FileInputStream(next), configuration, URI, sqlFragments).parse();
            }
            catch (Throwable t){
                System.err.println(next.toString());
            }
        }
        // 创建 SqlSessionFactory
        return new SqlSessionFactoryBuilder().build(configuration);
    }
}
