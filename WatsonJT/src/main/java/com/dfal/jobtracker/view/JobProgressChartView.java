package com.dfal.jobtracker.view;
 
import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.HorizontalBarChartModel;

import com.dfal.jobtracker.beans.CustomerBean;
import com.dfal.jobtracker.beans.DataBean;
import com.dfal.jobtracker.beans.JobBean;
import com.dfal.jobtracker.model.DataManagerAzure;
import com.dfal.jobtracker.model.DataService;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.primefaces.model.chart.ChartSeries;
 
@ManagedBean(name="jobProgressChartView")
@ViewScoped
@Getter @Setter @EqualsAndHashCode(callSuper=false) @ToString(callSuper=true, includeFieldNames=true)
public class JobProgressChartView implements Serializable {
	private static final long serialVersionUID = 1L;
	
    @ManagedProperty(value="#{dataBean}")	//get the SessionScoped data bean already in memory (or instantiate it if it doesn't exist)
    public DataBean dataBean;
 
    //private BarChartModel barModel;
    private HorizontalBarChartModel horizontalBarModel;

    @PostConstruct
    public void init() {
        createBarModels();
    }
 
    
    /*
    public BarChartModel getBarModel() {
        return barModel;
    }
     
    public HorizontalBarChartModel getHorizontalBarModel() {
        return horizontalBarModel;
    }
    */
 
    /*
    private BarChartModel initBarModel() {
        BarChartModel model = new BarChartModel();
 
        ChartSeries boys = new ChartSeries();
        boys.setLabel("Boys");
        boys.set("2004", 120);
        boys.set("2005", 100);
        boys.set("2006", 44);
        boys.set("2007", 150);
        boys.set("2008", 25);
 
        ChartSeries girls = new ChartSeries();
        girls.setLabel("Girls");
        girls.set("2004", 52);
        girls.set("2005", 60);
        girls.set("2006", 110);
        girls.set("2007", 135);
        girls.set("2008", 120);
 
        model.addSeries(boys);
        model.addSeries(girls);
         
        return model;
    }
    */ 
    private void createBarModels() {
        //createBarModel();
        createHorizontalBarModel();
    }
    /* 
    private void createBarModel() {
        barModel = initBarModel();
         
        barModel.setTitle("Bar Chart");
        barModel.setLegendPosition("ne");
         
        Axis xAxis = barModel.getAxis(AxisType.X);
        xAxis.setLabel("Gender");
         
        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setLabel("Births");
        yAxis.setMin(0);
        yAxis.setMax(200);
    }
    */
     
    private void createHorizontalBarModel() {
        horizontalBarModel = new HorizontalBarChartModel();       

        //create data series for jobs 
        ChartSeries jobs = new ChartSeries();
        jobs.setLabel("Jobs");
        
        //populate data
        for (JobBean entity : dataBean.getJobBeans()) {
        	jobs.set(entity.getRowKey(), Integer.parseInt(entity.getJobProgress()));
        	
        }

        horizontalBarModel.addSeries(jobs);

         
        horizontalBarModel.setTitle("Job Progress");
        horizontalBarModel.setLegendPosition("e");
        //horizontalBarModel.setStacked(true);
         
        Axis xAxis = horizontalBarModel.getAxis(AxisType.X);
        xAxis.setLabel("Percent Complete");
        xAxis.setMin(0);
        xAxis.setMax(100);
         
        Axis yAxis = horizontalBarModel.getAxis(AxisType.Y);
        yAxis.setLabel("Jobs"); 
        //yAxis.setMin(100);
        //yAxis.setMax(100);
    }
 
}