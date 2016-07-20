package com.shedule.zyx.myshedule;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.FacebookSdk;
import com.facebook.stetho.Stetho;
import com.shedule.zyx.myshedule.config.AppComponent;
import com.shedule.zyx.myshedule.config.AppModule;
import com.shedule.zyx.myshedule.config.DaggerAppComponent;

public class ScheduleApplication extends Application {
    private static AppComponent component;

    public static AppComponent getComponent() {
        return component;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
        Stetho.initializeWithDefaults(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}