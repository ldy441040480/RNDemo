package com.ldy.werty.reactnative.update;

import android.os.Environment;
import android.text.TextUtils;

import com.ldy.werty.BaseApplication;
import com.ldy.werty.reactnative.RNKeys;
import com.ldy.werty.reactnative.RNVersion;
import com.ldy.werty.util.DataUtil;
import com.ldy.werty.util.FileUtil;

import java.io.File;

/**
 * react-native 文件版本管理
 *
 * 文件目录结构：
 *
 * file://sdcard/babytree/care/.react_native/
 *     bundle/
 *          0.1.0 (version)
 *              index.jsbundle
 *              drawable-mdpi/
 *                  ...
 *
 *          0.2.0 (version)
 *              index.jsbundle
 *              drawable-mdpi/
 *                  ...
 *
 *     patch/
 *          patch.zip
 *
 *
 * Created by ldy on 2016/2/23.
 */
public final class RNFile {

    private static final String TAG = RNKeys.DEBUG_TAG;

    public static final String PATH_SD = Environment.getExternalStorageDirectory().getAbsolutePath();

    /** react-native 本地路径 */
    public static final String PATH_REACT = PATH_SD +
            File.separator + "com.ldy.werty" +
            File.separator + "react_native" +
            File.separator;

    /** 本地 bundle目录 */
    public static final String PATH_BUNDLE = PATH_REACT + "bundle" + File.separator;

    /** 本地 patch 压缩目录 */
    public static final String PATH_PATCH = PATH_REACT + "patch" + File.separator;

    /** pathch 增量包名称 */
    public static final String PATCH_ZIP_NAME = "patch.zip";

    /** 当前 version */
    public static final String VERSION_NAME = "react_native_version";

    /** 上一个 lastVersion */
    public static final String LAST_VERSION_NAME = "react_native_lastVersion";

    /** react-native assets 路径 */
    public static final String PATH_ASSETS_REACT = "react/" + RNVersion.REACT_VERSION;

    /** 本地bundle Md5  */
    public static final String KEY_BUNDLE_MD5 = "react_native_key_bundle_md5";

    /**
     * 获取当前 version 版本
     *
     * @return
     */
    public static String getVersion() {
        String version = getLocalVersion();
        return isEmpty(version) ? RNVersion.REACT_VERSION : version;
    }

    /**
     * 获取本地 version 版本
     *
     * @return
     */
    public static String getLocalVersion() {
        return DataUtil.getStringValue(BaseApplication.context, VERSION_NAME);
    }

    /**
     * 获取本地上一个版本 version
     *
     * @return
     */
    public static String getLocalLastVersion() {
        return DataUtil.getStringValue(BaseApplication.context, LAST_VERSION_NAME);
    }

    /**
     * 写 version 版本到本地
     *
     * @param version
     */
    public static void setLocalVersion(String version) {
        DataUtil.setValue(BaseApplication.context, VERSION_NAME, version);
    }

    /**
     * 写 version 版本到本地
     *
     * @param version
     * @param lastVersion
     */
    public static void setLocalVersion(String version, String lastVersion) {
        deleteLastBundleDir();
        deleteLastVersion();
        DataUtil.setValue(BaseApplication.context, VERSION_NAME, version);
        DataUtil.setValue(BaseApplication.context, LAST_VERSION_NAME, lastVersion);
    }

    public static void setBundleMd5ByVersion(String version, String md5) {
        DataUtil.setValue(BaseApplication.context, KEY_BUNDLE_MD5 + version, md5);
    }

    public static String getBundleMd5ByVersion(String version) {
        return DataUtil.getStringValue(BaseApplication.context, KEY_BUNDLE_MD5 + version, RNVersion.REACT_BUNDLE_MD5);
    }

    public static void removeBundleMd5ByVersion(String version) {
        DataUtil.removeKey(BaseApplication.context, KEY_BUNDLE_MD5 + version);
    }

    /**
     * 版本 value1 是否大于等于 value2
     *
     * @param value1
     * @param value2
     * @return
     */
    public static boolean isVersionValid(String value1, String value2) {
        if (isEmpty(value1) || isEmpty(value2))
            return false;

        try {
            int ver1 = parseVersion(value1);
            int ver2 = parseVersion(value2);
            if (ver1 > 0 && ver2 > 0) {
                return ver1 >= ver2;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 解析 version
     *
     * @param version  "1.2.3"
     * @return      1.2.3  -->  0001 0002 0003
     */
    public static int parseVersion(String version) {
        try {
            String[] verArr = version.split("\\.");
            version = addPrefix(verArr[0], 4, '0')
                    + addPrefix(verArr[1], 4, '0')
                    + addPrefix(verArr[2], 4, '0');
            return Integer.parseInt(version);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 添加前缀
     *
     * @param oriStr
     * @param len
     * @param c
     * @return
     */
    public static String addPrefix(String oriStr, int len, char c) {
        int length = oriStr.length();
        if (length < len) {
            for (int i = 0; i < len - length; i++) {
                oriStr = c + oriStr;
            }
        }
        return oriStr;
    }

    /**
     * 删除 Version文件
     */
    public static void deleteVersion() {
        DataUtil.removeKey(BaseApplication.context, VERSION_NAME);
    }

    /**
     * 删除 lastVersion文件
     */
    public static void deleteLastVersion() {
        DataUtil.removeKey(BaseApplication.context, LAST_VERSION_NAME);
    }

    /**
     * 指定version 是否与本地 version相等
     *
     * @param version
     * @return
     */
    public static boolean isEqualLocalVersion(String version) {
        return !isEmpty(version) && !isEmpty(getLocalVersion()) && version.equals(getLocalVersion());
    }

    /**
     * 指定version 是否与本地 lastVersion相等
     *
     * @param version
     * @return
     */
    public static boolean isEqualLocalLastVersion(String version) {
        return !isEmpty(version) && !isEmpty(getLocalLastVersion()) && version.equals(getLocalLastVersion());
    }

    /**
     * 本地 bundle 是否有效
     *
     *      1.获取 version 并检测有效性
     *              有效：返回
     *              无效：删除该version 对应的bundle目录
     *
     *      2.获取 lastVersion 并检测有效性
     *              有效：设置 lastVersion 为 version,删除lastVersion 返回
     *              无效：清空本地 bundle 目录
     * @return
     */
    public static boolean isLocalBundleValid() {

        String version = getLocalVersion();
        if (isLocalBundleValid(version)) {
            return true;
        }

        deleteBundleDir(version);
        deleteVersion();
        removeBundleMd5ByVersion(version);

        version = getLocalLastVersion();
        if (isLocalBundleValid(version)) {
            setLocalVersion(version);
            deleteLastVersion();
            return true;
        }
        removeBundleMd5ByVersion(version);
        clearBundleDir();
        return false;
    }

    /**
     * 本地指定 bundle 是否有效
     *
     * 检验
     * 1.file://sdcard/babytree/react_native/bundle/
     * 2.file://sdcard/babytree/react_native/bundle/version/
     * 3.file://sdcard/babytree/react_native/bundle/0.1.0(version)/index.android.jsbundle
     * 4.version 版本是否大于等于 离线包version版本
     *
     * @return
     */
    public static boolean isLocalBundleValid(String version) {

        if (isEmpty(version))
            return false;

        if (!FileUtil.isFolderExist(PATH_BUNDLE))
            return false;

        if (!isEqualLocalVersion(version)
                && !isEqualLocalLastVersion(version))
            return false;

        if (!FileUtil.isFileExist(getJsBundlePath(version)))
            return false;

        if (!isVersionValid(version, RNVersion.REACT_VERSION))
            return false;

        if (!RNBundle.bundleMd5Verify(getBundleMd5ByVersion(version), version)) {
            return false;
        }

        return true;
    }

    /**
     * 根据版本号获取 Bundle 目录
     *
     * @param version
     * @return
     */
    public static String getBundleDir(String version) {
        return PATH_BUNDLE + version;
    }

    /**
     * 根据版本号删除 bundle 目录
     *
     * ram version
     */
    public static void deleteBundleDir(String version) {
        FileUtil.deleteFile(getBundleDir(version));
    }

    /**
     * 删除上一个版本 bundle 目录
     */
    public static void deleteLastBundleDir() {
        deleteBundleDir(getLocalLastVersion());
    }

    /**
     * 清空 bundle 目录
     */
    public static void clearBundleDir() {
        FileUtil.deleteFile(PATH_BUNDLE);
        RNFile.deleteVersion();
        RNFile.deleteLastVersion();
    }

    /**
     * 获取 index.android.jsbundle 绝对路径
     *
     * @return
     */
    public static String getJsBundlePath() {
        if (isLocalBundleValid()) {
            return getJsBundlePath(getLocalVersion());
        }
        return null;
    }


    public static String getJsBundlePath(String version) {
        return getBundleDir(version) + File.separator + RNKeys.Default.BUNDLE_NAME;
    }

    /**
     * 获取 jsbundle.patch 绝对路径
     *
     * @param version
     * @return
     */
    public static String getPatchPath(String version) {
        return getBundleDir(version) + File.separator + RNKeys.Default.PATCH_NAME;
    }

    /**
     * 是否 jsbundle.patch 文件存在
     *
     * @param version
     * @return
     */
    public static boolean isPatchFileExist(String version) {
        return FileUtil.isFileExist(getPatchPath(version));
    }

    /**
     * 清空： file://sdcard/babytree/react_native/patch/ 目录
     */
    public static void clearPatchDir() {
        FileUtil.deleteFile(PATH_PATCH);
    }

    public static boolean isEmpty(String value) {
        return TextUtils.isEmpty(value);
    }
}
