package cn.whiteblue.utils;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 * <p>
 * ======================
 * by WhiteBlue
 * on 15/10/20
 */
public class JsonUtil {
    public static Map<String, Object> makeMsg(String code, Object object) {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("code", code);
        returnMap.put("content", object);
        return returnMap;
    }


    public static JSONObject makeMsg(String code, String message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("message", message);
        return jsonObject;
    }

}
