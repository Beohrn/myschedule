package com.shedule.zyx.myshedule.config;


import com.shedule.zyx.myshedule.teachers.TeachersRatingFragment;
import com.shedule.zyx.myshedule.ui.activities.BaseActivity;
import com.shedule.zyx.myshedule.ui.fragments.BaseFragment;
import com.shedule.zyx.myshedule.ui.fragments.ScheduleFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        AppModule.class
})
public interface AppComponent {
    void inject(AppPreference appPreference);
    void inject(ScheduleFragment scheduleFragment);
    void inject(TeachersRatingFragment teachersRatingFragment);
    void inject(BaseFragment baseFragment);
    void inject(BaseActivity baseActivity);
}
