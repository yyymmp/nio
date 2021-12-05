package cn.itcast.nio;

import static cn.itcast.utils.ByteBufferUtil.debugAll;
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

    private static void split(ByteBuffer buffer) {
        for (int i = 0; i < buffer.limit(); i++) {
            //找到\n
            if (buffer.get(i) == '\n') {
                //读出该条完整消息
                int length = i + 1 - buffer.position();
                ByteBuffer newBuf = ByteBuffer.allocate(length);
                //从buffer读出 写入  newBuf
                for (int j = 0; j < length; j++) {
                    newBuf.put(buffer.get());
                }
                debugAll(newBuf);
            }
        }

        buffer.compact();
    }

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
                    //将buf作为附件关联到socketChannel上 
                    ByteBuffer buffer = ByteBuffer.allocate(16);
                    SelectionKey selectionKey = socketChannel.register(selector, 0, buffer);
                    selectionKey.interestOps(SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    //拿到触发事件的channel
                    //发生读事件必然强转为socketChannel
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        //如果该buf不够接受一次的消息长度 会触发第二次read事件再次进行read
                        //获取key的附件
                        ByteBuffer attachment = ((ByteBuffer) key.attachment());
                        int read = channel.read(attachment);
                        if (read == -1) {
                            //客户端正常断开
                            key.cancel();
                        } else {
                            attachment.flip();
                            //这里需要处理消息边界
                            split(attachment);

                            if (attachment.position() == attachment.limit()) {
                                //说明该条消息超过了buf容量 则扩容 新建buf
                                ByteBuffer newBuf = ByteBuffer.allocate(attachment.capacity() * 2);
                                attachment.flip();
                                newBuf.put(attachment);
                                //将扩容后的buf替换掉之前的buf
                                key.attach(newBuf);
                            }
                            //debugRead(buffer);
                            System.out.println(Charset.defaultCharset().decode(attachment).toString());
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
