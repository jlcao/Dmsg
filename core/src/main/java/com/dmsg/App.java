package com.dmsg;

import com.dmsg.auth.AuthReqFilter;
import com.dmsg.auth.AuthResFilter;
import com.dmsg.auth.BroadcastReqFilter;
import com.dmsg.exception.ServerConfigException;
import com.dmsg.server.DmsgServerContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws ServerConfigException, IOException {
        final DmsgServerContext dmsgServerContext = DmsgServerContext.getServerContext();
        dmsgServerContext.addLastFilter(new AuthReqFilter());
        dmsgServerContext.addLastFilter(new AuthResFilter());
        dmsgServerContext.addLastFilter(new BroadcastReqFilter());
        dmsgServerContext.builderNetSocketServer(8080);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                synchronized (DmsgServerContext.class) {
                    try {

                        dmsgServerContext.close();
                        System.out.println("exit ok!");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        new Thread() {
            @Override
            public void run() {
                dmsgServerContext.start();
            }
        }.start();

        while (true) {
            System.out.println("输入exit退出程序：");
            String str = readString();
            if ("exit".equals(str)) {
                System.exit(-1);
            }
        }


    }

    private static String readString() throws IOException {
        InputStream in = System.in;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        return bufferedReader.readLine();
    }
}
