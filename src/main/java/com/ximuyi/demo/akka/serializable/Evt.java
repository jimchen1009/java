package com.ximuyi.demo.akka.serializable;

import scala.Serializable;

/**
 * Created by chenjingjun on 2018-04-08.
 */
public class Evt implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String data;

    public Evt(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}