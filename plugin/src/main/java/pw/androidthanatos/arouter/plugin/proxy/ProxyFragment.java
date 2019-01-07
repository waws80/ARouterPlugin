package pw.androidthanatos.arouter.plugin.proxy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.alibaba.android.arouter.core.LogisticsCenter;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavigationCallback;
import com.alibaba.android.arouter.facade.enums.RouteType;
import com.alibaba.android.arouter.utils.TextUtils;
import pw.androidthanatos.arouter.plugin.Logger;
import pw.androidthanatos.arouter.plugin.callback.ResultCallback;

/**
 *  @desc: 页面回调代理fragment
 *  @className: ProxyFragment.java
 *  @author: thanatos
 *  @createTime: 2019/1/7 13:34
 */
public class ProxyFragment extends Fragment {

    public static final String TAG = "ProxyFragment";

    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    private int mRequestCode = -1;
    private ResultCallback mCallBack;
    private Postcard mPostcard;

    private Intent mIntent;

    /**
     * 创建代理fragment实例对象
     * @param requestCode 请求码
     * @param callback 回调类
     * @return {@link ProxyFragment}
     */
    public static ProxyFragment newInstance(@NonNull Postcard postcard, @IntRange(from = 0, to = 255) int requestCode, @NonNull ResultCallback callback){
        ProxyFragment fragment = new ProxyFragment();
        fragment.mPostcard = postcard;
        fragment.mRequestCode = requestCode;
        fragment.mCallBack = callback;
        return fragment;
    }

    /**
     * 创建代理fragment实例对象
     * @param requestCode 请求码
     * @param callback 回调类
     * @return {@link ProxyFragment}
     */
    public static ProxyFragment newInstance(@NonNull Intent intent, @IntRange(from = 0, to = 255) int requestCode, @NonNull ResultCallback callback){
        ProxyFragment fragment = new ProxyFragment();
        fragment.mIntent = intent;
        fragment.mRequestCode = requestCode;
        fragment.mCallBack = callback;
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mRequestCode == -1){
            Logger.e("请添加请求码 值在 0 -- 255 之间");
            return;
        }
        if (mCallBack == null){
            Logger.e("请添加请求回调  ResultCallback ");
            return;
        }

        //启动系统级别页面调用 相册、相机 等
        if (mIntent != null){
            startActivityForResult(mIntent, mRequestCode, mPostcard.getOptionsBundle());
        }
        //启动活动页面
        else {
            if (mPostcard == null){
                Logger.e("请添加请求体  Postcard 具体使用方式 请查阅 ARouter");
                return;
            }
            navigation(getActivity());
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mRequestCode == requestCode){
            if (mCallBack != null){
                mCallBack.next(resultCode, data);
            }
        }
        //销毁自身
        destroy();

    }


    /**
     * 代码来自 ARouter {@link com.alibaba.android.arouter.launcher.ARouter} 类
     * @param context 上下文对象
     */
    private void navigation(final Context context){
        LogisticsCenter.completion(mPostcard);
        if (mPostcard.getType() == RouteType.ACTIVITY){
            // Build intent
            final Intent intent = new Intent(context, mPostcard.getDestination());
            intent.putExtras(mPostcard.getExtras());

            // Set flags.
            int flags = mPostcard.getFlags();
            if (-1 != flags) {
                intent.setFlags(flags);
            }
            // Set Actions
            String action = mPostcard.getAction();
            if (!TextUtils.isEmpty(action)) {
                intent.setAction(action);
            }
            // Navigation in main looper.
            if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
                sHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(mRequestCode, context, intent, mPostcard, navigationCallback);
                    }
                });
            } else {
                startActivity(mRequestCode, context, intent, mPostcard, navigationCallback);
            }
        }else {
            Logger.e("确定跳转目标是活动页面?" + mPostcard.getDestination());
        }

    }

    /**
     * 代码来自 ARouter {@link com.alibaba.android.arouter.launcher.ARouter} 类
     * @param requestCode 请求码
     * @param currentContext 上下文
     * @param intent {@link Intent}
     * @param postcard {@link Postcard}
     * @param callback {@link NavigationCallback}
     */
    private void startActivity(int requestCode, Context currentContext, Intent intent, Postcard postcard, NavigationCallback callback) {
        if (requestCode >= 0) {  // Need start for result
            startActivityForResult(intent, requestCode, postcard.getOptionsBundle());
        } else {
            startActivity(intent, postcard.getOptionsBundle());
        }

        if ((-1 != postcard.getEnterAnim() && -1 != postcard.getExitAnim()) && currentContext instanceof Activity) {    // Old version.
            ((Activity) currentContext).overridePendingTransition(postcard.getEnterAnim(), postcard.getExitAnim());
        }

        if (null != callback) { // Navigation over.
            callback.onArrival(postcard);
        }
    }

    /**
     * 销毁自身
     */
    void destroy(){
        if (getActivity() == null) return;
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .remove(this)
                .commitAllowingStateLoss();
    }

    /**
     * 跳转页面回调
     */
    private NavigationCallback navigationCallback = new NavigationCallback() {
        @Override
        public void onFound(Postcard postcard) {
        }

        @Override
        public void onLost(Postcard postcard) {
            Logger.e("未跳转目标页面?" + mPostcard.getDestination());
        }

        @Override
        public void onArrival(Postcard postcard) {

        }

        @Override
        public void onInterrupt(Postcard postcard) {

        }
    };
}
