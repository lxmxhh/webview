package cn.yahoo.asxhl2007.gamedownloader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.util.HashMap;

import android.content.Context;

public class AppStorage implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private static AppStorage instance;

    private static Context context;

    public static void init(Context context) {
        if (AppStorage.context == null) {
            AppStorage.context = context;
        }
    }

    /**
     * 获取游戏存储对象
     * 
     * @param activity 应用环境
     * @return 游戏存储对象
     */
    public static AppStorage getInstance() {
        if (instance == null) {
            String imei = null;
            try {
                imei = DeviceInfo.getIMEI(context);
            } catch (SecurityException e) {
                imei = NONE_IMEI;
            }
            if (imei == null || imei.equals("")) {
                imei = NONE_IMEI;
            }

            try {
                instance = (AppStorage) StatisticsHelper.load(context, FILE_NAME);
            } catch (Exception e) {
            }
            if (instance == null) {
                instance = new AppStorage();
            }
        }
        return instance;
    }

    private static final String FILE_NAME = "appstorage.data";
    private static final String KEY_IMEI = "imei";
    private static final String NONE_IMEI = "123456789012345";

    private HashMap<String, Serializable> data = new HashMap<String, Serializable>();

    /**
     * 获取保存的数据
     * 
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T getData(String key) {
        return (T) data.get(key);
    }

    /**
     * 获取保存的数据，如果不存在，返回指定的默认值，不会将此值保存。
     * 
     * @param key 键
     * @param defaultValue 数据不存在是返回的默认值
     * @return 保存的数据
     */
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T getData(String key, T defaultValue) {
        Serializable value = data.get(key);
        if (value == null) {
            return defaultValue;
        }
        return (T) value;
    }

    /**
     * 保存数据
     * 
     * @param key 键
     * @param value 值
     */
    public void putValue(String key, Serializable value) {
        data.put(key, value);
        save();
    }

    /**
     * 保存统计信息（一般情况不需要主动调用）
     */
    public void save() {
        try {
            StatisticsHelper.save(context, this, FILE_NAME);
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }
}
