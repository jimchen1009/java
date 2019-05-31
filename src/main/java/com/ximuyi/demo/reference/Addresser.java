package com.ximuyi.demo.reference;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/***
 * 在java中内存中的对象地址是可变的，所以获得的内存地址有可能会变化
 * 保存地址下来压根没啥用
 */
public class Addresser {

    private static Unsafe unsafe;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long addressOf(Object o) throws Exception {

        Object[] array = new Object[] { o };

        long baseOffset = unsafe.arrayBaseOffset(Object[].class);
        //arrayBaseOffset方法是一个本地方法，可以获取数组第一个元素的偏移地址
        int addressSize = unsafe.addressSize();
        long objectAddress;
        switch (addressSize) {
            case 4:
                objectAddress = unsafe.getInt(array, baseOffset);
                //getInt方法获取对象中offset偏移地址对应的int型field的值
                break;
            case 8:
                objectAddress = unsafe.getLong(array, baseOffset);
                //getLong方法获取对象中offset偏移地址对应的long型field的值
                break;
            default:
                throw new Error("unsupported address size: " + addressSize);
        }
        return (objectAddress);
    }
}
