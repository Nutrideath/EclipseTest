package com.dfal.jobtracker.beans;

import java.io.Serializable;
import java.util.Date;
//import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
//import org.primefaces.model.SelectableDataModel;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

//lombok does auto-generation of getters, setters, equals, and toString functions
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString; 

import com.dfal.jobtracker.model.DataService;
//import com.microsoft.azure.storage.table.TableServiceEntity;

@ManagedBean(name="dataBean")
@ViewScoped
@Getter @Setter @EqualsAndHashCode(callSuper=false) @ToString(callSuper=true, includeFieldNames=true)
public class DataBean implements Serializable {
	private static final long serialVersionUID = 1L;
	/*
	 * This bean is meant to interact with the DataService object to manage i/o with the database.
	 * 
	 * For example, a field providing a list of customers can be populated by supplying the List<String> customerNames
	 *   to the <f:selectItems> tag.
	 * In the xhtml doc, the snippet would look like this:
	 * 
	 * <p:selectOneMenu id="rowKey" value="#{jobBean.rowKey}" required="true" label="rowKey" validatorMessage="Invalid character" >  
     * 		<f:selectItems value="#{dataBean.customerNames}" />	               		
     * </p:selectOneMenu>
	 * 
	 */
	
    //The DataService bean is Session scoped, and so loads upon first use and remains available until end of session.
    @ManagedProperty(value="#{dataService}")	//get the SessionScoped data service already in memory (or instantiate it if it doesn't exist)
    public DataService dataService;
    
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //to hold selections and filtered lists
    @Setter(AccessLevel.NONE)	//special setter below to show debug output
    private CustomerBean selectedCustomerBean;
    
    private List<CustomerBean> selectedCustomerBeans;
    
    //@ManagedProperty(value="#{DataBean.filteredCustomerBeans}")
    private List<CustomerBean> filteredCustomerBeans; 
    
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//Managed properties
    
    @ManagedProperty(value="#{DataBean.customerNames}")
    private List<String> customerNames;
   	
    @ManagedProperty(value="#{DataBean.customerBeans}")
    private List<CustomerBean> customerBeans;
    
    
    
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  	//Constructor stuff   	
  	public DataBean() { 
  	//	customerNames = dataService.getCustomerNames();
    //    customerBeans = dataService.getCustomerBeans();
      	System.out.println(" XX DataBean XX Created new empty instance of DataBean"); 
    }
    
    @PostConstruct
    public void init() {
        customerNames = dataService.getCustomerNames();
        customerBeans = dataService.getCustomerBeans();
    }
    
    
    
    
    
    
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  	//special setters
    public void setSelectedCustomerBean(CustomerBean selectedCustomerBean) {
    	System.out.println(" XX DataBean^^setSelectedCustomerBean XX Called");
    	if(selectedCustomerBean == null) {
    		System.out.println(" XX DataBean^^setSelectedCustomerBean XX ERROR: Called with null CustomerBean arg");
    	} else {
    		this.selectedCustomerBean = selectedCustomerBean;
    		System.out.println(" XX DataBean^^setSelectedCustomerBean XX Selected: " + this.selectedCustomerBean.getRowKey());
    	}
    }
    
    
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//getters & setters
/*    
    public void setCustomerNames(List<String> customerNames) {
    	this.customerNames = customerNames;
    }
    public List<String> getCustomerNames() {
        return customerNames;
    } 
    
    public List<CustomerBean> getCustomerBeans() { 
		return customerBeans;
	}

	public void setCustomerBeans(List<CustomerBean> customerBeans) {
		this.customerBeans = customerBeans;
	}

	
	public DataService getDataService() {
    	return dataService;
    }    
    public void setDataService(DataService dataService) {
    	 this.dataService = dataService;
    	
    }
    
    
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //getters and setters for filters 
    
    public List<CustomerBean> getFilteredCustomerBeans() {
		return filteredCustomerBeans;
	}

	public void setFilteredCustomerBeans(List<CustomerBean> filteredCustomerBeans) {
		this.filteredCustomerBeans = filteredCustomerBeans;
	}

	
	
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //getters and setters for selections
	
	public CustomerBean getSelectedCustomerBean() {
        return selectedCustomerBean;
    }



    public List<CustomerBean> getSelectedCustomerBeans() {
        return selectedCustomerBeans;
    }

    public void setSelectedCustomerBeans(List<CustomerBean> selectedCustomerBeans) {
        this.selectedCustomerBeans = selectedCustomerBeans;
    }
*/    
    public void onRowSelect(SelectEvent event) {
    	System.out.println(" XX DataBean^^onRowSelect XX Selected: " + ((CustomerBean) event.getObject()).getRowKey());
        FacesMessage msg = new FacesMessage("Customer Selected", ((CustomerBean) event.getObject()).getRowKey());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowUnselect(UnselectEvent event) {
    	System.out.println(" XX DataBean^^onRowUnselect XX UnSelected: " + ((CustomerBean) event.getObject()).getRowKey());
        FacesMessage msg = new FacesMessage("Customer Unselected", ((CustomerBean) event.getObject()).getRowKey());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
}