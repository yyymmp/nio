package cn.itcast.nio;

import static cn.itcast.utils.ByteBufferUtil.debugRead;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * @author clearlove
 * @ClassName SelectServer.java
 * @Description
 * @createTime 2021年12月04日 21:26:00
 */
@Slf4j
public class SelectServer {

    public static void main(String[] args) throws IOException {

        //创建selector对象 管理多个channel
        Selector selector = Selector.open();

        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        //创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        //设为非阻塞模式  它影响的是accept方法
        ssc.configureBlocking(false);

        //channel与selector建立联系
        //selectionKey:事件发生后 通过它可以知道哪个channel发生的事件
        //事件类型:
        //accept: 服务端socket事件
        //connect:客户端事件 客户端连接请求
        //read:可读事件
        //write:可写事件
        //sscKe管的是ssc
        SelectionKey sscKey = ssc.register(selector, 0, null);
        log.info("{}", sscKey);
        //sscKey只需要关注accept事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT);

        //绑定端口
        ssc.bind(new InetSocketAddress(8080));
        //accept 建立与客户端连接
        while (true) {
            //调用select方法  没有事件发生 则select()阻塞 任四种事件发生即可
            //解决nio中无事件循环问题
            selector.select();

            //处理事件
            //拿到所有可用事件的key
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                log.info("{}", key);
                //区分事件类型
                //该事件是不是连接事件
                if (key.isAcceptable()) {
                    //因为现在只有连接事件 所以这个channel就是ServerSocketChannel
                    //这个key就是sscKey 并且多次连接始终只有一个
                    ServerSocketChannel channel = ((ServerSocketChannel) key.channel());
                    //调用accept与客户端建立连接
                    SocketChannel socketChannel = channel.accept();
                    socketChannel.configureBlocking(false);
                    SelectionKey selectionKey = socketChannel.register(selector, 0, null);
                    selectionKey.interestOps(SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    //拿到触发事件的channel
                    //发生读事件必然强转为socketChannel
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(4);
                        int read = channel.read(buffer);
                        if (read == -1) {
                            //客户端正常断开
                            key.cancel();
                        } else {
                            buffer.flip();
                            //debugRead(buffer);
                            System.out.println(Charset.defaultCharset().decode(buffer).toString());
                        }
                    } catch (IOException ioException) {
                        //当客户端强制关闭时 会引发一个read操作 异常断开
                        ioException.printStackTrace();
                        key.cancel();
                    }

                }

                //不处理连接的话可以使用cancel 否则select不会阻塞
                //key.cancel();
            }


        }
    }
}
