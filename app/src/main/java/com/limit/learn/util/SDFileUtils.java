package com.limit.learn.util;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.Formatter;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * sd卡管理
 */
public class SDFileUtils {

    private static SDFileUtils sdFile = null;

    public static synchronized SDFileUtils getInstance() {
        if (sdFile == null) {
            sdFile = new SDFileUtils();
        }
        return sdFile;
    }

    private SDFileUtils() {
        String basePath = "/.learn";
        createDir(getSDPath() + basePath);
    }

    /**
     * sd卡是否存在
     */
    public static boolean SDCardIsOk() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取sd卡路径
     */
    public String getSDPath() {
        // To determine whether a sd card
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdDir = Environment.getExternalStorageDirectory();// Access to the root directory
            return sdDir.getPath();
        }
        return "/mnt/sdcard";
    }

    /**
     * 创建文件夹
     */
    public void createDir(String dirName) {
        File destDir = new File(dirName);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
    }

    /**
     * 获取sd卡路径
     */
    public String getSDCardPath() {
        return Environment.getExternalStorageDirectory() + "/";
    }

    /**
     * 创建文件夹
     * */
    public void mkDir(File file) {
        if (file == null) return;
        if (file.getParentFile().exists()) {
            file.mkdir();
        } else {
            mkDir(file.getParentFile());
            file.mkdir();
        }
    }

    /**
     * 创建文件
     */
    public File creatSDFile(String fileName) {
        File file = new File(getSDCardPath() + fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 创建文件
     */
    public File createSDDir(String dirName) {
        File dir = new File(getSDCardPath() + dirName);
        dir.mkdir();
        return dir;
    }

    /**
     * 检测文件是否存在
     */
    public boolean isFileExit(String fileName) {
        File file = new File(getSDCardPath() + fileName);
        return file.exists();
    }

    /**
     * 检测文件是否存在
     */
    public boolean isFileExist(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    /**
     * 写入文件到sd卡
     */
    public File writeFileToSDCard(String path, String fileName,InputStream input) {
        File file = null;
        OutputStream ops = null;
        try {
            createSDDir(path);
            file = creatSDFile(path + fileName);
            ops = new FileOutputStream(file);
            byte[] buffer = new byte[4 * 1024];
            while ((input.read(buffer) != -1)) {
                ops.write(buffer);
            }
            ops.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != ops) {
                    ops.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }


    /**
     * 写入文件到sd
     */
    public void getFile(byte[] bfile, String imgPathTemp, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        createDir(imgPathTemp);
        File file = new File(imgPathTemp, fileName);// Save the file
        try {
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * byte转文件
     */
    public File ByteToFile(byte[] datas, String imgPathTemp, String fileName) {
        createDir(imgPathTemp);
        File file = new File(imgPathTemp, fileName);// Save the file
        try {
            if (!file.exists() && !file.isDirectory()) {
                // Can be judged by file name, here if local have this picture
                FileOutputStream fos = new FileOutputStream(file);
                ByteArrayInputStream bais = new ByteArrayInputStream(datas);
                int data = bais.read();
                while (data != -1) {
                    fos.write(data);
                    data = bais.read();
                }
                fos.close();
                bais.close();
            }
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除文件
     */
    public void deleteFile(String path) {
        File file = new File(path);
        file.delete();
    }

    /**
     * 获取系统可用内存
     * */
    public String getSystemAvaialbeMemorySize(Context ct) {
        ActivityManager mActivityManager = (ActivityManager) ct.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo memoryInfo = new MemoryInfo();
        mActivityManager.getMemoryInfo(memoryInfo);
        long memSize = memoryInfo.availMem;
        String availMemStr = formateFileSize(memSize, ct);
        return availMemStr;
    }

    /**
     * 格式化
     * */
    private String formateFileSize(long size, Context ct) {
        return Formatter.formatFileSize(ct, size);
    }

    /**
     * 删除文件
     * @param file
     */
    public static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            file.delete();
        }
    }

    /**
     * copy文件
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {  //File exists
                InputStream inStream = new FileInputStream(oldPath);  //Read the original file
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存crash文件
     */
    public static String saveCrashInfoToFile(String exceptionMsg) {
        if (TextUtils.isEmpty(exceptionMsg)) {
            return "";
        }
        String errorlog = SDCardCtrl.getErrorLogPath();
        FileWriter fw = null;
        PrintWriter pw = null;
        File logFile = null;
        try {
            StringBuilder logSb = new StringBuilder();
            logSb.append("crashlog");
            logSb.append("(");
            logSb.append(DateUtil.getSimpleDate());
            logSb.append(")");
            logSb.append(".txt");
            logFile = new File(errorlog, logSb.toString());
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            fw = new FileWriter(logFile, true);
            pw = new PrintWriter(fw);
            pw.write(exceptionMsg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.flush();
                pw.close();
            }
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return logFile == null ? "" : logFile.getAbsolutePath();
    }


}
