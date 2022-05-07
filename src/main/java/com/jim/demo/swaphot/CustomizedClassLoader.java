package com.jim.demo.swaphot;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by chenjingjun on 2018-04-02.
 */
public class CustomizedClassLoader extends ClassLoader  {
    public CustomizedClassLoader() {
        super(CustomizedClassLoader.class.getClassLoader());
        System.out.println(CustomizedClassLoader.class.getClassLoader());
    }

    public Class loadByte(byte[] classByte) {
        return defineClass(null, classByte, 0, classByte.length);
    }

    public Class loadByPath(String filePath) throws Exception {
        InputStream is = new FileInputStream(filePath);

        byte[] b = new byte[is.available()];

        is.read(b);
        is.close();
        return defineClass(null, b, 0, b.length);
    }
}
