package com.ximuyi.demo.tedis;

//import com.taobao.common.tedis.Group;
//import com.taobao.common.tedis.commands.DefaultValueCommands;
//import com.taobao.common.tedis.core.ValueCommands;
//import com.taobao.common.tedis.group.TedisGroup;

/***
 * Warning:<i><b>root project 'java': Web Facets/Artifacts will not be configured properly</b>
 * Details: org.gradle.api.internal.artifacts.ivyservice.DefaultLenientConfiguration$ArtifactResolveException: Could not resolve all files for configuration ':runtimeClasspath'.
 * Caused by: org.gradle.internal.resolve.ModuleVersionNotFoundException: Could not find hessian:hessian:3.0.13.
 * Required by:
 *     project : > com.taobao.common:tedis-group:1.1.8 > com.taobao.common:tedis-atomic:1.1.8 > com.taobao.common:tedis-common:1.1.8</i>
 *
 *
 * Could not resolve all files for configuration ':compile'.
 * > Could not find hessian:hessian:3.0.13.
 *   Searched in the following locations:
 *       http://localhost:8081/nexus/content/groups/public/hessian/hessian/3.0.13/hessian-3.0.13.pom
 *       http://localhost:8081/nexus/content/groups/public/hessian/hessian/3.0.13/hessian-3.0.13.jar
 *       http://repo.maven.apache.org/maven2/hessian/hessian/3.0.13/hessian-3.0.13.pom
 *       http://repo.maven.apache.org/maven2/hessian/hessian/3.0.13/hessian-3.0.13.jar
 *   Required by:
 *       project : > com.taobao.common:tedis-group:1.1.8 > com.taobao.common:tedis-atomic:1.1.8 > com.taobao.common:tedis-common:1.1.8
 *
 *
 * 这个jar在网上已经没有了：hessian:hessian:3.0.13，废除例子
 *      compile group: 'com.taobao.common', name: 'tedis-group', version: '1.1.8'
 *      compile group: 'com.taobao.diamond', name: 'diamond-client', version: '3.7.0-sopen'
 *      compile group: 'com.taobao.diamond', name: 'diamond-utils', version: '3.2.0'
 *
 */
public class TedisMain {

    public static void main(String[] args){

        //Group tedisGroup = new TedisGroup("app", "1.0.0");
        //tedisGroup.init();
        //ValueCommands valueCommands = new DefaultValueCommands(tedisGroup.getTedis());
        //// 写入一条数据
        //valueCommands.set(1, "test", "test value object");
        //// 读取一条数据
        //valueCommands.get(1, "test");
    }
}
