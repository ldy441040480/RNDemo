package com.ldy.werty.util;

import android.content.Context;
import android.provider.Settings.Secure;
import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Created by machenxi on 16/8/19.
 */
public class OpenUDID {
    public static final String TAG = OpenUDID.class.getName();
    private static final String OPEN_UDID = "open_udid";
    private static String mOpenUDID;

    public static String getOpenUDIDInContext(Context context) {
        if (mOpenUDID == null) {
            String udid = DataUtil.getStringValue(context, OPEN_UDID);
            if (TextUtils.isEmpty(udid)) {
                generateOpenUDIDInContext(context);
                DataUtil.setValue(context, OPEN_UDID, mOpenUDID);
            } else {
                mOpenUDID = udid;
            }
        }
        LogUtil.v(TAG, mOpenUDID);
        return mOpenUDID;
    }

    /**
     * Generate a new OpenUDID
     */
    private static void generateOpenUDIDInContext(Context mContext) {
        //Try to get the ANDROID_ID
        String androidId = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID).toLowerCase();
        if (androidId.length() > 14 && !androidId.equals("9774d56d682e549c")) {
            mOpenUDID = androidId;
            return;
        }
        generateRandomNumber();
    }

    private static String Md5(String input) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(input.getBytes(), 0, input.length());
            byte md5Data[] = m.digest();
            StringBuilder outputBuilder = new StringBuilder();
            for (byte i : md5Data) {
                int b = (0xFF & i);
                if (b <= 0xF) outputBuilder.append("0");
                outputBuilder.append(Integer.toHexString(b));
            }
            return outputBuilder.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static void generateRandomNumber() {
        mOpenUDID = Md5(UUID.randomUUID().toString());
    }

}