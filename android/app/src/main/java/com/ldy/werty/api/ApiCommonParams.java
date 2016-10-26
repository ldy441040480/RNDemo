package com.ldy.werty.api;

import android.content.Context;
import android.text.TextUtils;

import com.ldy.werty.BaseApplication;
import com.ldy.werty.okhttp.OkRequestParams;
import com.ldy.werty.util.OpenUDID;
import com.ldy.werty.util.TimeUtil;
import com.ldy.werty.util.Util;

import java.net.ConnectException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by lidongyang on 2016/10/19.
 */
public class ApiCommonParams {

    static {
        System.loadLibrary("api_encrypt");
    }

//    public static native String nativeGetParam(Context context, List<ComparatorParam.BasicNameValuePair> list);
    /**
	 * 上下文
	 */
	private static Context mContext = null;
	/**
	 * 应用标识(每个接口调用都需要传)
	 */
	private static String mAppName = null;
	/**
	 * mac地址(每个接口调用都需要传)
	 */
	private static String mMac = null;
	/**
	 * 版本号(每个接口调用都需要传)
	 */
	private static String mVersion = null;
    /**
     * IMEI
     */
    private static String mImei = null;
    /**
     * android id
     */
    private static String mAndroidId = null;
    /**
     * build_serial
     */
    private static String mBuildSerial = null;
    /**
     * 渠道号
     */
    private static String mChannel = null;

	static {
		mContext = BaseApplication.getContext();
		mAppName = BaseApplication.APPID;
		// 获取版本号
		mVersion = Util.getAppVersionName(mContext);
		// 获取mac
		mMac = Util.getMacAddress();
        mImei = Util.getImei(mContext);
        mAndroidId = Util.getAndroidId(mContext);
        mBuildSerial = Util.getBuildSerial(mContext);
        mChannel = Util.getAppChannel(mContext, "UMENG_CHANNEL");
	}

	/**
	 * 添加通用参数
	 *
	 * @param params
	 *            ：参数列表
	 * @throws ConnectException
	 */
	public static void addCommonParams(OkRequestParams params) {
		if (params != null) {
			params.put("client_type", "android");
			if (TextUtils.isEmpty(mMac)) {
				mMac = Util.getMacAddress();
			}
            if (TextUtils.isEmpty(mImei)) {
                mImei = Util.getImei(mContext);
            }
            if (TextUtils.isEmpty(mAndroidId)) {
                mAndroidId = Util.getAndroidId(mContext);
            }
            if (TextUtils.isEmpty(mBuildSerial)) {
                mBuildSerial = Util.getBuildSerial(mContext);
            }
            if (TextUtils.isEmpty(mChannel)) {
                mChannel = Util.getAppChannel(mContext, "UMENG_CHANNEL");
            }
			if (!TextUtils.isEmpty(mMac)) {
				params.put("mac", mMac);
			}
            if (!TextUtils.isEmpty(mImei)) {
                params.put("imei", mImei);
            }
            if (!TextUtils.isEmpty(mAndroidId)) {
                params.put("android_id", mAndroidId);
            }
            if (!TextUtils.isEmpty(mBuildSerial)) {
                params.put("build_serial", mBuildSerial);
            }
			params.put("app_id", mAppName);
			params.put("version", mVersion);
            if (!TextUtils.isEmpty(mChannel)) {
                params.put("source_channel", mChannel);
            }
            // 时间精确到秒
            params.put("local_ts", (TimeUtil.getCurrentTime()/1000));
			params.put("bbtid", OpenUDID.getOpenUDIDInContext(mContext));
			String secret = null;
			try {
//				secret = nativeGetParam(mContext, getParamsList(params.getUrlParams()));
			} catch (Throwable e) {
				e.printStackTrace();
			}
			if (!TextUtils.isEmpty(secret)) {
				params.put("secret", secret);
			}
        }
	}

	public static void getCommonParams(OkRequestParams params) {
		params.put("client_type", "android");
		if (TextUtils.isEmpty(mMac)) {
			mMac = Util.getMacAddress();
		}
		if (TextUtils.isEmpty(mImei)) {
			mImei = Util.getImei(mContext);
		}
		if (TextUtils.isEmpty(mAndroidId)) {
			mAndroidId = Util.getAndroidId(mContext);
		}
		if (TextUtils.isEmpty(mBuildSerial)) {
			mBuildSerial = Util.getBuildSerial(mContext);
		}
		if (TextUtils.isEmpty(mChannel)) {
			mChannel = Util.getAppChannel(mContext, "UMENG_CHANNEL");
		}
		if (!TextUtils.isEmpty(mMac)) {
			params.put("mac", mMac);
		}
		if (!TextUtils.isEmpty(mImei)) {
			params.put("imei", mImei);
		}
		if (!TextUtils.isEmpty(mAndroidId)) {
			params.put("android_id", mAndroidId);
		}
		if (!TextUtils.isEmpty(mBuildSerial)) {
			params.put("build_serial", mBuildSerial);
		}
		params.put("app_id", mAppName);
		params.put("version", mVersion);
		if (!TextUtils.isEmpty(mChannel)) {
			params.put("source_channel", mChannel);
		}
		// 时间精确到秒
		params.put("local_ts", (TimeUtil.getCurrentTime()/1000));
		params.put("bbtid", OpenUDID.getOpenUDIDInContext(mContext));
	}

	public static void getEncrypt(OkRequestParams params) {
		OkRequestParams tempParams = null;
		if (params != null) {
			tempParams = params;
			getCommonParams(tempParams);
			String secret = null;
			try {
//				secret = nativeGetParam(mContext, getParamsList(tempParams.getUrlParams()));
			} catch (Throwable e) {
				e.printStackTrace();
			}
			if (!TextUtils.isEmpty(secret)) {
				params.put("secret", secret);
			}
		}
	}

	/**
	 * 返回加密后的字符串
	 * @param params
	 * @return
     */
	public static String getEncryptStr(OkRequestParams params) {
		String secret = null;
		if (params != null) {
			getCommonParams(params);
			try {
//				secret = nativeGetParam(mContext, getParamsList(params.getUrlParams()));
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return secret;
	}

	public static List<ComparatorParam.BasicNameValuePair> getParamsList(Map<String, String> map) {
		List<ComparatorParam.BasicNameValuePair> lparams = new LinkedList<>();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			lparams.add(new ComparatorParam.BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		return lparams;
	}
}
