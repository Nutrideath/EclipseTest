package com.dfal.jobtracker.beans;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.FlowEvent;

import com.dfal.jobtracker.beans.JobBean;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
 
@ManagedBean(name = "newJobWizard")
@ViewScoped
@Getter @Setter @EqualsAndHashCode(callSuper=false) @ToString(callSuper=true, includeFieldNames=true)
public class NewJobWizard implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//works with wizard on createNewJob
 
	@ManagedProperty(value="#{jobBean}")
    private JobBean jobBean = new JobBean();
     
    private boolean skip;
          
    public void save() {        
        jobBean.saveToJobTable();
    }
    
    public String onFlowProcess(FlowEvent event) {
        if(skip) {
            skip = false;   //reset in case user goes back
            return "confirm";
        }
        else {
            return event.getNewStep();
        }
    }
}