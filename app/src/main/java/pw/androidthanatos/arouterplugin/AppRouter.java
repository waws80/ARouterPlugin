package pw.androidthanatos.arouterplugin;

import pw.androidthanatos.arouter.plugin.launcher.AbsRouter;

/**
 *  @desc: 路由
 *  @className: AppRouter.java
 *  @author: thanatos
 *  @createTime: 2019/1/7 16:11
 */
public class AppRouter extends AbsRouter {


    private AppRouter(){}


    private static final class Inner{
        private static final AppRouter APP_ROUTER = new AppRouter();
    }

    public static AppRouter getInstance(){
        return Inner.APP_ROUTER;
    }


}
