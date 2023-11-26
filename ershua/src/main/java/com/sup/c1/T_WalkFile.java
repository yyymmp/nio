package com.sup.c1;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

/**
 * @author jlz
 * @date 2023年11月26日 22:30
 */
public class T_WalkFile {

    public static void main(String[] args) throws IOException {
        //便利顺序,先进入目录 然后访问一个一个的文件 最后推出目录 所以递归删除思路如下:
        //进入目录时不能删 因为目录不为空 所以在访问文件时删除文件 最后退出目录时 删除目录
        String sou ="D:\\install\\Sublime Text 3";
        String tar ="D:\\install\\Sublime Text 3_bak";
        Files.walk(Paths.get(sou)).forEach(path -> {
            try {
                //目标文件只需要将前面的层级替换 后面的路径不变
                //是目录 则创建目录
                String replace = path.toString().replace(sou, tar);
                if (Files.isDirectory(path)){
                    Files.createDirectory(Paths.get(replace));
                }
                //是文件 则创建文件
                else if (Files.isRegularFile(path)){
                    Files.copy(path,Paths.get(replace));

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });


    }
}
