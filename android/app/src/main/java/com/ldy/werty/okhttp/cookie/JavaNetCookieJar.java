package com.ldy.werty.okhttp.cookie;

import com.ldy.werty.util.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class JavaNetCookieJar implements CookieJar {

    private static final String TAG = JavaNetCookieJar.class.getSimpleName();

    private final List<Cookie> mCookies = new CopyOnWriteArrayList<>();

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        try {
            if (cookies != null && cookies.size() > 0) {
                for (Cookie coo : cookies) {
                    for (int i = 0; i < mCookies.size(); i ++) {
                        Cookie cookie = mCookies.get(i);
                        if (coo.name().equals(cookie.name())) {
                            mCookies.remove(cookie);
                            i --;
                        }
                    }
                    if (checkNameAndValue(coo.name(), coo.value())) {
                        mCookies.add(coo);
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        LogUtil.i(TAG, "loadForRequest mCookies=[" + mCookies.size() + "]");
        List<Cookie> result = new ArrayList<>();
        for (Cookie cookie : mCookies) {
            if (cookie.matches(url)) {
                if (checkNameAndValue(cookie.name(), cookie.value())) {
                    result.add(cookie);
                }
            }
        }
        return result;
    }

    private boolean checkNameAndValue(String name, String value) {
        if (name == null)
            return false;
        if (name.isEmpty())
            return false;
        for (int i = 0, length = name.length(); i < length; i++) {
            char c = name.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                LogUtil.i(TAG, "Unexpected char name:" + name);
                return false;
            }
        }
        if (value == null)
            return false;
        for (int i = 0, length = value.length(); i < length; i++) {
            char c = value.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                LogUtil.i(TAG, "Unexpected char value:" + value);
                return false;
            }
        }
        return true;
    }
}
