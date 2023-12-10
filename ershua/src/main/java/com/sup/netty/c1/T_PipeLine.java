package com.sup.netty.c1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.nio.charset.Charset;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2023年12月10日 13:40
 */
@Slf4j
public class T_PipeLine {

    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        //添加入站  一般入站方法都关注读事件 因为有数据进来
                        //处理器 head->h1->h2->h3->h4->h5->h6->tail
                        //入:head->h1->h2->h3 出:h4<-h5<-h6<-tail
                        //入战按照添加顺序 出战按照添加相反顺序
                        //出战处理器 只有向channel写入数据才会触发

                        pipeline.addLast("h1",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.error("1");
                                ByteBuf byteBuf = (ByteBuf) msg;
                                String string = byteBuf.toString(Charset.defaultCharset());
                                //将处理结果继续传递给下一个处理器
                                super.channelRead(ctx,string);
                            };
                        }).addLast("h2",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.error("2");
                                String s = (String) msg;
                                Stu stu = new Stu(s);
                                //进一步加工 并传递给下一个handle
                                super.channelRead(ctx,stu);

                            };
                        }).addLast("h3",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.error("3,{},{}",msg,msg.getClass());
                                //写入操作触发出站处理器
                                ch.write(ctx.alloc().buffer().writeBytes("hello".getBytes()));
                            };
                        });
                        //出战处理器 主要重写是write方法 对数据进行写出
                        pipeline.addLast("h4",new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.error("4");
                                super.write(ctx, msg, promise);
                            }
                        });
                        //出战处理器 主要重写是write方法 对数据进行写出
                        pipeline.addLast("h5",new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.error("5");
                                super.write(ctx, msg, promise);
                            }
                        });
                        //出战处理器 主要重写是write方法 对数据进行写出
                        pipeline.addLast("h6",new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.error("6");
                                super.write(ctx, msg, promise);
                            }
                        });

                    }
                })
                .bind(8089);
    }

    @Data
    @AllArgsConstructor
    static class Stu{
        private String name;
    }
}
