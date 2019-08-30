package com.ximuyi.demo.swaphot.assist;

import javassist.*;

/**
 * Created by chenjingjun on 2018-04-03.
 */
public class ClassPoolMain {
    public static void main(String[] args) throws Exception {
        ClassPool cp = ClassPool.getDefault();
        CtClass cc = cp.get("com.ximuyi.demo.swaphot.assist.ClassPoolClass");
        CtConstructor constructor = new CtConstructor(new CtClass[] { cp.get(String.class.getName()),cp.get(String.class.getName()) }, cc);
        constructor.setModifiers(Modifier.PUBLIC);
        constructor.setBody("{this.value=$1;this.value2=$2;}");
        cc.addConstructor(constructor);
        CtMethod m = cc.getDeclaredMethod("say");
        m.insertBefore("{ System.out.println(\"Hello.say():\"); }");
        Class<?> c = cc.toClass();
        ClassPoolClass h = (ClassPoolClass) c.getConstructor(String.class, String.class).newInstance(new Object[] { "javassist", "param2" });
        h.say();
    }
}
