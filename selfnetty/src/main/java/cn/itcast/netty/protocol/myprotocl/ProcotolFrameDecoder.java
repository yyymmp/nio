package cn.itcast.netty.protocol.myprotocl;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 解决粘包半包问题
 * @author jlz
 * @date 2022年01月23日 20:02
 */
public class ProcotolFrameDecoder extends LengthFieldBasedFrameDecoder {

    public ProcotolFrameDecoder() {
        this(1024, 12, 4, 0, 0);
    }


    public ProcotolFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}
