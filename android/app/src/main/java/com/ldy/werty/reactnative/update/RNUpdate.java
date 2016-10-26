package com.ldy.werty.reactnative.update;

import android.content.Context;
import android.text.TextUtils;

import com.ldy.werty.BaseApplication;
import com.ldy.werty.okhttp.OkHttpUtil;
import com.ldy.werty.okhttp.OkRequestParams;
import com.ldy.werty.okhttp.response.OkHttpResInputStreamHandler;
import com.ldy.werty.okhttp.response.OkHttpResJsonHandler;
import com.ldy.werty.reactnative.RNKeys;
import com.ldy.werty.reactnative.update.bean.RNInfo;
import com.ldy.werty.reactnative.update.listener.OnReactUpdateListener;
import com.ldy.werty.util.LogUtil;
import com.ldy.werty.util.Util;

import org.json.JSONObject;

import java.io.InputStream;

import okhttp3.Headers;

/**
 * react-native 增量升级更新
 *
 * Created by ldy on 2016/2/23.
 */
public final class RNUpdate {

    private static final String TAG = RNKeys.DEBUG_TAG;

    private static OnReactUpdateListener mOnUpdateListener = null;

    /**
     * 从服务器更新 bundle
     *
     * @param context
     */
    public static void updateFromServer(Context context) {

        onUpdateStart();

        OkRequestParams params = new OkRequestParams();
        params.put("bundleV", RNFile.getVersion());
        params.put("appV", Util.getAppVersionName(context));
        params.put("platform", "android");
        params.put("dev", BaseApplication.DEBUG);
        OkHttpUtil.get("http://api.babytree.com/api/mobile_bundle/checkout_package", params, new OkHttpResJsonHandler() {
            @Override
            public void onSuccess(int code, Headers headers, JSONObject response) {
                LogUtil.i(TAG, "updateFromServer onSuccess response=[" + response + "]");
                RNInfo reactInfo = RNInfo.parse(response);
                if (reactInfo != null) {
                    if ("1".equals(reactInfo.canUpdate)) {
                        downPatchFromServer(reactInfo.patchUrl, reactInfo.canUpdateBundleV, reactInfo.bundleMd5, reactInfo.patchMd5);
                    } else {
                        onUpdateFailure(reactInfo.message);
                    }
                } else {
                    onUpdateFailure("数据错误");
                }
            }

            @Override
            public void onFailure(int code, Headers headers, int error, Throwable t) {
                LogUtil.i(TAG, "updateFromServer onFailure t[" + t + "]");
            }
        });
    }

    /**
     * 下载增量包 patch
     *
     */
    public static void downPatchFromServer(String patchUrl, final String version, final String bundleMd5,final String patchMd5) {
        if (TextUtils.isEmpty(patchUrl) || TextUtils.isEmpty(version) || TextUtils.isEmpty(bundleMd5)) {
            return;
        }

        OkHttpUtil.get(patchUrl, new OkHttpResInputStreamHandler() {

            @Override
            protected void onSuccessRequest(int code, Headers headers, InputStream response) {
                onSuccess(code, headers, response);
            }

            @Override
            public void onSuccess(int code, Headers headers, final InputStream response) {
                final boolean isSuccess = RNBundle.updateJsBundle(response, version, bundleMd5, patchMd5);
                getOkHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (isSuccess) {
                            onUpdateSuccess(version);
                        } else {
                            onUpdateFailure("客户端升级失败");
                        }
                    }
                });
            }

            @Override
            public void onFailure(int code, Headers headers, int error, Throwable t) {
                onUpdateFailure("从服务器下载增量包失败");
            }
        });
    }

    public static void setOnUpdateListener(OnReactUpdateListener onUpdateListener) {
        mOnUpdateListener = onUpdateListener;
    }

    private static void onUpdateFailure(String errMsg) {
        if (mOnUpdateListener != null) {
            mOnUpdateListener.onUpdateFailure(errMsg);
        }
    }

    private static void onUpdateSuccess(String version) {
        if (mOnUpdateListener != null) {
            mOnUpdateListener.onUpdateSuccess(version);
        }
    }

    private static void onUpdateStart() {
        if (mOnUpdateListener != null) {
            mOnUpdateListener.onUpdateStart();
        }
    }
}