package com.dfal.jobtracker.beans;

import java.io.Serializable;

import javax.annotation.PostConstruct;
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
	
	
	
	private CustomerBean selectedCustomerBean;
     
    private boolean skip; 
      
    
    
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  //Constructor stuff
    public NewJobWizard() {
    	
    }
    
    @PostConstruct
    public void init(){
    	jobBean.setJobStatus("Awaiting Measurer Assignment"); //This is a new job, so set initial status
    	jobBean.setJobStatusShort("AMA");
    	jobBean.setJobProgress("10");
    }
    
    
    // functions
    
    public void save() {        
        jobBean.saveToJobTable(); 
    }
    
   
    public String onFlowProcess(FlowEvent event) {
    	String customerType;
    	
    	//need to set a a few fields on the job from the selection of the customer
    	selectedCustomerBean = dataBean.getSelectedCustomerBean();	//get the selected customer, if there is one  
    	
    	if(selectedCustomerBean != null) {
    		jobBean.setRowKey(selectedCustomerBean.getRowKey());			//set the rowKey field on the jobBean (the customer name)
    		
    		customerType = selectedCustomerBean.getCustomerType();			//get the customer type (Corporate or Residential)    		
    		jobBean.setJobType(customerType);	//set the jobType field on the jobBean to match customer type (Corporate or Residential)
    		
    		if(customerType.equals("Residential")){
    			//inherit job contact fields if residential (but only if values aren't already set. Allows user to edit these fields.)
    			if(jobBean.getJobContactFirstName() == null) {
    				jobBean.setJobContactFirstName(selectedCustomerBean.getFirstName());
    			}
    			if(jobBean.getJobContactLastName() == null) {
    				jobBean.setJobContactLastName(selectedCustomerBean.getLastName());
    			}
    			if(jobBean.getJobContactEmail() == null) {
    				jobBean.setJobContactEmail(selectedCustomerBean.getEmail());
    			}
    			if(jobBean.getJobContactPhoneNumber() == null) {
    				jobBean.setJobContactPhoneNumber(selectedCustomerBean.getPhoneNumber());
    			}
    			
    			//inherit address fields if residential  (but only if values aren't already set. Allows user to edit these fields.)
    			if(jobBean.getJobLocationType() == null) {
    				jobBean.setJobLocationType("StreetAddress");
    			}
    			if(jobBean.getJobAddr_Line1() == null) {
    				jobBean.setJobAddr_Line1(selectedCustomerBean.getAddr_Line1());
    			}
    			if(jobBean.getJobAddr_Line2() == null) {
    				jobBean.setJobAddr_Line2(selectedCustomerBean.getAddr_Line2());
    			}
    			if(jobBean.getJobAddr_City() == null) {
    				jobBean.setJobAddr_City(selectedCustomerBean.getAddr_City());
    			}
    			if(jobBean.getJobAddr_State() == null) {
    				jobBean.setJobAddr_State(selectedCustomerBean.getAddr_State());
    			}
    			if(jobBean.getJobAddr_Zip() == null) {
    				jobBean.setJobAddr_Zip(selectedCustomerBean.getAddr_Zip());
    			}
    		} 
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