package com.dmsg;

import com.dmsg.auth.AuthReqFilter;
import com.dmsg.auth.AuthResFilter;
import com.dmsg.auth.BroadcastReqFilter;
import com.dmsg.exception.ServerConfigException;
import com.dmsg.server.DmsgServerContext;
import com.dmsg.utils.NullUtils;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.IOException;

/**
 * Created by jlcao on 2016/8/22.
 */
public class Dmsgstrap {
    DmsgServerContext dmsgServerContext;
    File file = new File("logs/pid.cat");
    private String configPath;

    public void start() throws ServerConfigException, IOException {

        if (!NullUtils.isEmpty(configPath)) {
            dmsgServerContext = DmsgServerContext.getServerContext(configPath);
        }else
            dmsgServerContext = DmsgServerContext.getServerContext();

        dmsgServerContext.addLastFilter(new AuthReqFilter());
        dmsgServerContext.addLastFilter(new AuthResFilter());
        dmsgServerContext.addLastFilter(new BroadcastReqFilter());
        dmsgServerContext.builderNetSocketServer();

        if (!file.exists()) {
            file.createNewFile();
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                synchronized (dmsgServerContext) {
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

        new Thread(){
            @Override
            public void run() {
                while (true) {
                    if (!file.exists()) {
                        System.exit(0);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }.start();
    }

    public static void main(String args[]) throws ServerConfigException, IOException {
        Dmsgstrap dmsgstrap = new Dmsgstrap();

        if (args != null && args.length > 0) {
            System.out.println("set properties ======================");
            PropertyConfigurator.configure("config/log4j.properties");
            dmsgstrap.setConfigPath("config/cfg.properties");
        }

        dmsgstrap.start();

    }


    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }
}
