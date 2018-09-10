package com.cheersmind.cheersgenie.main.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.util.Collection;

/**
 * json辅助类
 * @author zhouw
 */
public class JsonUtil {
    private static Gson gson = new Gson();

    /**
     * json转成对象
     * @param json
     * @param classOfT
     * @return
     */
    public static <T> T fromJson(String json, Class<T> classOfT){
        return gson.fromJson(json,classOfT);
    }

    /**
     * 转成json
     * @param src
     * @return
     */
    public static String toJson(Object src){
        return gson.toJson(src);
    }

    /**
     * 转成json数组
     * @param src
     * @return
     */
    public static JsonArray toJsonArray(Collection src){
        JsonElement jsonElement = gson.toJsonTree(src);
        JsonArray asJsonArray = jsonElement.getAsJsonArray();

        return asJsonArray;
    }

    /**
     * 判断是否是有效json
     * @param json
     * @return
     */
    public static boolean isJson(String json){
        try {
            new JsonParser().parse(json);
            return true;
        } catch (JsonParseException e) {
            return false;
        }
    }

}