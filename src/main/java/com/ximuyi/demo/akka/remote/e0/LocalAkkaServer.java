package com.ximuyi.demo.akka.remote.e0;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketException;
import java.util.concurrent.TimeUnit;

public class LocalAkkaServer {

    private static final Logger logger = LoggerFactory.getLogger(AkkaService.class);

    public static void main(String[] args) throws SocketException, InterruptedException {
        AkkaService localService = AkkaService.getInstance(10001, "localServer", "localActor");
        localService.init(false);
        logger.info("localServer启动成功....");
        for (int i = 0; i < 10000; i++){
            //由于在同一台机器上测试，所以直接取localService的ip
            String str = localService.visitService("remoteServer", localService.getHost(), 10002, "remoteActor",
                    "Hello I'm local " + i + " !");
            logger.info("reply:" + str);
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
