package com.zyyApp.netlib.protocol.Msg.MsgTrans;

import com.zyyApp.LogMgr;

import java.util.HashMap;

/**
 * 消息转换器池, 通过注册的方式, 由应用层控制消息的编码方式;
 * 可以每个消息ID对应不同的转换器(ProtocolBuf 有此需求)
 *
 * Created by zyy on 2017/4/14.
 */
public class MsgTransPool {
    // 消息转换器容器; HashMap<消息ID, 对应的转换器>;
    private HashMap<Integer, MsgTrans_Base> msgTransMap = new HashMap<>();


    // 单件;
    private static final MsgTransPool instance = new MsgTransPool();
    public static MsgTransPool getInstance() {
        return instance;
    }

    private MsgTransPool() {
        // null
    }

    /**
     * 注册 消息;
     * @param msgId  消息ID;
     * @param trans  消息转换器;   要确保客户端和服务端的转换器相同, 否则转换会出错;
     */
    public void register(int msgId, MsgTrans_Base trans){
        if (msgTransMap.containsKey(msgId)) {
            LogMgr.log.error("MsgPool register err; repeate msgId:{}", msgId);
            return;
        }

        msgTransMap.put(msgId, trans);
    }

    /**
     * 获取 消息对应的转换器;
     * @param msgId  消息ID;
     */
    public MsgTrans_Base getMessageTrans(int msgId){
        return msgTransMap.get(msgId);
    }
}
