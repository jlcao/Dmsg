package com.dmsg.message;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by cjl on 2016/7/11.
 */
public class MessageExecutor implements Executor {
    private static ExecutorService pool = Executors.newCachedThreadPool();

    private static final MessageExecutor EXECUTOR = new MessageExecutor();

    private MessageExecutor() {

    }

    public static MessageExecutor getInstance() {
        return EXECUTOR;
    }

    public void execute(Runnable command) {
        pool.execute(command);
    }
}
