package com.ximuyi.demo.akka.unmodifiable;

import akka.actor.UntypedActor;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by chenjingjun on 2018-04-01.
 */
public class Greeter extends UntypedActor {
    @Override
    public void onReceive(Object o) throws Exception {
        try {
            System.out.println("Greeter收到的数据为：" + JSONObject.toJSONString(o));
            getSender().tell("Greeter工作完成。", getSelf());//给发送至发送信息.
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
