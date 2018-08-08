package com.ximuyi.demo.akka.unmodifiable;

import java.util.Collections;
import java.util.List;

/**
 * Created by chenjingjun on 2018-04-01.
 */
public class Message {
    private final int age;
    private final List<String> list;

    public Message(int age, List<String> list){
        this.age = age;
        /**
         * 把普通list包装为不可变对象
         */
        this.list = Collections.unmodifiableList(list);
    }

    public int getAge() {
        return age;
    }

    public List<String> getList() {
        return list;
    }

}
