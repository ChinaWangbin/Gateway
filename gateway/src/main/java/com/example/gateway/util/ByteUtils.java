package com.example.gateway.util;

import java.nio.ByteBuffer;

/**
 * @author : wangbin
 * @date: 2026/2/12 - 02 - 12 - 17:09
 * @Description: com.example.gateway.util
 */
public class ByteUtils {
    public static char LF =10;

    public static byte[] emptyByte(){
        return new byte[0];
    }

    public static boolean isReceiveEnd(byte[] bytes){
        if (bytes[bytes.length-1] == LF){
            return true;
        }
        return false;
    }
}
