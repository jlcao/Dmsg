package com.dmsg.cache;

import com.dmsg.server.DmsgServerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jlcao on 2016/8/21.
 */
public class ClientJob extends Thread {
    DmsgServerContext dmsgServerContext;
    HostCache hostCache;
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private static ClientJob clientJob;

    public static ClientJob getInstance(DmsgServerContext context, HostCache hostCache) {
        if (clientJob == null) {
            clientJob = new ClientJob(context, hostCache);
            clientJob.start();
        }
        return clientJob;
    }


    private ClientJob(DmsgServerContext context, HostCache hostCache) {
        this.dmsgServerContext = context;
        this.hostCache = hostCache;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                hostCache.refresh();
                logger.info("远程服务器缓存刷新成功！");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
