package com.shedule.zyx.myshedule.config;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shedule.zyx.myshedule.BuildConfig;
import com.shedule.zyx.myshedule.FirebaseWrapper;
import com.shedule.zyx.myshedule.managers.BluetoothManager;
import com.shedule.zyx.myshedule.managers.DateManager;
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
    public BluetoothManager provideBluetoothManager(BluetoothSPP bt,
                                                    Context context, Gson gson) {
        return new BluetoothManager(context, bt, gson);
    }

    @Singleton
    @Provides
    public BluetoothSPP provideBluetoothSPP(Context context) {
        return new BluetoothSPP(context);
    }

    @SuppressLint("HardwareIds")
    @Provides
    @Singleton
    public String provideDeviceToken() {
        return Settings.Secure.getString(context.getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    @Provides
    @Singleton
    public FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Singleton
    @Provides
    DatabaseReference provideDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference();
    }

    @Singleton
    @Provides
    FirebaseWrapper provideFirebaseWrapper(DatabaseReference ref, AppPreference prefs, FirebaseAuth auth) {
        return new FirebaseWrapper(ref, prefs, auth);
    }

    @Singleton
    @Provides
    public ScheduleManager provideScheduleManager(AppPreference appPreference) {
        return new ScheduleManager(appPreference.getSchedule(), appPreference);
    }
}
