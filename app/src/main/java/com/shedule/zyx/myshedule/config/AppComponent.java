package com.shedule.zyx.myshedule.config;

import com.shedule.zyx.myshedule.managers.PreferencesManager;
import com.shedule.zyx.myshedule.ui.AddScheduleActivity;
import com.shedule.zyx.myshedule.ui.BondedDevicesFragment;
import com.shedule.zyx.myshedule.ui.MainActivity;
import com.shedule.zyx.myshedule.ui.NearbyDevicesFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        AppModule.class
})
public interface AppComponent {
    void inject(MainActivity mainActivity);
    void inject(BondedDevicesFragment bondedDevicesFragment);
    void inject(NearbyDevicesFragment nearbyDevicesFragment);
    void inject(AddScheduleActivity addScheduleActivity);
    void inject(PreferencesManager preferencesManager);
}
