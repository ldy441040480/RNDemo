package com.ldy.werty.reactnative.promise;

import android.util.SparseArray;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;


/**
 * RN 交互回调处理
 *
 * Created by ldy on 2016/1/21.
 */
public class PromiseManager {

    /** 存放 promise 对象数组 */
    public static SparseArray<Promise> mPromiseArray = new SparseArray<>();

    /**
     * 存放 promise 对象
     *
     * @param promise
     * @param promiseId
     */
    public static void put(Promise promise, int promiseId) {
        remove(promiseId);
        mPromiseArray.put(promiseId, promise);
    }

    /**
     * 读取 promise 对象
     *
     * @param promiseId
     * @return
     */
    public static Promise get(int promiseId) {
        return mPromiseArray.get(promiseId);
    }

    /**
     * 移除单个 promise 对象
     *
     * @param promiseId
     */
    public static void remove(int promiseId) {
        if (mPromiseArray.get(promiseId) != null) {
            mPromiseArray.remove(promiseId);
        }
    }

    /**
     * 清空 promise 数组
     */
    public static void removeAll() {
        mPromiseArray.clear();
    }

    /**
     * 回调成功
     *
     * @param promiseId
     * @param data      boolean 型数据
     * @param status
     */
    public static void success(int promiseId, boolean data, String status) {
        Promise promise = get(promiseId);
        success(promise, data, status);
        remove(promiseId);
    }

    /**
     * 回调成功
     *
     * @param promiseId
     * @param data      int 型数据
     * @param status
     */
    public static void success(int promiseId, int data, String status) {
        Promise promise = get(promiseId);
        success(promise, data, status);
        remove(promiseId);
    }

    /**
     * 回调成功
     *
     * @param promiseId
     * @param data      WritableMap 型数据
     * @param status
     */
    public static void success(int promiseId, WritableMap data, String status) {
        Promise promise = get(promiseId);
        success(promise, data, status);
        remove(promiseId);
    }

    /**
     * 回调成功
     *
     * @param promiseId
     * @param data      WritableArray 型数据
     * @param status
     */
    public static void success(int promiseId, WritableArray data, String status) {
        Promise promise = get(promiseId);
        success(promise, data, status);
        remove(promiseId);
    }

    /**
     * 回调成功
     *
     * @param promiseId
     * @param data      String 型数据
     * @param status
     */
    public static void success(int promiseId, String data, String status) {
        Promise promise = get(promiseId);
        success(promise, data, status);
        remove(promiseId);
    }

    /**
     * 回调成功
     *
     * @param promise
     * @param data      boolean 型数据
     * @param status
     */
    public static void success(Promise promise, boolean data, String status) {
        if (promise == null) return;
        success(promise, PromiseDictionary.getResultMap(data, status));
    }

    /**
     * 回调成功
     *
     * @param promise
     * @param data      int 型数据
     * @param status
     */
    public static void success(Promise promise, int data, String status) {
        if (promise == null) return;
        success(promise, PromiseDictionary.getResultMap(data, status));
    }

    /**
     * 回调成功
     *
     * @param promise
     * @param data      WritableMap 型数据
     * @param status
     */
    public static void success(Promise promise, WritableMap data, String status) {
        if (promise == null) return;
        success(promise, PromiseDictionary.getResultMap(data, status));
    }

    /**
     * 回调成功
     *
     * @param promise
     * @param data      WritableArray 型数据
     * @param status
     */
    public static void success(Promise promise, WritableArray data, String status) {
        if (promise == null) return;
        success(promise, PromiseDictionary.getResultMap(data, status));
    }

    /**
     * 回调成功
     *
     * @param promise
     * @param data      String 型数据
     * @param status
     */
    public static void success(Promise promise, String data, String status) {
        if (promise == null) return;
        success(promise, PromiseDictionary.getResultMap(data, status));
    }

    /**
     * 回调成功
     *
     * @param promise
     * @param value
     */
    public static void success(Promise promise, Object value) {
        if (promise == null || value == null) return;
        try {
            promise.resolve(value);
        } catch (Throwable e) {
            failure(promise, e);
        }
    }

    /**
     * 回调失败
     *
     * @param promiseId
     * @param reason    Throwable
     */
    public static void failure(int promiseId, Throwable reason) {
        Promise promise = get(promiseId);
        if (promise != null) {
            failure(promise, reason);
        }
        remove(promiseId);
    }

    /**
     * 回调失败
     *
     * @param promiseId
     * @param code
     * @param message
     */
    public static void failure(int promiseId, String code, String message) {
        Promise promise = get(promiseId);
        if (promise != null) {
            failure(promise, code, message);
        }
        remove(promiseId);
    }

    /**
     * 回调失败
     *
     * @param promise
     * @param reason
     */
    public static void failure(Promise promise, Throwable reason) {
        if (promise == null) return;
        try {
            promise.reject(reason);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 回调失败
     *
     * @param promise
     * @param code
     * @param message
     */
    public static void failure(Promise promise, String code, String message) {
        if (promise == null) return;
        try {
            promise.reject(code, message);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
