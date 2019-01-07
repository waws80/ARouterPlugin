package pw.androidthanatos.arouter.plugin;

import android.util.Log;
import com.alibaba.android.arouter.launcher.ARouter;

/**
 *  @desc:
 *  @className: Logger.java
 *  @author: thanatos
 *  @createTime: 2019/1/7 14:21
 */
public final class Logger {

    private static final String TAG = "ARouterPlugin";

    public static void e(String msg){
        if (ARouter.debuggable()){
            Log.e(TAG, msg);
        }

    }

    public static void w(String msg){
        if (ARouter.debuggable()){
            Log.w(TAG, msg);
        }
    }

    public static void d(String msg){
        if (ARouter.debuggable()){
            Log.d(TAG, msg);
        }
    }
}
