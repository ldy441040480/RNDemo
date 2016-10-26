package com.ldy.werty.reactnative.update.listener;

/**
 * react-native 更新监听
 *
 * Created by ldy on 2016/3/1.
 */
public interface OnReactUpdateListener {
    /**
     * 升级开始
     */
    void onUpdateStart();
    /**
     * 更新成功
     *
     * @param version
     */
    void onUpdateSuccess(String version);
    /**
     * 更新失败
     * @param errMsg
     */
    void onUpdateFailure(String errMsg);

}
