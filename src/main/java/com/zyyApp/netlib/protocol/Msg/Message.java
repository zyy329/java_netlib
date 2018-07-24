package com.zyyApp.netlib.protocol.Msg;

import com.zyyApp.util.simple.UniqueId;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 中转 消息类;
 * 将应用层的消息对象 传递给网络库, 进行编码并发送;
 *
 * Created by zyy on 2017/4/14.
 */
public class Message {
    /** 唯一ID 生成器; */
    private static final UniqueId idCreater = new UniqueId();
    /** 唯一ID; 用于保证不会被重复加入重用池中, 避免被交叉使用, 产生数据错误; */
    private long uniqueId;

    /** 消息ID; */
    private int msgId;

    /** 应用层使用的消息对象; */
    private Object msgObj;



    /* *************************************************************** */
    public int getMsgId() {
        return msgId;
    }
    public Object getMsgObj() {
        return msgObj;
    }

    public boolean init(Object msgObj) {
        this.msgId = MessagePool.getInstance().getMessageID(msgObj.getClass());
        this.msgObj = msgObj;
        return this.msgId == -1 ? false : true;
    }

//    public void setMsgId(int msgId) {
//        this.msgId = msgId;
//    }
//
//    public void setMsgObj(Object msgObj) {
//        this.msgObj = msgObj;
//    }




    /* *************************************************************** */
    // 重用池;
    private static ConcurrentLinkedQueue<Message> pool = new ConcurrentLinkedQueue<>();
    private static ConcurrentHashMap<Long, Integer> mapUniqueId = new ConcurrentHashMap<>();    // 在重用池中对象的 UniqueId 集合;
    public static Message poolPop() {
        Message msg = pool.poll();
        if (msg == null) {
            msg = new Message();
            msg.uniqueId = idCreater.getUniqueId(0);
        } else {
            mapUniqueId.remove(msg.uniqueId);
        }
        return msg;
    }
    // 消息使用完后, 主动调用该接口, 可以达到重用的目的; 不调也没关系, 交给gc 进行回收, 每次使用时重新分配;
    public static void poolPush(Message msg) {
        if (msg == null) {
            return;
        }
        if (pool.size() > 10240) {
            // 元素已经过多, 不用再往池里面放了, 交给gc进行回收;
            return;
        }
        if (mapUniqueId.containsKey(msg.uniqueId)) {
            // 已经在池中了, 不重复放入;
            return;
        }

        // 数据清理;
        msg.msgId = 0;
        msg.msgObj = null;

        // 放入池中;
        pool.offer(msg);
        mapUniqueId.put(msg.uniqueId, 0);
    }
}
