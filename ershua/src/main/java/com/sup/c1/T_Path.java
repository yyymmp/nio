package com.sup.c1;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jlz
 * @date 2023年11月26日 22:10
 */
public class T_Path {

    public static void main(String[] args) throws IOException {
        //是找到一个目录后,全部便利完成 再进行下一次目录的便利

        //该模式是一个访问者模式 具体的便利是Files.walkFileTree完成,具体需要干嘛可以自己加逻辑

        AtomicInteger dirCount = new AtomicInteger();
        AtomicInteger fileCount = new AtomicInteger();
        //1.7 遍历文件夹 不用使用递归便利
        Files.walkFileTree(Paths.get("D:\\language\\Java\\jdk1.8.0_251"),new SimpleFileVisitor<Path>(){
            //访问目录之前
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("dir ===>"+dir);
                dirCount.incrementAndGet();
                return super.preVisitDirectory(dir, attrs);
            };

            //访问文件
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println("file ===>"+file);
                fileCount.incrementAndGet();
                return super.visitFile(file, attrs);
            }
        });

    }
}
