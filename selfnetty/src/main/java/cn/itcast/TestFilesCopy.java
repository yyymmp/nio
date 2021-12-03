package cn.itcast;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author jlz
 * @className: TestFilesCopy
 * @date 2021/12/3 15:35
 * @description todo
 **/
public class TestFilesCopy {
    public static void main(String[] args) throws IOException {
        String source = "img";
        String to = "img_bak";

        Files.walk(Paths.get(source)).forEach(path -> {
            //复制文件只需要将前段路径改变
            String target = path.toString().replace(source, to);
            try {
                if (Files.isDirectory(path)){
                    //是目录
                    Files.createDirectories(Paths.get(target));
                }else if (Files.isRegularFile(path)){
                    //是文件
                    Files.copy(path,Paths.get(target));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }
}
