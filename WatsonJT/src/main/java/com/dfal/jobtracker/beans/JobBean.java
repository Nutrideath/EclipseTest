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
	//if this is a new empty job, need to set up a few basic properties.  Otherwise, leave them alone.	
	if(jobStatus == null) {
		
		
		Calendar cal = Calendar.getInstance();	//cal will be used to calculate target date info
		
		//this.callInDate = new Date();	//set default to today's date
		//this.targetDate = new Date();
		//this.targetDateEnd = new Date();
	
		
		this.callInDate = cal.getTime();//use cal to avoid differences in EDT and EST
		
	//set default for boolean flags
		this.rushJobFlag = false;	//is this a rush job?
		//this.rushJobFlag = true;
		
		this.promisedFlag = false;	//has target date been promised?
		//this.promisedFlag = true;
		
		this.lockedFlag = false;	//has target date been locked?
		//this.lockedFlag = true;
		
		
		//set default for ableToInstallFlag (indicates if unable to install on first installation visit)
		this.ableToInstallFlag = true;
		
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

		cal = Calendar.getInstance();	//reset cal
		//adjust to one hour later than targetDate
		cal.add(Calendar.DAY_OF_YEAR, 21);
		cal.set(Calendar.HOUR_OF_DAY,7);
		cal.set(Calendar.MINUTE,30);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		
		this.targetDateEnd = cal.getTime();
		
		System.out.println(" XX JobBean.init() XX targetDateEnd set to: " + targetDateEnd);
		
		System.out.println(" XX JobBean.init() XX  Call to init()");
	}
	
    }
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// "global" properties
	//@Setter(AccessLevel.NONE)	//special setter below
	@ManagedProperty(value="#{JobBean.jobStatus}")
	private String jobStatus;		//tracks the job's lifecycle
	
	
	
	@ManagedProperty(value="#{JobBean.jobStatusShort}")
	private String jobStatusShort;	//shortened version of each status, shown where full jobStatus takes too much room
	
	//@Getter(AccessLevel.NONE)	//special getter below
	//@Setter(AccessLevel.NONE)	//special setter below
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
	//	createNewJob.xhtml
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
	
	//@Setter(AccessLevel.NONE)	//special setter below, to make sure if set true, lockedFlag is also set to true
	@ManagedProperty(value="#{JobBean.promisedFlag}")	//is target date promised
	private boolean promisedFlag = false;
	
	@ManagedProperty(value="#{JobBean.lockedFlag}")		//is target date locked
	private boolean lockedFlag = false;
	
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
	//	jobForm.xhtml
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	//Tabs
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Assign Measurer
	
	@ManagedProperty(value="#{JobBean.measurer}")	
	private String measurer;
	
	@ManagedProperty(value="#{JobBean.dateReadyToMeasure}")
	private Date dateReadyToMeasure;		//date marked 'Ready To Measure'
	
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Measure

	@ManagedProperty(value="#{JobBean.measureScheduleMeetOptions}")
	private String measureScheduleMeetOptions;
	
	@ManagedProperty(value="#{JobBean.measureScheduleJobsiteVisitHistory}")	
	private String measureScheduleJobsiteVisitHistory;
	
	@ManagedProperty(value="#{JobBean.measurementNotesGeneral}")	
	private String measureNotesGeneral;
	
	@ManagedProperty(value="#{JobBean.measurementNotesForEstimator}")
	private String measureNotesForEstimator;
	
	@ManagedProperty(value="#{JobBean.measurementNotesForFabricator}")
	private String measureNotesForFabricator;
	
	@ManagedProperty(value="#{JobBean.measurementNotesForInsaller}")
	private String measureNotesForInstaller;
	
	@ManagedProperty(value="#{JobBean.dateToMeasure}")
	private Date dateToMeasure;		//date scheduled to go measure
	
	@ManagedProperty(value="#{JobBean.dateToMeasureEnd}") 
	private Date dateToMeasureEnd;		//date scheduled to go measure
	
	@ManagedProperty(value="#{JobBean.dateMeasured}")
	private Date dateMeasured;		//date measurements completed
	
	@ManagedProperty(value="#{JobBean.dateReadyToEstimate}")
	private Date dateReadyToEstimate;		//date marked 'Ready To Estimate'
	
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Estimate
	
	@ManagedProperty(value="#{JobBean.dateEstimated}")
	private Date dateEstimated;		//date estimation completed
	
	@ManagedProperty(value="#{JobBean.datePendingApproval}")
	private Date datePendingApproval;		//date marked 'Pending Approval'
	
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Approve
	
	@ManagedProperty(value="#{JobBean.dateApproved}")
	private Date dateApproved;		//date approved
	
	@ManagedProperty(value="#{JobBean.dateFabricationStart}")
	private Date dateFabricationStart;		//date fabrication begun
	
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Fabricate
	
	@ManagedProperty(value="#{JobBean.dateFabricationEnd}")
	private Date dateFabricationEnd;		//date fabrication finished
	
	@ManagedProperty(value="#{JobBean.dateGrindingStart}")
	private Date dateGrindingStart;		//date grinding begun
	
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Grind
	
	@ManagedProperty(value="#{JobBean.dateGrindingEnd}")
	private Date dateGrindingEnd;		//date grinding finished
	
	@ManagedProperty(value="#{JobBean.datePaintingStart}")
	private Date datePaintingStart;		//date painting begun
	
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Paint
	
	@ManagedProperty(value="#{JobBean.datePaintingEnd}")
	private Date datePaintingEnd;		//date painting finished
	
	@ManagedProperty(value="#{JobBean.datePendingInstallerAssg}")
	private Date datePendingInstallerAssg;		//date marked 'Pending Installer Assignment'
	
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Assign Installer
	
	@ManagedProperty(value="#{JobBean.installer}")	
	private String installer;
	
	@ManagedProperty(value="#{JobBean.dateAssignedForInstallation}")
	private Date dateAssignedForInstallation;		//date marked 'Assigned for Installation'

	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Install	
	
	@ManagedProperty(value="#{JobBean.ableToInstallFlag}")
	private boolean ableToInstallFlag = true;
	
	@ManagedProperty(value="#{JobBean.installTroubleReason}")	
	private String installTroubleReason;
	
	@ManagedProperty(value="#{JobBean.installVisitHistory}")	
	private String installVisitHistory;
	
	@ManagedProperty(value="#{JobBean.dateInstalled}")
	private Date dateInstalled;		//date installed
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~	

	//function stuff	
	public void saveToJobTable() {
    	System.out.println(" XX JobBean XX");
    	System.out.println(" XX JobBean XX Call to .saveToJobTable()");
      	//create DataManager object
    	DataManagerAzure dataManager = new DataManagerAzure();	//dataManager object to connect to Azure table storage
    	//System.out.println(" XX CustomerBean XX Created DataManagerAzure object");
    	
    	try{
        	// Create an operation to add the new customer to the tablebasics table.
        	TableOperation insertOrMergeThisJob = TableOperation.insertOrMerge(this);

             // Submit the operation to the table service.
             dataManager.jobTable.execute(insertOrMergeThisJob);
             //return "empty";	//operation succeeded!!
             System.out.println(" XX JobBean.saveToJobTable XX Job saved successfully.");
        }
    	
    	 catch(TableServiceException e) {
    		//return "error";	//operation failed!!
    		System.out.println(" XX JobBean.saveToJobTable XX [[EXCEPTION]] " + e);
    		System.out.println(" XX JobBean.saveToJobTable XX [[EXCEPTION]] " + e.getExtendedErrorInformation());
    		System.out.println(" XX JobBean.saveToJobTable XX [[EXCEPTION]] " + e.getErrorCode());
         	
    	 } catch(Throwable t) {
        	//return "error";	//operation failed!!
        	System.out.println(" XX JobBean.saveToJobTable XX [[THROWABLE ERROR]] " + t);
       
        } 
    }
	
	public void initialSaveToJobTable() {
		//called first time job is saved from createNewJob.xhtml  Sets initial values before save
		
    	System.out.println(" XX JobBean XX");
    	System.out.println(" XX JobBean XX Call to .initialSaveToJobTable()");
    	    	
    	
    	//TODO: Check for existing to avoid duplicates or overwriting another row of data
    	    	
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
    	String pattern = "yyyy-MM-dd";
    	SimpleDateFormat ymdDateFormat = new SimpleDateFormat(pattern);
    	pKey1 = ymdDateFormat.format(this.callInDate); 
    	
    	//pKey1 = convertSimpleDateWithDashes(this.callInDate);	//convert to simple date format, without the time info
    	
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
    	
    	
    	System.out.println(" XX JobBean.initialSaveToJobTable XX partitionKey = " + partitionKey);
    	System.out.println(" XX JobBean.initialSaveToJobTable XX rowKey = " + rowKey);
   	
    	//create DataManager object
    	DataManagerAzure dataManager = new DataManagerAzure();	//dataManager object to connect to Azure table storage
    	//System.out.println(" XX CustomerBean XX Created DataManagerAzure object");
    	
    	try{
        	// Create an operation to add the new customer to the tablebasics table.
        	TableOperation insertOrMergeThisJob = TableOperation.insertOrMerge(this);

             // Submit the operation to the table service.
             dataManager.jobTable.execute(insertOrMergeThisJob);
             //return "empty";	//operation succeeded!!
             System.out.println(" XX JobBean.initialSaveToJobTable XX Job saved successfully.");
        }
    	
    	 catch(TableServiceException e) {
    		//return "error";	//operation failed!!
    		System.out.println(" XX JobBean.initialSaveToJobTable XX [[EXCEPTION]] " + e);
    		System.out.println(" XX JobBean.initialSaveToJobTable XX [[EXCEPTION]] " + e.getExtendedErrorInformation());
    		System.out.println(" XX JobBean.initialSaveToJobTable XX [[EXCEPTION]] " + e.getErrorCode());
    		System.out.println(" XX JobBean.initialSaveToJobTable XX [[EXCEPTION]]  JobBean info:");
    		System.out.println(" XX JobBean.initialSaveToJobTable XX [[EXCEPTION]] ");
    		System.out.println(this.toString());
    		System.out.println(" XX JobBean.initialSaveToJobTable XX [[EXCEPTION]] ");
         	
    	 } catch(Throwable t) {
        	//return "error";	//operation failed!!
        	System.out.println(" XX JobBean.initialSaveToJobTable XX [[THROWABLE ERROR]] " + t);
       
        } 
    }
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//special date functions
	
	public void setDateFieldToNow(String fieldName) {
		//looks for the property of type date and sets its value to current date/time
	
		 
		switch(fieldName) 
		{ 
		//createNewJob
        	case "callInDate": 
        		this.callInDate = new Date();
        		break;
        	case "targetDate": 
        		this.targetDate = new Date();
        		break;
        	case "targetDateEnd": 
        		this.targetDateEnd = new Date(); 
        		break;
        //jobForm
        	//Assign Measurer tab
        	case "dateReadyToMeasure": 
        		this.dateReadyToMeasure = new Date(); 
        		break;
        		
        	//Measure tab	
        	case "dateToMeasure": 
        		this.dateToMeasure = new Date();
        		break;
        	case "dateMeasured": 
        		this.dateMeasured = new Date();
        		break;
        	case "dateReadyToEstimate": 
        		this.dateReadyToEstimate = new Date();
        		break;
        		
        	//Estimate tab
        	case "dateEstimated": 
        		this.dateEstimated = new Date();
        		break;
        	case "datePendingApproval": 
        		this.datePendingApproval = new Date();
        		break;
        	
        	//Approve tab	
        	case "dateApproved": 
        		this.dateApproved = new Date();
        		break;
        	case "dateFabricationStart": 
        		this.dateFabricationStart = new Date();
        		break;
        		
        	//Fabricate tab	
        	case "dateFabricationEnd": 
        		this.dateFabricationEnd = new Date();
        		break;	
        	case "dateGrindingStart": 
        		this.dateGrindingStart = new Date();
        		break;	
        	
        	//Grind tab	
        	case "dateGrindingEnd": 
        		this.dateGrindingEnd = new Date();
        		break;	
        	case "datePaintingStart": 
        		this.datePaintingStart = new Date();
        		break;	   
        	
        	//Paint tab
        	case "datePaintingEnd": 
        		this.datePaintingEnd = new Date();
        		break;
        	case "datePendingInstallerAssg": 
        		this.datePendingInstallerAssg = new Date();
        		break;	
        		
        	//Assign Installer tab	
        	case "dateAssignedForInstallation": 
        		this.dateAssignedForInstallation = new Date();
        		break;	
        		
        	//Install tab	
        	case "dateInstalled": 
        		this.dateInstalled = new Date();
        		break;	
        	default: 
        			
		} 
		
	}
	
	public void setDateFieldToNull(String fieldName) {
		//looks for the property of type date and sets its value to current date/time
	
		 
		switch(fieldName) 
		{ 
		//createNewJob
        	case "callInDate": 
        		this.callInDate = null;
        		break;
        	case "targetDate": 
        		this.targetDate = null;
        		break;
        	case "targetDateEnd": 
        		this.targetDateEnd = null; 
        		break;
        //jobForm
        	//Assign Measurer tab
        	case "dateReadyToMeasure": 
        		this.dateReadyToMeasure = null; 
        		break;
        		
        	//Measure tab	
        	case "dateToMeasure": 
        		this.dateToMeasure = null;
        		break;
        	case "dateMeasured": 
        		this.dateMeasured = null;
        		break;
        	case "dateReadyToEstimate": 
        		this.dateReadyToEstimate = null;
        		break;
        		
        	//Estimate tab
        	case "dateEstimated": 
        		this.dateEstimated = null;
        		break;
        	case "datePendingApproval": 
        		this.datePendingApproval = null;
        		break;
        	
        	//Approve tab	
        	case "dateApproved": 
        		this.dateApproved = null;
        		break;
        	case "dateFabricationStart": 
        		this.dateFabricationStart = null;
        		break;
        		
        	//Fabricate tab	
        	case "dateFabricationEnd": 
        		this.dateFabricationEnd = null;
        		break;	
        	case "dateGrindingStart": 
        		this.dateGrindingStart = null;
        		break;	
        	
        	//Grind tab	
        	case "dateGrindingEnd": 
        		this.dateGrindingEnd = null;
        		break;	
        	case "datePaintingStart": 
        		this.datePaintingStart = null;
        		break;	   
        	
        	//Paint tab
        	case "datePaintingEnd": 
        		this.datePaintingEnd = null;
        		break;
        	case "datePendingInstallerAssg": 
        		this.datePendingInstallerAssg = null;
        		break;	
        		
        	//Assign Installer tab	
        	case "dateAssignedForInstallation": 
        		this.dateAssignedForInstallation = null;
        		break;	
        		
        	//Install tab	
        	case "dateInstalled": 
        		this.dateInstalled = null;
        		break;	
        	default: 
        			
		} 
		
	}
	
	public String convertSimpleDateWithDashes(Date dateToConvert) {
		//takes java Date and returns simple version
		
    	//set up format of date
    	String pattern = "yyyy-MM-dd";
    	//String pattern = "MM/dd/yyyy hh:mm a";
    	SimpleDateFormat ymdDateFormat = new SimpleDateFormat(pattern);
    	return ymdDateFormat.format(dateToConvert); 
	}
	
	public String convertSimpleDate(Date dateToConvert) {
		//takes java Date and returns simple version
		
    	//set up format of date
    	//String pattern = "yyyy-MM-dd";
    	String pattern = "MM/dd/yyyy hh:mm a";
    	SimpleDateFormat ymdDateFormat = new SimpleDateFormat(pattern);
    	return ymdDateFormat.format(dateToConvert); 
	}
	
	public String convertSimpleDateNoTime(Date dateToConvert) {
		//takes java Date and returns simple version
		
    	//set up format of date
    	//String pattern = "yyyy-MM-dd";
    	//String pattern = "MM/dd/yyyy hh:mm a";
    	String pattern = "MM/dd/yyyy";
    	SimpleDateFormat ymdDateFormat = new SimpleDateFormat(pattern);
    	return ymdDateFormat.format(dateToConvert); 
	}

	public String getSimpleCallInDate() {
		return convertSimpleDate(callInDate);	//pattern "MM/dd/yyyy hh:mm a"
	}
	
	public String getSimpleTargetDate() {
		return convertSimpleDate(targetDate);	//pattern "MM/dd/yyyy hh:mm a"
	}

	public String getSimpleTargetDateEnd() {
		return convertSimpleDate(targetDateEnd);//pattern "MM/dd/yyyy hh:mm a"
	}	
	
	public String getSimpleTargetDateWithDashes() {
		return convertSimpleDateWithDashes(targetDate);	//pattern "yyyy-MM-dd"
	}
	
	public String getSimpleTargetDateNoTime() {
		return convertSimpleDateNoTime(targetDate);	//pattern "MM/dd/yyyy"
	}
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//special getters and setters
	
	public void checkPromisedFlag() {
		if(promisedFlag) {
			//if promisedFlag is true, need to also set lockedFlag to true
			
			this.lockedFlag = true;
		}
	}
	
	public void setJobAddr_State(String jobAddr_State) {	//Make sure to capitalize. But can't capitalize null, so check for it
		if(jobAddr_State == null) {
			this.jobAddr_State = jobAddr_State;
		} else {
			this.jobAddr_State = jobAddr_State.toUpperCase();
		}
	}
/*
	public void setJobStatus(String status) {
		this.jobStatus = status;
		System.out.println(" XX JobBean^^setJobStatus XX Call to setJobStatus" );
	}
	
	
	public void setJobProgress(String progress) {
		this.jobProgress = progress;
		System.out.println(" XX JobBean^^setJobProgress XX Call to setJobProgress" );
	}
	
	public String getJobProgress() {
		//don't allow null return value. (Crashes job progress barchart on dashboard) Send 5% if null
		if(jobProgress == null) {
			return "5";
		} else {
			return jobProgress;
		}
	}
*/
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
    
/*	
    public String getJobStatusShort() {
    	if (jobStatus == null) {
    		return "No Status";
    	} else {
    		
    		String str = jobStatus; 
    		switch(str) 
    		{ 
            	case "New": 
            		return "New";
            	case "Awaiting Measurer Assignment": 
            		return "AMA"; 
            	case "Ready to Measure": 
            		return "RTM"; 
            	case "Ready for Estimate": 
            		return "RFE";
            	case "Pending Approval": 
            		return "PendAppr"; 
            	case "Fabrication": 
            		return "Fab";
            	case "Grinding": 
            		return "Grnd";
            	case "Painting": 
            		return "Paint"; 
            	default: 
            		return "New";
    		} 
        
    	}
    }
  */  

}
