/*
 * 6月7日：优化IO操作
 */
package cn.yahoo.asxhl2007.gamedownloader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;

import android.content.Context;

/**
 * 用于寸取一些私有的轻量级数据
 * 
 * @author yangcheng
 * 
 */
public class StatisticsHelper {

    /**
     * 读取一个持久对象
     * 
     * @param context 应用环境
     * @param name 文件名
     * @return 读取的持久对象
     * @throws ClassNotFoundException
     * @throws OptionalDataException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static Serializable load(Context context, String name) throws ClassNotFoundException, OptionalDataException,
            FileNotFoundException, IOException {
        Serializable result = null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;

        try {

            fis = context.openFileInput(name);
            ois = new ObjectInputStream(fis);
            result = (Serializable) ois.readObject();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } finally {
                if (ois != null) {
                    ois.close();
                }
            }
        }

        return result;
    }

    /**
     * 写入一个持久对象
     * 
     * @param context 应用环境
     * @param serializable 持久对象
     * @param name 文件名
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void save(Context context, Serializable serializable, String name) throws FileNotFoundException,
            IOException {

        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = context.openFileOutput(name, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(serializable);
        } finally {
            try {
                if (oos != null) {
                    fos.close();
                }
            } finally {
                if (oos != null) {
                    oos.close();
                }
            }
        }
    }

    public static void delete(Context context, String name) {
        context.deleteFile(name);
    }
}
