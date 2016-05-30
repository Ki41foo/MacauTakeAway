package com.gvbyc.ki41foo.delivery.protocal;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by goodview on 22/02/16.
 */
public class BaseProtocol {
    public static JsonObject getReturnObject(String result) {
        return new JsonParser().parse(result).getAsJsonObject().get("returnObject").getAsJsonObject();
    }

    public static String getCode(String result) {
        return new JsonParser().parse(result).getAsJsonObject().get("code").getAsString();
    }

    public static String getMsg(String result) {
        return new JsonParser().parse(result).getAsJsonObject().get("msg").getAsString();
    }
}
