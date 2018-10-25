package com.dfal.jobtracker.view;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceHandlerWrapper;
import javax.faces.application.ViewResource;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import com.dfal.jobtracker.beans.DataBean;
import com.dfal.jobtracker.beans.JobBean;

public class JobProgressDynamicResourceHandler extends ResourceHandlerWrapper {
	/*
	 * NOTE: THIS DOES NOT CURRENTLY WORK.
	 * 		Could not get this to work.  For some reason, this object doesn't
	 * 		like the injection of the DataBean, so cannot get to the list of
	 * 		JobBean objects.
	 * 
	 * Creates a "pseudo xhtml" fragment, which should look like a horizontal 
	 *   bar chart, tied to project data to show project progress.
	 * 
	 * include in another xhtml doc by using:
	 * 
	 * <ui:include src="/jobProgressDynamic.xhtml" />
	 * 
	 * 
	 * Must add the following to the faces-config.xml:
	 * 
	 * 	<application>
     *		<resource-handler>com.dfal.jobtracker.view.JobProgressDynamicResourceHandler</resource-handler>
	 *	</application>
	 */

    private ResourceHandler wrapped;
    
    //@ManagedProperty(value="#{dataBean}")	//get the SessionScoped data bean already in memory (or instantiate it if it doesn't exist)
    //public DataBean dataBean;
    
    //private List<JobBean> jobBeans = dataBean.getJobBeans();

    public JobProgressDynamicResourceHandler(ResourceHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ViewResource createViewResource(FacesContext context, String resourceName) {
        if (resourceName.equals("/jobProgressDynamic.xhtml")) {
            try {
            	
            	
                File file = File.createTempFile("jobProgressDynamic-", ".xhtml");

                try (Writer writer = new FileWriter(file)) {
                	//open up
                    writer
                        .append("<ui:composition")
                        .append(" xmlns:ui='http://java.sun.com/jsf/facelets'")
                        .append(" xmlns:h='http://java.sun.com/jsf/html'")
                        .append(">")
                       

                       
                        .append("<div class=\"ui-g-12 ui-lg-6 ui-g-nopad\">")
                        .append("<div class=\"ui-g-12 status-bars\">")
                        .append("<div class=\"card\">")
                        .append("<ul>");
                    
                    	writer
                    	.append("<li>")
                    	.append("<div class=\"status-bar status-bar-1\">")
                    	.append("<div class=\"status-bar-value\" style=\"width:22%\">22%</div>")
                    	.append("</div>")
                    	.append("<span>#{dataBean.selectedCustomerBean.rowKey}</span>")
                    	//.append("<span>Project Name</span>")
                    	.append("<i class=\"material-icons\">&#xE853;</i>")
                    	.append("</li>");
                    
              /*    
                      //loop thru, loading project data
                        for (JobBean entity : jobBeans) {
                        	//jobs.set(entity.getRowKey(), 80);                     
                        
                        writer
                        	.append("<li>")
                      //  	.append("<div class=\"status-bar status-bar-1\">")
                      //  	.append("<div class=\"status-bar-value\" style=\"width:22%\">22%</div>")
                      //  	.append("</div>")
                      //  	.append("<span>" + entity.getRowKey() + "</span>")
                      //  	.append("<i class=\"material-icons\">&#xE853;</i>")
                        	.append("</li>");
                        }
              */
                    	//close it up
                        writer.append("</ul>")
                        	.append("</div>")
                        	.append("</div>")
                        	.append("</div>") 
                        	.append("</ui:composition>");
                    
                    
                    /*        
                    
                    <li>
                        <div class="status-bar status-bar-2">
                            <div class="status-bar-value" style="width:78%">78%</div>
                        </div>
                        <span>Confirmed</span>
                        <i class="material-icons">&#xE86C;</i>
                    </li>
                    <li>
                        <div class="status-bar status-bar-3">
                            <div class="status-bar-value" style="width:51%">51%</div>
                        </div>
                        <span>Defects</span>
                        <i class="material-icons">&#xE868;</i>
                    </li>
                    <li>
                        <div class="status-bar status-bar-4">
                            <div class="status-bar-value" style="width:68%">68%</div>
                        </div>
                        <span>Issues</span>
                        <i class="material-icons">&#xE8B2;</i>
                    </li>
                    <li>
                        <div class="status-bar status-bar-5">
                            <div class="status-bar-value" style="width:80%">80%</div>
                        </div>
                        <span>Searches</span>
                        <i class="material-icons">&#xE8B6;</i>
                    </li>
                
            
         */                     
                    
                    
                    
                }

                final URL url = file.toURI().toURL();
                return new ViewResource(){
                    @Override
                    public URL getURL() {
                        return url;
                    }
                };
            }
            catch (IOException e) {
                throw new FacesException(e);
            }
        }

        return super.createViewResource(context, resourceName);
    }

    @Override
    public ResourceHandler getWrapped() {
        return wrapped;
    }

}