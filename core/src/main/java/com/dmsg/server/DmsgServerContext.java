package com.dmsg.server;

import com.alibaba.fastjson.JSON;
import com.dmsg.cache.CacheManager;
import com.dmsg.cache.HostCache;
import com.dmsg.cache.RedisPoolBuilder;
import com.dmsg.cache.UserCache;
import com.dmsg.channel.LocalUserChannelManager;
import com.dmsg.channel.RemotHostChannelManager;
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
    private RemotHostChannelManager remotChannelHosts;
    private LocalUserChannelManager userChannelManager;

    private DmsgServerContext() {
        initConfig();
        executor = MessageExecutor.getInstance();
        redisPoolBuilder = new RedisPoolBuilder(config.getCacheHost(), config.getCachePort());
        cache = new CacheManager(redisPoolBuilder);
        remotChannelHosts = RemotHostChannelManager.getInstance();
        userChannelManager = LocalUserChannelManager.getInstance();
        filters = new ArrayList<Filter>();
        hostCache = new HostCache(this);
        userCache = new UserCache(this);
    }

    private void initConfig() {
        config = new DmsgServerConfig();
    }

    private void addLastFilter(Filter filter) {
        filters.add(filter);
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void builderNetSocketServer() throws ServerConfigException {
        netSocketServer = new NetSocketServer(bossGroup, workerGroup, InitializerFactory.create(config.getProtocol()), config.getPort());

    }

    public static DmsgServerContext getServerContext() {
        if (serverContext == null) {
            serverContext = new DmsgServerContext();
        }
        return serverContext;
    }

    public static void setServerContext(DmsgServerContext serverContext) {
        DmsgServerContext.serverContext = serverContext;
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
        netSocketServer.run();
        RouteFilter routeFilter = new RouteFilter();
        routeFilter.appendAttentionType(MessageType.SAVE_TEXT);
        routeFilter.appendAttentionType(MessageType.SAVE_TEXT);
        this.addLastFilter(routeFilter);
        try {
            saveNode();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    public MessageExecutor getExecutor() {
        return executor;
    }

    private void saveNode() throws UnknownHostException {
        InetAddress addr = InetAddress.getLocalHost();
        String agentIp = config.getAgentIp();
        String agentPort = config.getAgentPort();
        String ip=addr.getHostAddress().toString();//获得本机IP
        String port = config.getPort()+"";

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
        cache.getResource().hset(config.getServerNodeFlag(), host, JSON.toJSONString(hostDetail));
    }

    public HostDetail getHostDetail() {
        return hostDetail;
    }



    public CacheManager getCache() {
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
            userCache = new UserCache(this);
        }
        return userCache;
    }



    public HostCache getHostCache() {
        if (hostCache == null) {
            hostCache = new HostCache(this);
        }
        return hostCache;
    }

    public MessageSender getSender() {
        if (sender == null) {
            sender = new MessageSender(this);
        }
        return sender;
    }

    public RemotHostChannelManager getRemotHostsChannelManager() {
        return remotChannelHosts;
    }

    public LocalUserChannelManager getUserChannelManager() {
        return userChannelManager;
    }
}
