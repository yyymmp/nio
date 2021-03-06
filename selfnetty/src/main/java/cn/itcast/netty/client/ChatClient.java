package cn.itcast.netty.client;

import cn.itcast.netty.protocol.myprotocl.MessageCodecShare;
import cn.itcast.netty.protocol.myprotocl.ProcotolFrameDecoder;
import cn.itcast.netty.protocol.myprotocl.message.ChatRequestMessage;
import cn.itcast.netty.protocol.myprotocl.message.GroupChatRequestMessage;
import cn.itcast.netty.protocol.myprotocl.message.GroupCreateRequestMessage;
import cn.itcast.netty.protocol.myprotocl.message.GroupJoinRequestMessage;
import cn.itcast.netty.protocol.myprotocl.message.GroupMembersRequestMessage;
import cn.itcast.netty.protocol.myprotocl.message.GroupQuitRequestMessage;
import cn.itcast.netty.protocol.myprotocl.message.LoginRequestMessage;
import cn.itcast.netty.protocol.myprotocl.message.LoginResponseMessage;
import cn.itcast.netty.protocol.myprotocl.message.PingMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatClient {

    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        CountDownLatch countDownLatch  = new CountDownLatch(1);
        try {
            Bootstrap bootstrap = new Bootstrap();
            LoggingHandler loggingHandler = new LoggingHandler();
            MessageCodecShare messageCodec = new MessageCodecShare();
            AtomicBoolean atomicBoolean = new AtomicBoolean();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    //???????????????????????????
                    //??????
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    //??????+??????
                    //ch.pipeline().addLast(loggingHandler);
                    //??????+??????
                    ch.pipeline().addLast(messageCodec);

                    //????????????????????????????????????  ?????????????????????????????????0
                    ch.pipeline().addLast(new IdleStateHandler(0,3,0));

                    ch.pipeline().addLast(new ChannelDuplexHandler(){
                        /**
                         * ???????????????????????????
                         * ????????????????????????
                         * @param ctx
                         * @param evt
                         * @throws Exception
                         */
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent evt1 = (IdleStateEvent) evt;
                            //???????????????
                            if (evt1.state() == IdleState.WRITER_IDLE) {
                                log.info("3s ???????????? ???????????????");
                                ctx.writeAndFlush(new PingMessage());
                            }
                        }
                    });


                    //??????????????????handler
                    //?????????????????????????????????????????? ??????????????????handle
                    ch.pipeline().addLast("client handle",new ChannelInboundHandlerAdapter(){
                        //?????????????????????
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            //????????????????????????????????????
                            new Thread(()->{
                                //????????????????????????????????? ??????????????????????????????
                                Scanner scanner = new Scanner(System.in);
                                System.out.println("??????????????????");
                                String username = scanner.nextLine();
                                if(atomicBoolean.get()){
                                    return;
                                }
                                System.out.println("???????????????");
                                String pwd = scanner.nextLine();
                                if(atomicBoolean.get()){
                                    return;
                                }
                                LoginRequestMessage loginRequestMessage = new LoginRequestMessage(username,pwd);
                                //????????????????????????????????? ,?????????????????????????????? ,???????????????,???????????????????????????
                                //????????????messageCodec  loggingHandler head(??????)
                                ctx.writeAndFlush(loginRequestMessage);

                                System.out.println("?????????????????????");

                                try {
                                    //????????????????????????????????????????????????
                                    countDownLatch.await();

                                    if (!atomicBoolean.get()){
                                        System.out.println("????????????");
                                        //???????????? ??????????????????
                                        ctx.channel().close();
                                        return;
                                    }

                                    //????????????
                                    while (true) {
                                        System.out.println("==================================");
                                        System.out.println("send [username] [content]");
                                        System.out.println("gsend [group name] [content]");
                                        System.out.println("gcreate [group name] [m1,m2,m3...]");
                                        System.out.println("gmembers [group name]");
                                        System.out.println("gjoin [group name]");
                                        System.out.println("gquit [group name]");
                                        System.out.println("quit");
                                        System.out.println("==================================");

                                        String command = scanner.nextLine();
                                        //????????????
                                        String[] s = command.split(" ");
                                        switch (s[0]){
                                            //????????????
                                            case "send":
                                                ChatRequestMessage chatRequestMessage = new ChatRequestMessage(username,s[1],s[2]);
                                                ctx.writeAndFlush(chatRequestMessage);
                                                break;
                                            case "gsend":
                                                GroupChatRequestMessage groupChatRequestMessage = new GroupChatRequestMessage(username,s[1],s[2]);
                                                ctx.writeAndFlush(groupChatRequestMessage);
                                                break;
                                            case "gcreate":
                                                List<String> list = Arrays.asList(s[2].split(","));
                                                Set<String> set =  new HashSet<>(list);
                                                //add self
                                                set.add(username);
                                                GroupCreateRequestMessage groupCreateRequestMessage = new GroupCreateRequestMessage(s[1],set);
                                                ctx.writeAndFlush(groupCreateRequestMessage);
                                                break;
                                            case "gmembers":
                                                GroupMembersRequestMessage groupMembersRequestMessage = new GroupMembersRequestMessage(s[1]);
                                                ctx.writeAndFlush(groupMembersRequestMessage);
                                                break;
                                            case "gjoin":
                                                GroupJoinRequestMessage groupJoinRequestMessage = new GroupJoinRequestMessage(username,s[1]);
                                                ctx.writeAndFlush(groupJoinRequestMessage);
                                                break;
                                            case "gquit":
                                                GroupQuitRequestMessage groupQuitRequestMessage = new GroupQuitRequestMessage(username,s[1]);
                                                ctx.writeAndFlush(groupQuitRequestMessage);
                                                break;
                                            case "quit":
                                                ctx.channel().close();
                                                return;
                                        }
                                        if (command.startsWith("send")){

                                        }
                                    }

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            },"system in").start();
                        }

                        /**
                         * channelRead?????????????????????
                         * @param ctx
                         * @param msg
                         * @throws Exception
                         */
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            //????????????????????????????????????????????????????????? ???????????? ??????????????????countDownLatch
                            if(msg instanceof LoginResponseMessage){
                                LoginResponseMessage loginResponseMessage = (LoginResponseMessage) msg;
                                if (loginResponseMessage.isSuccess()){
                                    atomicBoolean.set(true);
                                }else {
                                    atomicBoolean.set(false);
                                }
                                countDownLatch.countDown();
                            }
                        }

                        //????????????
                        @Override
                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                            log.debug("???????????????????????????????????????..");
                            atomicBoolean.set(true);
                        }

                        //????????????
                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            log.debug("???????????????????????????????????????..");
                            atomicBoolean.set(true);
                        }
                    });
                }
            });
            Channel channel = bootstrap.connect("localhost", 9001).sync().channel();
            log.info("??????????????????...");
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
