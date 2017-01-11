package com.shedule.zyx.myshedule;

import com.shedule.zyx.myshedule.managers.DateManager;
import com.shedule.zyx.myshedule.managers.ScheduleManager;
import com.shedule.zyx.myshedule.models.HomeWork;
import com.shedule.zyx.myshedule.models.Schedule;

import org.junit.Test;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import static junit.framework.Assert.assertEquals;

public class ExampleUnitTest {
    ScheduleManager manager = new ScheduleManager(new ArrayList<>(), null);

    DateManager dateManager = new DateManager(new GregorianCalendar());

    Schedule schedule1 = new Schedule("1", "1", null, null);
    Schedule schedule2 = new Schedule("2", "2", null, null);
    Schedule schedule3 = new Schedule("3", "3", null, null);

    HomeWork homeWork = new HomeWork();

    ArrayList schedules = new ArrayList();
    ArrayList homeworks = new ArrayList();

    @Test
    public void getScheduleSize() throws Exception {

        schedules.add(schedule1);
        schedules.add(schedule2);
        schedules.add(schedule3);

        manager.setGlobalList(schedules);

        assertEquals(3, manager.getGlobalList().size());
    }

    @Test
    public void getHomeworkSize() throws Exception {

        homeworks.add(homeWork);

        schedule1.setHomework(homeworks);
        schedules.add(schedule1);

        manager.setGlobalList(schedules);

        assertEquals(1, manager.getAllHomework().size());
    }

    @Test
    public void getHomeworkByDate() throws Exception {

        homeWork.setDeadLine("13");

        homeworks.add(homeWork);

        schedule1.setHomework(homeworks);
        schedules.add(schedule1);

        manager.setGlobalList(schedules);

        assertEquals(1, manager.getHomeWorkByDate(schedule1, "13").size());
    }

    @Test
    public void getScheduleByDay() throws Exception {

        ArrayList dates = new ArrayList();
        dates.add("2");
        dates.add("5");
        dates.add("12");

        schedule1.setDates(dates);
        schedules.add(schedule1);

        manager.setGlobalList(schedules);

        assertEquals(0, manager.getScheduleByDay("13").size());
    }

    @Test
    public void deleteSchedule() throws Exception {

        schedules.add(schedule1);
        schedules.add(schedule2);
        schedules.add(schedule3);

        manager.setGlobalList(schedules);

        manager.deleteSchedule();

        assertEquals(0, manager.getGlobalList().size());
    }

    //need to see logic in application 0 can be change
    @Test
    public void getPositionByDay() throws Exception {
        assertEquals(0, dateManager.getPositionByCalendar());
    }

    @Test
    public void getMonthName() throws Exception {
        assertEquals("февраля", dateManager.getMonthName(1));
    }

    @Test
    public void getDayByPosition() throws Exception {
        assertEquals(true, dateManager.getDayByPosition(1).contains("10"));
    }
}
