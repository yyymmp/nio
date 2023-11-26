package com.sup.c1;

import static com.sup.c1.ByteBufferUtil.debugAll;

import java.nio.ByteBuffer;

/**
 * @author jlz
 * @date 2023年11月23日 18:54
 */
public class T_ByteBuf3 {

    public static void main(String[] args) {
        //大小不可动态调整
        ByteBuffer allocate = ByteBuffer.allocate(10);  //堆内存
        System.out.println(allocate.getClass());
        System.out.println(ByteBuffer.allocateDirect(10).getClass()); //直接内存 分配效率低 手动释放
        allocate.put(new byte[]{'a','b','c','d'});
        allocate.flip();
        //一次性读取四个字节
        allocate.get(new byte[4]);
        debugAll(allocate);
        //重设 重新开始读---rewind
        allocate.rewind();
        debugAll(allocate);

        //mark & reset
        //mark标记  reset:重置到mark的地方
        System.out.println(allocate.get()); //97
        System.out.println(allocate.get()); //98
        allocate.mark(); //标记
        System.out.println(allocate.get()); //99
        System.out.println(allocate.get()); //100
        allocate.reset(); //回到标记位置
        System.out.println(allocate.get()); //99
        System.out.println(allocate.get()); //100
        debugAll(allocate);
        //get(i)  不会改变读指针
        System.out.println(allocate.get(1));;

    }
}
