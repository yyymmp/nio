package cn.itcast.nio;

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

/**
 * @author clearlove
 * @ClassName WriteServer.java
 * @Description
 * @createTime 2021年12月05日 22:07:00
 */
public class WriteServer {

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        //对于ServerSocketChannel 只需要关注accept事件
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));

        while (true) {
            selector.select();

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    //ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = ssc.accept();
                    socketChannel.configureBlocking(false);
                    SelectionKey scKey = socketChannel.register(selector, 0, null);
                    scKey.interestOps(SelectionKey.OP_READ);

                    //像客户端写入大量数据
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 30000000; i++) {
                        sb.append("a");
                    }
                    ByteBuffer buffer = Charset.defaultCharset().encode(sb.toString());
                    int write = socketChannel.write(buffer);
                    System.out.println("实际写入字节数" + write);
                    //返回实际写入字节
                    if (buffer.hasRemaining()) {
                        //关注可写事件
                        scKey.interestOps(scKey.interestOps() + SelectionKey.OP_WRITE);
                        //未写完的数据挂到scKey上
                        scKey.attach(buffer);
                    }

                }
                //发送缓冲区空出来 可以继续写入
                else if (key.isWritable()) {
                    ByteBuffer attachment = (ByteBuffer) key.attachment();
                    SocketChannel channel = (SocketChannel) key.channel();

                    //如果这轮写不完 下一次将会继续触发写事件
                    int write = channel.write(attachment);
                    System.out.println("写事件实际写入字节数" + write);

                    if (!attachment.hasRemaining()) {
                        //当附件数据已经写完了 那么就删除该附件
                        key.attach(null);
                        //取消这个事件
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);
                    }

                }
            }


        }
    }
}
