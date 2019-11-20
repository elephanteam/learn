package com.limit.learn.util;

import android.util.Log;

import com.limit.learn.base.BaseModuleConfig;
import com.orhanobut.logger.Logger;

/**
 * 日志打印工具类
 */
public class LogUtils {

    public static void v(String msg) {
        if (!BaseModuleConfig.isDebug) return;
        Logger.v(msg);
    }

    public static void v(String tag, String msg) {
        if (!BaseModuleConfig.isDebug) return;
        Logger.t(tag).v(msg);
    }

    public static void d(String msg) {
        if (!BaseModuleConfig.isDebug) return;
        Logger.d(msg);
    }

    public static void d(String tag, String msg) {
        if (!BaseModuleConfig.isDebug) return;
        Logger.t(tag).d(msg);
    }

    public static void i(String msg) {
        if (!BaseModuleConfig.isDebug) return;
        Logger.i(msg);
    }

    public static void i(String tag, String msg) {
        if (!BaseModuleConfig.isDebug) return;
        Logger.t(tag).i(msg);
    }

    public static void e(String msg) {
        if (!BaseModuleConfig.isDebug) return;
        Logger.e(msg);
    }

    public static void e(String tag, String msg) {
        if (!BaseModuleConfig.isDebug) return;
        Logger.t(tag).e(msg);
    }

    public static void json(String json) {
        if (!BaseModuleConfig.isDebug) return;
        Logger.json(json);
    }

    public static void json(String tag, String json) {
        if (!BaseModuleConfig.isDebug) return;
        Logger.t(tag).json(json);
    }

    /**
     * 系统日志------------------------------------------------------------------------------------
     */
    public static void sys_d(String msg) {
        if (!BaseModuleConfig.isDebug) return;
        Log.d("MY_ZH_LOG", msg);
    }

}
