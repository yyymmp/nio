package cn.itcast.netty.protocol.myprotocl.message;

public class PingMessage extends Message {
    @Override
    public int getMessageType() {
        return PingMessage;
    }
}
