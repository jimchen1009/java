package com.ximuyi.demo.beetl;

import org.beetl.sql.core.annotatoin.SqlResource;
import org.beetl.sql.core.mapper.BaseMapper;

import java.util.List;

@SqlResource("beetlUser")
public interface BeetlDao extends BaseMapper<BeetlUser> {

    /**
     * 如果你使用JDK8，不必为参数提供名称，自动对应。但必须保证java编译的时候开启-parameters选项。
     * 如果使用JDK8 以下的版本，则可以使用@Param注解()
     * List<User> select(@Param("name") String name);
     * @param name
     * @return
     */
    List<BeetlUser> selectByName(String name);
}
