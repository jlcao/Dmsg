package com.dmsg.server;

import com.alibaba.fastjson.JSON;
import com.dmsg.cache.CacheManager;
import com.dmsg.cache.HostCache;
import com.dmsg.cache.RedisPoolBuilder;
import com.dmsg.cache.UserCache;
import com.dmsg.channel.LocalUserChannelManager;
import com.dmsg.channel.RemoteHostChannelManager;
import com.dmsg.data.HostDetail;
import com.dmsg.exception.ServerConfigException;
import com.dmsg.filter.Filter;
import com.dmsg.message.MessageExecutor;
import com.dmsg.message.MessageSender;
import com.dmsg.message.vo.MessageType;
import com.dmsg.netty.NetSocketServer;
import com.dmsg.netty.initializer.InitializerFactory;
import com.dmsg.route.BufferRouteHandler;
import com.dmsg.route.RouteFilter;
import com.dmsg.route.RouteHandler;
import com.dmsg.utils.NullUtils;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cjl on 2016/7/15.
 */
public class DmsgServerContext {
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private MessageExecutor executor;
    private RedisPoolBuilder redisPoolBuilder;
    private HostDetail hostDetail;
    private CacheManager cache;
    private List<Filter> filters;
    private HostCache hostCache;
    private UserCache userCache;
    private static DmsgServerContext serverContext;
    private NetSocketServer netSocketServer;
    private DmsgServerConfig config;
    private RouteHandler routeHander;
    private MessageSender sender;
    private RemoteHostChannelManager remotChannelHosts;
    private LocalUserChannelManager userChannelManager;

    private Logger logger = LoggerFactory.getLogger(DmsgServerContext.class);

    private DmsgServerContext() {
        initConfig();
        executor = MessageExecutor.getInstance();
        redisPoolBuilder = new RedisPoolBuilder(config.getCacheHost(), config.getCachePort());


        filters = new ArrayList<Filter>();
    }

    private void initConfig() {
        config = new DmsgServerConfig();
    }

    public void addLastFilter(Filter filter) {
        filters.add(filter);
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void builderNetSocketServer() throws ServerConfigException {

        builderNetSocketServer(config.getPort());
    }
    public void builderNetSocketServer(int port) throws ServerConfigException {
        try {
            logger.info("saveNode");
            saveNode(port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        netSocketServer = new NetSocketServer(bossGroup, workerGroup, InitializerFactory.create(config.getProtocol()), port);
    }

    public static DmsgServerContext getServerContext() {
        if (serverContext == null) {
            serverContext = new DmsgServerContext();
        }
        return serverContext;
    }


    public DmsgServerConfig getConfig() {
        return config;
    }

    public void start() {
        if (netSocketServer == null) {
            try {
                builderNetSocketServer();
            } catch (ServerConfigException e) {
                e.printStackTrace();
            }

        }

        RouteFilter routeFilter = new RouteFilter();
        routeFilter.appendAttentionType(MessageType.SAVE_TEXT);
        routeFilter.appendAttentionType(MessageType.SEND_TEXT);
        this.addLastFilter(routeFilter);
        netSocketServer.run();

    }

    public MessageExecutor getExecutor() {
        return executor;
    }

    private void saveNode(int portInt) throws UnknownHostException {

        InetAddress addr = InetAddress.getLocalHost();
        String agentIp = config.getAgentIp();
        String agentPort = config.getAgentPort();
        String ip=addr.getHostAddress().toString();//获得本机IP
        String port = portInt+"";

        if (!NullUtils.isEmpty(agentIp)) {
            ip = agentIp;
        }
        if (!NullUtils.isEmpty(agentPort)) {
            port = agentPort;
        }

        String host = (ip + ":" + port);
        hostDetail = new HostDetail();
        hostDetail.setHostId(host.hashCode());
        hostDetail.setIp(ip);
        hostDetail.setPort(Integer.parseInt(port));
        hostDetail.setUserSize(0);
        hostDetail.setMsgSize(0);
        hostDetail.setLastTime(System.currentTimeMillis());
        logger.info(hostDetail.toString());
        Jedis jedis = getCache().getResource();
        jedis.hdel(config.getServerNodeFlag(), host);
        jedis.hset(config.getServerNodeFlag(), host, JSON.toJSONString(hostDetail));
    }

    public HostDetail getHostDetail() {
        return hostDetail;
    }



    public CacheManager getCache() {
        if (cache == null) {
            cache = new CacheManager(redisPoolBuilder);
        }
        return cache;
    }

    public RouteHandler getRouteHandler() {
        if (routeHander == null) {
            routeHander = new BufferRouteHandler(this);
        }

        return routeHander;
    }

    public UserCache getUserCache() {
        if (userCache == null) {
            userCache = UserCache.getInstance(this);
        }
        return userCache;
    }



    public HostCache getHostCache() {
        if (hostCache == null) {
            hostCache = HostCache.getInstance(this);
        }
        return hostCache;
    }

    public MessageSender getSender() {
        if (sender == null) {
            sender = new MessageSender(this);
        }
        return sender;
    }

    public RemoteHostChannelManager getRemotHostsChannelManager() {
        if (remotChannelHosts == null) {
            remotChannelHosts = RemoteHostChannelManager.getInstance(this);
        }

        return remotChannelHosts;
    }

    public LocalUserChannelManager getUserChannelManager() {
        if (userChannelManager == null) {
            userChannelManager = LocalUserChannelManager.getInstance();
        }
        return userChannelManager;
    }
}
