package com.ldy.werty.reactnative.promise;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;

/**
 * 回调参数字典
 *
 * Created by ldy on 2016/2/2.
 */
public class PromiseDictionary {

    /**
     * @param data
     * @return
     *  {
     *    "data":data,
     *    "status":status
     *  }
     */
    public static WritableNativeMap getResultMap(boolean data, String status) {
        try {
            WritableNativeMap map = new WritableNativeMap();
            map.putBoolean("data", data);
            map.putString("status", status);
            return map;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static WritableNativeMap getResultMap(String data, String status) {
        try {
            WritableNativeMap map = new WritableNativeMap();
            map.putString("data", data);
            map.putString("status", status);
            return map;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static WritableNativeMap getResultMap(int data, String status) {
        try {
            WritableNativeMap map = new WritableNativeMap();
            map.putInt("data", data);
            map.putString("status", status);
            return map;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static WritableNativeMap getResultMap(WritableMap data, String status) {
        try {
            WritableNativeMap map = new WritableNativeMap();
            map.putMap("data", data);
            map.putString("status", status);
            return map;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static WritableNativeMap getResultMap(WritableArray data, String status) {
        try {
            WritableNativeMap map = new WritableNativeMap();
            map.putArray("data", data);
            map.putString("status", status);
            return map;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getString(ReadableMap readableMap, String name) {
        if (readableMap == null) return "";
        try {
            return readableMap.hasKey(name) ? readableMap.getString(name) : "";
        } catch (Throwable e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean getBoolean(ReadableMap readableMap, String name) {
        if (readableMap == null) return false;
        try {
            return readableMap.hasKey(name) && readableMap.getBoolean(name);
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getInt(ReadableMap readableMap, String name) {
        if (readableMap == null) return -1;
        try {
            return readableMap.hasKey(name) ? readableMap.getInt(name) : -1;
        } catch (Throwable e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static ReadableMap getReadableMap(ReadableMap readableMap, String name) {
        if (readableMap == null) return null;
        try {
            return readableMap.hasKey(name) ? readableMap.getMap(name) : null;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ReadableArray getReadableArray(ReadableMap readableMap, String name) {
        if (readableMap == null) return null;
        try {
            return readableMap.hasKey(name) ? readableMap.getArray(name) : null;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static WritableArray getWritableArray(String ... values) {
        if (values == null) return null;
        try {
            WritableArray array = new WritableNativeArray();
            for (String str : values) {
                array.pushString(str);
            }
            return array;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
