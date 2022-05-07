package com.jim.demo.beetl;

import com.jim.common.ResourceUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.beetl.sql.core.ClasspathLoader;
import org.beetl.sql.core.ConnectionSource;
import org.beetl.sql.core.ConnectionSourceHelper;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.SQLLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLReady;
import org.beetl.sql.core.UnderlinedNameConversion;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.ext.DebugInterceptor;
import org.beetl.sql.ext.SimpleCacheInterceptor;
import org.beetl.sql.ext.gen.GenConfig;
import org.beetl.sql.ext.gen.MapperCodeGen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * http://ibeetl.com/guide/#beetl
 */
public class BeetlMain {

    private static final Logger logger = LoggerFactory.getLogger(BeetlMain.class);

    public static void main(String[] args) throws Exception {
        Properties properties = ResourceUtil.getResourceAsProperties("demo.properties");
        String driver = properties.getProperty("jdbc.driverClassName");
        String url = properties.getProperty("jdbc.url");
        String username = properties.getProperty("jdbc.username");
        String password = properties.getProperty("jdbc.password");
        ConnectionSource source = ConnectionSourceHelper.getSimple(driver, url, username, password);
        DBStyle mysql = new MySqlStyle();
        // sql语句放在classpagth的/sql 目录下
        SQLLoader loader = new ClasspathLoader("/beetlsql");
        // 数据库命名跟java命名一样，所以采用DefaultNameConversion，还有一个是UnderlinedNameConversion，下划线风格的，
        UnderlinedNameConversion nc = new  UnderlinedNameConversion();
        // 最后，创建一个SQLManager,DebugInterceptor 不是必须的，但可以通过它查看sql执行情况
        List<String> lcs = new ArrayList<>();
        lcs.add("beetlUser");
        SimpleCacheInterceptor cache = new SimpleCacheInterceptor(lcs);
        Interceptor[] interceptors = new Interceptor[]{ new DebugInterceptor(), cache};
        SQLManager sqlManager = new SQLManager(mysql, loader, source, nc, interceptors);
        genSQL(sqlManager);
        execute(sqlManager);
        template();
    }

    private static final void execute(SQLManager sqlManager){
        //使用内置的生成的sql 新增用户，如果需要获取主键，可以传入KeyHolder
        BeetlUser user = new BeetlUser();
        user.setAge(19);
        user.setName("Jim");
        sqlManager.insert(user);

        //使用内置sql查询用户
        int id = 1;
        user = sqlManager.unique(BeetlUser.class,id);
        logger.debug(ToStringBuilder.reflectionToString(user, ToStringStyle.NO_CLASS_NAME_STYLE));

        //模板更新,仅仅根据id更新值不为null的列
        BeetlUser newUser = new BeetlUser();
        newUser.setId(1);
        newUser.setAge(20);
        sqlManager.updateTemplateById(newUser);

        //模板查询
        BeetlUser query = new BeetlUser();
        query.setName("Jim");
        List<BeetlUser> beetlUsers0 = sqlManager.template(query);
        log("模板查询", beetlUsers0);

        List<BeetlUser> beetlUsers1 = sqlManager.query(BeetlUser.class).andEq("name","Jim").orderBy("create_date").select();
        log("模板查询", beetlUsers1);
        //Query查询
//        Query userQuery = sqlManager.query(BeetlUser.class);
//        List<BeetlUser> beetlUsers2 = userQuery.lambda().andEq(BeetlUser::getName,"Jim").orderBy(BeetlUser::getCreateDate).select();

        List<PageQuery> pageQueries = new ArrayList<>();
        pageQueries.add(sqlManager.execute(new SQLReady("select * from beetl_user order by id"), BeetlUser.class, new PageQuery(1, 20)));
        pageQueries.add(new PageQuery());
        for (PageQuery pageQuery : pageQueries) {
            pageQuery.setPageSize(3);
            pageQuery.setPageNumber(2);
            sqlManager.pageQuery("beetlUser.queryNewUser", BeetlUser.class, pageQuery);
            logger.debug("totalPage:{} totalRow:{} pageNumber:{}",
                         pageQuery.getTotalPage(), pageQuery.getTotalRow(), pageQuery.getPageNumber());
            log("pageQuery", pageQuery.getList());
        }

        BeetlUserDao mapper = sqlManager.getMapper(BeetlUserDao.class);
        List<BeetlUser> beetlUsers2 = mapper.template(new BeetlUser());
        log("Dao查询", beetlUsers2);

    }

    private static void log(String message, List<BeetlUser> beetlUsers){
        logger.debug(message);
        for (BeetlUser beetlUser : beetlUsers) {
            logger.debug(ToStringBuilder.reflectionToString(beetlUser, ToStringStyle.NO_CLASS_NAME_STYLE));
        }
    }

    private static final void genSQL(SQLManager sqlManager) throws Exception {
        String tableName = "beetl_user";
        String pkg = "com.jim.demo.beetl";
        /***
         * queryNewUser
         * ===
         * select * from beetl_user order by id desc ;
         *
         * queryNewUser$count
         * ===
         * select count(1) from beetl_user
         */
        GenConfig config = new GenConfig();
//        sqlManager.genSQLFile(tableName);
        MapperCodeGen mapper = new MapperCodeGen(pkg);
        config.codeGens.add(mapper);
        sqlManager.genPojoCode(tableName, pkg, config);
        //Console ouput
//        sqlManager.genPojoCodeToConsole(tableName);
//        sqlManager.genSQLTemplateToConsole(tableName);
    }

    private static final void template() throws IOException {
        StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
        //Configuration类总是会先加载默认的配置文件（位于/org/beetl/core/beetl-default.properties)
        Configuration configuration = Configuration.defaultConfiguration();
        GroupTemplate groupTemplate = new GroupTemplate(resourceLoader, configuration);
        Template template = groupTemplate.getTemplate("hello,${name}");
        template.binding("name", "Jim");
        logger.debug(template.render());
    }
}
