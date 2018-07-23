package com.zyyApp.netlib.server.Netty;

import com.zyyApp.util.simple.UniqueId;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.util.internal.PlatformDependent;

import java.util.Collection;
import java.util.Map;

/**
 * 连接管理器;
 * Created by zyy on 2017/3/31.
 */
public class ChannelMgr {
    // UID, 供应用层使用的唯一ID;
    private final Map<Long, Channel> uid2channels = PlatformDependent.newConcurrentHashMap();
    // ChannelId, netty 内部的唯一ID;
    private final Map<ChannelId, Long> id2uid = PlatformDependent.newConcurrentHashMap();
    /** 唯一ID 生成器; 仅保证 同一 ChannelMgr 下唯一 */
    private UniqueId idCreater = new UniqueId();

    // 分配一个新的连接ID;
    private Long newChannelId() {
        return idCreater.getUniqueId(0);
    }

    /**
     * 新增一个连接;
     * @return 返回该连接对应的 UID;
     */
    public Long addChannel(Channel channel) {
        Long uid = newChannelId();
        uid2channels.put(uid, channel);
        id2uid.put(channel.id(), uid);
        return uid;
    }

    public Long getUID(ChannelId id) {
        return id2uid.get(id);
    }
    public Channel findChannel(Long uid) {
        return uid2channels.get(uid);
    }
    public Channel findChannel(ChannelId id) {
        Long uid = getUID(id);
        if (uid != null) {
            return findChannel(uid);
        }
        return null;
    }
    public Collection<Channel> getAllChannel() {
        return uid2channels.values();
    }

    public boolean removeChannel(Long uid) {
        Channel channel = findChannel(uid);
        if (channel != null) {
            uid2channels.remove(uid);
            id2uid.remove(channel.id());
            return true;
        }

        return false;
    }

    public boolean removeChannel(ChannelId id) {
        Long uid = getUID(id);
        if (uid != null) {
            uid2channels.remove(uid);
            id2uid.remove(id);
            return true;
        }

        return false;
    }

    public void clearAll() {
        uid2channels.clear();
        id2uid.clear();
    }
}
