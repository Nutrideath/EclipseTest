package com.dfal.jobtracker.beans;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
//import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import com.microsoft.azure.storage.table.TableServiceEntity;
//import java.util.Date;
import com.dfal.jobtracker.model.*;
import com.microsoft.azure.storage.table.*; 
/*
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
*/

@ManagedBean(name = "customerBean")
@ViewScoped
public class CustomerBean extends TableServiceEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String partitionKey;	//used in Azure storage tables as part of unique id
	
	@ManagedProperty(value="#{CustomerBean.rowKey}")
	private String rowKey;			//used in Azure storage tables as part of unique id
    
    @ManagedProperty(value="#{CustomerBean.lastName}")
    private String lastName;
    
    @ManagedProperty(value="#{CustomerBean.firstName}")
    private String firstName;
    
    private String fullName;
    private String fullNameLastFirst;
    
    @ManagedProperty(value="#{CustomerBean.email}")
    private String email;
    
    @ManagedProperty(value="#{CustomerBean.phoneNumber}")
    private String phoneNumber;
    
    @ManagedProperty(value="#{CustomerBean.addr_Line1}")
    private String addr_Line1;
    
    @ManagedProperty(value="#{CustomerBean.addr_Line2}")
    private String addr_Line2;

    @ManagedProperty(value="#{CustomerBean.addr_City}")
    private String addr_City;
    
    @ManagedProperty(value="#{CustomerBean.addr_State}")
    private String addr_State;
    
    @ManagedProperty(value="#{CustomerBean.addr_Zip}")
    private String addr_Zip;
    
    
    public CustomerBean(String _corp, String _lastName, String _firstName, String _email) {
    	this.partitionKey = _lastName + ", " + _firstName + "<" + _email + ">";
    	this.rowKey = _corp;
    	this.lastName = _lastName;
    	this.firstName = _firstName;
    	this.email = _email;
    	this.fullName = _firstName + " " + _lastName;
    	this.fullNameLastFirst = _lastName + ", " + _firstName;
        }

        public CustomerBean() { 
        	//System.out.println(" XX CustomerBean XX Created empty instance of CustomerBean"); 
        }
        
        public void saveToCustomerTable() {
        	//System.out.println(" XX CustomerBean XX Call to .saveToCustomerTable()");
        	
        	//set up concatenated fields, if needed
        	this.partitionKey = this.lastName + ", " + this.firstName + "<" + this.email + ">";
        	this.fullName = this.firstName + " " + this.lastName;
        	this.fullNameLastFirst = this.lastName + ", " + this.firstName;
        	
        	//System.out.println(" XX CustomerBean XX Set concatenated fields...");
        	
        	
        	//create DataManager object
        	DataManagerAzure dataManager = new DataManagerAzure();	//dataManager object to connect to Azure table storage
        	//System.out.println(" XX CustomerBean XX Created DataManagerAzure object");
        	
        	try{
            	// Create an operation to add the new customer to the tablebasics table.
            	TableOperation insertOrMergeThisCustomer = TableOperation.insertOrMerge(this);	//inserts new or updates existing if this matches one already present
            																					//careful with changes to PartitionKey or RowKey - will create new customer, leaving two

                 // Submit the operation to the table service.
                 dataManager.customerTable.execute(insertOrMergeThisCustomer);
                 //return "empty";	//operation succeeded!!
                 
            }
            catch(Throwable t){
            	//return "error";	//operation failed!!
            }
        }
        	
/*  
        public boolean testSaveToCustomerTable() {
        		System.out.println(" XX CustomerBean XX Call to testSaveToCustomerTable()");
        		
            	//set up concatenated fields, if needed
        		String _corp = "TestCorp"; 
        		String _lastName = "TestLast"; 
        		String _firstName = "TestFirst"; 
        		String _email = "Test@email.com";
        		
        		
        		this.partitionKey = _lastName + ", " + _firstName + "<" + _email + ">";
            	this.rowKey = _corp;
            	this.lastName = _lastName;
            	this.firstName = _firstName;
            	this.email = _email;
            	this.fullName = _firstName + " " + _lastName;
            	this.fullNameLastFirst = _lastName + ", " + _firstName;
            	
            	//create DataManager object
            	DataManagerAzure dataManager = new DataManagerAzure();	//dataManager object to connect to Azure table storage
            	
            	
            	try{
                	// Create an operation to add the new customer to the tablebasics table.
                	TableOperation insertThisCustomer = TableOperation.insert(this);

                     // Submit the operation to the table service.
                     dataManager.customerTable.execute(insertThisCustomer);
                     return true;	//operation succeeded!!
                     
                }
                catch(Throwable t){
                	return false;	//operation failed!!
                }
        	
        	
        	
        }
*/
        //partitionKey in Azure table storage is the primary key, so must make it unique.
        public String getPartitionKey() {
        	return this.partitionKey;
        }
        public void setPartitionKey(String _lastName, String _firstName, String _email) {		//set value explicitly
        	this.partitionKey = _lastName + ", " + _firstName + "<" + _email + ">";
        }
        public void setPartitionKey() {
        	this.partitionKey = this.lastName + ", " + this.firstName + "<" + this.email + ">";	//set value using info already present in object
        }
        
        public String getCorp() {
        	return this.rowKey;
        }
        public void setCorp(String corp) {
        	this.rowKey = corp;
        }
        
        public String getFirstName() {
            return this.firstName;
        }
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
        
        public String getLastName() {
            return this.lastName;
        }
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
        
        //use fullName and fullNameLastFirst for sorting
        public String getFullName() {
        	return this.fullName;
        }
        public void setFullName(String fullName) {
            this.fullName = fullName;
        }
        
        public String getFullNameLastFirst() {
        	return this.fullNameLastFirst;
        }
        public void setFullNameLastFirst(String fullNameLastFirst) {
            this.fullNameLastFirst = fullNameLastFirst;
        }
        
        public String getEmail() {
            return this.email;
        }
        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhoneNumber() {
            return this.phoneNumber;
        }
        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
        
        public String getAddr_Line1() {
            return this.addr_Line1;
        }        
        public void setAddr_Line1(String addr_Line1) {
        	this.addr_Line1 = addr_Line1;
        }
        
        public String getAddr_Line2() {
            return this.addr_Line2;
        }
        public void setAddr_Line2(String addr_Line2) {
        	this.addr_Line2 = addr_Line2;
        }
        
        public String getAddr_City() {
            return this.addr_City;
        }
        public void setAddr_City(String addr_City) {
        	this.addr_City = addr_City;
        }
        
        public String getAddr_State() {
            return this.addr_State;
        }
        public void setAddr_State(String addr_State) {
        	this.addr_State = addr_State;
        }
        
        public String getAddr_Zip() {
            return this.addr_Zip;
        }
        public void setAddr_Zip(String addr_Zip) {
        	this.addr_Zip = addr_Zip;
        }
    }
