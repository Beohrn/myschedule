package com.shedule.zyx.myshedule.config;


import com.shedule.zyx.myshedule.teachers.TeachersActivity;
import com.shedule.zyx.myshedule.ui.activities.AddScheduleActivity;
import com.shedule.zyx.myshedule.ui.activities.AllHomeWorksActivity;
import com.shedule.zyx.myshedule.ui.activities.CreateHomeWorkActivity;
import com.shedule.zyx.myshedule.ui.activities.HomeWorkActivity;
import com.shedule.zyx.myshedule.ui.activities.MainActivity;
import com.shedule.zyx.myshedule.ui.activities.SplashActivity;
import com.shedule.zyx.myshedule.ui.fragments.BondedDevicesFragment;
import com.shedule.zyx.myshedule.ui.fragments.NearbyDevicesFragment;
import com.shedule.zyx.myshedule.ui.fragments.ScheduleFragment;

import javax.inject.Singleton;

import com.shedule.zyx.myshedule.auth.fragments.CreateAccountFragment;
import com.shedule.zyx.myshedule.teachers.TeachersRatingFragment;
import com.shedule.zyx.myshedule.comments.DiscussionActivity;
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
    void inject(CreateHomeWorkActivity createHomeWorkActivity);
    void inject(AllHomeWorksActivity allHomeWorksActivity);
    void inject(CreateAccountFragment createAccountFragment);
    void inject(TeachersRatingFragment teachersRatingFragment);
    void inject(SplashActivity splashActivity);
}
