package com.dfal.jobtracker.model;

import java.io.Serializable;
import java.util.Date;

import org.primefaces.model.ScheduleEvent;

import lombok.Getter;
import lombok.Setter;


public class JobScheduleEvent implements ScheduleEvent, Serializable {
	private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private Date startDate;
    private Date endDate;
    private boolean allDay = false;
    private String styleClass;
    private Object data;
    private boolean editable = true;
    private String description;
    private String url;

    
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Constructor stuff
    public JobScheduleEvent() {
    }

    public JobScheduleEvent(String title, Date start, Date end) {
        this.title = title;
        this.startDate = start;
        this.endDate = end;
    }

    public JobScheduleEvent(String title, Date start, Date end, boolean allDay) {
        this.title = title;
        this.startDate = start;
        this.endDate = end;
        this.allDay = allDay;
    }

    public JobScheduleEvent(String title, Date start, Date end, String styleClass) {
        this.title = title;
        this.startDate = start;
        this.endDate = end;
        this.styleClass = styleClass;
    }

    public JobScheduleEvent(String title, Date start, Date end, Object data) {
        this.title = title;
        this.startDate = start;
        this.endDate = end;
        this.data = data;
    }

    //getters and setters
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JobScheduleEvent other = (JobScheduleEvent) obj;
        if ((this.title == null) ? (other.title != null) : !this.title.equals(other.title)) {
            return false;
        }
        if (this.startDate != other.startDate && (this.startDate == null || !this.startDate.equals(other.startDate))) {
            return false;
        }
        if (this.endDate != other.endDate && (this.endDate == null || !this.endDate.equals(other.endDate))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 61 * hash + (this.startDate != null ? this.startDate.hashCode() : 0);
        hash = 61 * hash + (this.endDate != null ? this.endDate.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "JobScheduleEvent{title=" + title + ",startDate=" + startDate + ",endDate=" + endDate + "}";
    }
}
