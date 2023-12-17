package com.sup.netty.c4.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author jlz
 * @date 2023年12月17日 21:17
 */
//基于既定协议 用于解码
public class ProtocolFrameDecoder extends LengthFieldBasedFrameDecoder {

    /**
     * 提供一个无参构造
     */
    public ProtocolFrameDecoder(){
        this(1024, 12, 4, 0, 0);
    }

    public ProtocolFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}
