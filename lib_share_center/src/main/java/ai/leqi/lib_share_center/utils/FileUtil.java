package ai.leqi.lib_share_center.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {

    public static boolean copyFile(String fromFilePath, String toFilePath) {
        if (TextUtils.isEmpty(fromFilePath) || TextUtils.isEmpty(toFilePath)) {
            return false;
        }
        File fromFile = new File(fromFilePath);
        if (!fromFile.exists()) {
            return false;
        }
        if (!fromFile.canRead()) {
            return false;
        }
        //
        File toFile = new File(toFilePath);
        if (!toFile.exists()) {
            deleteFiles(toFile.getPath());
        }
        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }
        try {
            InputStream inStream = new FileInputStream(fromFile);
            OutputStream outStream = new FileOutputStream(toFile);
            byte[] bytes = new byte[1024];
            int i = 0;
            // 将内容写到新文件当中
            while ((i = inStream.read(bytes)) > 0) {
                outStream.write(bytes, 0, i);
            }
            inStream.close();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static boolean deleteFiles(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }
        // 如果是文件
        if (file.isFile()) {
            return file.delete();
        } else
            // 如果是文件夹
            if (file.isDirectory()) {
                File[] childFiles = file.listFiles();
                // 文件夹没有内容,删除文件夹
                if (childFiles == null || childFiles.length == 0) {
                    return file.delete();
                }
                // 删除文件夹内容
                boolean reslut = true;
                for (File item : file.listFiles()) {
                    reslut = reslut && item.delete();
                }
                // 删除文件夹
                return reslut && file.delete();
            }
        return false;
    }

}
