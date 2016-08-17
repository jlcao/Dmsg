package com.dmsg;

import com.dmsg.auth.Authentication;
import com.dmsg.auth.BroadcastReqHandler;
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
        dmsgServerContext.addLastFilter(new Authentication());
        dmsgServerContext.addLastFilter(new BroadcastReqHandler());
        dmsgServerContext.builderNetSocketServer(8080);
        dmsgServerContext.start();
    }
}
