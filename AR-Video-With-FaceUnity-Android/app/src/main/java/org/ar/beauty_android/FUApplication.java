package org.ar.beauty_android;

import android.app.Application;
import android.content.Context;

import org.ar.beauty_android.database.DatabaseOpenHelper;
import org.ar.beauty_android.utils.ThreadHelper;
import com.faceunity.utils.FileUtils;

/**
 * Created by tujh on 2018/3/30.
 */
public class FUApplication extends Application {
    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        DatabaseOpenHelper.register(this);
        ThreadHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                // 异步拷贝 assets 资源
                FileUtils.copyAssetsLivePhotoTemplate(sContext);
                FileUtils.copyAssetsChangeFaceTemplate(sContext);
            }
        });
    }
}
