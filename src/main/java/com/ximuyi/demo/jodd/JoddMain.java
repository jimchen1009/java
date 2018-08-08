package com.ximuyi.demo.jodd;

import com.ximuyi.demo.freemarker.FreeMarkerMain;
import jodd.io.findfile.ClassScanner;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class JoddMain {

    public static void main(String[] args){
        String clsspath = FreeMarkerMain.class.getResource("/").getPath();
        ClassScanner scanner = new ClassScanner(){
            @Override
            protected void onEntry(EntryData entryData) {
                super.onEntry(entryData);
                System.out.println(ReflectionToStringBuilder.toString(entryData));
            }
        };
        scanner.includeResources(true);
        scanner.includeAllJars(true);
        scanner.scan(clsspath).start();
    }
}
