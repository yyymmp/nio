package cn.itcast.netty.server.handler;

import cn.itcast.netty.protocol.myprotocl.message.GroupChatRequestMessage;
import cn.itcast.netty.protocol.myprotocl.message.GroupCreateRequestMessage;
import cn.itcast.netty.protocol.myprotocl.message.GroupCreateResponseMessage;
import cn.itcast.netty.server.session.Group;
import cn.itcast.netty.server.session.GroupSession;
import cn.itcast.netty.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.List;
import java.util.Set;

/**
 * @author jlz
 * @date 2022年01月26日 23:46
 */
@Sharable
public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();
        //群管理器
        GroupSession groupSession = GroupSessionFactory.getGroupSession();

        Group group = groupSession.createGroup(groupName, members);
        if (group == null) {
            List<Channel> membersChannel = groupSession.getMembersChannel(groupName);
            for (Channel channel : membersChannel) {
                channel.writeAndFlush(new GroupCreateResponseMessage(true, "你已进入群" + groupName));
            }

            ctx.writeAndFlush(new GroupCreateResponseMessage(true, "群创建成功,群名:" + groupName));
        } else {
            ctx.writeAndFlush(new GroupCreateResponseMessage(false, "群创建失败,群名:" + groupName));
        }
    }
}
