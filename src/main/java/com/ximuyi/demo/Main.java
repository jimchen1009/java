    package com.ximuyi.demo;

    import org.apache.commons.lang3.RandomUtils;
    import org.apache.commons.lang3.concurrent.BasicThreadFactory;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

    import java.io.BufferedReader;
    import java.io.File;
    import java.io.FileNotFoundException;
    import java.io.FileReader;
    import java.io.FileWriter;
    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.Collections;
    import java.util.HashMap;
    import java.util.Iterator;
    import java.util.List;
    import java.util.Map;
    import java.util.Set;
    import java.util.concurrent.ScheduledThreadPoolExecutor;
    import java.util.concurrent.TimeUnit;

    /**
 * Created by chenjingjun on 2018-04-04.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static abstract class Cls<T>{
        private Class<T> cls ;

        public Cls(Class<T> cls) {
            this.cls = cls;
        }

        public void function(Object object){
            if (object.getClass().isAssignableFrom(cls)){
                print(cls.cast(object));
            }
        }

        public abstract void print(T value);
    }

    private static class Obj extends Cls<Integer> {

        public Obj() {
            super(Integer.class);
        }

        @Override
        public void print(Integer value) {

        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
	    List<String> readLines = new ArrayList<>();
	    for (int i = 1; i <= 50000; i++) {
		    readLines.add("ptGM" + i);
	    }
	    for (int i = 0; i < 5; i++) {
	    	String fileName = "协会账号乱序 " + i + ".txt";
		    FileWriter fileWriter = new FileWriter("C:\\Users\\chenjingjun\\Desktop\\" + fileName);
		    fileWriter.write("openid;\n");
		    for (int j = 0; j < 10000; j++) {
			    int index = RandomUtils.nextInt(1, readLines.size() - 1);
			    String string = readLines.get(index);
			    fileWriter.write(string + ";\n");
		    }
		    fileWriter.flush();
	    }


	    BasicThreadFactory factory = new BasicThreadFactory.Builder()
                .namingPattern("CacheScheduler-%d")
                .uncaughtExceptionHandler((t, e) -> logger.error("", e))
                .daemon(true)
                .build();
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, factory);
        executor.scheduleWithFixedDelay(()-> {
            logger.debug("UnsupportedOperationException");
            //如果抛出异常，不捕获的话，这个任务就不会再被定时调度了。
            throw new UnsupportedOperationException();
        }, 2, 1, TimeUnit.MILLISECONDS);

        Thread.sleep(TimeUnit.HOURS.toMillis(10));

        valueOf("zookeeper://root:000000@10.17.2.68:2181,10.17.2.68:2182,10.17.2.68:2183");
        int intValue = 1;
        Set<Long> longValues = Collections.singleton(1L);
        if (longValues.contains(intValue)) {
            System.out.println("yes");
        }
        String key = "ALTER TABLE `guild_name%d` CHANGE COLUMN `Name` `Name` VARCHAR(15) NOT NULL COLLATE 'utf8mb4_unicode_ci';";
        for (int i = 0; i < 100; i++) {
            System.out.println(String.format(key, i));
        }

        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            map.put(i, i);
        }
        for (Iterator<Map.Entry<Integer, Integer>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            if (entry.getKey() == 2){
                iterator.remove();
            }
        }
        /***
         * -XX:CompileThreshold=5000
         */
        final int count = 500000000;
        long current = 0;
        Object value = new Obj();


        int times = 2;
        while (times-- > 0) {
            current = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                if (value.getClass().isAssignableFrom(Obj.class)) {
                }
            }
            System.out.println("第" + times + "次isAssignableFrom耗时：" + (System.currentTimeMillis() - current));

            current = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                if (value instanceof Obj) {
                }
            }
            System.out.println("第" + times + "次instanceof耗时：" + (System.currentTimeMillis() - current));
        }
    }

    public static void valueOf(String url) {
        if (url != null && (url = url.trim()).length() != 0) {
            String protocol = null;
            String username = null;
            String password = null;
            String host = null;
            int port = 0;
            String path = null;
            Map<String, String> parameters = null;
            int i = url.indexOf("?");
            if (i >= 0) {
                String[] parts = url.substring(i + 1).split("\\&");
                parameters = new HashMap();
                String[] var10 = parts;
                int var11 = parts.length;

                for(int var12 = 0; var12 < var11; ++var12) {
                    String part = var10[var12];
                    part = part.trim();
                    if (part.length() > 0) {
                        int j = part.indexOf(61);
                        if (j >= 0) {
                            parameters.put(part.substring(0, j), part.substring(j + 1));
                        } else {
                            parameters.put(part, part);
                        }
                    }
                }

                url = url.substring(0, i);
            }

            i = url.indexOf("://");
            if (i >= 0) {
                if (i == 0) {
                    throw new IllegalStateException("url missing protocol: \"" + url + "\"");
                }

                protocol = url.substring(0, i);
                url = url.substring(i + 3);
            } else {
                i = url.indexOf(":/");
                if (i >= 0) {
                    if (i == 0) {
                        throw new IllegalStateException("url missing protocol: \"" + url + "\"");
                    }

                    protocol = url.substring(0, i);
                    url = url.substring(i + 1);
                }
            }

            i = url.indexOf("/");
            if (i >= 0) {
                path = url.substring(i + 1);
                url = url.substring(0, i);
            }

            i = url.indexOf("@");
            if (i >= 0) {
                username = url.substring(0, i);
                int j = username.indexOf(":");
                if (j >= 0) {
                    password = username.substring(j + 1);
                    username = username.substring(0, j);
                }

                url = url.substring(i + 1);
            }

            if (!url.contains(",")) {
                i = url.indexOf(":");
                if (i >= 0 && i < url.length() - 1) {
                    port = Integer.parseInt(url.substring(i + 1));
                    url = url.substring(0, i);
                }
            }

            if (url.length() > 0) {
                host = url;
            }
        } else {
            throw new IllegalArgumentException("url == null");
        }
    }
}
