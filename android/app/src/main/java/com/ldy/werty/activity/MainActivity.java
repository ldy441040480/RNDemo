package com.ldy.werty.activity;

import android.os.Bundle;

import com.facebook.react.ReactActivity;
import com.ldy.werty.reactnative.RNKeys;
import com.ldy.werty.reactnative.promise.PromiseManager;
import com.ldy.werty.reactnative.update.RNBundle;
import com.ldy.werty.reactnative.update.RNUpdate;

public class MainActivity extends ReactActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        // 初始化 jsBundle
        RNBundle.init();
//        // 增量更新从服务器
        RNUpdate.updateFromServer(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PromiseManager.removeAll();
        RNUpdate.setOnUpdateListener(null);
    }

    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     */
    @Override
    protected String getMainComponentName() {
        return RNKeys.Default.JS_MODULE_NAME;
    }
}
