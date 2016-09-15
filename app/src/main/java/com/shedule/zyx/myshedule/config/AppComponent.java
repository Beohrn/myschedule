package com.shedule.zyx.myshedule.config;


import com.shedule.zyx.myshedule.ui.activities.AddScheduleActivity;
import com.shedule.zyx.myshedule.ui.activities.HomeWorkActivity;
import com.shedule.zyx.myshedule.ui.activities.MainActivity;
import com.shedule.zyx.myshedule.ui.activities.TeachersActivity;
import com.shedule.zyx.myshedule.ui.fragments.BondedDevicesFragment;
import com.shedule.zyx.myshedule.ui.fragments.NearbyDevicesFragment;
import com.shedule.zyx.myshedule.ui.fragments.ScheduleFragment;

import javax.inject.Singleton;

import app.voter.xyz.comments.DiscussionActivity;
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
    void inject(ScheduleFragment scheduleFragment);
    void inject(TeachersActivity teachersActivity);
    void inject(DiscussionActivity discussionActivity);
    void inject(HomeWorkActivity homeWorkActivity);
}
