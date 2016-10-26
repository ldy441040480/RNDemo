package com.ldy.werty.reactnative.module;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.ldy.werty.reactnative.RNKeys;

/**
 * 处理 RN 与 Native 交互 module
 *
 * Created by ldy on 2016/1/19.
 */
public class BBPageRouterRNM extends ReactContextBaseJavaModule {

    public BBPageRouterRNM(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @ReactMethod
    public void showLoginPage(Promise promise) {
    }

    @ReactMethod
    public void shareOpen(ReadableMap readableMap, final Promise promise) {
    }

    @ReactMethod
    public void showPage(ReadableMap readableMap, Promise promise) {
    }

    @ReactMethod
    public void schemeOpen(ReadableMap readableMap, Promise promise) {
    }

    @ReactMethod
    public void payOpen(ReadableMap readableMap, final Promise promise) {
    }

    @ReactMethod
    public void choosePhoto(ReadableMap readableMap, Promise promise) {
    }

    @ReactMethod
    public void popModule(Promise promise) {
        if (getCurrentActivity() != null) {
            getCurrentActivity().finish();
        }
    }

    @Override
    public String getName() {
        return RNKeys.Module.BB_PAGE_ROUTER_RNM;
    }
}
