package com.dmsg;

import com.dmsg.auth.AuthReqFilter;
import com.dmsg.auth.AuthResFilter;
import com.dmsg.auth.BroadcastReqFilter;
import com.dmsg.exception.ServerConfigException;
import com.dmsg.server.DmsgServerContext;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws ServerConfigException {
        DmsgServerContext dmsgServerContext = DmsgServerContext.getServerContext();
        dmsgServerContext.addLastFilter(new AuthReqFilter());
        dmsgServerContext.addLastFilter(new AuthResFilter());
        dmsgServerContext.addLastFilter(new BroadcastReqFilter());
        dmsgServerContext.builderNetSocketServer(8080);
        dmsgServerContext.start();
    }
}
