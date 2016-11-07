package com.dmsg.safeguard;

import com.dmsg.data.HostAskMessage;
import com.dmsg.data.HostDetail;
import com.dmsg.data.OfflineMessage;
import com.dmsg.message.MessageSender;
import com.dmsg.message.vo.MessageBase;
import com.dmsg.server.DmsgServerContext;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by cjl on 2016/8/30.
 */
public class MessageAskMechanism extends Thread {
    private final int MAXRET;
    private MessageSender sender;

    public MessageAskMechanism(DmsgServerContext serverContext) {
        this.MAXRET = serverContext.getConfig().getMaxRet();
        sender = serverContext.getSender();
    }

    LinkedBlockingQueue<HostAskMessage> messages = new LinkedBlockingQueue<HostAskMessage>(1024000);
    List<Long> removeList = new LinkedList<Long>();

    public void put(MessageBase messageBase,HostDetail hostDetail) throws InterruptedException {
        HostAskMessage message = new HostAskMessage();
        message.setMessage(messageBase);
        message.setHost(hostDetail);
        this.put(message);
    }

    public void put(HostAskMessage message) throws InterruptedException {
        message.retry();
        messages.put(message);
    }

    @Override
    public void run() {
        while (true) {
            try {
                HostAskMessage offlineMessage = messages.take();
                if (!isRemove(offlineMessage)) {
                    if (isOver(offlineMessage)) {

                    } else {
                        sender.send(offlineMessage);
                        this.put(offlineMessage);
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }


    }


    private boolean isOver(HostAskMessage offlineMessage) {
        return offlineMessage.getRetrySize() >= MAXRET ? true : false;
    }

    private boolean isRemove(HostAskMessage offlineMessage) {
        if (removeList.contains(offlineMessage.getMsgId())) {
            removeList.remove(offlineMessage.getMsgId());
            return true;
        }
        return false;
    }

    public void remvoe(HostAskMessage message) {
        removeList.add(message.getMsgId());
    }

}
