package cn.itcast.netty.bytebuf;

import static cn.itcast.netty.bytebuf.NettyByteBuf.log;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

/**
 * @author jlz
 * @date 2022年01月09日 23:01
 */
class SliceAndComposite {

    public static void main(String[] args) {
        //切分byteBuff而不发生数据复制------slice
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer(16);

        byteBuf.writeBytes(new byte[] {
            'a','b','c','d','e','f','g','h','j','k',
        });
        System.out.println("原数据");
        log(byteBuf);
        ByteBuf s1 = byteBuf.slice(0, 8);
        ByteBuf s2 = byteBuf.slice(8, 8);
        log(s1);
        log(s2);
        s1.setByte(0,'z');
        System.out.println("修改后原数据");
        log(byteBuf);

        //合并bytebuf而不发生数据复制------composite
        CompositeByteBuf compositeByteBuf = ByteBufAllocator.DEFAULT.compositeBuffer();

        ByteBuf c1 = ByteBufAllocator.DEFAULT.buffer(4);
        c1.writeBytes(new byte[] {'a','a','a','a'});
        ByteBuf c2 = ByteBufAllocator.DEFAULT.buffer(4);
        c2.writeBytes(new byte[] {'b','b','b','b'});

        compositeByteBuf.addComponents(true,c1,c2);
        log(compositeByteBuf);

    }
}
