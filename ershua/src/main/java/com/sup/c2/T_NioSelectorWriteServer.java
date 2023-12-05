package com.sup.c2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2023年11月27日 22:35
 */
@Slf4j
public class T_NioSelectorWriteServer {

    //演示可写事件
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        Selector selector = Selector.open();
        //对于ServerSocketChannel 只需要关注accept事件
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8089));
        while (true){

            selector.select();
            //获取所有事件
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            log.error("事件发生,当前事件数量:{}",selectionKeys.size());
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()){
                    //服务器连接事件  获取连接
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    //总管该scchannel事件得key
                    SelectionKey scKey = sc.register(selector, 0, null);
                    //项客户端连接发送数据
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0;i < 30000000;i++){
                        sb.append("a");
                    }
                    ByteBuffer byteBuffer = Charset.defaultCharset().encode(sb.toString());
                    //这种情况下不一定能一次写完 返回实际写入字节 循环写入
                    //但在这种情况下不符合非阻塞  如果一个sc有大量数据在这里循环,且如果缓冲区满了还会导致写入失败 返回字节数为0
                    // 会导致其他sc的读写 所以改用关注可写事件
                    /*
                    while (byteBuffer.hasRemaining()){

                        log.error("实际写入字节数量:{}",write);
                    }
                     */
                    int write = sc.write(byteBuffer);
                    log.error("连接事件中写入字节:{}",write);
                    //写入一次后判断是否写完
                    if (byteBuffer.hasRemaining()){
                        log.error("连接事件中字节仍有剩余,关注可写事件");
                        //若写不完 则关注可写事件 当发送缓冲器又可写时,则会触发可写事件 此时上方 selector.select();
                        // 会检测到从而继续往下执行
                        // 那么可写事件触发后 可以继续在可写事件逻辑处理
                        scKey.interestOps(scKey.interestOps()+SelectionKey.OP_WRITE);

                        //要将未写完得数据挂到key上(附件方式) 以便在写事件触发时获取
                        scKey.attach(byteBuffer);
                    }
                }else if (key.isWritable()){
                    //发送缓冲区可写触发
                    ByteBuffer byteBuffer = (ByteBuffer)key.attachment();
                    SocketChannel sc = (SocketChannel)key.channel();
                    int write = sc.write(byteBuffer);
                    log.error("可写事件中写入字节:{}",write);
                    //写完清理附件
                    if (!byteBuffer.hasRemaining()){
                        log.error("数据写完 清除附件 清除可写事件");
                        key.attach(null);
                        //去掉可写事件
                        key.interestOps(key.interestOps()-SelectionKey.OP_WRITE);
                    }
                }
            }

        }

    }

}
