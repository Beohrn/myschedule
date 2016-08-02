package com.shedule.zyx.myshedule.config;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shedule.zyx.myshedule.managers.BluetoothManager;
import com.shedule.zyx.myshedule.managers.DateManager;

import java.util.Calendar;

import javax.inject.Singleton;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.voter.xyz.config.AppPreferences;
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
    AppPreferences providePrefs() {
        return new AppPreferences(context);
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
    public DateManager provideDateManager(Calendar calendar) {
        return new DateManager(calendar);
    }

    @Singleton
    @Provides
    public Calendar provideCalendar() {
        return Calendar.getInstance();
    }

    @Singleton
    @Provides
    public BluetoothManager provideBluetoothManager(BluetoothSPP bt) {
        return new BluetoothManager(bt);
    }

    @Singleton
    @Provides
    public BluetoothSPP provideBluetoothSPP(Context context) {
        return new BluetoothSPP(context);
    }
}
