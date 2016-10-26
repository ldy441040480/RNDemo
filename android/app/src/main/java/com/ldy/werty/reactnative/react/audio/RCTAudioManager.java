package com.ldy.werty.reactnative.react.audio;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class RCTAudioManager extends SimpleViewManager<RCTAudio> implements LifecycleEventListener {

    private RCTAudio mRCTAudio;

    @Override
    public String getName() {
        return "RCTAudio";
    }

    @Override
    protected RCTAudio createViewInstance(ThemedReactContext themedReactContext) {
        themedReactContext.addLifecycleEventListener(this);
        return new RCTAudio(themedReactContext);
    }


    @ReactProp(name = "src")
    public void setSrc(RCTAudio view, String src) {
        if (view != null) {
            view.setSrc(src);
        }
    }


    @Override
    public @Nullable Map<String, Integer> getCommandsMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("load", 1);
        map.put("play", 2);
        map.put("pause", 3);
        map.put("setCurrentTime", 4);
        map.put("getDuration", 5);
        map.put("getCurrentTime", 6);
        map.put("stop", 7);
        map.put("release", 8);
        map.put("isPlaying", 9);
        return map;
    }

    @Override
    public void receiveCommand(RCTAudio root, int commandId, @Nullable ReadableArray args) {
        if (root == null) return;
        mRCTAudio = root;
        switch (commandId) {
            case 1:
                root.load();
                break;
            case 2:
                root.play();
                break;
            case 3:
                root.pause();
                break;
            case 4:
                if (args != null)
                    root.setCurrentTime(args.getInt(0));
                break;
            case 5:
                if (args != null)
                    root.getDuration(args.getString(0));
                break;
            case 6:
                if (args != null)
                    root.getCurrentTime(args.getString(0));
                break;
            case 7:
                root.stop();
                break;
            case 8:
                root.release();
                break;
            case 9:
                root.isPlaying();
                break;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable Map getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.of(
                "onPlay", MapBuilder.of("registrationName", "onPlay"),
                "onPause", MapBuilder.of("registrationName", "onPause"),
                "onFail", MapBuilder.of("registrationName", "onFail"),
                "onLoadFinish", MapBuilder.of("registrationName", "onLoadFinish"),
                "onEnded", MapBuilder.of("registrationName", "onEnded")
        );
    }

    @Override
    public void onHostResume() {
    }

    @Override
    public void onHostPause() {
        if (mRCTAudio != null && mRCTAudio.isPlaying()) {
            mRCTAudio.pause();
        }
    }

    @Override
    public void onHostDestroy() {
        if (mRCTAudio != null) {
            mRCTAudio.release();
            mRCTAudio = null;
        }
    }
}
