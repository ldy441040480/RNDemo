package com.ldy.werty.reactnative.module;

import android.text.TextUtils;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.ldy.werty.api.ApiCommonParams;
import com.ldy.werty.okhttp.OkRequestParams;
import com.ldy.werty.reactnative.RNKeys;
import com.ldy.werty.reactnative.promise.PromiseDictionary;
import com.ldy.werty.reactnative.promise.PromiseManager;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 工具方法 module
 *
 * Created by ldy on 2016/1/21.
 */
public class BBToolRNM extends ReactContextBaseJavaModule {

    public BBToolRNM(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @ReactMethod
    public void currentHostName(Promise promise) {
    }

    @ReactMethod
    public void babytreeLog(ReadableMap readableMap, Promise promise) {
    }

    @ReactMethod
    public void umengLog(ReadableMap readableMap, Promise promise) {
    }

    @ReactMethod
    public void makeRequest(ReadableMap readableMap, Promise promise) {
        OkRequestParams readableParams = new OkRequestParams();
        if (readableMap != null) {
            ReadableMap paramsMap = PromiseDictionary.getReadableMap(readableMap, "params");
            if (paramsMap != null) {
                ReadableMapKeySetIterator iterator = paramsMap.keySetIterator();
                if (iterator != null) {
                    while (iterator.hasNextKey()) {
                        String key = iterator.nextKey();
                        String value = PromiseDictionary.getString(paramsMap, key);
                        if (!TextUtils.isEmpty(key) && value != null) {
                            readableParams.put(key, value);
                        }
                    }
                }
            }
        }

        OkRequestParams okRequestParams = new OkRequestParams();
        ApiCommonParams.getCommonParams(okRequestParams);
        String encryptStr = ApiCommonParams.getEncryptStr(readableParams);
        if (!TextUtils.isEmpty(encryptStr)) {
            okRequestParams.put("secret", encryptStr);
        }

        WritableMap data = new WritableNativeMap();
        for (ConcurrentHashMap.Entry<String, String> entry : okRequestParams.getUrlParams().entrySet()) {
            data.putString(entry.getKey(), entry.getValue());
        }
        PromiseManager.success(promise, data, RNKeys.Status.SUCCESS);
    }

    @ReactMethod
    public void showDatePicker(ReadableMap readableMap, final Promise promise) {
    }

    @ReactMethod
    public void openPhotoBrowse(ReadableMap readableMap, Promise promise) {
    }

    @ReactMethod
    public void isAppInstalled(ReadableMap readableMap, Promise promise) {
    }

    @ReactMethod
    public void showBottomTab(ReadableMap readableMap, Promise promise) {
    }

    @ReactMethod
    public void downApp(ReadableMap readableMap, Promise promise) {
    }

    @Override
    public String getName() {
        return RNKeys.Module.BB_TOOL_RNM;
    }
}
