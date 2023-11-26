package com.sup.c1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @author jlz
 * @date 2023年11月23日 21:03
 */
public class T_FileChannel {

    public static void main(String[] args) {
        try (
                FileChannel from = new FileInputStream("D:\\down_package\\maven_work\\nio\\data.txt").getChannel();
                FileChannel to = new FileOutputStream("D:\\down_package\\maven_work\\nio\\data2.txt").getChannel();

        ) {
            //将输入input 传输到输出to的channel 底层用了操作系统的零拷贝 性能高  传输最多只能拷贝2g,但可以多次传输
            from.transferTo(0, from.size(), to);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
