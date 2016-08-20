package com.shedule.zyx.myshedule.config;

import com.shedule.zyx.myshedule.ui.activities.MainActivity;
import com.shedule.zyx.myshedule.ui.fragments.ScheduleFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(MainActivity mainActivity);
    void inject(ScheduleFragment scheduleFragment);
}
