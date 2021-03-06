package kr.ac.sunmoon.shopface.work.timetable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import kr.ac.sunmoon.shopface.work.schedule.Schedule;
import kr.ac.sunmoon.shopface.work.schedule.ScheduleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@RequiredArgsConstructor
@Slf4j
@Service
public class TimetableServiceImple implements TimetableService {
	private final TimetableMapper timetableMapper;
	private final ScheduleMapper scheduleMapper;
	
	@Override
	public boolean addTimetable(Timetable timetable, Schedule schedule) {
		boolean isSuccess = false;
		
			if (timetable.getWorkStartTime() != null 
					&& !"".equals(timetable.getWorkStartTime())
					&& timetable.getWorkEndTime() != null
					&& !"".equals(timetable.getWorkEndTime())
					&& timetable.getOccupName() != null
					&& !"".equals(timetable.getOccupName())
					&& timetable.getOccupColor() != null
					&& !"".equals(timetable.getOccupColor())
					&& schedule.getMemberId() != null
					&& !"".equals(schedule.getMemberId())) {
				List<Timetable> timetables = this.timetableMapper.selectAll(timetable);
				
				if (timetables.size() == 0) {
					timetableMapper.insert(timetable);
					
					schedule.setTimetableNo(timetableMapper.selectAll(timetable).get(0).getTimetableNo());
					schedule.setState('R');
					scheduleMapper.insert(schedule);
					
					isSuccess = true;
				} else if (timetables.size() == 1) {
					schedule.setTimetableNo(timetables.get(0).getTimetableNo());
					schedule.setState('R');
					scheduleMapper.insert(schedule);

					isSuccess = true;
				}  
			}
		return isSuccess;
	}

	@Override
	public List<TimetableSchedule> getTimetableList(int branchNo) {
		List<TimetableSchedule> timetableSchedules = new ArrayList<TimetableSchedule>();
		try {
			if (branchNo > 0) { 
				Timetable timetable = new Timetable();
				timetable.setBranchNo(branchNo);
				List<Timetable> timetables = this.timetableMapper.selectAll(timetable);
				if (timetables.size() > 0) {
					for (int i = 0; i < timetables.size(); i++) {
						int no = timetables.get(i).getTimetableNo();
						
						Schedule parameterSchedule = new Schedule();
						parameterSchedule.setTimetableNo(no);
						parameterSchedule.setBranchNo(branchNo);
						
						List<Schedule> schedules = this.scheduleMapper.selectAll(parameterSchedule);
						for (int j = 0; j < schedules.size(); j++) {
							
							timetableSchedules.add(new TimetableSchedule(timetables.get(i), schedules.get(j)));
						}
					}
					return timetableSchedules;
				} else {
					return timetableSchedules;
				}
			} else {
				return timetableSchedules;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return timetableSchedules;
		} 
	}

	@Override
	public boolean editTimetable(Timetable timetable, Schedule schedule) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("YY-MM-dd HH:mm:ss");
			Date currentTime = new Date();
			String current = dateFormat.format(currentTime);
			
			String startTime = timetable.getWorkStartTime();

			int compare = current.compareTo(startTime);
			if (compare < 0) {
				Schedule stateCheck = this.scheduleMapper.select(schedule);
				
				if (stateCheck.getState() == 'R' || stateCheck.getState() == 'L') {
					Schedule select = new Schedule();
					select.setTimetableNo(schedule.getTimetableNo());
					List<Schedule> schedules = this.scheduleMapper.selectAll(select);
					
					if (schedules != null && schedules.size() == 1) {
						this.timetableMapper.update(timetable);
						return true;
					} else if (schedules != null && schedules.size() > 1) {
						this.timetableMapper.insert(timetable);
						
						List<Timetable> result = this.timetableMapper.selectAll(timetable);
						if (result != null && result.size() == 1) {
							
							schedule.setTimetableNo(result.get(0).getTimetableNo());
							this.scheduleMapper.update(schedule);
							return true;
						}
						return false;
					}
					return false;
				}
				return false;
			}
			return false;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean removeTimetable(Schedule schedule) {
		boolean isSuccess = false;
		
		List<Schedule> existSchedules = scheduleMapper.selectAll(schedule);
		if (existSchedules.size() > 0) {
			for (Schedule existSchedule : existSchedules) {
				if (existSchedule.getState() == 'R' 
						|| existSchedule.getState() == 'A') {
					scheduleMapper.delete(existSchedule);
					
					Schedule parameter = new Schedule();
					parameter.setTimetableNo(schedule.getTimetableNo());
					
					List<Schedule> resultSchedules = this.scheduleMapper.selectAll(parameter);

					if (resultSchedules != null) {
						if (resultSchedules.size() == 0) {
							this.timetableMapper.delete(new Timetable(schedule.getTimetableNo()));
							isSuccess = true;
						} else {
							isSuccess = true;
						}
					} else {
						isSuccess = true;
					}
				}
			}
		}
		
		return isSuccess;
	}
}