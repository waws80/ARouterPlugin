package pw.androidthanatos.arouter.plugin.callback;

import android.content.Intent;

/**
 *  @desc: 跳转回调
 *  @className: ResultCallback.java
 *  @author: thanatos
 *  @createTime: 2019/1/7 13:33
 */
public interface ResultCallback {

    /**
     * 跳转回调结果函数
     * @param resultCode 响应码
     * @param data 响应数据
     */
    void next(int resultCode, Intent data);
}
