package cn.itcast.netty.protocol.myprotocl.message;

public class PongMessage extends Message {
    @Override
    public int getMessageType() {
        return PongMessage;
    }
}
