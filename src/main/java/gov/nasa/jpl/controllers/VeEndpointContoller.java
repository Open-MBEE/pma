package gov.nasa.jpl.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nasa.jpl.jenkinsUtil.JenkinsBuildConfig;
import gov.nasa.jpl.jenkinsUtil.JenkinsEngine;
import gov.nasa.jpl.mmsUtil.MMSUtil;
import gov.nasa.jpl.model.Job;
import gov.nasa.jpl.model.JobFromVE;
import gov.nasa.jpl.model.JobInstance;

@Controller
public class VeEndpointContoller {

	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs/{jobSysmlID}", method = RequestMethod.GET)
	@ResponseBody
	public String getJob(@PathVariable String projectID, @PathVariable String refID, @PathVariable String jobSysmlID) {
		return "job" + "\n" + projectID + "\n" + refID + "\n" + jobSysmlID;
	}

	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs/{jobSysmlID}/instances", method = RequestMethod.GET)
	@ResponseBody
	public String getJobInstances(@PathVariable String projectID, @PathVariable String refID, @PathVariable String jobSysmlID) {
		return "job instance" + "\n" + projectID + "\n" + refID + "\n" + jobSysmlID;
	}

	/**
	 * Creates job element on mms and job on Jenkins.
	 * 
	 * @param projectID magicdraw project ID
	 * @param refID id of workspace
	 * @param jobjobFromVE 
	 * @return
	 */
	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs", method = RequestMethod.POST)
	@ResponseBody
	public String createJob(@PathVariable String projectID, @PathVariable String refID, @RequestBody final JobFromVE jobFromVE) {
		ObjectMapper mapper = new ObjectMapper();
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		
		String jobName = jobFromVE.getJobName();
		String alfrescoToken = jobFromVE.getAlfrescoToken();
		String mmsServer = jobFromVE.getMmsServer();
		String associatedElementID = jobFromVE.getAssociatedElementID();
		String jobElementID = "PMA_" + timestamp.getTime();
		String ownerID = projectID+"_pm";
		
		MMSUtil mmsUtil = new MMSUtil(alfrescoToken);
		ObjectNode on = mmsUtil.buildJobElementJSON(jobElementID, ownerID, jobName);
		
		String elementCreationResponse = mmsUtil.post(mmsServer, projectID, refID, on);
		System.out.println("MMS Job element response: "+elementCreationResponse);
		System.out.println("");
		if (elementCreationResponse.equals("HTTP/1.1 200 OK"))
		{
			System.out.println("Element Created");
			
			// Post to jenkins using jobElementID as the job name
	        String buildAgent = "Analysis01-Int";
	        
	        JenkinsBuildConfig jbc = new JenkinsBuildConfig();
	        jbc.setBuildAgent(buildAgent);
	        jbc.setDocumentID(associatedElementID);
	        jbc.setMmsServer(mmsServer);
	        jbc.setTeamworkProject(projectID);
	        jbc.setJobID(jobElementID);
//	        System.out.println("Jenkins XML: "+jbc.generateBaseConfigXML());
	        
	        JenkinsEngine je = login();

	        String jobCreationResponse = je.postConfigXml(jbc, jobElementID, true);
	        System.out.println("Jenkins Job creation response: "+jobCreationResponse);
	        
	        return jobCreationResponse;
		}
		else {
			return elementCreationResponse;
		}
	}
	
	// This will run the job on jenkins and create an instance of a job
	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs/{jobSysmlID}/instances", method = RequestMethod.POST)
	@ResponseBody
	public String runJob(@PathVariable String projectID, @PathVariable String refID,@PathVariable String jobSysmlID, @RequestBody final JobInstance jobInstance) {
		
		
		// Check if job exists on jenkins first
    	JenkinsEngine je = login();
    	String jobResponse = je.getJob(jobSysmlID).toString();
    	
    	if(jobResponse!=null)
    	{
    		System.out.println("");
    		String alfrescoToken = jobInstance.getAlfrescoToken();
    		String mmsServer = jobInstance.getMmsServer();
    		// Create job instance element. Use the jobSysmlID as the owner.
    		
    		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
          	String jobElementID = "PMA_" + timestamp.getTime();		
          	
    		MMSUtil mmsUtil = new MMSUtil(alfrescoToken);
    		ObjectNode on = mmsUtil.buildJobElementJSON(jobElementID, jobSysmlID, jobSysmlID+"_instance"+timestamp.getTime()); //job element will be the owner of the instance element
    		
    		String elementCreationResponse = mmsUtil.post(mmsServer, projectID, refID, on);
    		
    		System.out.println("job instance element creation response"+elementCreationResponse);
    		if (elementCreationResponse.equals("HTTP/1.1 200 OK"))
    		{
    			
    			// run job on jenkins

    	        String runResponse = je.executeJob(jobSysmlID); // job name should be the job sysmlID
    	        je.getBuildNumber(jobSysmlID);
    	        
    			System.out.println("Job run response: "+runResponse);
    			return runResponse;
    		}
    		return elementCreationResponse;
    	}
    	else
    	{
    		jobResponse = "Job doesn't exist";
    		return jobResponse;
    	}
  

	}
	
	
	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs/{jobSysmlID}", method = RequestMethod.DELETE)
	@ResponseBody
	public String deleteJob(@PathVariable String projectID, @PathVariable String refID, @PathVariable String jobSysmlID) {
		System.out.println("job" + "\n" + projectID + "\n" + refID + "\n");
		// Delete job element on MMS.
//		MMSUtil mmsUtil = new MMSUtil(alfrescoToken);
//		String elementDeleteResponse = mmsUtil.delete(mmsServer, projectID, refID, projectID);;
		
		// delete job on jenkins
    	JenkinsEngine je = login();
    	je.deleteJob(jobSysmlID);
        
		return "job deleted" + "\n" + projectID + "\n" + refID + "\n" + jobSysmlID;
	}
	
    public JenkinsEngine login()
    {
        JenkinsEngine je = new JenkinsEngine();
        je.setCredentials();
        je.login();
    	return je;
    }

}