package com.ldy.werty;

import android.app.Application;
import android.content.Context;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.ldy.werty.okhttp.OkHttpUtil;
import com.ldy.werty.reactnative.RNKeys;
import com.ldy.werty.reactnative.update.RNFile;
import com.ldy.werty.util.LogUtil;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

public class BaseApplication extends Application implements ReactApplication {

    public static final String APPID = "care";
    public static final boolean DEBUG = true;

    public static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        // 初始化日志配置
        LogUtil.init(this, DEBUG);
        // 初始化网络模块
        OkHttpUtil.init();
    }

    private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {

        @Override
        protected String getJSMainModuleName() {
            return RNKeys.Default.JS_MAIN_MODULE_NAME;
        }

        @Override
        protected boolean getUseDeveloperSupport() {
            return BuildConfig.DEBUG;
        }

        @Nullable
        @Override
        protected String getBundleAssetName() {
            return RNFile.PATH_ASSETS_REACT + "/" + RNKeys.Default.BUNDLE_NAME;
        }

        @javax.annotation.Nullable
        @Override
        public String getJSBundleFile() {
            return RNFile.getJsBundlePath();
        }

        @Override
        protected List<ReactPackage> getPackages() {
            return Arrays.<ReactPackage>asList(
                    new MainReactPackage()
            );
        }
    };

    @Override
    public ReactNativeHost getReactNativeHost() {
        return mReactNativeHost;
    }
}
