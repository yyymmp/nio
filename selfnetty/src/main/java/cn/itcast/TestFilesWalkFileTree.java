package cn.itcast;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jlz
 * @className: TestFilesWalkFileTree
 * @date 2021/12/3 11:38
 * @description todo
 **/
public class TestFilesWalkFileTree {
    //1.7前遍历文件树 只能递归  Files 7之后出现的工具类

    public static void main(String[] args) throws IOException {
        AtomicInteger fileCount = new AtomicInteger();
        //统计jar
        Files.walkFileTree(Paths.get("F:\\env\\java8"), new SimpleFileVisitor<Path>() {
            /**
             * 遍历文件时
             * @param file
             * @param attrs
             * @return
             * @throws IOException
             */
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toFile().getName().endsWith(".jar")){
                    fileCount.incrementAndGet();
                }
                return super.visitFile(file, attrs);
            }
        });

        System.out.println("fileCount = " + fileCount);
    }

    private static void m1() throws IOException {
        AtomicInteger dirCount = new AtomicInteger();
        AtomicInteger fileCount = new AtomicInteger();

        Files.walkFileTree(Paths.get("F:\\env\\java8"), new SimpleFileVisitor<Path>() {
            //访问者模式

            /**
             * 遍历文件夹之前
             * @param dir
             * @param attrs
             * @return
             * @throws IOException
             */
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("=====>" + dir);
                dirCount.incrementAndGet();
                return super.preVisitDirectory(dir, attrs);
            }

            /**
             * 遍历文件时
             * @param file
             * @param attrs
             * @return
             * @throws IOException
             */
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println("=======>" + file.getFileName());
                fileCount.incrementAndGet();
                return super.visitFile(file, attrs);
            }
        });

        System.out.println("dirCount = " + dirCount);
        System.out.println("fileCount = " + fileCount);
    }
}
