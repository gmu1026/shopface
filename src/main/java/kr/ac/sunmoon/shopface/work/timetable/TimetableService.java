package kr.ac.sunmoon.shopface.work.timetable;

import java.util.List;

import kr.ac.sunmoon.shopface.work.timetable.Timetable;
import kr.ac.sunmoon.shopface.work.schedule.Schedule;

public interface TimetableService {
	public boolean addTimetable(Timetable timetable, Schedule schedule);
	public List<Timetable> getTimetableList(int branchNo);
	public boolean editTimetable(Timetable timetable, Schedule schedule);
	public boolean removeTimetable(Schedule schedule);
}