package com.ldy.werty.api;

import java.security.MessageDigest;

/**
 * 用于api请求接口md5加密   注意so中有使用此接口，不可删，不可混淆
 *
 * Created by lidongyang on 2016/10/19.
 */
public class ApiMd5Util {

    public static String getMD5Str(String str) {
        MessageDigest messageDigest = null;
        String result = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
            byte[] byteArray = messageDigest.digest();
            StringBuffer md5StrBuff = new StringBuffer();
            for (int i = 0; i < byteArray.length; i++) {
                md5StrBuff.append(String.format("%02x", ((int) byteArray[i]) & 0xff));
            }
            result = md5StrBuff.toString().toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
