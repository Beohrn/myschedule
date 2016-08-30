package com.shedule.zyx.myshedule.config;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shedule.zyx.myshedule.managers.BTConnectionManager;
import com.shedule.zyx.myshedule.managers.BluetoothManager;
import com.shedule.zyx.myshedule.managers.DateManager;
import com.shedule.zyx.myshedule.managers.ReceiveManager;
import com.shedule.zyx.myshedule.managers.ScheduleManager;

import java.util.Calendar;

import javax.inject.Singleton;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;

@Module
public class AppModule {

    private Context context;

    public AppModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return context;
    }

    @Provides
    @Singleton
    public AppPreference provideAppPreference(Context context, Gson gson) {
        return new AppPreference(context, gson);
    }

    @Singleton
    @Provides
    Realm provideRealm(Context context) {
        RealmConfiguration config = new RealmConfiguration.Builder(context)
                .build();
        Realm.setDefaultConfiguration(config);
        return Realm.getDefaultInstance();
    }

    @Singleton
    @Provides
    public Gson provideGson() {
        return new GsonBuilder()
                .create();
    }

    @Singleton
    @Provides
    public DateManager provideDateManager(Calendar calendar, ScheduleManager scheduleManager) {
        return new DateManager(calendar, scheduleManager);
    }

    @Singleton
    @Provides
    public Calendar provideCalendar() {
        return Calendar.getInstance();
    }

    @Singleton
    @Provides
    public BluetoothManager provideBluetoothManager(BluetoothSPP bt, Context context) {
        return new BluetoothManager(bt, context);
    }

    @Singleton
    @Provides
    public BluetoothSPP provideBluetoothSPP(Context context) {
        return new BluetoothSPP(context);
    }

    @Singleton
    @Provides
    public ReceiveManager provideReceiveManager(Context context) {
        return new ReceiveManager(context);
    }

    @Singleton
    @Provides
    public BTConnectionManager provideBTConnectionManager(Context context) {
        return new BTConnectionManager(context);
    }

    @Singleton
    @Provides
    public ScheduleManager provideScheduleManager(AppPreference appPreference) {
        return new ScheduleManager(appPreference.getSchedule(), appPreference);
    }
}
