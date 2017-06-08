package com.zyyApp.netlib.protocol.Msg.Head;

/**
 * 消息头处理类型 枚举定义;
 * 消息长度默认是第一位的, 不算在消息头中进行处理;
 */
public enum HeadType {
    HEAD_ID_ONLY,           // 仅包含 msgID 的消息头类型;
    HEAD_ICARRY_EXTERNAL,   // iCarry, 对外通讯消息头; 包含 校验码，消息id，发送时间（占位但是已经没有使用了）
}
