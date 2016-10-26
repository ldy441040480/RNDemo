package com.ldy.werty.reactnative.update.bean;

import org.json.JSONObject;

/**
 * 升级信息
 *
 * Created by ldy on 2016/2/24.
 */
public class RNInfo {

    /** 本次请求后台是否发生了错误 **/
    public String status = "";

    /** 给用户看的中文提示信息，静默升级时候没什么用 **/
    public String message = "";

    /** 当前的最新bundle版本 **/
    public String latestBundleV = "";

    /**  最新bundle要求的app最低版本 **/
    public String latestAppMinV = "";

    /** 能否升级 '1'-能 '0'-不能 **/
    public String canUpdate = "";

    /** 能升级的bundle版本 **/
    public String canUpdateBundleV = "";

    /** 能升级的bundle要求的app最低版本 **/
    public String canUpdateAppMinV = "";

    /** 补丁包相对地址 **/
    public String patchUrl = "";

    /** 平台：ios or android **/
    public String platform = "";

    /** jsbundle文件的md5 **/
    public String bundleMd5 = "";

    /** 补丁包文件的md5 **/
    public String patchMd5 = "";

    public static RNInfo parse(JSONObject response) {
        try {
            RNInfo bean = new RNInfo();
            bean.message = response.optString("message");
            bean.status = response.optString("status");
            JSONObject json = response.optJSONObject("data");
            if (json != null) {
                bean.latestBundleV = json.optString("latestBundleV");
                bean.latestAppMinV = json.optString("latestAppMinV");
                bean.canUpdate = json.optString("canUpdate");
                bean.canUpdateBundleV = json.optString("canUpdateBundleV");
                bean.canUpdateAppMinV = json.optString("canUpdateAppMinV");
                bean.patchUrl = json.optString("patchUrl");
                bean.platform = json.optString("platform");
                bean.bundleMd5 = json.optString("bundleMd5");
                bean.patchMd5 = json.optString("patchMd5");
            }
            return bean;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
}
