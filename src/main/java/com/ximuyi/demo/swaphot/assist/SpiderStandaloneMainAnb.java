package com.ximuyi.demo.swaphot.assist;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.util.HotSwapper;

/**
 * Created by chenjingjun on 2018-04-03.
 */
public class SpiderStandaloneMainAnb {
    public static void main(String[] args) throws Throwable {
//        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spider-base-service.xml");
//        System.out.println("---" + context.getApplicationName());
        Standard standard = new Standard();
        standard.doSomething();
        ClassPool pool = ClassPool.getDefault();
        CtClass clazz = pool.get("com.ximuyi.demo.swaphot.assist.Standard");
        CtMethod cm = clazz.getDeclaredMethod("doSomething");
        cm.insertAt(1,"{System.out.println(\"hello HotSwapper HHHH.\");}");  // clazz完全可以是全新的，这里只是为了测试方便而已
        /***
         * Exception in thread "main" java.net.ConnectException: Connection refused: connect
         at java.net.DualStackPlainSocketImpl.connect0(Native Method)
         at java.net.DualStackPlainSocketImpl.socketConnect(DualStackPlainSocketImpl.java:79)
         at java.net.AbstractPlainSocketImpl.doConnect(AbstractPlainSocketImpl.java:345)
         at java.net.AbstractPlainSocketImpl.connectToAddress(AbstractPlainSocketImpl.java:206)
         at java.net.AbstractPlainSocketImpl.connect(AbstractPlainSocketImpl.java:188)
         at java.net.PlainSocketImpl.connect(PlainSocketImpl.java:172)
         at java.net.SocksSocketImpl.connect(SocksSocketImpl.java:392)
         at java.net.Socket.connect(Socket.java:589)
         at com.sun.tools.jdi.SocketTransportService.attach(SocketTransportService.java:222)
         at com.sun.tools.jdi.GenericAttachingConnector.attach(GenericAttachingConnector.java:116)
         at com.sun.tools.jdi.SocketAttachingConnector.attach(SocketAttachingConnector.java:90)
         at javassist.util.HotSwapper.<init>(HotSwapper.java:119)
         at javassist.util.HotSwapper.<init>(HotSwapper.java:98)
         at com.ximuyi.demo.swaphot.assist.SpiderStandaloneMainAnb.main(SpiderStandaloneMainAnb.java:21)
         */
        HotSwapper swap = new HotSwapper(8000);
        swap.reload("com.ximuyi.demo.swaphot.assist.Standard", clazz.toBytecode());
        standard.doSomething();
    }
}
