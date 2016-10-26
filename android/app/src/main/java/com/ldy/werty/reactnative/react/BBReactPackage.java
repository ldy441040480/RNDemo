package com.ldy.werty.reactnative.react;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.ldy.werty.reactnative.module.BBPageRouterRNM;
import com.ldy.werty.reactnative.module.BBToolRNM;
import com.ldy.werty.reactnative.react.audio.RCTAudioManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ldy on 2016/1/19.
 */
public class BBReactPackage implements ReactPackage {

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        modules.add(new BBPageRouterRNM(reactContext));
        modules.add(new BBToolRNM(reactContext));
        return modules;
    }

    @Override
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        List<ViewManager> managers = new ArrayList<>();
        managers.add(new RCTAudioManager());
        return managers;
    }
}
