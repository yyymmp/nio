package cn.itcast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @author jlz
 * @className: TestFileChannelTransfer
 * @date 2021/12/3 10:50
 * @description todo
 **/
public class TestFileChannelTransfer {
    public static void main(String[] args) {
        try (
                FileChannel from = new FileInputStream("data.txt").getChannel();
                FileChannel to = new FileOutputStream("datato.txt").getChannel();
        ) {
            //效率高 -> transfer底层零拷贝优化 坑: 传输大小有上限 2G 超过2G的部分传输不了
            //from.transferTo(0,from.size(),to);
            //大文件上传
            for (long left = from.size(); left > 0; ) {
                //返回传输字节数
                long l = from.transferTo(from.size() - left, left, to);
                left -= l;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
