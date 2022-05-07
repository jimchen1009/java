package com.jim.demo.nio2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {

    private static final Logger logger = LoggerFactory.getLogger(EchoServer.class);

    public void serve(int port) throws IOException {
        final ServerSocket socket = new ServerSocket(port);
        try {
            while (true) {
                final Socket client = socket.accept();
                System.out.println("accepted connection from " + client);
                new Thread(() -> {
                    try {
                        InputStream in = client.getInputStream();
                        OutputStream out = client.getOutputStream();
                        while(true) {
                            byte[] array = new byte[10];
                            int read = in.read(array);
                            logger.debug("client[{}] read num: {}", client, read);
                            if (read > 0){
                                out.write(array, 0, read);
                            }
                        }
                    } catch (IOException e) {
                        logger.error("", e);
                        try {
                            client.close();
                        } catch (IOException ex) {
                            // ignore on close
                            logger.error("", ex);
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        new EchoServer().serve(8001);
    }
}
