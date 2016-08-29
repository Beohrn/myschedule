package com.shedule.zyx.myshedule.config;



import com.shedule.zyx.myshedule.ui.activities.AddScheduleActivity;
import com.shedule.zyx.myshedule.ui.activities.MainActivity;
import com.shedule.zyx.myshedule.ui.fragments.BondedDevicesFragment;
import com.shedule.zyx.myshedule.ui.fragments.NearbyDevicesFragment;

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
    void inject(AppPreference appPreference);
    void inject(AddScheduleActivity addScheduleActivity);
}
