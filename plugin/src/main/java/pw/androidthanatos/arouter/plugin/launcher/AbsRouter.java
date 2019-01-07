package pw.androidthanatos.arouter.plugin.launcher;


import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavigationCallback;
import com.alibaba.android.arouter.facade.enums.RouteType;
import com.alibaba.android.arouter.facade.template.ILogger;
import com.alibaba.android.arouter.launcher.ARouter;
import pw.androidthanatos.arouter.plugin.Logger;
import pw.androidthanatos.arouter.plugin.callback.ResultCallback;
import pw.androidthanatos.arouter.plugin.proxy.ProxyFragment;

import java.lang.ref.WeakReference;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *  @desc: 封装Arouter
 *  @className: AbsRouter.java
 *  @author: thanatos
 *  @createTime: 2019/1/7 11:54
 */
public abstract class AbsRouter {


    private ARouter router = ARouter.getInstance();


    public static void init(Application application, boolean debug){
        if (debug){
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(application);
    }

    /**
     * 绑定当前类获取注解值
     */
    public void inject(Object thiz) {
        if (checkNoDependenciesARouter())return;
        getRouter().inject(thiz);
    }


    /**
     * 获取ARouter
     * @return {@link ARouter}
     */
    public ARouter getRouter(){
        if (checkNoDependenciesARouter())return null;
        return router;
    }

    /**
     * 路由跳转
     *
     * @param path Where you go.
     */
    public void navigation(String path) {
        if (checkNoDependenciesARouter())return;
        getRouter().build(path).navigation();
    }

    /**
     * 路由跳转
     *
     * @param url the path
     */
    public void navigation(Uri url) {
        if (checkNoDependenciesARouter())return;
        getRouter().build(url).navigation();
    }

    /**
     * 获取 对象
     * @param path
     * @param <T>
     * @return
     */
    public <T> T get(String path){
        if (checkNoDependenciesARouter())return null;
        return get(path, new Bundle());
    }

    /**
     * 获取 对象
     * @param path
     * @param bundle
     * @param <T>
     * @return
     */
    public <T> T get(String path, Bundle bundle){
        if (checkNoDependenciesARouter())return null;
        //noinspection unchecked
        return (T) getRouter().build(path).with(bundle).navigation();
    }

    /**
     * 获取 对象
     * @param uri
     * @param <T>
     * @return
     */
    public <T> T get(Uri uri){
        if (checkNoDependenciesARouter())return null;
        return get(uri, new Bundle());
    }

    /**
     * 获取对象
     * @param uri
     * @param bundle
     * @param <T>
     * @return
     */
    public <T> T get(Uri uri, Bundle bundle){
        if (checkNoDependenciesARouter())return null;
        //noinspection unchecked
        return (T) getRouter().build(uri).with(bundle).navigation();
    }

    //activity======================  页面跳转 带回调

    public void navigation(String path, FragmentActivity activity,
                           int requestCode, ResultCallback callback){
        this.navigation(path, activity, null, requestCode, callback);
    }

    public void navigation(String path, FragmentActivity activity, Bundle bundle,
                           int requestCode, ResultCallback callback){
        this.navigation(path, activity, bundle, requestCode, null, callback);
    }

    public void navigation(String path, FragmentActivity activity, Bundle bundle, int requestCode,
                           ActivityOptionsCompat option, ResultCallback callback){
        this.navigation(path, activity, bundle, requestCode, option, -1, -1, callback);
    }

    public void navigation(String path, FragmentActivity activity, Bundle bundle, int requestCode,
                           int enter, int exit, ResultCallback callback){
        this.navigation(path, activity, bundle, requestCode, null, enter, exit, callback);
    }

    //fragment======================  页面跳转 带回调

    public void navigation(String path, Fragment fragment,
                           int requestCode, ResultCallback callback){
        this.navigation(path, fragment, null, requestCode, callback);
    }

    public void navigation(String path, Fragment fragment, Bundle bundle,
                           int requestCode, ResultCallback callback){
        this.navigation(path, fragment, bundle, requestCode, null, callback);
    }

    public void navigation(String path, Fragment fragment, Bundle bundle, int requestCode,
                           ActivityOptionsCompat option, ResultCallback callback){
        this.navigation(path, fragment, bundle, requestCode, option, -1, -1, callback);
    }

    public void navigation(String path, Fragment fragment, Bundle bundle, int requestCode,
                           int enter, int exit, ResultCallback callback){
        this.navigation(path, fragment, bundle, requestCode, null, enter, exit, callback);
    }


    /**
     *
     * @param path
     * @param fragment
     * @param bundle
     * @param requestCode
     * @param callback
     */
    private void navigation(String path, Fragment fragment, Bundle bundle, int requestCode, ActivityOptionsCompat option, int enter, int exit, ResultCallback callback){
        if (fragment.getActivity() == null){
            Logger.e("目标 getActivity() 为空");
            return;
        }
        navigation(path, fragment.getActivity(), bundle, requestCode, option, enter, exit, callback);
    }

    /**
     * 跳转页面并获取返回值
     * @param path
     * @param activity
     * @param bundle
     * @param requestCode
     * @param callback
     */
    private void navigation(String path, FragmentActivity activity, Bundle bundle, int requestCode,
                            ActivityOptionsCompat option, int enter, int exit, ResultCallback callback){
        if (checkNoDependenciesARouter())return;
        WeakReference<FragmentActivity> activityWeakReference = new WeakReference<>(activity);
        Postcard postcard = getRouter().build(path).with(bundle)
                .withOptionsCompat(option)
                .withTransition(enter, exit);
        postcard.setType(RouteType.ACTIVITY);
        Fragment fragment = activityWeakReference.get()
                .getSupportFragmentManager()
                .findFragmentByTag(ProxyFragment.TAG);
        if (fragment != null){
            activityWeakReference.get().getSupportFragmentManager()
                    .beginTransaction()
                    .remove(fragment)
                    .commitAllowingStateLoss();
        }
        fragment = ProxyFragment.newInstance(postcard, requestCode, callback);
        activityWeakReference.get().getSupportFragmentManager()
                .beginTransaction()
                .add(fragment, ProxyFragment.TAG)
                .commitAllowingStateLoss();

    }


    /**
     * 跳转页面并获取返回值
     * @param activity
     * @param intent
     * @param requestCode
     * @param callback
     */
    public void navigation(FragmentActivity activity, Intent intent, int requestCode, ResultCallback callback){
        if (checkNoDependenciesARouter())return;
        WeakReference<FragmentActivity> activityWeakReference = new WeakReference<>(activity);
        Fragment fragment = activityWeakReference.get()
                .getSupportFragmentManager()
                .findFragmentByTag(ProxyFragment.TAG);
        if (fragment != null){
            activityWeakReference.get().getSupportFragmentManager()
                    .beginTransaction()
                    .remove(fragment)
                    .commitAllowingStateLoss();
        }
        fragment = ProxyFragment.newInstance(intent, requestCode, callback);
        activityWeakReference.get().getSupportFragmentManager()
                .beginTransaction()
                .add(fragment, ProxyFragment.TAG)
                .commitAllowingStateLoss();
    }

    /**
     * 跳转页面并获取返回值
     * @param fragment
     * @param intent
     * @param requestCode
     * @param callback
     */
    public void navigation(Fragment fragment, Intent intent, int requestCode, ResultCallback callback){
        if (fragment.getActivity() == null){
            Logger.e("目标 getActivity() 为空");
            return;
        }
        navigation(fragment.getActivity(), intent, requestCode, callback);
    }


    /**
     * Launch the navigation by type
     *
     * @param service interface of service
     * @param <T>     return type
     * @return instance of service
     */
    public <T> T navigation(Class<? extends T> service) {
        if (checkNoDependenciesARouter())return null;
        return getRouter().navigation(service);
    }

    /**
     * Launch the navigation.
     *
     * @param mContext    .
     * @param postcard    .
     * @param requestCode Set for startActivityForResult
     * @param callback    cb
     */
    public Object navigation(Context mContext, Postcard postcard, int requestCode, NavigationCallback callback) {
        if (checkNoDependenciesARouter())return null;
        return getRouter().navigation(mContext, postcard, requestCode, callback);
    }

    /**
     * 检测是否依赖了ARouter
     * @return true 没有依赖，   false 依赖了ARouter
     */
    private static boolean checkNoDependenciesARouter(){
        try {
            Class.forName("com.alibaba.android.arouter.launcher.ARouter");
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return true;
        }
    }


    public static synchronized void openDebug() {
        if (checkNoDependenciesARouter())return;
        ARouter.openDebug();
    }

    public static boolean debuggable() {
        if (checkNoDependenciesARouter())return false;
        return ARouter.debuggable();
    }

    public static synchronized void openLog() {
        if (checkNoDependenciesARouter())return;
        ARouter.openLog();
    }

    public static synchronized void printStackTrace() {
        if (checkNoDependenciesARouter())return;
        ARouter.printStackTrace();
    }

    public static synchronized void setExecutor(ThreadPoolExecutor tpe) {
        if (checkNoDependenciesARouter())return;
        ARouter.setExecutor(tpe);
    }

    public static synchronized void monitorMode() {
        if (checkNoDependenciesARouter())return;
        ARouter.monitorMode();
    }

    public static boolean isMonitorMode() {
        if (checkNoDependenciesARouter())return false;
        return ARouter.isMonitorMode();
    }

    public static void setLogger(ILogger userLogger) {
        if (checkNoDependenciesARouter())return;
        ARouter.setLogger(userLogger);
    }

    public static synchronized void destroy(){
        if (checkNoDependenciesARouter())return;
        ARouter.getInstance().destroy();
    }
}
