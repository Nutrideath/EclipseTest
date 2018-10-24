package com.dfal.jobtracker.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ManagedBean;
//import javax.servlet.annotation.WebListener;
//import javax.servlet.ServletContextEvent;
//import javax.servlet.ServletContextListener;

import com.dfal.jobtracker.beans.CustomerBean;
import com.dfal.jobtracker.beans.JobBean;
import com.microsoft.azure.storage.table.TableQuery;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ManagedBean(name="dataService", eager = true)
@SessionScoped
@Getter @Setter @EqualsAndHashCode(callSuper=false) @ToString(callSuper=true, includeFieldNames=true)
//@WebListener
//public class DataService implements ServletContextListener {
public class DataService implements Serializable {
	private static final long serialVersionUID = 1L;
	
	
	/*
	 * //Load this bean at beginning of session
		@Override
	    public void contextInitialized(ServletContextEvent event) {
	        event.getServletContext().setAttribute("dataService", new DataService());
	        System.out.println(" XX DataService XX ServletContextListener started");
	    }
		@Override
		public void contextDestroyed(ServletContextEvent event) {
			System.out.println(" XX DataService XX ServletContextListener destroyed");
		}
    */
	
	public DataManagerAzure dataManager;		//handles interaction with the database
	
	//for customers
	private List<String> customerNames;
	private List<CustomerBean> customerBeans;	
	private int customerCount;		//tracks how many
	
	//for jobs
	private List<String> jobNames;
	private List<JobBean> jobBeans;	    
    private int jobCount;			//tracks how many
    
    
    
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  	//Constructor stuff   	
  	public DataService() {      
        System.out.println(" XX DataService XX Created new empty instance of DataService");
    }

  	
  	
    @PostConstruct
    public void init() {
    	System.out.println(" XX DataService^^init() XX init() started");
    	
        //create DataManager object
    	dataManager = new DataManagerAzure();	//dataManager object to connect to Azure table storage
    	System.out.println(" XX DataService^^init() XX Created DataManagerAzure object");
    	
    //load up customer data	
        customerNames = new ArrayList<String>(); 
        customerBeans = new ArrayList<CustomerBean>();
         
    	// Specify a query of customers (get all records in this case)
        TableQuery<CustomerBean> customerQuery = TableQuery.from(CustomerBean.class);           
        
        // Loop through the results, loading data into array
        customerCount = 0; //counter
        for (CustomerBean entity : dataManager.customerTable.execute(customerQuery)) {
        	customerBeans.add(entity);
        	customerNames.add(entity.getRowKey()); //rowKey represents customer name 
        	customerCount++;	//increment counter
        }
    	        
        Collections.sort(customerNames);		//sort the names
        customerNames.add(0, " ");        		//make first one blank (so nothing looks like default option)

        
        
    //load up job data	
        jobNames = new ArrayList<String>(); 
        jobBeans = new ArrayList<JobBean>();        
        
    	// Specify a query of jobs (get all records in this case)
        TableQuery<JobBean> jobQuery = TableQuery.from(JobBean.class);  

        // Loop through the results, loading data into array
        jobCount = 0; //counter
        for (JobBean entity : dataManager.jobTable.execute(jobQuery)) {
        	jobBeans.add(entity);
        	jobNames.add(entity.getJobName()); //jobName represents short identification of job  
        	jobCount++;		//increment counter
        }        

        Collections.sort(jobNames);		//sort the names
        jobNames.add(0, " ");        	//make first one blank (so nothing looks like default option)

        
        System.out.println(" XX DataService^^init() XX init() completed");
    }
   
    
    
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // getters & setters  
/*    
    public List<String> getCustomerNames() {
        return customerNames;
    } 
    
    public List<CustomerBean> getCustomerBeans() {
        return customerBeans;
    }
*/
    
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//methods
    //TODO: need to add corresponding refresh job methods
    
    public void refreshCustomerLists(){
    	//refresh both local lists - of customer names and customer beans - from the database
    	System.out.println(" XX DataService^^refreshCustomerLists() XX call received");
    	
    	//blank out the lists, so don't create duplicates.  gc will clean up memory
    	customerNames = new ArrayList<String>(); 
    	customerBeans = new ArrayList<CustomerBean>();
    	
    	// Specify a query (get all records in this case)
    	//TODO: Exclude inactive customers from query
        TableQuery<CustomerBean> query = TableQuery.from(CustomerBean.class);           
        
        // Loop through the results, loading customer names and objects into arrays
        customerCount = 0; //counter
        for (CustomerBean entity : dataManager.customerTable.execute(query)) { 
        	customerBeans.add(entity);
        	customerNames.add(entity.getRowKey()); //rowKey represents customer name  
        	customerCount++;	//increment counter
        }
    	        
        Collections.sort(customerNames);		//sort the names
        customerNames.add(0, " ");        		//make first one blank (so nothing looks like default option)
        
        System.out.println(" XX DataService^^refreshCustomerLists() XX both ArrayLists updated, " + Integer.toString(customerCount) + " customers.");
       
    }
    
    public void refreshJobLists() {
    	//refresh both local lists - of job names and job beans - from the database
    	System.out.println(" XX DataService^^refreshJobLists() XX call received");
    	
    	//blank out the lists, so don't create duplicates.  gc will clean up memory
    	jobNames = new ArrayList<String>(); 
        jobBeans = new ArrayList<JobBean>(); 
        
    	// Specify a query of jobs (get all records in this case)
        //TODO: Exclude inactive status jobs from query
        TableQuery<JobBean> jobQuery = TableQuery.from(JobBean.class);  

        // Loop through the results, loading data into array
        jobCount = 0; //counter
        for (JobBean entity : dataManager.jobTable.execute(jobQuery)) {
        	jobBeans.add(entity);
        	jobNames.add(entity.getJobName()); //jobName represents short identification of job  
        	jobCount++;		//increment counter
        }        

        Collections.sort(jobNames);		//sort the names
        jobNames.add(0, " ");        	//make first one blank (so nothing looks like default option)

        
        System.out.println(" XX DataService^^refreshJobLists() XX both ArrayLists updated, " + Integer.toString(jobCount) + " jobs.");
    	
    }
    
    
    /*  //removed 2018.10.23 - FW NOTE: if adding back in, make sure to update the customerCount
    public void refreshCustomerNames(){
    	//refresh local list of customer names from the database
    	System.out.println(" XX DataService^^refreshCustomerNames() XX call received");
    	
    	int cnt = 0; //counter
    	
    	//blank out the list, so don't create duplicates.  gc will clean up memory
    	customerNames = new ArrayList<String>(); 
    	
    	// Specify a query (get all records in this case)
        TableQuery<CustomerBean> query = TableQuery.from(CustomerBean.class);           
        
        // Loop through the results, loading customer names into array
        for (CustomerBean entity : dataManager.customerTable.execute(query)) {        	
        	customerNames.add(entity.getRowKey()); //rowKey represents customer name  
        	cnt++;	//increment counter
        }
    	        
        Collections.sort(customerNames);		//sort the names
        customerNames.add(0, " ");        		//make first one blank (so nothing looks like default option)
        
        System.out.println(" XX DataService^^refreshCustomerNames() XX customerNames ArrayList updated, " + Integer.toString(cnt) + " customers.");
        //return customerNames;
    }
    
    public void refreshCustomerBeans(){
    	//refresh local list of customer beans from the database
    	System.out.println(" XX DataService^^refreshCustomerBeans() XX call received");
    	
    	int cnt = 0; //counter
    	
    	//blank out the list, so don't create duplicates.  gc will clean up memory
    	customerBeans = new ArrayList<CustomerBean>();
    	
    	// Specify a query (get all records in this case)
        TableQuery<CustomerBean> query = TableQuery.from(CustomerBean.class);           
        
        // Loop through the results, loading customer entities into array
        for (CustomerBean entity : dataManager.customerTable.execute(query)) { 
        	customerBeans.add(entity); 
        	cnt++;	//increment counter
        }
        
        System.out.println(" XX DataService^^refreshCustomerBeans() XX customerBeans ArrayList updated, " + Integer.toString(cnt) + " customerBeans.");
        //TODO: These beans are not sorted in the array!!
        //return customerBeans;
    }
    */
}