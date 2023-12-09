package com.sup.nio.c1;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * ByteBuf的基本使用
 * @author jlz
 * @date 2023年11月19日 22:53
 */
@Slf4j
public class T_ByteBuf {

    public static void main(String[] args) {
        //获取fileChannel 通过输入输出流间接获取
        try (FileChannel channel = new FileInputStream("D:\\down_package\\maven_work\\nio\\data.txt").getChannel()) {
            while (true){
                //构造一个缓冲区 且只有十个字节 初始状态是写入模式
                ByteBuffer byteBuffer = ByteBuffer.allocate(10);
                //读取fileChannel, 往byteBuffer中写
                int len = channel.read(byteBuffer);
                log.info("读到字节长度:{}",len);
                if (len == -1){
                    break;
                }
                //切换到读模式
                byteBuffer.flip();
                while (byteBuffer.hasRemaining()) {
                    byte b = byteBuffer.get();
                    log.info("读到字节:{}", (char) b);
                }

                //重新切换为写模式
                byteBuffer.clear();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
