package com.zyyApp.netlib.protocol.Msg.Body;

/**
 * 消息缓存 bean 接口; 用于粘合不同网络类库使用的不同消息缓存 类;
 * Created by zyy on 2017/5/15.
 */
public abstract class BufferBean {
    public abstract void writeInt(int value);
    public abstract void writeString(String value);
    public abstract void writeLong(long value);
    public abstract void writeShort(int value);
    public abstract void writeShort(short value);
    public abstract void writeByte(byte value);
    public abstract void writeBytes(byte[] value);


    public abstract int readInt();
    public abstract String readString();
    public abstract long readLong();
    public abstract short readShort();
    public abstract byte readByte();
    public abstract byte[] readBytes(int length);
}
