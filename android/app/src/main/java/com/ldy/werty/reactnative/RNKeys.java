package com.ldy.werty.reactnative;

/**
 * Created by ldy on 2016/1/19.
 */
public final class RNKeys {

    public static final String DEBUG_TAG ="ReactDebug";

    public static class Default {
        /** jsMainModuleName **/
        public static final String JS_MAIN_MODULE_NAME ="index.android";
        /** moduleName **/
        public static final String JS_MODULE_NAME = "RNDemo";
        /** bundle name */
        public static final String BUNDLE_NAME = "index.android.bundle";
        /** patch name */
        public static final String PATCH_NAME = "jsbundle.patch";
    }

    public static class Promise {
        /** 登陆 **/
        public static final int LOGIN = 1;
        /** 分享 **/
        public static final int SHARE = 2;
        /** 上传图片 **/
        public static final int UPLOAD_PHOTO = 3;
        /** 大图浏览 **/
        public static final int OPEN_PHOTO = 4;
        /** 支付 **/
        public static final int PAY = 5;
    }

    public static class Module {
        /** 流程交互 Module **/
        public static final String BB_PAGE_ROUTER_RNM = "BBPageRouterRNM";
        /** 工具 Module **/
        public static final String BB_TOOL_RNM = "BBToolRNM";
    }

    public static class Status {
        /** 成功 **/
        public static final String SUCCESS = "success";
        /** 失败 **/
        public static final String FAILURE = "failure";
    }
}
