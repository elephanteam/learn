package com.limit.learn.util;

import android.content.Context;
import android.os.Environment;

/**
 * Folder control class
 */
public class SDCardCtrl {

    /**
     * 隐藏文件
     */
    public static String HIDDEN_ROOT_PATH = "/.learn";

    /**
     * 普通文件路径
     * */
    public static String ROOT_PATH = "/learn";



    public static String DOWNLOAD = "/download";

    /**
     * ERROR_LOG_PATH
     */
    public static String ERROR_LOG_PATH = "/errorLog";

    /**
     * OFFLINE
     */
    public static String OFFLINE = "/offLine";

    /**
     * OFFLINE
     */
    public static String FILE = "/file";



    public static String VIDEO = "/video";


    public static String AUDIO = "/audio";

    /**
     * FACE
     */
    public static String FACE = "/face";



    public static String getErrorLogPath() {
        return ERROR_LOG_PATH;
    }


    /**
     * @return FILE
     */
    public static String getFilePath() {
        return FILE;
    }

    /**
     * @return AUDIO
     */
    public static String getAudioPath() {
        return AUDIO;
    }


    /**
     * @return Is or not exist SD card
     */
    public static boolean sdCardIsExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * <Build data file for this application >
     */
    public static void initPath(Context context) {
        String ROOT;
        if (sdCardIsExist()) {
            ROOT = Environment.getExternalStorageDirectory().getPath();
        } else {
            ROOT = "/mnt/sdcard";
        }
        if (HIDDEN_ROOT_PATH.equals("/.learn")) {
            //根目录
            HIDDEN_ROOT_PATH = ROOT + HIDDEN_ROOT_PATH;
            ROOT_PATH = ROOT + ROOT_PATH;

            //隐藏文件目录
            ERROR_LOG_PATH = HIDDEN_ROOT_PATH + ERROR_LOG_PATH;
            OFFLINE = HIDDEN_ROOT_PATH + OFFLINE;
            FILE = HIDDEN_ROOT_PATH + FILE;
            VIDEO = HIDDEN_ROOT_PATH + VIDEO;
            AUDIO = HIDDEN_ROOT_PATH + AUDIO;
            FACE = HIDDEN_ROOT_PATH + FACE;
            DOWNLOAD = ROOT_PATH + DOWNLOAD;
        }
        SDFileUtils.getInstance().createDir(HIDDEN_ROOT_PATH);
        SDFileUtils.getInstance().createDir(ROOT_PATH);
        SDFileUtils.getInstance().createDir(ERROR_LOG_PATH);
        SDFileUtils.getInstance().createDir(OFFLINE);
        SDFileUtils.getInstance().createDir(VIDEO);
        SDFileUtils.getInstance().createDir(AUDIO);
        SDFileUtils.getInstance().createDir(FACE);
    }

}
