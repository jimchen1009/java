package com.jim.demo.jodd;

import com.jim.demo.freemarker.FreeMarkerMain;
import jodd.io.findfile.ClassScanner;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class JoddMain {

    public static void main(String[] args){
        String clsspath = FreeMarkerMain.class.getResource("/").getPath();
        ClassScanner scanner = new ClassScanner(){
            @Override
            protected void onEntry(ClassPathEntry classPathEntry) {
                super.onEntry(classPathEntry);
                System.out.println(ReflectionToStringBuilder.toString(classPathEntry));
            }
        };
        scanner.includeResources(true);
        scanner.includeAllJars(true);
        scanner.scan(clsspath).start();
    }
}
