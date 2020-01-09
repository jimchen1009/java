package com.ximuyi.demo.alipay.sofa;

import com.alipay.remoting.rpc.RpcServer;

public class SofaRpcServer {
	
	public static void main(String[] args){
		RpcServer rpcServer = new RpcServer("127.0.0.1", 1000);
		rpcServer.startup();
	}
}
