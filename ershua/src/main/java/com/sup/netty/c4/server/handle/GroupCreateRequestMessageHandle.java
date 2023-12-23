package com.sup.netty.c4.server.handle;

import com.sup.netty.c4.message.GroupCreateRequestMessage;
import com.sup.netty.c4.message.GroupCreateResponseMessage;
import com.sup.netty.c4.server.session.Group;
import com.sup.netty.c4.server.session.GroupSession;
import com.sup.netty.c4.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jlz
 * @date 2023年12月19日 22:50
 */
@Slf4j
@Sharable
public class GroupCreateRequestMessageHandle extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(groupName, members);
        if (null == group) {
            //发送成功消息
            ctx.writeAndFlush(new GroupCreateResponseMessage(true, groupName + "创建成功"));
            //发送拉群消息
            List<Channel> membersChannel = groupSession.getMembersChannel(groupName);
            for (Channel channel : membersChannel) {
                channel.writeAndFlush(new GroupCreateResponseMessage(true, "您已被拉入群"+groupName));
            }
        } else {
            ctx.writeAndFlush(new GroupCreateResponseMessage(true, groupName + "创建失败,群已存在"));
        }
    }
}
