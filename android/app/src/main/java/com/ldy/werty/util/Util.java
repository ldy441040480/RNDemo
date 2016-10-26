package com.ldy.werty.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    private static final String TAG = Util.class.getSimpleName();

    /**
     * 获取AndroidMenifest.xml中Application中的meta data
     *
     * @param context ：上下文
     * @param name    ：meta data的名称
     * @return：meta data的值
     */
    public static String getApplicationMetaData(Context context, String name) {
        String result = "";
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            result = appInfo.metaData.get(name) + "";
        } catch (Exception e) {
            LogUtil.e(TAG, "getApplicationMetaData e[" + e + "]");
        }
        return result;
    }

    public static String getAppChannel(Context context, String name) {
        String result = "";
        try {
            String channel = ChannelUtil.getChannel(context);
            if (!TextUtils.isEmpty(channel)) {
                result = channel;
            } else {
                result = getApplicationMetaData(context, name);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "getApplicationMetaData e[" + e + "]");
        }
        return result;
    }

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 返回当前程序版本号
     */
    public static String getAppVersionCode(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return String.valueOf(pi.versionCode);
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    /**
     * 返回当前程序名
     */
    public static String getAppLabel(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            return String.valueOf(pm.getApplicationLabel(context.getApplicationInfo()));
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    /**
     * 返回mac地址
     */
    public static String getMacAddress() {
        String macSerial = null;
        LineNumberReader numberReader = null;
        FileReader fileReader = null;
        try {
            Process process = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            numberReader = new LineNumberReader(new InputStreamReader(process.getInputStream()));
            String str = "";
            while (str != null) {
                str = numberReader.readLine();
                if (str != null) {
                    macSerial = str.trim();
                    break;
                }
            }
            if (TextUtils.isEmpty(macSerial)) {
                fileReader = new FileReader("/sys/class/net/eth0/address");
                StringBuilder builder = new StringBuilder();
                char[] buffer = new char[4096];
                int readLength;
                while ((readLength = fileReader.read(buffer)) >= 0) {
                    builder.append(buffer, 0, readLength);
                }
                macSerial = builder.toString().trim().substring(0, 17);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (numberReader != null) numberReader.close();
                if (fileReader != null) fileReader.close();
            } catch (Exception e) {
            }
        }
        return macSerial;
    }

    /**
     * 网络状态判断
     *
     * @param context
     * @return
     */
    public static boolean hasNetwork(Context context) {
        try {
            if (null == context || null == context.getApplicationContext()) {
                return false;
            }
            ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            if (manager == null) {
                return false;
            }
            NetworkInfo networkinfo = manager.getActiveNetworkInfo();
            if (networkinfo == null || !networkinfo.isAvailable()) {
                return false;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            LogUtil.e(TAG, "hasNetwork e[" + e + "]");
        }
        return true;
    }

    /**
     * 是否在wifi环境下
     *
     * @param context
     * @return
     */
    public static boolean isWifiActive(Context context) {
        boolean result = false;
        try {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (null != manager) {
                NetworkInfo networkinfo = manager.getActiveNetworkInfo();
                if (networkinfo != null && (networkinfo.getType() == ConnectivityManager.TYPE_WIFI)) {
                    result = true;
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "isWifiActive e[" + e + "]");
        }
        LogUtil.i(TAG, "isWifiActive result[" + result + "]");
        return result;
    }

    /**
     * 是否在移动环境下，且已连接
     *
     * @param context
     * @return
     */
    public static boolean isMobileActive(Context context) {
        try {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            return info != null && (info.getType() == ConnectivityManager.TYPE_MOBILE)
                    && info.isConnected();
        } catch (Throwable e) {
            e.printStackTrace();
            LogUtil.e(TAG, "isMobileActive e[" + e + "]");
        }
        return false;
    }

    /**
     * 获取网络
     *
     * @param context
     * @return
     */
    public static String getExtraInfo(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nwInfo = connectivity.getActiveNetworkInfo();
        if (nwInfo == null) {
            return null;
        }
        Log.e("BabytreeTag", nwInfo.toString());
        String extraInfo = nwInfo.getExtraInfo();
        String typeName = nwInfo.getTypeName();
        if (typeName != null && typeName.equalsIgnoreCase("WIFI")) {
            return typeName;
        }
        return extraInfo;
    }

    /**
     * 返回网络信息
     *
     * @param context
     * @return
     */
    public static String getNetType(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nwInfo = connectivity.getActiveNetworkInfo();
            if (nwInfo == null) {
                return "unknow";
            }
            return nwInfo.toString();
        } catch (Exception ex) {
            return "unknow";
        }
    }

    /**
     * 获取运营商
     *
     * @param context
     * @return 0，无/其他；1，移动；2，联通；3，电信
     */
    public static int getOperator(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String IMSI = telephonyManager.getSubscriberId();
        if (!TextUtils.isEmpty(IMSI) && IMSI.length() >= 5) {
            switch (IMSI.substring(0, 5)) {
                case "46000":
                case "46002":
                case "46007": // 移动
                    return 1;
                case "46001":
                case "46006":
                    return 2; // 联通
                case "46003":
                case "46005":// 电信
                    return 3;
                default:
                    return 0;
            }
        }
        return 0;
    }

    /**
     * 枚举网络状态
     * NET_NO：没有网络
     * NET_2G:2g网络
     * NET_3G：3g网络
     * NET_4G：4g网络
     * NET_WIFI：wifi
     * NET_UNKNOWN：未知网络
     */
    public interface NetState {
        Integer NET_NO = 0;
        Integer NET_WIFI = 1;
        Integer NET_2G = 2;
        Integer NET_3G = 3;
        Integer NET_4G = 4;
        Integer NET_UNKNOWN = 5;
    }

    /**
     * 判断当前是否网络连接
     *
     * @param context
     * @return 状态码
     */
    public static int isConnected(Context context) {
        int stateCode = NetState.NET_NO;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni != null && ni.isConnectedOrConnecting()) {
                switch (ni.getType()) {
                    case ConnectivityManager.TYPE_WIFI:
                        stateCode = NetState.NET_WIFI;
                        break;
                    case ConnectivityManager.TYPE_MOBILE:
                        switch (ni.getSubtype()) {
                            case TelephonyManager.NETWORK_TYPE_GPRS: //联通2g
                            case TelephonyManager.NETWORK_TYPE_CDMA: //电信2g
                            case TelephonyManager.NETWORK_TYPE_EDGE: //移动2g
                            case TelephonyManager.NETWORK_TYPE_1xRTT:
                            case TelephonyManager.NETWORK_TYPE_IDEN:
                                stateCode = NetState.NET_2G;
                                break;
                            case TelephonyManager.NETWORK_TYPE_EVDO_A: //电信3g
                            case TelephonyManager.NETWORK_TYPE_UMTS:
                            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                            case TelephonyManager.NETWORK_TYPE_HSDPA:
                            case TelephonyManager.NETWORK_TYPE_HSUPA:
                            case TelephonyManager.NETWORK_TYPE_HSPA:
                            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                            case TelephonyManager.NETWORK_TYPE_EHRPD:
                            case TelephonyManager.NETWORK_TYPE_HSPAP:
                                stateCode = NetState.NET_3G;
                                break;
                            case TelephonyManager.NETWORK_TYPE_LTE:
                                stateCode = NetState.NET_4G;
                                break;
                            default:
                                stateCode = NetState.NET_UNKNOWN;
                        }
                        break;
                    default:
                        stateCode = NetState.NET_UNKNOWN;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return stateCode;
    }


    /**
     * 获取手机系统对应的SDK_INT
     *
     * @return
     */
    public static int getSDKINT() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取手机系统版本
     *
     * @return
     */
    public static String getVersionRelease() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取厂商
     *
     * @return
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 手机型号
     *
     * @return
     */
    public static String getPhoneModel() {
        return Build.MODEL;
    }

    public static String getOSVersionString() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 设置cookies
     *
     * @param context
     * @param url     需要设置的网页地址
     * @param cookie  内容
     */
    public static void setCookies(Context context, String url, String cookie, String cookieName) {
        if (cookie != null && !cookie.trim().equals("")) {
            CookieSyncManager.createInstance(context); // 得到同步cookie的对象
            CookieManager cookieManager = CookieManager.getInstance(); // 管理cookie的对象
            cookieManager.setCookie(url, encodeCookieKV(cookieName, cookie));
            // 设置cookie
            // cookieManager.setCookie(url, "NL=" + cookie);
            // cookieManager.setCookie(url, "domain=" + ".babytree.com");
            // cookieManager.setCookie(url, "version=" + "0");
            // cookieManager.setCookie(url, "path=" + "/");
//			 cookieManager.setCookie(url, "expiry=" + "86400*30");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                CookieSyncManager.getInstance().sync();
            } else {
                cookieManager.flush();
            }
        }
    }

    /**
     * 获取cookie信息
     *
     * @param context
     * @param url
     * @return
     */
    public static String getCookie(Context context, String url) {
        try {
            CookieSyncManager.createInstance(context); // 得到同步cookie的对象
            CookieManager cookieManager = CookieManager.getInstance(); // 管理cookie的对象
            // 设置cookie
            String cookie = cookieManager.getCookie(url);
            LogUtil.d(TAG, "请求地址url = " + url + " \n 对应的Encoder Cookie【NL=Value】 = " + cookie);
            return cookie;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    /**
     * 编码key value
     * <p/>
     * <b>Note:</b> 解决网站cookie验证【NL与L cookie】，严重延迟问题
     * <p/>
     * <p/>
     * 【Cookie问题--里程碑】
     */
    @SuppressWarnings("deprecation")
    private static String encodeCookieKV(String key, String value) {
        String encodeKey = URLEncoder.encode(key);// encode Key
        String encodeValue = URLEncoder.encode(value);// encode Value
        LogUtil.i(TAG, "encodeCookieKV encodeValue[" + encodeValue + "] value[" + value + "]");
        return encodeKey + "=" + encodeValue;
    }

    /**
     * 清除cookies
     *
     * @param context
     */
    public static void clearCookies(Context context) {
        CookieSyncManager.createInstance(context); // 得到同步cookie的对象
        CookieManager cookieManager = CookieManager.getInstance(); // 管理cookie的对象
        cookieManager.removeAllCookie();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().sync();
        } else {
            cookieManager.flush();
        }
    }

    /**
     * 获取raw 文件夹下 Uri
     *
     * @param context
     * @param key
     * @return
     */
    public static Uri getRawUri(Context context, String key) {
        if (TextUtils.isEmpty(key))
            return null;
        return getRawUri(context, getResbyName(context, "raw", key));
    }

    public static Uri getRawUri(Context context, int id) {
        if (id <= 0) return null;
        try {
            return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + id);
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * 获取资源id
     *
     * @param context
     * @param file    raw 、drawable ...
     * @param resName
     * @return
     */
    public static int getResbyName(Context context, String file, String resName) {
        try {
            return context.getResources().getIdentifier(resName, file, context.getPackageName());
        } catch (Throwable e) {
            return 0;
        }
    }

    /**
     * 设置html格式的颜色值
     *
     * @param text  需要设置的文本
     * @param color 需要设置的颜色：#ffffff
     * @return
     */
    public static String setHtmlColor(String text, String color) {
        return "<font color=\"" + color + "\">" + text + "</font>";
    }

    public static boolean isBiaoQing(String lastString) {
        if (lastString != null) {
            final String reg = "\\d+\\.{0,1}\\d*";
            String ziMu = "[abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ]";
            boolean isDigits = lastString.matches(reg);
            String regEx = "[\\u4e00-\\u9fa5]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(lastString);
            if (m.find() || isDigits == true || lastString.matches(ziMu) == true) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置html格式的下划线
     *
     * @param text 需要设置的文本
     * @return
     */
    public static String setHtmlBottomLine(String text) {
        return "<u\">" + text + "</u>";
    }

    /**
     * 判断是否为手机号
     *
     * @param phoneNum
     * @return
     */
    public static boolean checkPhoneNum(String phoneNum) {
        boolean flag = false;
        try {
            Pattern p = Pattern.compile("^((1))\\d{10}$");
            Matcher m = p.matcher(phoneNum);
            flag = m.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    public static boolean checkNumber(String num) {
        boolean flag = false;
        try {
            Pattern p = Pattern.compile("[0-9]*");
            Matcher m = p.matcher(num);
            flag = m.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 验证邮箱
     *
     * @param email
     * @return
     */
    private static final String EMAIL_REGULAR = "^([\\w\\.-]+)@([\\w\\.-]+)\\.([A-Za-z\\.]{2,6})$";

    public static boolean checkEmail(String email) {
        if (TextUtils.isEmpty(email) || !email.matches(EMAIL_REGULAR)) {
            return false;
        }
        return true;
    }

    private static final String PASSWORD_REGULAR = "(?!^\\d+$)(?!^[a-zA-Z]+$)(?!^[_]+$)\\w{8,24}";

    public static boolean checkPassword(String password) {
        if (TextUtils.isEmpty(password) || !password.matches(PASSWORD_REGULAR)) {
            return false;
        }
        return true;
    }

    /**
     * 获取应用安装时间(TS)秒数
     *
     * @return
     */
    public static long getAppInstallTime(Context context) {

        // if (pushMessage.serial_number <
        // BabytreeUtil.getAppInstallTime(getApplicationContext())) {
        // continue;
        // }

        long ret = System.currentTimeMillis() / 1000;

        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), 0);
            String appFile = appInfo.sourceDir;
            long installed = new File(appFile).lastModified(); // Ep
            if (installed != 0) {
                ret = installed / 1000;
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "getAppInstallTime e[" + e + "]");
        }

        return ret;
    }

    /**
     * 隐藏键盘
     *
     * @param context
     */
    public static void hideSoftInputKeyboard(Activity context) {
        try {
            ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                    context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            LogUtil.e(TAG, "hideSoftInputKeyboard e[" + e + "]");
        }
    }

    /**
     * 显示输入框
     *
     * @param editText
     */
    public static void showSoftKeyboardAt(EditText editText) {
        if (editText == null) return;
        try {
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.requestFocus();
            InputMethodManager m = (InputMethodManager) editText.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean startsWithIgnoreCase(String string, String prefix) {
        if ((null == string) || (null == prefix)) {
            return false;
        }
        if (string.length() < prefix.length()) {
            return false;
        }
        String temp = string.substring(0, prefix.length());
        return temp.equalsIgnoreCase(prefix);
    }

    public static boolean endsWithIgnoreCase(String string, String suffix) {
        if ((null == string) || (null == suffix)) {
            return false;
        }
        if (string.length() < suffix.length()) {
            return false;
        }
        String temp = string.substring(string.length() - suffix.length(), string.length());
        return temp.equalsIgnoreCase(suffix);
    }

    public static boolean containsWithIgnoreCase(String string, String sub) {
        boolean result = false;
        if ((null != string) && (null != sub)) {
            String temp1 = string.toLowerCase(Locale.getDefault());
            String temp2 = sub.toLowerCase(Locale.getDefault());
            result = temp1.contains(temp2);
        }

        return result;
    }

    /**
     * 隐藏输入法键盘
     *
     * @param context
     * @param editText
     */
    public static void hideInputMethod(Context context, EditText editText) {
        if (editText != null) {
            try {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            } catch (Exception e) {
                LogUtil.e(TAG, "hideInputMethod e[" + e + "]");
            }
        }
    }

    public static boolean isHoneycomb() {
        // Can use static final constants like HONEYCOMB, declared in later
        // versions
        // of the OS since they are inlined at compile time. This is guaranteed
        // behavior.
        return Build.VERSION.SDK_INT >= 11;
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static boolean isHoneycombTablet(Context context) {
        return isHoneycomb() && isTablet(context);
    }

    /**
     * 首页是否在运行
     *
     * @return
     * @author wangshuaibo
     */
    public static boolean isAppRunning(Context context) {
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            RunningTaskInfo runningTaskInfo = manager.getRunningTasks(1).get(0);
            LogUtil.d("pack:" + runningTaskInfo.topActivity.getPackageName() + " | " + context.getPackageName());
            return runningTaskInfo.topActivity.getPackageName().equalsIgnoreCase(context.getPackageName());
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 在某些经过简洁版本的Android系统中
     * RunningAppProcessInfo.importance的值一直为IMPORTANCE_BACKGROUND.
     * 记录是否为特殊这种系统
     */
    private static boolean mIsSpecialSystem = false;

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (!mIsSpecialSystem) {
            try {
                boolean isSpecial = true;
                String packageName = context.getPackageName();
                List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
                if (null != appProcesses) {
                    for (RunningAppProcessInfo appProcess : appProcesses) {
                        if (appProcess.processName.equals(packageName)) {
                            if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                                // 在onStop/onDestroy方法中调用时，正常appProcess.importance != RunningAppProcessInfo.IMPORTANCE_VISIBLE
                                KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                                return !keyguardManager.inKeyguardRestrictedInputMode();
                            }
                        }
                        if (isSpecial) {
                            if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                                isSpecial = false;
                            }
                        }
                    }
                    if (isSpecial) {
                        mIsSpecialSystem = true;
                        return !isApplicationBroughtToBackgroundByTask(context, activityManager);
                    }
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "isAppOnForeground e[" + e + "]");
            }
        } else {
            return !isApplicationBroughtToBackgroundByTask(context, activityManager);
        }
        return false;
    }

    /**
     * 判断当前应用程序是否处于后台，通过getRunningTasks的方式
     *
     * @return true 在后台; false 在前台
     */
    public static boolean isApplicationBroughtToBackgroundByTask(Context context, ActivityManager activityManager) {
        try {
            List<RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
            if (!tasks.isEmpty()) {
                ComponentName topActivity = tasks.get(0).topActivity;
                if (!topActivity.getPackageName().equals(context.getPackageName())) {
                    return true;
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "isApplicationBroughtToBackgroundByTask e[" + e + "]");
        }
        return false;
    }

    /**
     * 下载app
     */
    public static void downapp(Context context, String downURL) {
        try {
            Intent intent = null;
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(downURL));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取定位key
     *
     * @param context
     * @return
     */
    public static String getAK(Context context) {
        return getApplicationMetaData(context, "com.baidu.lbsapi.API_KEY");
    }

    /**
     * 获取IMEI
     *
     * @param context
     * @return
     */
    public static String getImei(Context context) {
        String result = null;
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (null != tm) {
                result = tm.getDeviceId();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取android id
     *
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        String result = null;
        try {
            result = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取build serial
     *
     * @param context
     * @return
     */
    public static String getBuildSerial(Context context) {
        String result = null;
        try {
            result = Build.SERIAL;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * server IP 地址
     *
     * @param host
     * @return
     */
    public static String getHostIPAddress(String host) {
        try {
            return InetAddress.getByName(host).getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * client IP 地址
     *
     * @return
     */
    public static String getLocalIPAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * provide default 0 to avoid NumberFormatException
     *
     * @param value integer in format of string
     * @return 0 or value as int
     */
    public static int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }

    public static int parseInt(String value, int defValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defValue;
        }
    }

    public static long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return 0;
        }
    }

    public static double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0;
        }
    }

    public static float parseFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            return 0;
        }
    }

    public static float parseFloat(String value, float defValue) {
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            return defValue;
        }
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;

        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Util.parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sbar;
    }

    /**
     * 判断class对应的Service是否开启
     *
     * @param context
     * @param className service 全称
     *                  eg： com.babytree.apps.pregnancy.music.MusicService
     *                  <p/>
     *                  获取当前手机运行的前200个服务
     * @return
     */
    public static boolean isServiceRunning(Context context, String className) {
        boolean isRunning = false;
        if (!TextUtils.isEmpty(className)) {
            try {
                ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(200);
                if (serviceList == null || !(serviceList.size() > 0)) {
                    return false;
                }
                for (int i = 0; i < serviceList.size(); i++) {
                    ComponentName service = serviceList.get(i).service;
                    if (service != null && className.equals(service.getClassName())) {
                        isRunning = true;
                        break;
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return isRunning;
    }

    /**
     * 获取Asset目录下的Json文件
     *
     * @param context
     * @param fileName 文件名
     * @return
     */
    public static String getAssetJsonByName(Context context, String fileName) {
        StringBuilder sb = new StringBuilder();
        BufferedReader bf = null;
        try {
            AssetManager am = context.getAssets();
            bf = new BufferedReader(new InputStreamReader(am.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                sb.append(line);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (bf != null) {
                    bf.close();
                    bf = null;
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return sb.toString().length() > 0 ? sb.toString() : null;
    }

    /**
     * 获取指定app VersionCode
     */
    public static int getAppVersionCode(Context context, String packageName) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS);
            return info.versionCode;
        } catch (Throwable e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 指定Apk是否安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAppInstall(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        return intent != null;
    }

    /**
     * 判定输入汉字
     *
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    /**
     * 检测String是否全是中文
     *
     * @param name
     * @return
     */
    public static boolean checkNameChese(String name) {
        boolean res = true;
        try {
            char[] cTemp = name.toCharArray();
            for (int i = 0; i < name.length(); i++) {
                if (!isChinese(cTemp[i])) {
                    res = false;
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return res;
    }
}