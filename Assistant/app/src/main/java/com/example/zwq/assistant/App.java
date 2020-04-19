package com.example.zwq.assistant;

import android.app.Application;

import com.example.zwq.assistant.manager.CosManager;
import com.example.zwq.assistant.manager.UserInfoManager;
import com.hapi.ut.AppCache;
import com.hapi.ut.helper.ActivityManager;

import androidx.multidex.MultiDex;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        AppCache.setContext(this);
        ActivityManager.get().init(this);
        UserInfoManager.getInstance().init();
        CosManager.getInstance().init(BuildConfig.cosSecretId,BuildConfig.cosSecretKey);
    }
}
