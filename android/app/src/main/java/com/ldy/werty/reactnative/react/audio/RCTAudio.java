package com.ldy.werty.reactnative.react.audio;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.events.RCTEventEmitter;

public class RCTAudio extends View implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {


    private String src;
    private MediaPlayer player = new MediaPlayer();

    public RCTAudio(Context context) {
        super(context);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            player.setOnBufferingUpdateListener(this);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
    }

    protected void play() {
        if (this.player != null) {
            this.player.start();
            this.fireEvent("onPlay", Arguments.createMap());
        }
    }

    protected void pause() {
        if (this.player != null) {
            this.player.pause();
            this.fireEvent("onPause", Arguments.createMap());
        }
    }

    protected boolean isPlaying() {
        return this.player != null && this.player.isPlaying();
    }

    protected void stop() {
        if (this.player != null) {
            this.player.stop();
        }
    }

    protected void release() {
        try {
            if (this.player != null) {
                this.player.release();
                this.player = null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    protected void load() {
        try {
            if (this.player != null) {
                player.reset();
                player.setDataSource(this.src);
                player.prepareAsync();
            }
        } catch (Exception e) {
            WritableMap event = Arguments.createMap();
            event.putString("msg", "load error");
            this.fireEvent("onFail", event);
            e.printStackTrace();
        }
    }

    protected void setCurrentTime(int time) {
        if (player != null) {
            player.seekTo(time);
        }
    }

    protected void getDuration(String reqId) {
        if (player != null) {
            WritableMap event = Arguments.createMap();
            event.putInt("duration", player.getDuration());
            this.callCbManager(event, reqId);
        }
    }

    protected void getCurrentTime(String reqId) {
        if (player != null) {
            WritableMap event = Arguments.createMap();
            event.putInt("currentTime", player.getCurrentPosition());
            this.callCbManager(event, reqId);
        }
    }

    @Override
    public void onPrepared(MediaPlayer player) {
        Log.e("mediaPlayer", "onPrepared");
        this.fireEvent("onLoadFinish", Arguments.createMap());
    }

    @Override
    public void onCompletion(MediaPlayer player) {
        Log.e("mediaPlayer", "onCompletion");
        this.fireEvent("onEnded", Arguments.createMap());
    }

    private void fireEvent(String name, WritableMap event) {
        ReactContext reactContext = (ReactContext) getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                getId(),
                name,
                event);
    }

    private void callCbManager(WritableMap event, String reqId) {
        event.putString("reqId", reqId);
        ReactContext reactContext = (ReactContext) getContext();
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("BbtCbManagerCallBack", event);
    }

    protected String getSrc() {
        return src;
    }

    protected void setSrc(String src) {
        this.src = src;
    }

    protected MediaPlayer getPlayer() {
        return player;
    }

}
