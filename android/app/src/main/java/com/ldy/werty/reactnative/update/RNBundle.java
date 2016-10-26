package com.ldy.werty.reactnative.update;

import android.text.TextUtils;

import com.ldy.werty.reactnative.RNKeys;
import com.ldy.werty.reactnative.RNVersion;
import com.ldy.werty.reactnative.update.patch.BsdiffPatch;
import com.ldy.werty.util.FileUtil;
import com.ldy.werty.util.LogUtil;
import com.ldy.werty.util.Md5Util;

import java.io.File;
import java.io.InputStream;

/**
 * react-native bundle管理
 *      1.离线包初始化
 *      2.增量更新
 *
 * Created by ldy on 2016/2/22.
 */
public final class RNBundle {

    private static final String TAG = RNKeys.DEBUG_TAG;

    /**
     * 增量更新（jsbundle.patch）; 全量更新（index.jsbundle）
     *
     *         1.判断本地bundle是否有效
     *                      无效： 离线包初始化
     *
     *         2.判断本地 version是否大于等于 将更新的version
     *                      是：不更新
     *
     *         3.将下载的patch增量包下载到本地
     *                      下载失败：不更新
     *
     *         4.md5校验，校验patch增量包的md5值是否与服务器返回一致
     *                       不一致：不更新并删除patch增量包
     *
     *         5.拷贝上一个版本本地bundle目录下所有文件 到新的bundle目录
     *
     *         6.解压增量patch包到新的bundle目录下
     *
     *         7.检测文件jsbundle.patch是否存在
     *                  (1)不存在：则为全量更新，
     *                  (2) 存在： 通过patch算法，生成新的index.jsbundle文件
     *                      上一个版本 index.jsbundle + patch
     *                      >>>增量patch集合长度不能过大，diff_match_patch算法待优化，
     *                         消耗过多cpu，会导致页面卡顿<<<
     *
     *
     *         8.md5校验，校验新的  index.jsbundle 的md5值是否与服务器返回一致
     *                       不一致：不更新并删除新的bundle目录
     *                       一致：生成新的version文件
     *
     *         9.校验本地新的bundle目录是否有效
     *                       有效：更新成功  删除上一个版本bundle目录
     *                       无效：更新失败  删除新的bundle目录并回退version文件
     *
     *
     * @param inputStream
     *              增量资源
     * @param version
     *              当前版本
     * @param bundleMd5
     *              jsbundle文件的md5
     */
    public static boolean updateJsBundle(InputStream inputStream, String version, String bundleMd5, String patchMd5) {
        if (inputStream == null || isEmpty(version) || isEmpty(bundleMd5)) return false;

        try {

            init();

            if (!RNFile.isLocalBundleValid()) {
                LogUtil.i(TAG, "updateJsBundle !isLocalBundleValid");
                return false;
            }

            if (RNFile.isVersionValid(RNFile.getVersion(), version)) {
                LogUtil.i(TAG, "updateJsBundle isVersionValid");
                return false;
            }

            if (!writePatch2SDCard(inputStream)) {
                RNFile.clearPatchDir();
                LogUtil.i(TAG, "updateJsBundle writePatch2SDCard [ failure ]");
                return false;
            }

            if (!patchMd5PatchVerify(patchMd5)) {
                RNFile.clearPatchDir();
                LogUtil.i(TAG, "updateJsBundle patchMd5PatchVerify [ failure ]");
                return false;
            }

            if (!copyBundleDir(version)) {
                RNFile.clearPatchDir();
                RNFile.deleteBundleDir(version);
                LogUtil.i(TAG, "updateJsBundle copyBundleDir [ failure ]");
                return false;
            }

            if (!unZipPatch2Bundle(version)) {
                RNFile.clearPatchDir();
                RNFile.deleteBundleDir(version);
                LogUtil.i(TAG, "updateJsBundle unZipPatch2Bundle [ failure ]");
                return false;
            }

            RNFile.clearPatchDir();

            if (RNFile.isPatchFileExist(version) && !patchApplyBundle(version)) {
                RNFile.deleteBundleDir(version);
                LogUtil.i(TAG, "updateJsBundle patchApplyBundle [ failure ]");
                return false;
            }

            if (!bundleMd5Verify(bundleMd5, version)) {
                RNFile.deleteBundleDir(version);
                LogUtil.i(TAG, "updateJsBundle bundleMd5Verify [ failure ]");
                return false;
            }

            RNFile.removeBundleMd5ByVersion(RNFile.getLocalLastVersion());
            RNFile.setLocalVersion(version, RNFile.getVersion());
            RNFile.setBundleMd5ByVersion(version, bundleMd5);

            if (!RNFile.isLocalBundleValid(version)) {
                RNFile.deleteBundleDir(version);
                RNFile.setLocalVersion(RNFile.getLocalLastVersion());
                RNFile.deleteLastVersion();
                RNFile.removeBundleMd5ByVersion(version);
                LogUtil.i(TAG, "updateJsBundle isLocalBundleValid [ failure ]");
                return false;
            }

            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            RNFile.clearPatchDir();
            RNFile.deleteBundleDir(version);
            LogUtil.i(TAG, "updateJsBundle  [" + e + "]");
        }

        return false;
    }

    /**
     * 离线包初始化
     *
     *      1.检测本地是否存在 bundle 以及有效性，无效继续
     *      2.清空本地react_native目录
     *      3.从 assets目录解压 bundle 到本地
     *      4.校验解压到本地的bundle文件md5是否正确
     *      5.记录当前版本version到本地
     */
    public static void init() {
        if (!RNFile.isLocalBundleValid()) {
            RNFile.clearBundleDir();
            RNFile.clearPatchDir();
            if (copyBundle2SD()) {
                if (bundleMd5Verify(RNVersion.REACT_BUNDLE_MD5, RNVersion.REACT_VERSION)) {
                    RNFile.setLocalVersion(RNVersion.REACT_VERSION);
                    RNFile.setBundleMd5ByVersion(RNVersion.REACT_VERSION, RNVersion.REACT_BUNDLE_MD5);
                    LogUtil.i(TAG, "init bundleMd5Verify [ success ]");
                } else {
                    RNFile.clearBundleDir();
                    LogUtil.i(TAG, "init bundleMd5Verify [ failure ]");
                }
            } else {
                RNFile.clearBundleDir();
            }
        }
    }

    /**
     * 复制 assets目录下 bundle到本地
     */
    private static boolean copyBundle2SD() {
        try {
            FileUtil.copyAssetsFiles(RNFile.PATH_ASSETS_REACT, RNFile.getBundleDir(RNVersion.REACT_VERSION));
            LogUtil.i(TAG, "copyJsBundle2SD [ success ]");
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            FileUtil.deleteFile(RNFile.PATH_BUNDLE);
            LogUtil.i(TAG, "copyJsBundle2SD [" + e + "]");
            return false;
        }
    }

    /**
     * 比较版本是否相同
     *
     * @param version1
     * @param version2
     * @return
     */
    private static boolean isVersionEquals(String version1, String version2) {
        if (isEmpty(version1) || isEmpty(version2))
            return false;
        LogUtil.i(TAG, "isVersionEquals version1=[" + version1 + "]; version2=[" + version2 + "]");
        return version1.equals(version2);
    }

    /**
     * 写 Patch 增量文件到本地
     *
     * @param inputStream
     *               增量资源
     * @return
     */
    private static boolean writePatch2SDCard(InputStream inputStream) {
        if (inputStream == null)
            return false;

        try {
            FileUtil.writeInputStream(inputStream, RNFile.PATH_PATCH, RNFile.PATCH_ZIP_NAME);
            LogUtil.i(TAG, "writePatch2SDCard [ success ]");
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            LogUtil.i(TAG, "writePatch2SDCard [" + e + "]");
        }
        return false;
    }

    /**
     * 复制上一个版本 bundle到新文件夹
     *
     * @param version
     */
    private static boolean copyBundleDir(String version) {
        try {
            FileUtil.copyFolder(RNFile.getBundleDir(RNFile.getVersion()), RNFile.getBundleDir(version));
            LogUtil.i(TAG, "copyBundleDir [ success ]");
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            LogUtil.i(TAG, "copyBundleDir [" + e + "]");
        }
        return false;
    }

    /**
     * 解压增量包到指定目录    ../bundle/0.1.0 (version)
     *
     * @param version
     *            当前版本
     * @return
     */
    private static boolean unZipPatch2Bundle(String version) {
        if (isEmpty(version))
            return false;
        try {
            FileUtil.unZip(RNFile.PATH_PATCH + RNFile.PATCH_ZIP_NAME, RNFile.getBundleDir(version));
            LogUtil.i(TAG, "unZipPatch2Bundle [ success ]");
            return true;
        } catch (Throwable e) {
            LogUtil.i(TAG, "unZipPatch2Bundle [" + e + "]");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * patch文件与老版本bundle生成新的bundle
     *
     * @return 新的 bundle ()
     */
    public static boolean patchApplyBundle(String version) {
        try {
            byte[] origin = FileUtil.readerStreamByte(RNFile.getJsBundlePath(version));
            byte[] patch = FileUtil.readerStreamByte(RNFile.getPatchPath(version));

            if (origin == null || patch == null) {
                LogUtil.i(TAG, "patchApplyBundle [ origin == null || patch == null ]");
                return false;
            }

            byte[] bytes = BsdiffPatch.bsdiffPatch(origin, patch);

            FileUtil.writerStreamByte(RNFile.getJsBundlePath(version), bytes);
            FileUtil.deleteFile(RNFile.getPatchPath(version));
            LogUtil.i(TAG, "patchApplyBundle [ success ]");
            return true;
        } catch (Throwable e) {
            FileUtil.deleteFile(RNFile.getPatchPath(version));
            e.printStackTrace();
            LogUtil.i(TAG, "patchApplyBundle [" + e + "]");
        }
        return false;
    }

    /**
     * patchMd5 校验
     *
     * @param patchMd5
     * @return
     */
    private static boolean patchMd5PatchVerify(String patchMd5) {
        String newPatchMd5 = Md5Util.md5(new File(RNFile.PATH_PATCH + RNFile.PATCH_ZIP_NAME));
        LogUtil.i(TAG, "patchMd5PatchVerify patchMd5=[" + patchMd5 + "]; newPatchMd5=[" + newPatchMd5 + "]");
        return md5Verify(patchMd5, newPatchMd5);
    }

    /**
     * bundleMd5 校验
     *
     * @param bundleMd5
     * @return
     */
    public static boolean bundleMd5Verify(String bundleMd5, String version) {
        String newBundleMd5 = Md5Util.md5(new File(RNFile.getJsBundlePath(version)));
        LogUtil.i(TAG, "bundleMd5Verify bundleMd5=[" + bundleMd5 + "]; newBundleMd5=[" + newBundleMd5 + "]");
        return md5Verify(bundleMd5, newBundleMd5);
    }

    /**
     * md5 校验
     *
     * @return
     */
    private static boolean md5Verify(String oldMd5, String newMd5) {
        if (isEmpty(oldMd5) || isEmpty(newMd5)) {
            return false;
        }
        return oldMd5.equals(newMd5);
    }

    private static boolean isEmpty(String value) {
        return TextUtils.isEmpty(value);
    }

}