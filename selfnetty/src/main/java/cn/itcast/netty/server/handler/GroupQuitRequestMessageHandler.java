package cn.itcast.netty.server.handler;

import cn.itcast.netty.protocol.myprotocl.message.GroupJoinResponseMessage;
import cn.itcast.netty.protocol.myprotocl.message.GroupQuitRequestMessage;
import cn.itcast.netty.server.session.Group;
import cn.itcast.netty.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author jlz
 * @date 2022年01月27日 0:02
 */
@Sharable
public class GroupQuitRequestMessageHandler extends SimpleChannelInboundHandler<GroupQuitRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupQuitRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        Group group = GroupSessionFactory.getGroupSession().removeMember(groupName, msg.getUsername());
        if (group != null) {
            ctx.writeAndFlush(new GroupJoinResponseMessage(true, "已退出群" + msg.getGroupName()));
        } else {
            ctx.writeAndFlush(new GroupJoinResponseMessage(true, msg.getGroupName() + "群不存在"));
        }
    }
}
