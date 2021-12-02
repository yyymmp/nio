package cn.itcast;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author jlz
 * @className: TestByteBuf
 * @date 2021/12/2 14:56
 * @description todo
 **/
@Slf4j
public class TestByteBuf {
    public static void main(String[] args) {
        //FileChannel channel = new FileChannel
        try (FileChannel channel = new FileInputStream("E:\\nio\\selfnetty\\src\\data.txt").getChannel()) {
            //10字节
            ByteBuffer byteBuf = ByteBuffer.allocate(10);

            while(true){
                int read = channel.read(byteBuf);
                log.info("长度:{}",read);
                if (read == -1){
                    break;
                }
                //打印byte_buf
                //1切换到读模式
                byteBuf .flip();
                //2 获取数据
                while (byteBuf.hasRemaining()){
                    byte b = byteBuf.get();
                    System.out.println((char)b);
                }
                //读完切换为写模式
                byteBuf.clear();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
