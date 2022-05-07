package com.jim.demo.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.*;

public class FreeMarkerMain {

    public static void main(String[] args) throws IOException, TemplateException {
//        StringReader reader = new StringReader("userName:${user}; URL:${url};");
//        Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
//        Template template = new Template(null, reader, configuration);
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("user", "jim");
//        map.put("url", "www.jim.org");
//
//        StringWriter writer = new StringWriter();
//        template.process(map, writer);
//        System.out.println(writer.toString());

        Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        String templepath = FreeMarkerMain.class.getResource("/").getPath();
        configuration.setDirectoryForTemplateLoading(new File(templepath));
        configuration.setClassicCompatible(false);
        Template template0 = configuration.getTemplate("freemaker.ftl");
        List<Property> properties = new ArrayList<>();
        properties.add(new Property(Integer.class, "value"));
        properties.add(new Property(String.class, "string"));
        properties.add(new Property(Date.class, "date"));
        Entity entity = new Entity("FreeMarkerOut", FreeMarkerMain.class.getPackage().getName(), properties);
        File javaFile = new File("D:/demo/java/src/main/java/com/jim/demo/freemarker/" + "FreeMarkerOut.java");
        Map<String, Object> map0 = new HashMap<String, Object>();
        map0.put("entity", entity);
        if (!javaFile.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            System.out.println("目标文件所在目录不存在，准备创建它！");
            if (!javaFile.getParentFile().mkdirs()) {
                System.out.println("创建目标文件所在目录失败！");
            }
        }
        // 步骤四：合并 模板 和 数据模型
        // 创建.java类文件
        Writer javaWriter = new FileWriter(javaFile);
        template0.process(map0, javaWriter);
        javaWriter.flush();
        System.out.println("文件生成路径：" + javaFile.getCanonicalPath());

        javaWriter.close();
        // 输出到Console控制台
        Writer out = new OutputStreamWriter(System.out);
        template0.process(map0, out);
        out.flush();
        out.close();
    }
}
