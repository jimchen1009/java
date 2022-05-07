package com.jim.demo.akka.remote.e0;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketException;

public class RemoteAkkaServer {
    private static final Logger logger = LoggerFactory.getLogger(AkkaService.class);

    public static void main(String[] args) throws SocketException {
        AkkaService remoteService = AkkaService.getInstance(10002, "remoteServer", "remoteActor");
        remoteService.init(true);
        logger.info("remoteServer启动成功");
    }
}
