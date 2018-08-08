package com.ximuyi.demo.gson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by chenjingjun on 2018-03-08.
 */
public class Student {
    @Expose
    private int id;
    @Expose
    @SerializedName("bir")
    private Date birthDay;
    private String name;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    @Override
    public String toString() {
        return "Student [birthDay=" + birthDay + ", id=" + id + ", name="
                + name + "]";
    }
}
