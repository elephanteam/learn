package com.limit.learn.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.multidex.MultiDexApplication;

import com.limit.learn.BuildConfig;
import com.limit.learn.MainActivity;
import com.limit.learn.entity.UserBean;
import com.limit.learn.greendao.DaoMaster;
import com.limit.learn.greendao.DaoSession;


/**
 * Application 基类
 */
public class BaseApplication extends MultiDexApplication {

    private static BaseApplication app;

    public static UserBean myInfo;

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        myInfo = new UserBean();
        // 设置 Debug 模式
        BaseApplication.setDebugStatus(BuildConfig.APP_DEBUG);
        // 设置 请求 域名
        BaseApplication.setAppHost(BuildConfig.APP_URL);
        // 设置主页面标记
        BaseApplication.setHomeActivityName(MainActivity.class.getSimpleName());
        // 对接服务器 API 版本号
        BaseApplication.setServerVersionName(BuildConfig.SERVER_VERSION_NAME);
        initGreenDao();
    }

    /**
     * 获取 Application
     *
     * @return
     */
    public static BaseApplication getInstance() {
        return app;
    }

    /**
     * 获取 Context
     *
     * @return
     */
    public static Context getContext() {
        return app.getApplicationContext();
    }

    /**
     * 数据库初始化
     */
    private void initGreenDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "zero.db");
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    /**
     * 设置调试状态
     *
     * @param appDebug
     */
    public static void setDebugStatus(boolean appDebug) {
        BaseModuleConfig.isDebug = appDebug;
    }

    /**
     * 设置服务器域名地址
     *
     * @param appHost
     */
    public static void setAppHost(String appHost) {
        BaseModuleConfig.APP_HOST = appHost;
    }

    /**
     * 设置主 Activity 名称
     *
     * @param homeName
     */
    public static void setHomeActivityName(String homeName) {
        BaseModuleConfig.HOME_ACT_NAME = homeName;
    }

    /**
     * 设置对接服务器 API 版本号
     *
     * @param versionCode
     */
    public static void setServerVersionName(String versionCode) {
        BaseModuleConfig.SERVER_CODE_NAME = versionCode;
    }

}
