package com.shedule.zyx.myshedule.config;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shedule.zyx.myshedule.managers.DateManager;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import javax.inject.Singleton;

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
    public DateManager provideDateManager() {
        return new DateManager();
    }

    @Singleton
    @Provides
    public LocalDate provideLocalDate() {
        return new LocalDate();
    }

    @Singleton
    @Provides
    public DateTime provideDateTime() {
        return new DateTime();
    }

    @Singleton
    @Provides
    public LocalTime provideLocalTime() {
        return new LocalTime();
    }

}
