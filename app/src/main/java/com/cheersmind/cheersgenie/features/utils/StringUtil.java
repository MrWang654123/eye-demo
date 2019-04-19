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
    public static String bytes2HexString(byte[] b,int length) {
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

    /**
     * 字符串按照一定长度分割并返回数组！(String按照给定长度分割)
      * @param s 源字符串
     * @param len 子串长度
     * @return 返回结果
     */
    public static String[] stringSpilt(String s,int len){
        int spiltNum;//len->想要分割获得的子字符串的长度
        int i;
        int cache = len;//存放需要切割的数组长度
        spiltNum = (s.length())/len;
        String[] subs;//创建可分割数量的数组
        if((s.length()%len)>0){
            subs = new String[spiltNum+1];
        }else{
            subs = new String[spiltNum];
        }

//可用一个全局变量保存分割的数组的长度
//System.out.println(subs.length);
//        leng = subs.length;
        int start = 0;
        if(spiltNum>0){
            for(i=0;i<subs.length;i++){
                if(i==0){
                    subs[0] = s.substring(start, len);
                    start = len;
                }else if(i>0 && i<subs.length-1){
                    len = len + cache ;
                    subs[i] = s.substring(start,len);
                    start = len ;
                }else{
                    subs[i] = s.substring(len,s.length());
                }
            }
        }
        return subs;
    }

}
