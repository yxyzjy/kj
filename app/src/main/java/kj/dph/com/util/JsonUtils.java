package kj.dph.com.util;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {
    /**
     * @return 将字符串转化成json
     */
    public static JSONObject parseFromJson(String json) {
        JSONObject object = new JSONObject();
        if (TextUtils.isEmpty(json))
            return null;

        try {
            object = new JSONObject(json);
            return object;
        } catch (JSONException e) {

            e.printStackTrace();
        }
        return object;
    }

    /**
     * @return 获取JSON对象中的JSON
     */
    public static JSONObject getJsonObj(JSONObject obj, String key) {
        JSONObject jsonobj = new JSONObject();
        if (obj != null) {
            if (obj.has(key)) {
                try {
                    jsonobj = obj.getJSONObject(key);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonobj;
    }

    public static <T> T fromJson(String json, Class<T> classOfT)
            throws Exception {
        return com.alibaba.fastjson.JSONObject.parseObject(json, classOfT);
    }

    public static String toJson(Object src) {
        return com.alibaba.fastjson.JSONObject.toJSONString(src);
    }

    /**
     * @return 获取JSON对象中的JSONArray
     */
    public static JSONArray getJsonArry(JSONObject obj, String key) {
        JSONArray jsonobj = new JSONArray();
        if (obj != null) {
            if (obj.has(key)) {
                try {
                    jsonobj = obj.getJSONArray(key);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonobj;
    }

    /**
     * @return 获取JSON对象中的String
     */
    public static String getJsonString(JSONObject obj, String key) {
        String str = "";
        if (obj != null) {
            if (obj.has(key)) {
                try {
                    str = obj.getString(key);
                    if (null == str || str.equals("null")) {
                        str = "";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return str;
    }

    /**
     * @return 获取JSON对象中的String
     */
    public static Object getJsonObject(JSONObject obj, String key) {
        Object object = "";
        if (obj != null) {
            if (obj.has(key)) {
                try {
                    object = obj.get(key);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return object;
    }

    /**
     * @return 获取JSON对象中的boolean
     */
    public static boolean getJsonBoolean(JSONObject obj, String key) {
        boolean flag = false;
        if (obj != null) {
            if (obj.has(key)) {
                try {
                    flag = obj.getBoolean(key);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    /**
     * @return 获取JSON对象中的Float
     */
    public static float getJsonFloat(JSONObject obj, String key) {
        double f = 0;
        if (obj != null) {
            if (obj.has(key)) {
                try {
                    f = obj.getDouble(key);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return (float) f;
    }

    /**
     * @return 获取JSON对象中的double
     */
    public static double getJsonDouble(JSONObject obj, String key) {
        double dd = 0;
        if (obj != null) {
            if (obj.has(key)) {
                try {
                    dd = obj.getDouble(key);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return dd;
    }

    /**
     * @return 获取JSON对象中的long
     */
    public static long getJsonLong(JSONObject obj, String key) {
        long ll = 0;
        if (obj != null) {
            if (obj.has(key)) {
                try {
                    ll = obj.getLong(key);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return ll;
    }

    /**
     * @return 获取JSON对象中的int
     */
    public static int getJsonInt(JSONObject obj, String key) {
        int str = 0;
        if (obj != null) {
            if (obj.has(key)) {
                try {
                    str = obj.getInt(key);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return str;
    }
}
