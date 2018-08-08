package com.ximuyi.demo.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerMain {

    private static final Logger logger = LoggerFactory.getLogger(LoggerMain.class);

    public static void main(String[] args){
        logger.info("helo world");
    }
}
