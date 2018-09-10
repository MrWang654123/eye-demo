package com.cheersmind.cheersgenie.features.utils;


import java.math.BigInteger;

/**
 * 字符串工具
 */
public class StringUtil {

    /**
     * 判断是空串（"null"字符串也视为空串）
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        boolean res = false;
        if (str == null) {
            res = true;
        } else {
            String tempStr = str.trim();
            if (tempStr.length() == 0) {
                res = true;
            } else {
                // "null"字符串也视为空串
                if (tempStr.equalsIgnoreCase("null")) {
                    res = true;
                }
            }
        }

        return res;
    }


    /**
     * 判断非空串（"null"字符串也视为空串）
     * @param str
     * @return
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }


    /**
     *  普通字符转换成16进制字符串
     * @param str
     * @return
     */
    public static String str2HexStr(String str)
    {
        byte[] bytes = str.getBytes();
        // 如果不是宽类型的可以用Integer
        BigInteger bigInteger = new BigInteger(1, bytes);
        return bigInteger.toString(16);
    }

    /*
     * 字节数组转16进制字符串显示
     */
    public String bytes2HexString(byte[] b,int length) {
        String r = "";

        for (int i = 0; i < length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            r += hex.toUpperCase();
        }

        return r;
    }

}
