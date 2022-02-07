package cn.itcast.netty.server.handler;

import cn.itcast.netty.protocol.myprotocl.message.GroupJoinRequestMessage;
import cn.itcast.netty.protocol.myprotocl.message.GroupJoinResponseMessage;
import cn.itcast.netty.server.session.Group;
import cn.itcast.netty.server.session.GroupSessionFactory;
import cn.itcast.netty.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.List;

/**
 * @author jlz
 * @date 2022年01月27日 0:02
 */
@Sharable
public class GroupJoinRequestMessageHandler extends SimpleChannelInboundHandler<GroupJoinRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupJoinRequestMessage msg) throws Exception {

        Group group = GroupSessionFactory.getGroupSession().joinMember(msg.getGroupName(), msg.getUsername());
        GroupJoinResponseMessage groupJoinResponseMessage;
        if (group != null) {
            groupJoinResponseMessage = new GroupJoinResponseMessage(true, "加入成功");
        } else {
            groupJoinResponseMessage = new GroupJoinResponseMessage(false, "群不存在-加入失败");
        }
        ctx.writeAndFlush(groupJoinResponseMessage);
    }
}
