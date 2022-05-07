package com.jim.demo.javap;

/**
 * Created by chenjingjun on 2018-02-01.
 */
public class Javap implements IJavap {
    private String name;
    private int value;


    public Javap(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String getString() {
        return "{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
