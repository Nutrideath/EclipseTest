package com.dfal.jobtracker.beans;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.FlowEvent;

import com.dfal.jobtracker.beans.JobBean;
import com.dfal.jobtracker.beans.DataBean;

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
	
	@ManagedProperty(value="#{dataBean}")
	private DataBean dataBean = new DataBean();
	
	private CustomerBean selectedCB;
     
    private boolean skip;
      
    
    // functions
    
    public void save() {        
        jobBean.saveToJobTable();
    }
    
    
    public String onFlowProcess(FlowEvent event) {
    	
    	//need to set a a few fields on the job from the selection of the customer
    	selectedCB = dataBean.getSelectedCustomerBean();	//get the selected customer, if there is one    	
    	if(selectedCB != null) {
    		jobBean.setRowKey(selectedCB.getRowKey());			//set the rowKey field on the jobBean (the customer name)
    		jobBean.setJobType(selectedCB.getCustomerType());	//set the jobType field on the jobBean to match customer type (Corporate or Residential)
    	}
    	
    	//checks for skip boolean (not currently used)
        if(skip) {
            skip = false;   //reset in case user goes back
            return "confirm";
        }
        else {
            return event.getNewStep();
        } 
    }
}