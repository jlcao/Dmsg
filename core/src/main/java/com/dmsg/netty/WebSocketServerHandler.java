package com.dmsg.netty;


import com.dmsg.message.MessageContext;
import com.dmsg.message.MessageExecutor;
import com.dmsg.message.MessageHandler;
import com.dmsg.message.vo.ControllerMessage;
import com.dmsg.message.vo.FileMessage;
import com.dmsg.message.vo.MessageType;
import com.dmsg.message.vo.TextMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cjl on 2016/6/17.
 */
@Deprecated
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger log = LoggerFactory.getLogger(WebSocketServerHandler.class);
    private WebSocketServerHandshaker handshaker;
    private MessageExecutor executor;

    public WebSocketServerHandler() {
        executor = MessageExecutor.getInstance();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        //传统的HTTP接入
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        //判断是否是关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            ControllerMessage controllerMessage = new ControllerMessage(MessageType.CONTROLLER_CLOSE.getCode());
            executor.execute(new MessageHandler(new MessageContext(ctx, controllerMessage)));
            return;
        }
        //判断是否是Ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        if (frame instanceof ContinuationWebSocketFrame) {
            TextMessage message = new TextMessage(((ContinuationWebSocketFrame) frame).text());
            executor.execute(new MessageHandler(new MessageContext(ctx, message)));
            ctx.channel().writeAndFlush(new TextWebSocketFrame("收到消息").toString());
        }
        if (frame instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame binaryFrame = (BinaryWebSocketFrame) frame;
            FileMessage fileMessage = new FileMessage();
            fileMessage.setByteBuf(binaryFrame.content());
            executor.execute(new MessageHandler(new MessageContext(ctx, fileMessage)));
        }
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame text = ((TextWebSocketFrame) frame);
            TextMessage message = new TextMessage(text.text());
            executor.execute(new MessageHandler(new MessageContext(ctx, message)));
            ctx.channel().writeAndFlush(new TextWebSocketFrame("收到消息").toString());
        }


    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        //如果HTTP解码失败，返回HTTP异常
        if (!req.decoderResult().isSuccess() || (!"websocket".equals(req.headers().get("Upgrade")))) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "ws://localhost:8080/websocket", null, false
        );
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse res) {
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            setContentLength(res, res.content().readableBytes());
        }
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void setContentLength(DefaultFullHttpResponse res, int i) {
        res.headers().addObject("Content-Length", i);
    }

    private boolean isKeepAlive(FullHttpRequest req) {
        String connection = (String) req.headers().get("Connection");
        if (connection != null && "close".equalsIgnoreCase(connection)) {
            return false;
        }

        if (req.protocolVersion().isKeepAliveDefault()) {
            return !"close".equalsIgnoreCase(connection);
        } else {
            return "keep-alive".equalsIgnoreCase(connection);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


}
