package gov.nasa.jpl.controllers;

/**
 * Endpoints for applications to interface with PMA.
 */
import java.sql.Timestamp;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nasa.jpl.jenkinsUtil.JenkinsBuildConfig;
import gov.nasa.jpl.jenkinsUtil.JenkinsEngine;
import gov.nasa.jpl.mmsUtil.MMSUtil;
import gov.nasa.jpl.model.JobFromVE;
import gov.nasa.jpl.model.JobInstanceFromVE;

@Controller
public class VeEndpointController {

	/**
	 * Returns all the jobs of a project.
	 * @param projectID
	 * @param refID
	 * @return
	 */
	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs", method = RequestMethod.GET)
	@ResponseBody
	public String getJobs(@PathVariable String projectID, @PathVariable String refID,@RequestParam String alf_ticket,@RequestParam String mmsServer) {
		
		MMSUtil mmsUtil = new MMSUtil(alf_ticket);
		mmsUtil.getJobElements(mmsServer,projectID, refID);
		
		return "job" + "\n" + projectID + "\n" + refID+ "\n"+alf_ticket;
	}
	
	/**
	 *  Returns information about a job.
	 * @param projectID
	 * @param refID
	 * @param jobSysmlID
	 * @return
	 */
	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs/{jobSysmlID}", method = RequestMethod.GET)
	@ResponseBody
	public String getJob(@ PathVariable String projectID, @PathVariable String refID, @PathVariable String jobSysmlID,@RequestParam String alf_ticket,@RequestParam String mmsServer) {
		
		// Check if job exists on jenkins first
    	JenkinsEngine je = login();
    	String jobResponse = je.getJob(jobSysmlID);
    	System.out.println("Job Response: "+jobResponse);
    	if(!jobResponse.equals("Job Not Found"))
    	{
    		MMSUtil mmsUtil = new MMSUtil(alf_ticket);
    		return mmsUtil.getJobElement(mmsServer, projectID, refID, jobSysmlID);
    	}
    	else
    	{
    		jobResponse = "Job not found on Jenkins";
    		return jobResponse;
    	}
    	
		
		
	}
	
	/**
	 * Return job instances of a job.
	 * @param projectID
	 * @param refID
	 * @param jobSysmlID
	 * @return
	 */
	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs/{jobSysmlID}/instances", method = RequestMethod.GET)
	@ResponseBody
	public String getJobInstances(@PathVariable String projectID, @PathVariable String refID, @PathVariable String jobSysmlID, @RequestParam String alf_ticket, @RequestParam String mmsServer) {
		
		// Check if job exists on jenkins first
    	JenkinsEngine je = login();
    	String jobResponse = je.getJob(jobSysmlID);
    	System.out.println("Job Response: "+jobResponse);
    	if(!jobResponse.equals("Job Not Found"))
    	{
    		MMSUtil mmsUtil = new MMSUtil(alf_ticket);
    		
    		return mmsUtil.getJobInstanceElements(mmsServer, projectID, refID, jobSysmlID);
    	}
    	else
    	{
    		jobResponse = "Job not found on Jenkins";
    		return jobResponse;
    	}

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
		
		String jobName = jobFromVE.getJobName();
		String alfrescoToken = jobFromVE.getAlfrescoToken();
		String mmsServer = jobFromVE.getMmsServer();
		String associatedElementID = jobFromVE.getAssociatedElementID();
		String schedule = jobFromVE.getSchedule();
		String command = jobFromVE.getCommand();
		
		MMSUtil mmsUtil = new MMSUtil(alfrescoToken);
		String jobElementID = mmsUtil.createId();
		ObjectNode on = mmsUtil.buildJobElementJSON(jobElementID, associatedElementID, jobName,command,schedule);
		
		System.out.println("Job class JSON: "+on.toString());
		String elementCreationResponse = mmsUtil.post(mmsServer, projectID, refID, on);
		System.out.println("MMS Job element response: "+elementCreationResponse);
		System.out.println("");
		if (elementCreationResponse.equals("HTTP/1.1 200 OK"))
		{
			System.out.println("Created Job Element ID: "+jobElementID);
			
			// Post to jenkins using jobElementID as the job name
	        String buildAgent = "CAE-Jenkins2-AgentL01-UAT";
	        
	        JenkinsBuildConfig jbc = new JenkinsBuildConfig();
	        jbc.setBuildAgent(buildAgent);
	        jbc.setDocumentID(associatedElementID);
	        jbc.setMmsServer(mmsServer);
	        jbc.setTeamworkProject(projectID);
	        jbc.setJobID(jobElementID);
	        jbc.setSchedule(schedule); 
//	        System.out.println("Jenkins XML: "+jbc.generateBaseConfigXML());
	        
	        JenkinsEngine je = login();

	        String jobCreationResponse = je.postConfigXml(jbc, jobElementID, true);
	        System.out.println("Jenkins Job creation response: "+jobCreationResponse);
	        
	        if(jobCreationResponse.equals("HTTP/1.1 200 OK"))
	        {
	        	return jobCreationResponse + " " + jobElementID;
	        }
	        return jobCreationResponse +" Jenkins";
		}
		else {
			return elementCreationResponse+" MMS";
		}
	}
	
	// This will run the job on jenkins and create an instance of a job
	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs/{jobSysmlID}/instances", method = RequestMethod.POST)
	@ResponseBody
	public String runJob(@PathVariable String projectID, @PathVariable String refID,@PathVariable String jobSysmlID, @RequestBody final JobInstanceFromVE jobInstance) {
		
		
		// Check if job exists on jenkins first
    	JenkinsEngine je = login();
    	String jobResponse = je.getJob(jobSysmlID);
    	System.out.println("Job Response: "+jobResponse);
    	if(!jobResponse.equals("Job Not Found"))
    	{
    		System.out.println("");
    		String alfrescoToken = jobInstance.getAlfrescoToken();
    		String mmsServer = jobInstance.getMmsServer();
    		
    		String nextBuildNumber = je.getNextBuildNumber(jobSysmlID);
    		
    		// Create job instance element. Use the jobSysmlID as the owner.
    		
    		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
          	
    		MMSUtil mmsUtil = new MMSUtil(alfrescoToken);
    		String jobInstanceElementID = mmsUtil.createId();
    		ObjectNode on = mmsUtil.buildJobInstanceJSON(jobInstanceElementID, jobSysmlID, jobSysmlID+"_instance_"+timestamp.getTime(),nextBuildNumber,"pending"); //job element will be the owner of the instance element
    		System.out.println("job instance JSON: "+on.toString());
    		String elementCreationResponse = mmsUtil.post(mmsServer, projectID, refID, on);
    		
    		System.out.println("job instance element creation response"+elementCreationResponse);
    		if (elementCreationResponse.equals("HTTP/1.1 200 OK"))
    		{
    			// run job on jenkins
    	        String runResponse = je.executeJob(jobSysmlID); // job name should be the job sysmlID
    	        je.getBuildNumber(jobSysmlID);
    	        
    			System.out.println("Job run response: "+runResponse);
    			return runResponse + " Jenkins";
    		}
    		return elementCreationResponse +" MMS";
    	}
    	else
    	{
    		jobResponse = "Job not found on Jenkins";
    		return jobResponse;
    	}
  

	}
	
	
	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs/{jobSysmlID}", method = RequestMethod.DELETE)
	@ResponseBody
	public String deleteJob(@PathVariable String projectID, @PathVariable String refID, @PathVariable String jobSysmlID,@RequestParam String alf_ticket,@RequestParam String mmsServer) {
		System.out.println("job" + "\n" + projectID + "\n" + refID + "\n");
		
		// Delete job element on MMS.
		MMSUtil mmsUtil = new MMSUtil(alf_ticket);
		String elementDeleteResponse = mmsUtil.delete(mmsServer, projectID, refID, jobSysmlID);
		System.out.println("Element delete response: "+elementDeleteResponse);
		
		if(!elementDeleteResponse.equals("HTTP/1.1 200 OK"))
		{
			return elementDeleteResponse+" MMS";
		}
		
		// delete job on jenkins
    	JenkinsEngine je = login();
    	String jenkinsDeleteResponse = je.deleteJob(jobSysmlID);
    	System.out.println("Jenkins delete response: "+jenkinsDeleteResponse);
    	
    	if(!jenkinsDeleteResponse.equals("HTTP/1.1 302 Found"))
		{
			return jenkinsDeleteResponse+" Jenkins";
		}
    	
		return "HTTP/1.1 200 OK";
	}
	
    public JenkinsEngine login()
    {
        JenkinsEngine je = new JenkinsEngine();
        je.setCredentials();
        je.login();
    	return je;
    }

}