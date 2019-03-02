package com.ximuyi.demo.tedis;

import com.taobao.common.tedis.Group;
import com.taobao.common.tedis.commands.DefaultValueCommands;
import com.taobao.common.tedis.core.ValueCommands;
import com.taobao.common.tedis.group.TedisGroup;

public class TedisMain {
    public static void main(String[] args){
        Group tedisGroup = new TedisGroup("app", "1.0.0");
        tedisGroup.init();
        ValueCommands valueCommands = new DefaultValueCommands(tedisGroup.getTedis());
        // 写入一条数据
        valueCommands.set(1, "test", "test value object");
        // 读取一条数据
        valueCommands.get(1, "test");
    }
}
