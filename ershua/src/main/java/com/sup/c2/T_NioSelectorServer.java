package com.sup.c2;

import static com.sup.c1.ByteBufferUtil.debugAll;
import static com.sup.c1.ByteBufferUtil.debugRead;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2023年11月27日 22:35
 */
@Slf4j
public class T_NioSelectorServer {

    //单线程模配合selector
    public static void main(String[] args) throws IOException {
        //1 创建selector 管理多个channel 此时有serverSocketChannel 还会有多个SocketChannel
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(8089));
        //2 建立selector与channel的联系(注册)
        //注册后 若发生事件 则通过selectionKey可得到事件与事件发生channel  将ssckey加入到selector下的集合中
        SelectionKey ssckey = ssc.register(selector, 0, null);
        log.error("register key:{}",ssckey);
        //指定这个ssckey只关注客户端连接事件  因为这个key是serverSocketChannel注册 只需要关注客户端连接
        ssckey.interestOps(SelectionKey.OP_ACCEPT);

        while (true){
            //3 select方法 该方法没有事件发生时 则阻塞 有事件发生时 则执行
            //select在事件未处理时 他不会阻塞 事件发生后 要么处理 要么取消
            //发生事件时 会有新的集合 就是selectedKeys,发生事件的key会被移入这个集合中,比如accept事件时,会将上方的ssckey加入到selectedKeys集合中
            //并且selectedKeys集合 每次发生事件只会加 不会移除(需要手动移除)
            selector.select();
            //4 处理事件  selectionKeys包含所有发生的事件
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            log.error("事件数量:{}",selectionKeys.size());
            while (iterator.hasNext()){
                //这个key  其实就是上面ssc注册时 返回的key 每次客户端连接事件时 都时同样一个key
                SelectionKey key = iterator.next();
                //事件处理后 一定要从selectionKeys中移除
                //因为 select 在事件发生后，就会将相关的 key 放入 selectedKeys 集合，但不会在处理完后从 selectedKeys 集合中移除，需要我们自己编码删除
                //如果处理不移除 下一次事件时还会继续处理accept事件 但这个accept已在本次处理中处理 会导致channel.accept();返回为空
                iterator.remove();
                log.error("事件发生 key:{}",key);
                //selectionKeys包含了所有的事件 所以这里需要区分以便走不同的逻辑
                //连接事件
                if (key.isAcceptable()){
                    //通过key 获取channel 这个就是  连接事件下 则此key.channel()就是上面的ServerSocketChannel
                    //因为ServerSocketChannel专门负责客户端的连接事件
                    ServerSocketChannel channel = (ServerSocketChannel)key.channel();
                    SocketChannel sc = channel.accept();
                    log.error("SocketChannel:{}",sc);
                    //配合select工作必须时非阻塞
                    sc.configureBlocking(false);
                    //sc也要注册到selector上  那么这个scKey就是负责这个SocketChannel的事件
                    ByteBuffer buffer= ByteBuffer.allocateDirect(16);
                    //附件 将bytebuf作为附件关联到SelectionKey
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    //指明关注的事件 对于SocketChannel 要么关注读或写
                    scKey.interestOps(SelectionKey.OP_READ);
                }
                //可读事件
                else if (key.isReadable()){
                    //可读事件下 则此chanel是一个SocketChannel
                    try {
                        SocketChannel channel = (SocketChannel)key.channel();
                        //若客户端发生数据超过该缓冲区 不会报错 会再次出发read事件 将剩余数据发送过来
                        //所以需要在两处读事件发生时 获取完整数据
                        //ByteBuffer buffer= ByteBuffer.allocateDirect(16);
                        //当key发生事件时,获取key上的附件
                        ByteBuffer buffer = (ByteBuffer)key.attachment();
                        int read = channel.read(buffer);
                        //客户端正常断开 也会出发一次read 与正常read不同的是 此时read返回-1
                        if (read == -1){
                            //客户端正常断开
                            key.cancel();
                        }else {
                            split(buffer);

                            if (buffer.limit() == buffer.position()){
                                //说明需要扩容
                                ByteBuffer newBuf = ByteBuffer.allocate(buffer.capacity() * 2);
                                //旧内容拷贝
                                buffer.flip();
                                newBuf.put(buffer);
                                //将newBuf作为新的附件替换掉之前的附件
                                key.attach(newBuf);
                            }
                            //buffer.flip();
                            //debugRead(buffer);
                        }

                    }catch (IOException e){
                        e.printStackTrace();
                        //客户端被强制异常断开 需要将其管理员key cancel
                        // 会取消注册在 selector 上的 channel，并从 keys 集合中删除 key 后续不会再监听事件
                        key.cancel();
                    }
                }




            }


        }

    }

    private static void split(ByteBuffer byteBuffer) {
        byteBuffer.flip();
        for (int i = 0; i < byteBuffer.limit(); i++) {
            if (byteBuffer.get(i) == '\n') {
                //每次消息结尾的位置
                int len = i + 1 - byteBuffer.position();
                //使用一个新的bytebuf来接受
                ByteBuffer tar = ByteBuffer.allocate(len);
                for (int j = 0; j < len; j++) {
                    tar.put(byteBuffer.get());
                }
                debugAll(tar);

            }

        }
        //将已读去除
        byteBuffer.compact();  //如果position和limit还是相同 则没有解析到一条完整消息


    }
}
