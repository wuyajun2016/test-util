package com.dfire.test.util.http;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

/**
 * 全局单例
 *
 * @author ljw
 * @date 14-6-19
 */
public final class SingleInstance {
    public static Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
    public static JsonParser jsonParser = new JsonParser();

}
