package com.dfal.jobtracker.beans;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.AccessLevel;

@ManagedBean(name = "lombokTestBean")
@ViewScoped
@Getter @Setter @EqualsAndHashCode @ToString
public class lombokTestBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private String leaveThisOneAlone;
	
	@ManagedProperty(value="#{lombokTestBean.rushJob}")
	private boolean rushJob;
	
	@ManagedProperty(value="#{lombokTestBean.location}")
	private String location;
	
	@ManagedProperty(value="#{lombokTestBean.height}")
	private String height;
	
	@ManagedProperty(value="#{lombokTestBean.width}")
	private String width;
	
	@ManagedProperty(value="#{lombokTestBean.supersName}")
	private String supersName;
	
	@ManagedProperty(value="#{lombokTestBean.supersAddress_line1}")
	private String supersAddress_line1;
	
	@ManagedProperty(value="#{lombokTestBean.supersAddress_line2}")
	private String supersAddress_line2;
	
	@ManagedProperty(value="#{lombokTestBean.supersAddress_city}")
	private String supersAddress_city;
	
	@ManagedProperty(value="#{lombokTestBean.supersAddress_state}")
	private String supersAddress_state;
	
	@ManagedProperty(value="#{lombokTestBean.supersAddress_zip}")
	private String supersAddress_zip;


	
	
	
}