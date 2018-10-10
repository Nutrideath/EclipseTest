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
	
	private List<String> customerNames;
	private List<CustomerBean> customerBeans;	
	
        
    public DataManagerAzure dataManager;		//handles interaction with the database
    
    
    
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  	//Constructor stuff   	
  	public DataService() {      
        System.out.println(" XX DataService XX Created new empty instance of DataService");
    }

  	
  	
    @PostConstruct
    public void init() {
    	System.out.println(" XX DataService^^init() XX init() started");
        customerNames = new ArrayList<String>(); 
        customerBeans = new ArrayList<CustomerBean>();
           
    //create DataManager object
    	dataManager = new DataManagerAzure();	//dataManager object to connect to Azure table storage
    	System.out.println(" XX DataService^^init() XX Created DataManagerAzure object");
  
    //set up customerNames list
    	// Specify a query (get all records in this case)
        TableQuery<CustomerBean> query = TableQuery.from(CustomerBean.class);           
        
        // Loop through the results, loading customer names into array
        for (CustomerBean entity : dataManager.customerTable.execute(query)) {
        	customerBeans.add(entity);
        	customerNames.add(entity.getRowKey()); //rowKey represents customer name                
        }
    	        
        Collections.sort(customerNames);		//sort the names
        customerNames.add(0, " ");        		//make first one blank (so nothing looks like default option)

        
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
    
    public void refreshCustomerLists(){
    	//refresh both local lists - of customer names and customer beans - from the database
    	System.out.println(" XX DataService^^refreshCustomerLists() XX call received");
    	
    	int cnt = 0; //counter
    	
    	//blank out the lists, so don't create duplicates.  gc will clean up memory
    	customerNames = new ArrayList<String>(); 
    	customerBeans = new ArrayList<CustomerBean>();
    	
    	// Specify a query (get all records in this case)
        TableQuery<CustomerBean> query = TableQuery.from(CustomerBean.class);           
        
        // Loop through the results, loading customer names and objects into arrays
        for (CustomerBean entity : dataManager.customerTable.execute(query)) { 
        	customerBeans.add(entity);
        	customerNames.add(entity.getRowKey()); //rowKey represents customer name  
        	cnt++;	//increment counter
        }
    	        
        Collections.sort(customerNames);		//sort the names
        customerNames.add(0, " ");        		//make first one blank (so nothing looks like default option)
        
        System.out.println(" XX DataService^^refreshCustomerLists() XX both ArrayLists updated, " + Integer.toString(cnt) + " customers.");
       
    }
    
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
}