package com.ximuyi.demo;

/**
 * Created by chenjingjun on 2018-04-04.
 */
public class Main {
    public static abstract class Cls<T>{
        private Class<T> cls ;

        public Cls(Class<T> cls) {
            this.cls = cls;
        }

        public void function(Object object){
            if (object.getClass().isAssignableFrom(cls)){
                print(cls.cast(object));
            }
        }

        public abstract void print(T value);
    }

    private static class Obj extends Cls<Integer> {

        public Obj() {
            super(Integer.class);
        }

        @Override
        public void print(Integer value) {

        }
    }

    public static void main(String[] args) {
        /***
         * -XX:CompileThreshold=5000
         */
        final int count = 500000000;
        long current = 0;
        Object value = new Obj();


        int times = 2;
        while (times-- > 0) {
            current = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                if (value.getClass().isAssignableFrom(Obj.class)) {
                }
            }
            System.out.println("第" + times + "次isAssignableFrom耗时：" + (System.currentTimeMillis() - current));

            current = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                if (value instanceof Obj) {
                }
            }
            System.out.println("第" + times + "次instanceof耗时：" + (System.currentTimeMillis() - current));
        }
    }
}
