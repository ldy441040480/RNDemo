package com.ldy.werty.reactnative.update.patch;

/**
 * Created by lidongyang on 2016/8/9.
 */
public class BsdiffPatch {

    static {
        System.loadLibrary("rnupdate");
    }

    public static native byte[] bsdiffPatch(byte[] origin, byte[] patch);
}
