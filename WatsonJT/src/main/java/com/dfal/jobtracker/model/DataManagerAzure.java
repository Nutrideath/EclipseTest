package com.dfal.jobtracker.model;

import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.table.*;

public class DataManagerAzure {
	
    /**
     * MODIFY THIS!
     * 
     * Stores the storage connection string.
     * 
     * There are two below - one for use online at Azure, and the other for development (local, uses Azure Storage Emulator)
     */
	/*
	//This is the actual, online Azure account
    public static final String storageConnectionString = "DefaultEndpointsProtocol=https;"
            + "AccountName=dfalstoragetest;"
            + "AccountKey=GKPPduvRKUCV8h7ik7q9BsOlf8DPS8k/6J2VFl5mrCpGOQqTC7qJ4oRaokrBlWfMPwkszTJCPaG3L+HiGJWyiQ==";
    */
	
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
     */
    public static final String CUSTOMER_TABLE_NAME = "WatsonCustomers";
    public static final String JOB_TABLE_NAME = "WatsonJobs";
    
    //set up objects
    protected static CloudTableClient tableClient = null;
    protected static CloudTable azureTable = null;
    public CloudTable customerTable = null; 
    public CloudTable jobTable = null; 
    
    
    public DataManagerAzure () {
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
    		System.out.println("Could not create connection to one or more table storage objects: " + t.getMessage());
    	}
    }

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
