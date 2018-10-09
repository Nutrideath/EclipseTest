package com.dfal.jobtracker.beans;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

//import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
//import javax.servlet.ServletContext;
//import javax.servlet.ServletContextEvent;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.serenity.domain.Car;

//import org.apache.catalina.core.ApplicationContext;

import com.microsoft.azure.storage.table.TableServiceEntity;
//import java.util.Date;
import com.dfal.jobtracker.model.*;
import com.microsoft.azure.storage.table.*;
//import com.dfal.jobtracker.model.DataManagerAzureService;;
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
    
    @ManagedProperty(value="#{CustomerBean.fullName}")
    private String fullName;
    
    @ManagedProperty(value="#{CustomerBean.fullNameLastFirst}")
    private String fullNameLastFirst; 
    
    @ManagedProperty(value="#{CustomerBean.customerType}")
    private String customerType;	
    
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
    
    @ManagedProperty(value="#{CustomerBean.companyInputDisabledFlag}")
    private boolean companyInputDisabledFlag = false;	//determines whether Company name is editable (it is auto-set to true if customer type is "Residential")
    
    @ManagedProperty(value="#{CustomerBean.companyInputLabel}")
    private String companyInputLabel;	//Value is displayed at company name input (rowKey property)
    
    //@ManagedProperty(value="#{dataManagerAzureService}")	//get the SessionScoped data manager already in memory (or instantiate it if it doesn't exist)
    //private DataManagerAzureService dataManager;

    //The DataService bean is Session scoped, and so loads upon first use and remains available to all other objects until end of session.
    //@ManagedProperty(value="#{dataService}")	//get the SessionScoped data service already in memory (or instantiate it if it doesn't exist)
    //private DataService dataService;
    
    //@ManagedProperty(value="#{dataManager}")
    //private DataManagerAzure dataManager;
    
    //@ManagedProperty(value="#{dataBean}")
    //private DataBean dataBean;
    
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Constructor stuff   	
	public CustomerBean() { 
    	System.out.println(" XX CustomerBean XX Created empty instance of CustomerBean"); 
    }
	 
	public CustomerBean(String _corp, String _lastName, String _firstName, String _email) {
    	this.partitionKey = _lastName + ", " + _firstName + "<" + _email + ">";
    	this.rowKey = _corp;
    	this.lastName = _lastName;
    	this.firstName = _firstName;
    	this.email = _email;
    	this.fullName = _firstName + " " + _lastName;
    	this.fullNameLastFirst = _lastName + ", " + _firstName;
    }
    
    @PostConstruct
    public void init() { 
    	System.out.println(" XX CustomerBean^^init() XX Created empty instance of CustomerBean");
    	companyInputLabel = "Company Name";	//set default value. Will change if user selects "Residential" as customer type
        customerType = "Corporate";			//set default value
    }
    	

        
        public void saveToCustomerTable() {
        	System.out.println(" XX CustomerBean XX Call to .saveToCustomerTable()");
        	
        	//set up concatenated fields, if needed
        	this.partitionKey = this.lastName + ", " + this.firstName + "<" + this.email + ">";
        	this.fullName = this.firstName + " " + this.lastName;
        	this.fullNameLastFirst = this.lastName + ", " + this.firstName;
        	
        	System.out.println(" XX CustomerBean XX Set concatenated fields...");
        	
        	// Use DataManagerAzureService bean to save this customer
        	//dataManager.insertOrMergeCustomer(this);
        	
        	
        	
        	//create DataManager object
        	DataManagerAzure dataManager = new DataManagerAzure();	//dataManager object to connect to Azure table storage
        	System.out.println(" XX CustomerBean XX Created DataManagerAzure object");
        	
        	//make sure dataService is already instantiated.  If not, do it...
        	//if (dataService == null) {
        	//	dataService = new DataService();
        	//	System.out.println(" XX CustomerBean XX Created DataService object");
        	//}
        	
        	
        	try{
            	// Create an operation to add the new customer to the customer table.
            	TableOperation insertOrMergeThisCustomer = TableOperation.insertOrMerge(this);	//inserts new or updates existing if this matches one already present
            																					//careful with changes to PartitionKey or RowKey - will create new customer, leaving two

                 // Submit the operation to the table service.
            	 //use DataManager object already instantiated by the DataService
            	 dataManager.customerTable.execute(insertOrMergeThisCustomer);
            	 
                 System.out.println(" XX CustomerBean XX Save operation succeeded.");
                 //return "empty";	//operation succeeded!!
                 
            }
            catch(Throwable t){
            	System.out.println(" XX CustomerBean XX !!! Save operation failed !!!");
            	System.out.println(" XX CustomerBean XX Error: " + t.getMessage());
            	//return "error";	//operation failed!!
            }
        	
        	
        }
        	

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
        
        //rowKey in Azure table storage is related to the primary key, so must make it unique. We are also using it as the customer's company name       
        public String getRowKey() {
        	return this.rowKey;
        }
        public void setRowKey(String rowKey) {
        	this.rowKey = rowKey;
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
        
        public String getCustomerType() {
			return customerType;
		}

		public void setCustomerType(String customerType) {
			this.customerType = customerType;
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
    		if(addr_State == null) {
    			this.addr_State = addr_State;
    		} else {
    			this.addr_State = addr_State.toUpperCase();
    		}
        }
        
        public String getAddr_Zip() {
            return this.addr_Zip;
        }
        public void setAddr_Zip(String addr_Zip) {
        	this.addr_Zip = addr_Zip;
        }
        
		public boolean isCompanyInputDisabledFlag() {
			return companyInputDisabledFlag;
		}

		public void setCompanyInputDisabledFlag(boolean companyInputDisabledFlag) {
			this.companyInputDisabledFlag = companyInputDisabledFlag;
		}

		public String getCompanyInputLabel() {
			return companyInputLabel;
		}

		public void setCompanyInputLabel(String companyInputLabel) {
			this.companyInputLabel = companyInputLabel;
		}
        
        /*
        public DataManagerAzureService getDataManager() {
        	return dataManager;
        }
        
        public void setDataManager(DataManagerAzureService dataManager) {
        	 this.dataManager = dataManager;
        	
        }
        */
		
		
    	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    	//ajax listener 
        
        public void customerTypeListener(AjaxBehaviorEvent event) {
        	/*
        	 * Calculates rowKey value if customer type is "Residential",
        	 * 	and disables input on company name field (rowKey property)
        	 * Called by input fields on createNewCustomer form.  
        	 */
        	String ln;
        	String fn;
        	String comma;
        	String res;
            if(customerType.equals("Residential")) {
            	//check for null values
            	if(lastName != null) {ln = lastName; } else {ln = "";}
            	if(firstName != null) {fn = firstName; } else {fn = "";}
            	if(ln != "" && fn != "") {comma = ", "; } else {comma = "";}			//determine if we need a comma
            	if(ln=="" && fn == "") {res = ""; } else { res = " (Residential)"; }	//if no fn or ln, make blank
            	
        		this.rowKey = ln + comma + fn + res;	//set value of company name 
        		
        		
        		this.companyInputDisabledFlag = true;		//disable entry
        		this.companyInputLabel = "Residential";		//change input label to let user know why entry is disabled
        	} else {
        		this.rowKey = "";							//blank out company name
        		this.companyInputDisabledFlag = false;		//enable (or re-enable) entry 
        		this.companyInputLabel = "Company Name";	//change input label
        	} 
        	/*
        	System.out.println("XX CustomerBean XX listener fired:");
        	System.out.println("XX CustomerBean XX   firstName = " + firstName);
        	System.out.println("XX CustomerBean XX   lastName = " + lastName);
        	System.out.println("XX CustomerBean XX   customerType = " + customerType);
            System.out.println("XX CustomerBean XX   rowKey = " + rowKey);
            System.out.println("XX CustomerBean XX   addr_Line1 = " + addr_Line1);
            System.out.println("XX CustomerBean XX   companyInputDisabledFlag = " + Boolean.toString(companyInputDisabledFlag));
            System.out.println("XX CustomerBean XX   companyInputLabel = " + companyInputLabel);
            System.out.println("XX CustomerBean XX");
            */
        } 
 
        /*        
        public DataBean getDataBean() {
			return dataBean;
		}

		public void setDataBean(DataBean dataBean) {
			this.dataBean = dataBean;
		}

		public DataService getDataService() {
			return dataService;
		}

		public void setDataService(DataService dataService) {
			this.dataService = dataService;
		}

		
		public DataManagerAzure getDataManager() {
			return dataManager;
		}

		public void setDataManager(DataManagerAzure dataManager) {
			this.dataManager = dataManager;
		}  
*/	
        
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 59 * hash + (this.partitionKey != null ? this.partitionKey.hashCode() : 0);
            return hash;
        }
        

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final CustomerBean other = (CustomerBean) obj;
            if ((this.partitionKey == null) ? (other.partitionKey != null) : !this.partitionKey.equals(other.partitionKey)) {
                return false;
            }
            return true;
        }
        
    }
