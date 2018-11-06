package com.dfal.jobtracker.view;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleModel;
//import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import com.dfal.jobtracker.beans.JobBean;
import com.dfal.jobtracker.model.DataService;
import com.dfal.jobtracker.model.JobScheduleEvent;

import lombok.Getter;
import lombok.Setter;

@ManagedBean(name = "jobScheduleView")
@ViewScoped
@Getter @Setter
public class JobScheduleView implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
    @ManagedProperty(value="#{DataBean.jobBeans}")
    private List<JobBean> jobBeans;   
    
	private ScheduleModel eventModel;
	
	//private ScheduleModel lazyEventModel;
	
    //The DataService bean is Session scoped, and so loads upon first use and remains available until end of session.
    @ManagedProperty(value="#{dataService}")	//get the SessionScoped data service already in memory (or instantiate it if it doesn't exist)
    public DataService dataService;

	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Constructor stuff
	private ScheduleEvent event = new JobScheduleEvent();

    @PostConstruct
	public void init() {
    	dataService.refreshJobLists();	//make sure we have latest list    	
    	jobBeans = dataService.getJobBeans();
    	Calendar cal = Calendar.getInstance();	//cal will be used to calculate target date info
    	
		eventModel = new DefaultScheduleModel();
		//add jobs to schedule according to their target date
		for (JobBean entity : jobBeans) {
			
			//if there is no targetDateEnd on the jobBean, calculate an arbitrary hour's difference
			
			if(entity.getTargetDateEnd() == null) {
				System.out.println(" XX JobScheduleView() XX Found JobBean with no targetDateEnd.  Filling it in...");
				cal.setTime(entity.getTargetDate());	//get the targetDate
				cal.add(Calendar.HOUR_OF_DAY, 1); 		// adds one hour
				entity.setTargetDateEnd(cal.getTime());
			}
			
			//												Title				Start Date				End Date		JobBean
			eventModel.addEvent(new JobScheduleEvent(entity.getRowKey(), entity.getTargetDate(), entity.getTargetDateEnd(), entity));

		}
		

		/*
		lazyEventModel = new LazyScheduleModel() {
			
			@Override
			public void loadEvents(Date start, Date end) {
				Date random = getRandomDate(start);
				addEvent(new JobScheduleEvent("Lazy Event 1", random, random));
				
				random = getRandomDate(start);
				addEvent(new JobScheduleEvent("Lazy Event 2", random, random));
			}
			
		};
		*/
		
	}
    
    
    

	
	
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //getters and setters
    
	public ScheduleModel getEventModel() {
		return eventModel;
	}
	
	/*
	public ScheduleModel getLazyEventModel() {
		return lazyEventModel;
	}
	*/

	
	public ScheduleEvent getEvent() {
		return event;
	}

	public void setEvent(ScheduleEvent event) {
		this.event = event;
	}
	
	public void addEvent(ActionEvent actionEvent) {
		if(event.getId() == null)
			eventModel.addEvent(event);
		else
			eventModel.updateEvent(event);
		
		event = new JobScheduleEvent();
	}
	
	public void onEventSelect(SelectEvent selectEvent) {
		event = (ScheduleEvent) selectEvent.getObject();
	}
	
	public void onDateSelect(SelectEvent selectEvent) {
		event = new JobScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
	}
	
	public void onEventMove(ScheduleEntryMoveEvent event) {
		//this is a move, so change start date and time, plus end date and time (move both times equally)
		Calendar cal = Calendar.getInstance();
		JobBean movedJob = (JobBean) event.getScheduleEvent().getData();	//get the jobBean from the event
		
		//TODO: Fix problem with timezone difference
		//	When cal uses setTime(movedJob.getTargetDate()) to set itself, the time there is saved in UTC, 
		//	so is off by 5 hours (depending on daylight saving time...)
		
		//change targetDate
		cal.setTime(movedJob.getTargetDate());		//get the targetDate
		cal.add(Calendar.HOUR_OF_DAY, event.getDayDelta()); 		// change by day delta
		cal.add(Calendar.MINUTE, event.getMinuteDelta()); 			//change by minute delta
		movedJob.setTargetDate(cal.getTime());		//set targetDate to new value
		
		//change targetDateEnd by same amounts, since entire job was moved
		cal.setTime(movedJob.getTargetDateEnd());		//get the targetDateEnd
		cal.add(Calendar.HOUR_OF_DAY, event.getDayDelta()); 		// change by day delta
		cal.add(Calendar.MINUTE, event.getMinuteDelta()); 			//change by minute delta
		movedJob.setTargetDateEnd(cal.getTime());		//set targetDateEnd to new value
		
		//save the jobBean with new values
		movedJob.saveToJobTable();
		
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());		
		addMessage(message);
	}
	
	public void onEventResize(ScheduleEntryResizeEvent event) {
		//this is a resize, so only change TargetDateEnd  (Does that logic work?)
		Calendar cal = Calendar.getInstance();
		JobBean resizedJob = (JobBean) event.getScheduleEvent().getData();	//get the jobBean from the event
		
		//change targetDateEnd 
		cal.setTime(resizedJob.getTargetDateEnd());		//get the targetDateEnd
		cal.add(Calendar.HOUR_OF_DAY, event.getDayDelta()); 		// change by day delta
		cal.add(Calendar.MINUTE, event.getMinuteDelta()); 			//change by minute delta
		resizedJob.setTargetDateEnd(cal.getTime());		//set targetDateEnd to new value
				
		//save the jobBean with new values
		resizedJob.saveToJobTable();
		
		
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());		
		addMessage(message);
	}
	
	private void addMessage(FacesMessage message) {
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	/*
	 * methods for filling in fake/display data; removed, but may be useful
	 * 
	 * 
	public Date getRandomDate(Date base) {
		Calendar date = Calendar.getInstance();
		date.setTime(base);
		date.add(Calendar.DATE, ((int) (Math.random()*30)) + 1);	//set random day of month
		
		return date.getTime();
	}
	
	public Date getInitialDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), Calendar.FEBRUARY, calendar.get(Calendar.DATE), 0, 0, 0);
		
		return calendar.getTime();
	}
	
		private Calendar today() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);

		return calendar;
	}
	
	private Date previousDay8Pm() {
		Calendar t = (Calendar) today().clone();
		t.set(Calendar.AM_PM, Calendar.PM);
		t.set(Calendar.DATE, t.get(Calendar.DATE) - 1);
		t.set(Calendar.HOUR, 8);
		
		return t.getTime();
	}
	
	private Date previousDay11Pm() {
		Calendar t = (Calendar) today().clone();
		t.set(Calendar.AM_PM, Calendar.PM);
		t.set(Calendar.DATE, t.get(Calendar.DATE) - 1);
		t.set(Calendar.HOUR, 11);
		
		return t.getTime();
	}
	
	private Date today1Pm() {
		Calendar t = (Calendar) today().clone();
		t.set(Calendar.AM_PM, Calendar.PM);
		t.set(Calendar.HOUR, 1);
		
		return t.getTime();
	}
	
	private Date theDayAfter3Pm() {
		Calendar t = (Calendar) today().clone();
		t.set(Calendar.DATE, t.get(Calendar.DATE) + 2);		
		t.set(Calendar.AM_PM, Calendar.PM);
		t.set(Calendar.HOUR, 3);
		
		return t.getTime();
	}

	private Date today6Pm() {
		Calendar t = (Calendar) today().clone(); 
		t.set(Calendar.AM_PM, Calendar.PM);
		t.set(Calendar.HOUR, 6);
		
		return t.getTime();
	}
	
	private Date nextDay9Am() {
		Calendar t = (Calendar) today().clone();
		t.set(Calendar.AM_PM, Calendar.AM);
		t.set(Calendar.DATE, t.get(Calendar.DATE) + 1);
		t.set(Calendar.HOUR, 9);
		
		return t.getTime();
	}
	
	private Date nextDay11Am() {
		Calendar t = (Calendar) today().clone();
		t.set(Calendar.AM_PM, Calendar.AM);
		t.set(Calendar.DATE, t.get(Calendar.DATE) + 1);
		t.set(Calendar.HOUR, 11);
		
		return t.getTime();
	}
	
	private Date fourDaysLater3pm() {
		Calendar t = (Calendar) today().clone(); 
		t.set(Calendar.AM_PM, Calendar.PM);
		t.set(Calendar.DATE, t.get(Calendar.DATE) + 4);
		t.set(Calendar.HOUR, 3);
		
		return t.getTime();
	}
	*/
}
