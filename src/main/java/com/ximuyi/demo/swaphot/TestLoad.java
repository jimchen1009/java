package com.ximuyi.demo.swaphot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjingjun on 2018-04-02.
 */
public class TestLoad {
    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        HotSwapClassLoader classloader = new HotSwapClassLoader();
        String packagePath = TestLoad.class.getName();
        packagePath = packagePath.substring(0, packagePath.lastIndexOf(".") + 1) + "MainTest";
        String clsPath = System.getProperty("user.dir") + "\\target\\classes\\" + packagePath.replace(".", "\\") + ".class";

        MainTest test = new MainTest(5);
        test.print();

        while (true) {
            String str = br.readLine();

            if (str.equals("stop")) {
                break;
            }
            else {
                classloader = new HotSwapClassLoader();
                /**
                 * 以下注释为，本次测试重点信息
                 */
                // 下面一行：类加载器在尝试自己去查找某个类的字节代码并定义它时，
                // 会先代理给其父类加载器，由父类加载器先去尝试加载这个类，依次类推
                // 也就是说：如存在此行时，下一行，就报错
                // java.lang.LinkageError: loader (instance of
                // HotSwapClassLoader): attempted duplicate class definition for
                // name: "MainTest"

//                Class clazz = Class.forName(packagePath, false, classloader);
                classloader.loadByPath(clsPath);
            }
            List names = new ArrayList<>();
            names.add("main");
            names.add("add");
            Class clazz = Class.forName(packagePath, true, classloader);
            for (Method method : clazz.getMethods()){
                System.out.println("函数：" + method.getName());
                if (names.contains(method.getName())){
                    method.invoke(null, new String[] { null });
                }
            }
            for (Field field : clazz.getFields()){
                System.out.println("字段：" + field.getName());
                if (names.contains(field.getName())){
                    System.out.println(field.get(null));
                }
            }
        }
        test = new MainTest(5);
        test.print();
    }
}
