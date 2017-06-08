package com.zyyApp.netlib.protocol.Msg.Body;

import com.zyyApp.LogMgr;
import io.netty.buffer.ByteBuf;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 基于 ByteBuf 包装的 bufferBean
 * Created by zyy on 2017/5/15.
 */
public class Buffer_ByteBuf extends BufferBean{
    private ByteBuf buf;

    public void init(ByteBuf buf) { this.buf = buf; }
    public void clear() { this.buf = null; }


    public void writeInt(int value) {
        buf.writeInt(value);
    }

    public void writeString(String value) {
        if (value == null) {
            buf.writeInt(0);
            return;
        }
        try {
            byte[] bytes = value.getBytes("UTF-8");
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
        } catch (UnsupportedEncodingException e) {
            LogMgr.log.error("", e);
        }
    }

    public void writeLong(long value) {
        buf.writeLong(value);
    }

    public void writeShort(int value) {
        buf.writeShort((short) value);
    }

    public void writeShort(short value) {
        buf.writeShort(value);
    }

    public void writeByte(byte value) {
        buf.writeByte(value);
    }

    public void writeBytes(byte[] value) {
        if (value == null) {
            buf.writeInt(0);
            return;
        }

        buf.writeInt(value.length);
        buf.writeBytes(value);
    }



    public int readInt() {
        return buf.readInt();
    }

    public String readString() {
        int length = buf.readInt();
        if (length <= 0)
            return null;
        if (buf.readableBytes() < length)
            return null;
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LogMgr.log.error("Decode String Error: ", e);
        }
        return null;
    }

    public long readLong() {
        return buf.readLong();
    }

    public short readShort() {
        return buf.readShort();
    }

    public byte readByte() {
        return buf.readByte();
    }

    public byte[] readBytes(int length) {
        int bufLength = buf.readInt();
        int readLength = bufLength > length ? length : bufLength;

        if (readLength == 0)
            return new byte[0];
        byte[] bytes = new byte[readLength];
        buf.readBytes(bytes);
        return bytes;
    }



    /* *************************************************************** */
    // 重用池;
    private static ConcurrentLinkedQueue<Buffer_ByteBuf> pool = new ConcurrentLinkedQueue<>();
    public static Buffer_ByteBuf poolPop() {
        Buffer_ByteBuf buf = pool.poll();
        if (buf == null) {
            buf = new Buffer_ByteBuf();
        }
        return buf;
    }
    // 使用完后, 主动调用该接口, 可以达到重用的目的; 不调也没关系, 交给gc 进行回收, 每次使用时重新分配;
    public static void poolPush(Buffer_ByteBuf buf) {
        if (buf == null) {
            return;
        }
        if (pool.size() > 10240) {
            // 元素已经过多, 不用再往池里面放了, 交给gc进行回收;
            return;
        }
        // 数据清理;
        buf.clear();

        // 放入池中;
        pool.offer(buf);
    }
}
