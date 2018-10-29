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
import java.util.Random;


import com.dfal.jobtracker.model.*;
import com.microsoft.azure.storage.table.*;

//lombok does auto-generation of getters, setters, equals, and toString functions
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString; 


@ManagedBean(name = "jobBean")
@ViewScoped
@Getter @Setter @EqualsAndHashCode(callSuper=false) @ToString(callSuper=true, includeFieldNames=true)
public class JobBean extends TableServiceEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	
	@ManagedProperty(value="#{JobBean.jobStatus}")
	private String jobStatus;		//tracks the job's lifecycle
	
	@Getter(AccessLevel.NONE)	//special getter below
	@ManagedProperty(value="#{JobBean.jobProgress}")
	private String jobProgress;		//like jobStatus, but is a percentage of completion
	
	@ManagedProperty(value="#{JobBean.jobName}")
	private String jobName;			//short designation of job
	
	/*
	 * The properties below are grouped according to how they appear on the corresponding
	 *   createNewJob.xhtml and jobForm.xhtml.  Those forms are used to create and manage
	 *   the jobBean objects.
	 */
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Job Customer
	private String partitionKey;	//used in Azure storage tables as part of unique id
	
	@ManagedProperty(value="#{JobBean.rowKey}")
	private String rowKey;			//used in Azure storage tables as part of unique id (This is the job's Customer)
	
	//@ManagedProperty(value="#{JobBean.jobCustomer}")	//using rowKey as customer, so removed
	//private String jobCustomer;


	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Job Contact Info
	@ManagedProperty(value="#{JobBean.jobContactLastName}")
    private String jobContactLastName;
    
    @ManagedProperty(value="#{JobBean.jobContactFirstName}")
    private String jobContactFirstName;
    
    @ManagedProperty(value="#{JobBean.jobContactFullName}")
    private String jobContactFullName;
    
    @ManagedProperty(value="#{JobBean.jobContactFullNameLastFirst}")
    private String jobContactFullNameLastFirst;   	
    
    @ManagedProperty(value="#{JobBean.jobContactEmail}")
    private String jobContactEmail;
    
    @ManagedProperty(value="#{JobBean.jobContactPhoneNumber}")
    private String jobContactPhoneNumber;
    
   

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Job Info (General info on the Job)
	
	@ManagedProperty(value="#{JobBean.jobType}")
	private String jobType;	//whether Corporate or Residential
	
	@ManagedProperty(value="#{JobBean.jobMake}")
	private String jobMake;	//whether Rails or Mailbox or Custom
	
	@ManagedProperty(value="#{JobBean.callInDate}")
	private Date callInDate;		//date originally contacted by client and job created
		
	@ManagedProperty(value="#{JobBean.targetDate}")
	private Date targetDate;		//default target date should initially default to 3 weeks from call-in date
	
	@ManagedProperty(value="#{JobBean.targetDateEnd}")
	private Date targetDateEnd;		//used during scheduling to show installation appt has length
	
	@ManagedProperty(value="#{JobBean.rushJobFlag}")
	private boolean rushJobFlag = false; 
	
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Job Location
	
	@ManagedProperty(value="#{JobBean.jobLocationType}")
	private String jobLocationType;	//whether Subdiv/Lot or StreetAddress

	@ManagedProperty(value="#{JobBean.jobAddrSubdivisionLot}")	
	private String jobAddrSubdivisionLot;	//Job Address, if using Subdivision/Lot# format
	
	@ManagedProperty(value="#{JobBean.jobAddr_Line1}")	
	private String jobAddr_Line1;			//Job Address, 1st line, if using Street Address
	
	@ManagedProperty(value="#{JobBean.jobAddr_Line2}")	
	private String jobAddr_Line2;			//Job Address, 1st line, if using Street Address
	
	@ManagedProperty(value="#{JobBean.jobAddr_City}")	
	private String jobAddr_City;
	
	@Setter(AccessLevel.NONE)	//special setter below to make sure input is capitalized
	@ManagedProperty(value="#{JobBean.jobAddr_State}")	
	private String jobAddr_State;
	
	@ManagedProperty(value="#{JobBean.jobAddr_Zip}")	
	private String jobAddr_Zip;
	
	

	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	
	

	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Constructor stuff
	
	public JobBean() {
		//System.out.println(" XX JobBean XX Created empty instance of JobBean"); 
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
		//this.jobStatus = "New";
		
		Calendar cal = Calendar.getInstance();	//cal will be used to calculate target date info
		
		//this.callInDate = new Date();	//set default to today's date
		this.callInDate = cal.getTime();//use cal to avoid differences in EDT and EST
		
		//set default for rushJobFlag
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
		
		
		
		//set time to default of 6:30 am
		cal.set(Calendar.HOUR_OF_DAY,6);
		cal.set(Calendar.MINUTE,30);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
				
		this.targetDate = cal.getTime();
		
		System.out.println(" XX JobBean.init() XX targetDate set to: " + targetDate);  // dow mon dd hh:mm:ss zzz yyyy
		
		//for display in a schedule (looks like a calendar) we need to track the end of the installation appt
		//add an arbitrary hour. User will set actual appt length during scheduling 
		//  NOTE: This targetDateEnd field will be created with the arbitrary hour the first time the schedule is displayed.
		//		No need to set it here.
		//cal.set(Calendar.HOUR_OF_DAY,8);
		//this.targetDateEnd = cal.getTime();
	
		System.out.println(" XX JobBean.init() XX  Call to init()");
    }
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	

	//function stuff	

	public void saveToJobTable() {
    	System.out.println(" XX JobBean XX");
    	System.out.println(" XX JobBean XX Call to .saveToJobTable()");
    	
    	//TODO: Check for existing to avoid duplicates or overwriting another row of data
    	//TODO: Possibly make separate save function for initial save
    	
    	//set contact name values
    	this.jobContactFullName = this.jobContactFirstName + " " + this.jobContactLastName;
    	this.jobContactFullNameLastFirst = this.jobContactLastName + ", " + this.jobContactFirstName;
    	
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
    	 
    	// set up variables to concatenate later into the partitionKey and jobName fields
    	String pKey1, pKey2, pKey3, pKey4;
    	
    	//set up format of date
    	//String pattern = "yyyy-MM-dd";
    	//SimpleDateFormat ymdDateFormat = new SimpleDateFormat(pattern);
    	//pKey1 = ymdDateFormat.format(this.callInDate); 
    	
    	pKey1 = convertSimpleDate(this.callInDate);	//convert to simple date format, without the time info
    	
    	pKey2 = "[" + jobMake + "]";	//Rails or Mailbox or Custom

    	pKey3 = rowKey;	//customer name
 

    	//figure out which type of address to use, based on the jobLocationType
    	String jobAddr_Line2CommaFix;
    	if(jobLocationType.equals("StreetAddress")) {
    		if(jobAddr_Line2 != null && !jobAddr_Line2.isEmpty()) {	//if no value for jobAddr_Line2, then leave out the comma
    			jobAddr_Line2CommaFix = ", " + jobAddr_Line2;
    			//System.out.println(" XX JobBean XX NOT null. jobAddr_Line2 = [" + jobAddr_Line2 + "]");
    		} else {
    			jobAddr_Line2CommaFix = "";
    			//System.out.println(" XX JobBean XX IS null.");
    		}
    		pKey4 = jobAddr_Line1 + jobAddr_Line2CommaFix + ", " + jobAddr_City + ", " + jobAddr_State + ", " + jobAddr_Zip;
    	} else {
    		pKey4 = jobAddrSubdivisionLot;    		
    	}
    	
    	//debug out
    	//System.out.println(" XX JobBean XX pKey1 = " + pKey1);
    	//System.out.println(" XX JobBean XX pKey2 = " + pKey2);
    	//System.out.println(" XX JobBean XX pKey3 = " + pKey3);
    	//System.out.println(" XX JobBean XX pKey4 = " + pKey4);
    	
    	
    	// concatenate them
    	partitionKey = pKey1 + " " + pKey2 + " " + pKey3 + " - " + pKey4;
    	
    	//also use them to set the jobName
    	jobName = pKey3 + " - " + pKey4 + " " + pKey2;
    	
    	
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
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//special date functions
	public String convertSimpleDate(Date dateToConvert) {
		//takes java Date and returns simple version
		
    	//set up format of date
    	//String pattern = "yyyy-MM-dd";
    	String pattern = "MM/dd/yyyy hh:mm a";
    	SimpleDateFormat ymdDateFormat = new SimpleDateFormat(pattern);
    	return ymdDateFormat.format(dateToConvert); 
	}

	public String getSimpleCallInDate() {
		return convertSimpleDate(callInDate);
	}
	
	public String getSimpleTargetDate() {
		return convertSimpleDate(targetDate);
	}

	public String getSimpleTargetDateEnd() {
		return convertSimpleDate(targetDateEnd);
	}	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//special getters and setters
	public void setJobAddr_State(String jobAddr_State) {	//Make sure to capitalize. But can't capitalize null, so check for it
		if(jobAddr_State == null) {
			this.jobAddr_State = jobAddr_State;
		} else {
			this.jobAddr_State = jobAddr_State.toUpperCase();
		}
	}

	public String getJobProgress() {
		//don't allow null return value.  Send 5% if null
		if(jobProgress == null) {
			return "5";
		} else {
			return jobProgress;
		}
	}
	
    public void setJobProgressRandom() {
    	System.out.println(" XX JobBean^^setJobProgressRandom XX Call to setJobProgressRandom" );
 		//this.jobProgress = percentage;	//disabled for debug, see below
 		
 		//for debug purposes, set to a random number between 10 and 100
    	

    	Random rand = new Random();
    	
    	//100 is the maximum and the 10 is our minimum
    	int  n = rand.nextInt(100) + 10;
    	
    	
    	this.jobProgress = Integer.toString(n);
    	//return Integer.toString(n);
    	System.out.println(" XX JobBean^^setJobProgressRandom XX Created random value for jobProgress: " + this.jobProgress ); 
     }
	


}
