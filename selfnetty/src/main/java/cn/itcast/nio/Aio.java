package cn.itcast.nio;

import static cn.itcast.utils.ByteBufferUtil.debugAll;

import com.sun.xml.internal.bind.api.impl.NameConverter.Standard;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import lombok.extern.slf4j.Slf4j;
import sun.nio.ByteBuffered;

/**
 * @author jlz
 * @date 2021年12月22日 23:55
 */
@Slf4j
class Aio {

    public static void main(String[] args) throws IOException {
        try (AsynchronousFileChannel asynchronousFileChannel = AsynchronousFileChannel.open(Paths.get("data.txt"), StandardOpenOption.READ)) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(16);
            log.info("read begin");
            //这里的异步事件会另起线程执行.最终的日志执行顺序为
            //00:01:21 [INFO ] [main] cn.itcast.nio.Aio - read begin
            //00:01:21 [INFO ] [main] cn.itcast.nio.Aio - read end
            //00:01:21 [DEBUG] [Thread-8] cn.itcast.nio.Aio - read completed,字节数14
            asynchronousFileChannel.read(byteBuffer, 0, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                //正确读取后执行
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    log.debug("read completed,字节数{}",result);
                    attachment.flip();
                    debugAll(attachment);
                }
                //正确错误后执行
                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    exc.printStackTrace();
                }
            });
            log.info("read end");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        System.in.read();
    }
}
