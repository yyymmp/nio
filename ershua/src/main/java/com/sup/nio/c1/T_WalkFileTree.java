package com.sup.nio.c1;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author jlz
 * @date 2023年11月26日 22:30
 */
public class T_WalkFileTree {

    public static void main(String[] args) throws IOException {
        //便利顺序,先进入目录 然后访问一个一个的文件 最后推出目录 所以递归删除思路如下:
        //进入目录时不能删 因为目录不为空 所以在访问文件时删除文件 最后退出目录时 删除目录

        Files.walkFileTree(Paths.get("D:\\maven\\apache-maven-3.3.9"),new SimpleFileVisitor<Path>(){
            //访问目录之前
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("进入 ===>"+dir);

                return super.preVisitDirectory(dir, attrs);
            };

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                System.out.println("退出 <==="+dir);
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file);
                Files.delete(file);
                return super.visitFile(file, attrs);
            }
        });
    }
}
