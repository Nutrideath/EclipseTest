package com.dfal.jobtracker.beans;

import java.io.Serializable;


import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
//import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
//import java.util.UUID;


import com.dfal.jobtracker.model.*;
import com.microsoft.azure.storage.table.*; 


@ManagedBean(name = "jobBean")
@ViewScoped
public class JobBean extends TableServiceEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String partitionKey;	//used in Azure storage tables as part of unique id
	
	@ManagedProperty(value="#{JobBean.rowKey}")
	private String rowKey;			//used in Azure storage tables as part of unique id (This is the job's Customer)
	
	@ManagedProperty(value="#{JobBean.jobCustomer}")
	private String jobCustomer;
		
	@ManagedProperty(value="#{JobBean.rushJobFlag}")
	private boolean rushJobFlag = false;
	
	@ManagedProperty(value="#{JobBean.jobType}")
	private String jobType;	//whether Corporate or Residential
	
	@ManagedProperty(value="#{JobBean.jobLocationType}")
	private String jobLocationType;	//whether Subdiv/Lot or StreetAddress
	
	@ManagedProperty(value="#{JobBean.jobMake}")
	private String jobMake;	//whether Rails or Mailbox or Custom

	@ManagedProperty(value="#{JobBean.jobStatus}")
	private String jobStatus;
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Job Address
	@ManagedProperty(value="#{JobBean.jobAddrSubdivisionLot}")	
	private String jobAddrSubdivisionLot;	//Job Address, if using Subdivision/Lot# format
	
	@ManagedProperty(value="#{JobBean.jobAddr_Line1}")	
	private String jobAddr_Line1;			//Job Address, 1st line, if using Street Address
	
	@ManagedProperty(value="#{JobBean.jobAddr_Line2}")	
	private String jobAddr_Line2;			//Job Address, 1st line, if using Street Address
	
	@ManagedProperty(value="#{JobBean.jobAddr_City}")	
	private String jobAddr_City;
	
	@ManagedProperty(value="#{JobBean.jobAddr_State}")	
	private String jobAddr_State;
	
	@ManagedProperty(value="#{JobBean.jobAddr_Zip}")	
	private String jobAddr_Zip;
	
	
	
	//@ManagedProperty(value="#{JobBean.jobNumber}")	
	//private String jobNumber;		//unique id for this job
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//date fields
	@ManagedProperty(value="#{JobBean.callInDate}")
	private Date callInDate;		//date originally contacted by client and job created
	
	@ManagedProperty(value="#{JobBean.targetDate}")
	private Date targetDate;		//default target date should initially default to 3 weeks from call-in date
	

	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Constructor stuff
	
	public JobBean() {
		System.out.println(" XX JobBean XX Created empty instance of JobBean"); 
		//this.partitionKey = UUID.randomUUID().toString();
		//System.out.println(" XX JobBean XX   partitionKey set to: " + this.partitionKey); 
	}

/*
	public JobBean(String jobCustomer) {
		//this.partitionKey = UUID.randomUUID().toString();
		this.rowKey = jobCustomer;
	}
*/	
	@PostConstruct
    public void init(){
		Calendar cal = Calendar.getInstance();
		
		this.callInDate = new Date();	//set default to today's date
		this.rushJobFlag = false;
		//this.rushJobFlag = true;
		
		//Set targetDate default to 3 weeks in future from call date (or in other words, from now)
		cal.add(Calendar.DAY_OF_YEAR, 21);
		
		//make sure is not on a Saturday or Sunday		
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		while (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY ) {
			//if this is a weekend, add a day and check again
			cal.add(Calendar.DAY_OF_YEAR, 1);
			dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		}
		
		this.targetDate = cal.getTime();
		
	
		System.out.println(" XX JobBean.init() XX  Call to init()");
    }
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	

	//function stuff	

	public void saveToJobTable() {
    	System.out.println(" XX JobBean XX");
    	System.out.println(" XX JobBean XX Call to .saveToJobTable()");
    	
    	
    	/*
    	 * Jobs are either:  Corporate or Residential (stored in jobType)
    	 * 		and either:	 Rails or Mailbox or Custom (stored in jobMake)
    	 * 
    	 * 
    	 * 
    	 * Calculate partitionKey.  This MUST be unique, thus the date in the calculation.
    	 * Calculates differently if is Corporate or Residential jobType, and for each possible value of jobLocationType (Subdiv_Lot or StreetAddress)
    	 * It is concatenated from: 
    	 * 		1) Call Date    	 
    	 * 		2) jobMake (Rails, Mailbox, or Custom)
    	 * 		3) if jobType is Corporate, Customer name.  If Residential, person's Last Name
    	 * 		4) if jobLocationType is "Subdiv_Lot", then jobAddrSubdivisionLot
    	 * 			if it is "StreetAddress", jobAddr_Line1, jobAddr_Line2, jobAddr_City, jobAddr_State, jobAddr_Zip
    	 * 
    	 *  Also, rowKey and partitionKey cannot contain any of the following: 
		 * 	The forward slash (/) character
		 *	The backslash (\) character
		 *	The number sign (#) character
		 *	The question mark (?) character
		 *	Also might be best to remove (%) and (+)
		 *	Plus, partitionKey cannot begin with an underscore (_)
		 */
    	
    	// set up variables
    	String pKey1, pKey2, pKey3, pKey4;
    	
    	//set up format of date
    	String pattern = "yyyy-MM-dd";
    	SimpleDateFormat ymdDateFormat = new SimpleDateFormat(pattern);
    	pKey1 = ymdDateFormat.format(this.callInDate); 
    	
    	pKey2 = jobMake;
    	/*
    	if(this.jobType.equals("Corporate")){
    		pKey3 = this.rowKey;
    	} else {
    		pKey3 = this.rowKey + "(Residential)";	//TODO: fix this, once we have better way to fill out Residential data. Should be last name of residential customer
    	}
    	*/
    	pKey3 = this.rowKey;
    	//pKey3 = this.jobCustomer;
    	//this.rowKey = this.jobCustomer;
    	
    	if(this.jobLocationType.equals("StreetAddress")) {
    		pKey4 = this.jobAddr_Line1 + ", " + this.jobAddr_Line2 + ", " + this.jobAddr_City + ", " + this.jobAddr_State + ", " + this.jobAddr_Zip;
    	} else {
    		pKey4 = this.jobAddrSubdivisionLot;    		
    	}
    	//debug out
    	System.out.println(" XX JobBean XX pKey1 = " + pKey1);
    	System.out.println(" XX JobBean XX pKey2 = " + pKey2);
    	System.out.println(" XX JobBean XX pKey3 = " + pKey3);
    	System.out.println(" XX JobBean XX pKey4 = " + pKey4);
    	
    	
    	// concatenate them
    	partitionKey = pKey1 + " " + pKey2 + ": " + pKey3 + " - " + pKey4;
    	
    	
    	System.out.println(" XX JobBean XX partitionKey = " + partitionKey);
    	System.out.println(" XX JobBean XX rowKey = " + rowKey);
   	
    	//create DataManager object
    	DataManagerAzure dataManager = new DataManagerAzure();	//dataManager object to connect to Azure table storage
    	//System.out.println(" XX CustomerBean XX Created DataManagerAzure object");
    	
    	try{
        	// Create an operation to add the new customer to the tablebasics table.
        	TableOperation insertOrMergeThisJob = TableOperation.insertOrMerge(this);

             // Submit the operation to the table service.
             dataManager.jobTable.execute(insertOrMergeThisJob);
             //return "empty";	//operation succeeded!!
             System.out.println(" XX JobBean XX Job saved successfully.");
        }
    	
    	 catch(TableServiceException e) {
    		//return "error";	//operation failed!!
    		System.out.println(" XX JobBean XX [[EXCEPTION]] " + e);
         	
    	 } catch(Throwable t) {
        	//return "error";	//operation failed!!
        	System.out.println(" XX JobBean XX [[THROWABLE ERROR]] " + t);
       
        } 
    }
	
	//TODO
	//public getSpecificJob()
	
	

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//getters and setters for fields other than dates (those are all grouped together below this section)
	
	public String getPartitionKey() {
		return partitionKey;
	}
	public void setPartitionKey(String partitionKey) {
		this.partitionKey = partitionKey;
	}
	
	public String getRowKey() {
		return rowKey;
	}
	public void setRowKey(String rowKeyInbound) {
		
		/* 	rowKey cannot contain any of the following: 
		 * 	The forward slash (/) character
		 *	The backslash (\) character
		 *	The number sign (#) character
		 *	The question mark (?) character
		 *	Also might be best to remove (%) and (+)
		 */
		//this.rowKey = sanitizeString(rowKey);
		//final String DisallowedCharsInTableKeys = new String("[\\\\#%+/?\u0000-\u001F\u007F-\u009F]");
	/*	
		if(rowKey != null) {
			System.out.println(" XX JobBean::setRowKey XX : rowKey is not null. It's value is: " + rowKey);
		} else {
			System.out.println(" XX JobBean::setRowKey XX : rowKey is null");
		}
		
		if(rowKeyInbound != null) {
			System.out.println(" XX JobBean::setRowKey XX : rowKeyInbound is not null; It's value is: " + rowKeyInbound);
			rowKeyCleaned = rowKeyInbound.replaceAll("\\"," ");
			System.out.println(" XX JobBean::setRowKey XX : rowKeyCleaned is not null; It's value is: " + rowKeyCleaned);
		} else {
			System.out.println(" XX JobBean::setRowKey XX : rowKeyInbound is null");
		}
	*/	
		this.rowKey = rowKeyInbound;
	}
	
	
	public String getJobCustomer() {
		return jobCustomer;
	}
	public void setJobCustomer(String jobCustomer) {
		this.jobCustomer = jobCustomer;
	}

	public boolean getRushJobFlag() {
		return rushJobFlag;
	}
	public void setRushJobFlag(boolean rushJobFlag) {
		this.rushJobFlag = rushJobFlag;
	}

	public String getJobStatus() {
		return jobStatus;
	}
	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}

	public String getJobType() {
		return jobType;
	}
	public void setJobType(String jobType) {
		this.jobType = jobType;
	}
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Job Address & location
	public String getJobLocationType() {
		return jobLocationType;
	}
	public void setJobLocationType(String jobLocationType) {
		this.jobLocationType = jobLocationType;
	}
	
	
	public String getJobAddrSubdivisionLot() {
		return jobAddrSubdivisionLot;
	}
	public void setJobAddrSubdivisionLot(String jobAddrSubdivisionLot) {
		this.jobAddrSubdivisionLot = jobAddrSubdivisionLot;
	}

	public String getJobAddr_Line1() {
		return jobAddr_Line1;
	}
	public void setJobAddr_Line1(String jobAddr_Line1) {
		this.jobAddr_Line1 = jobAddr_Line1;
	}

	public String getJobAddr_Line2() {
		return jobAddr_Line2;
	}
	public void setJobAddr_Line2(String jobAddr_Line2) {
		this.jobAddr_Line2 = jobAddr_Line2;
	}

	public String getJobAddr_City() {
		return jobAddr_City;
	}
	public void setJobAddr_City(String jobAddr_City) {
		this.jobAddr_City = jobAddr_City;
	}

	public String getJobAddr_State() {
		return jobAddr_State;
	}
	public void setJobAddr_State(String jobAddr_State) {	//Make sure to capitalize. But can't capitalize null, so check for it
		if(jobAddr_State == null) {
			this.jobAddr_State = jobAddr_State;
		} else {
			this.jobAddr_State = jobAddr_State.toUpperCase();
		}
	}

	public String getJobAddr_Zip() {
		return jobAddr_Zip;
	}
	public void setJobAddr_Zip(String jobAddr_Zip) {
		this.jobAddr_Zip = jobAddr_Zip;
	}

	
	
	public String getJobMake() {
		return jobMake;
	}
	public void setJobMake(String jobMake) {
		this.jobMake = jobMake;
	}

	/*
	public String getJobNumber() {
		return jobNumber;
	}
	public void setJobNumber(String jobNumber) {
		this.jobNumber = jobNumber;
	}
	*/
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//getters/setters for date fields  
	public Date getCallInDate() {
		return callInDate;
	}
	public void setCallInDate(Date callInDate) {
		this.callInDate = callInDate;
	}

	public Date getTargetDate() {
		return targetDate;
	}
	public void setTargetDate(Date targetDate) {
		this.targetDate = targetDate;
	}

}
