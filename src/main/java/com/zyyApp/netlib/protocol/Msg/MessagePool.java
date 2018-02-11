package com.zyyApp.netlib.protocol.Msg;

import com.zyyApp.LogMgr;

import java.util.HashMap;

/**
 * 消息ID 和 消息对象类型 关联对应池;
 * 将这两者对应, 减少不匹配带来的错误;
 * Created by zyy on 2018-2-11.
 */
public class MessagePool {
    // 对应关联容器; HashMap<消息对象 类型, 消息ID>;
    private HashMap<Class<?>, Integer> msgMaps = new HashMap<>();


    // 单件;
    private static final MessagePool instance = new MessagePool();
    public static MessagePool getInstance() {
        return instance;
    }

    private MessagePool() {
        // null
    }

    /**
     * 注册 对应关系;
     * @param msgId  消息ID;
     * @param msgClass 消息对象类型;
     */
    public void register(int msgId, Class<?> msgClass){
        if (msgMaps.containsKey(msgClass)) {
            LogMgr.log.error("MessagePool register err; repeate msgClass:{}", msgClass);
            return;
        }

        msgMaps.put(msgClass, msgId);
    }

    /**
     * 获取 消息对象对应的消息ID;
     * @param msgClass  消息对象类型;
     */
    public int getMessageID(Class<?> msgClass){
        Integer msgId = msgMaps.get(msgClass);
        if (msgId == null) {
            LogMgr.log.error("msgClass is not regist: {}", msgClass);
            return -1;
        }
        return msgId;
    }
}
