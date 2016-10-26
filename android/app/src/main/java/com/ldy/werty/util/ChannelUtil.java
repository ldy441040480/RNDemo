package com.ldy.werty.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;

import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ChannelUtil {

    private static final String TAG = ChannelUtil.class.getSimpleName();

    private static final String CHANNEL_KEY = "babytree";

    private static String mCurrentChannelKey;

    /**
     * 返回市场。  如果获取失败返回""
     *
     * @param context
     * @return
     */
    public static String getChannel(Context context) {
        LogUtil.d(TAG, " CurrentChannelKey : " + mCurrentChannelKey);
        if (TextUtils.isEmpty(mCurrentChannelKey)) {
            mCurrentChannelKey = getChannel(context, "");
        }
        return mCurrentChannelKey;
    }

    /**
     * 返回市场。  如果获取失败返回defaultChannel
     *
     * @param context
     * @param defaultChannel
     * @return
     */
    public static String getChannel(Context context, String defaultChannel) {
        //从apk中获取
        String channel = getChannelFromApk(context, CHANNEL_KEY);
        if (!TextUtils.isEmpty(channel)) {
            return channel;
        }
        return defaultChannel;
    }

    /**
     * 从apk中获取版本信息
     *
     * @param context
     * @param channelKey
     * @return
     */
    private static String getChannelFromApk(Context context, String channelKey) {
        //从apk包中获取
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        //默认放在meta-inf/里， 所以需要再拼接一下
        String key = "META-INF/" + channelKey;
        String ret = "";
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.startsWith(key)) {
                    ret = entryName;
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        String channel = null;
        try {
            String[] split = ret.split("_");
            channel = "";
            if (split != null && split.length >= 2) {
                channel = ret.substring(split[0].length() + 1);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return channel;
    }
}
