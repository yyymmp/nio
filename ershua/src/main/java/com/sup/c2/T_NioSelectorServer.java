package com.sup.c2;

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
                    SelectionKey scKey = sc.register(selector, 0, null);
                    //指明关注的事件 对于SocketChannel 要么关注读或写
                    scKey.interestOps(SelectionKey.OP_READ);
                }
                //可读事件
                else if (key.isReadable()){
                    //可读事件下 则此chanel是一个SocketChannel
                    SocketChannel channel = (SocketChannel)key.channel();
                    ByteBuffer buffer= ByteBuffer.allocateDirect(16);
                    channel.read(buffer);
                    buffer.flip();
                    debugRead(buffer);
                }




            }


        }

    }
}
