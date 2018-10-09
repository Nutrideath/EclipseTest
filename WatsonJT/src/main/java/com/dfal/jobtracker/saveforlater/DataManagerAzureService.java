package com.dfal.jobtracker.saveforlater;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
//import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.SessionScoped;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import com.dfal.jobtracker.beans.CustomerBean;
import com.dfal.jobtracker.beans.JobBean;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableOperation;

//@ManagedBean(name="dataManagerAzureService", eager = true)
//@ManagedBean(name="dataManagerAzureService")
//@SessionScoped
//@ApplicationScoped
//@WebListener
public class DataManagerAzureService {
//public class DataManagerAzureService implements ServletContextListener {	
	/*
	//Load this bean at beginning of session
	@Override
    public void contextInitialized(ServletContextEvent event) {
        event.getServletContext().setAttribute("dataManagerAzureService", new DataManagerAzureService());
        System.out.println(" XX DataManagerAzureService XX ServletContextListener started");
    }
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		System.out.println(" XX DataManagerAzureService XX ServletContextListener destroyed");
	}
	*/
	
    /**
     * MODIFY THIS!
     * 
     * Stores the storage connection string.
     * 
     * There are two below - one for use online at Azure, and the other for development (local, uses Azure Storage Emulator)
     */
	//This is the actual, online Azure account
    //public static final String storageConnectionString = "DefaultEndpointsProtocol=https;"
    //        + "AccountName=dfalstoragetest;"
    //        + "AccountKey=GKPPduvRKUCV8h7ik7q9BsOlf8DPS8k/6J2VFl5mrCpGOQqTC7qJ4oRaokrBlWfMPwkszTJCPaG3L+HiGJWyiQ==";
    
    //This is the local Development account using the Azure Storage Emulator
    public static final String storageConnectionString = "DefaultEndpointsProtocol=http;"
    + "AccountName=devstoreaccount1;"
    + "AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;"
    + "BlobEndpoint=http://127.0.0.1:10000/devstoreaccount1;"
    + "TableEndpoint=http://127.0.0.1:10002/devstoreaccount1;"
    + "QueueEndpoint=http://127.0.0.1:10001/devstoreaccount1;";
    
  
    /**
     * MODIFY THIS TOO!
     * 
     * Stores the actual names of the client's customer table and job table in the Azure table storage account.
     * Modify for each client/application
     * 
     */
    public static final String CUSTOMER_TABLE_NAME = "WatsonCustomers";
    public static final String JOB_TABLE_NAME = "WatsonJobs";
    
    //set up objects
    protected static CloudTableClient tableClient = null;
    protected static CloudTable azureTable = null;
    public CloudTable customerTable = null; 
    public CloudTable jobTable = null; 
    
    //for data lookup
    
    private List<CustomerBean> customers;
    
    @ManagedProperty(value="#{DataManagerAzureService.customerList}")
    private List<String> customerList;
    
    private List<JobBean> jobs;
    
    private List<String> jobList;
    
    
    @PostConstruct
    public void init() {
    	System.out.println(" XX DataManagerAzureService XX Created DataManagerAzureService object");
    	customers = new ArrayList<CustomerBean>();
    	customerList = new ArrayList<String>();
    	jobs = new ArrayList<JobBean>();
    	jobList = new ArrayList<String>();
    	
    	//create DataManager object
    	//DataManagerAzure dataManager = new DataManagerAzure();	//dataManager object to connect to Azure table storage
    	//System.out.println(" XX DataManagerAzureService XX Created DataManagerAzureService object");
    	
    	//set up CloudStorageAccount and TableClient objects
    	try {
    		// Setup the cloud storage account.
    		CloudStorageAccount account = CloudStorageAccount.parse(storageConnectionString);
    	
    		// Create a table service client.
    		tableClient = account.createCloudTableClient();	
    		
    		// Create a connection to the customerTable. Create the table if it doesn't exist.
    		customerTable = tableClient.getTableReference(CUSTOMER_TABLE_NAME);
    		customerTable.createIfNotExists();
        
    		// Create a connection to the jobsTable. Create the table if it doesn't exist.
    		jobTable = tableClient.getTableReference(JOB_TABLE_NAME);
    		jobTable.createIfNotExists();
    	}
    	catch (Throwable t) {
    		System.out.println(" XX DataManagerAzureService XX Could not create connection to one or more table storage objects: " + t.getMessage());
    	}
    	
    	
    }
    
    
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //	Customer table operations
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~    
    public boolean insertOrMergeCustomer(CustomerBean customerToSave) {
    	System.out.println(" XX DataManagerAzureService XX received call to .insertOrMergeCustomer");
    	try{
        	// add the new customer to the customerTable.
        	TableOperation insertOrMergeThisCustomer = TableOperation.insertOrMerge(customerToSave);	//inserts new or updates existing if this matches one already present
        																								//careful with changes to PartitionKey or RowKey - will create new customer, leaving two

             // Submit the operation to the table service.
             customerTable.execute(insertOrMergeThisCustomer);
             System.out.println(" XX DataManagerAzureService XX Save operation succeeded.");
             return true;	//operation succeeded!!
             
        }
        catch(Throwable t){
        	 System.out.println(" XX DataManagerAzureService XX !!! Save operation failed !!!");
        	 System.out.println("Error: " + t.getMessage());
        	return false;	//operation failed!!
        }
    }
    
    
    public List<CustomerBean> getCustomers() {	//returns an array of CustomerBean objects
    	//Populate customers 
    	
        return customers;
    } 
    
    
    public void setCustomerList(List<String> customerList) {
    	this.customerList = customerList;
    }
    public List<String> getCustomerList() {	//returns an array of customers by name (e.g. - for a selection list)
    	  	
        return customerList;
    } 
    
    
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //	Job table operations
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~    
    
    
    
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //	Generic table operations
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public CloudTable getCustomerTable() {
    	return this.customerTable;
    }
    public CloudTable getJobTable() {
    	return this.jobTable;
    }
    
    //return a generically named table from Azure table storage
    public CloudTable getTableConnection(String tableName) {
    	//returns a connection to the requested table

    	//get the table that was requested
        try{ 
            azureTable = tableClient.getTableReference(tableName);
            
         // Create the table if it doesn't already exist.
            azureTable.createIfNotExists();
    	
        }
        catch (Throwable e) {
          System.out.println("Could not connect to CloudTable DB: " + e.getMessage());
        }
        
        return azureTable;        
    	}
}

