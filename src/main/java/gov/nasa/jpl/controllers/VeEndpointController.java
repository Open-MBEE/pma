package gov.nasa.jpl.controllers;

/**
 * Endpoints for applications to interface with PMA.
 */
import java.sql.Timestamp;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nasa.jpl.dbUtil.DBUtil;
import gov.nasa.jpl.jenkinsUtil.JenkinsBuildConfig;
import gov.nasa.jpl.jenkinsUtil.JenkinsEngine;
import gov.nasa.jpl.mmsUtil.MMSUtil;
import gov.nasa.jpl.model.JobFromVE;
import gov.nasa.jpl.model.JobInstanceFromVE;

@Controller
public class VeEndpointController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Returns all the jobs of a project.
	 * @param projectID
	 * @param refID
	 * @return
	 */
	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String getJobs(@PathVariable String projectID, @PathVariable String refID,@RequestParam String alf_ticket,@RequestParam String mmsServer) {
		
		logger.info("Get Jobs was called");
		logger.info( "projectID: "+ projectID + "\n" +"refID: "+ refID+ "\n"+"alf_ticket: "+alf_ticket+ "\n"+"mmsServer: "+mmsServer);
		System.out.println("Get JOBS was called");
		MMSUtil mmsUtil = new MMSUtil(alf_ticket);
		
		
		return mmsUtil.getJobElements(mmsServer,projectID, refID);
	}
	
	/**
	 *  Returns information about a job.
	 * @param projectID
	 * @param refID
	 * @param jobSysmlID
	 * @return
	 */
	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs/{jobSysmlID}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<String> getJob(@ PathVariable String projectID, @PathVariable String refID, @PathVariable String jobSysmlID,@RequestParam String alf_ticket,@RequestParam String mmsServer) {
		
		logger.info("Get Job was called");
		logger.info( "projectID: "+ projectID + "\n" +"refID: "+ refID+ "\n"+"Job SysmlID: "+jobSysmlID+ "\n"+"alf_ticket: "+alf_ticket+ "\n"+"mmsServer: "+mmsServer);
		
		// Check if job exists on jenkins first
    	JenkinsEngine je = login();
    	String jobResponse = je.getJob(jobSysmlID);
    	System.out.println("Job Response: "+jobResponse);
    	HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    	if(!jobResponse.equals("Job Not Found"))
    	{
    		MMSUtil mmsUtil = new MMSUtil(alf_ticket);
    		jobResponse = mmsUtil.getJobElement(mmsServer, projectID, refID, jobSysmlID);
    		status = HttpStatus.OK;
    	}
    	else
    	{
    		ObjectMapper mapper = new ObjectMapper();
    		ObjectNode jobJSON = mapper.createObjectNode();
    		jobJSON.put("message", "Job not found on Jenkins");
    		jobResponse = jobJSON.toString();
    		status = HttpStatus.NOT_FOUND;
    				
    	}
    	logger.info("Get Job Response: "+jobResponse);
    	return new ResponseEntity<String>(jobResponse,status);
		
	}
	
	/**
	 * Return job instances of a job.
	 * @param projectID
	 * @param refID
	 * @param jobSysmlID
	 * @return
	 */
	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs/{jobSysmlID}/instances", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<String> getJobInstances(@PathVariable String projectID, @PathVariable String refID, @PathVariable String jobSysmlID, @RequestParam String alf_ticket, @RequestParam String mmsServer) {
		
		logger.info("Get Job Instances was called");
		logger.info( "projectID: "+ projectID + "\n" +"refID: "+ refID+ "\n"+"Job SysmlID: "+jobSysmlID+ "\n"+"alf_ticket: "+alf_ticket+ "\n"+"mmsServer: "+mmsServer);
		
		// Check if job exists on jenkins first
    	JenkinsEngine je = login();
    	String jobResponse = je.getJob(jobSysmlID);
    	System.out.println("Job Response: "+jobResponse);
    	HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    	if(!jobResponse.equals("Job Not Found"))
    	{
    		MMSUtil mmsUtil = new MMSUtil(alf_ticket);
    		
    		jobResponse = mmsUtil.getJobInstanceElements(mmsServer, projectID, refID, jobSysmlID);
    		status = HttpStatus.OK;
    	}
    	else
    	{
    		ObjectMapper mapper = new ObjectMapper();
    		ObjectNode jobJSON = mapper.createObjectNode();
    		jobJSON.put("message", "Job not found on Jenkins");
    		jobResponse = jobJSON.toString();
    		status = HttpStatus.NOT_FOUND;
    	}
    	
      	logger.info("Get Job Response: "+jobResponse);
    	return new ResponseEntity<String>(jobResponse,status);

	}

	/**
	 * Creates job element on mms and job on Jenkins.
	 * 
	 * @param projectID magicdraw project ID
	 * @param refID id of workspace
	 * @param jobjobFromVE 
	 * @return
	 */
	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<String> createJob(@PathVariable String projectID, @PathVariable String refID, @RequestBody final JobFromVE jobFromVE) {
		
		logger.info("Create Job was called");
		logger.info("projectID: " + projectID + "\n" + "refID: " + refID + "\n" + "JSON input: " + "\n" + "Job Name: "
				+ jobFromVE.getJobName() + "\n" + "Command: " + jobFromVE.getCommand() + "\n" + "Arguments: "
				+ jobFromVE.getArguments() + "\n" + "Schedule: " + jobFromVE.getSchedule() + "\n"
				+ "Associated Element ID: " + jobFromVE.getAssociatedElementID() + "\n" + "MMS Server: "
				+ jobFromVE.getMmsServer() + "\n" + "Alfresco Token: " + jobFromVE.getAlfrescoToken() );
		
		ObjectMapper mapper = new ObjectMapper();
		
		String jobName = jobFromVE.getJobName();
		String alfrescoToken = jobFromVE.getAlfrescoToken();
		String mmsServer = jobFromVE.getMmsServer();
		String associatedElementID = jobFromVE.getAssociatedElementID();
		String schedule = jobFromVE.getSchedule();
		String command = jobFromVE.getCommand();
		String arguments = Arrays.toString(jobFromVE.getArguments());
		
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		
		MMSUtil mmsUtil = new MMSUtil(alfrescoToken);
		
		Boolean jobPackageExists = mmsUtil.jobPackageExists(mmsServer, projectID, refID);
		
		if(!jobPackageExists) // create jobs bin package if it doesn't exist.
		{
			ObjectNode packageNode = mmsUtil.buildPackageJSON("jobs_bin_"+projectID,projectID+"_pm");
			System.out.println(packageNode.toString());
			mmsUtil.post(mmsServer, projectID, alfrescoToken, packageNode);
		}
		
		String jobElementID = mmsUtil.createId();
		ObjectNode on = mmsUtil.buildJobElementJSON(jobElementID, associatedElementID, jobName,command,schedule,"jobs_bin_"+projectID,arguments); // Job elements should be created in the jobs bin package
		
		System.out.println("Job class JSON: "+on.toString());
		logger.info("Job class JSON: "+on.toString());
		
		String elementCreationResponse = mmsUtil.post(mmsServer, projectID, refID, on);
		System.out.println("MMS Job element response: "+elementCreationResponse);
		logger.info("MMS Job element response: "+elementCreationResponse);
		System.out.println("");
		if (elementCreationResponse.equals("HTTP/1.1 200 OK"))
		{
			System.out.println("Created Job Element ID: "+jobElementID);
			
			// Post to jenkins using jobElementID as the job name
	       
			DBUtil dbUtil = new DBUtil();
			dbUtil.getCredentials();
			String jenkinsAgent = dbUtil.getJenkinsAgent();
	        
	        JenkinsBuildConfig jbc = new JenkinsBuildConfig();
	        jbc.setBuildAgent(jenkinsAgent);
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
	        	logger.info("Return message: "+jobCreationResponse + " " + jobElementID);
//	        	return jobCreationResponse + " " + jobElementID; //job element created on mms and job is created on jenkins
	    		
	    		String response = mmsUtil.getJobElement(mmsServer, projectID, refID, jobElementID);
	    		status = HttpStatus.OK;
	        	return new ResponseEntity<String>(response,status);
	        }
	        
	        mmsUtil.delete(mmsServer, projectID, refID, jobElementID); // Delete the job element since the job wasn't created on Jenkins.
	        
	        logger.info("Return message: "+jobCreationResponse +" Jenkins"); // job not created on jenkins 
	        System.out.println("jobCreationResponse");
    		ObjectNode responseJSON = mapper.createObjectNode();
    		responseJSON.put("message", jobCreationResponse+" Jenkins");
    		jobCreationResponse = responseJSON.toString();
	        return new ResponseEntity<String>(jobCreationResponse,status);
		}
		else {
    		ObjectNode responseJSON = mapper.createObjectNode();
    		responseJSON.put("message", elementCreationResponse+" MMS");
    		elementCreationResponse = responseJSON.toString();
    		System.out.println("Return message: "+elementCreationResponse);
			logger.info("Return message: "+elementCreationResponse+" MMS"); // job element not created on mms. 
			return new ResponseEntity<String>(elementCreationResponse,status);
		}
	}
	
	// This will run the job on jenkins and create an instance of a job
	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs/{jobSysmlID}/instances", method = RequestMethod.POST)
	@ResponseBody
	public String runJob(@PathVariable String projectID, @PathVariable String refID,@PathVariable String jobSysmlID, @RequestBody final JobInstanceFromVE jobInstance) {
		
		logger.info("Run job was called");
		
		logger.info("projectID: " + projectID + "\n" + "refID: " + refID + "\n" + "JSON input: " + "\n" + "Arguments: "
				+ jobInstance.getArguments() + "\n" + "MMS Server: " + jobInstance.getMmsServer() + "\n"
				+ "Alfresco Token: " + jobInstance.getAlfrescoToken());
		
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
    		logger.info("job instance JSON: "+on.toString());
    		String elementCreationResponse = mmsUtil.post(mmsServer, projectID, refID, on);
    		
    		System.out.println("job instance element creation response"+elementCreationResponse);
    		logger.info("job instance element creation response"+elementCreationResponse);
    		if (elementCreationResponse.equals("HTTP/1.1 200 OK"))
    		{
    			// run job on jenkins
    	        String runResponse = je.executeJob(jobSysmlID); // job name should be the job sysmlID
    	        je.getBuildNumber(jobSysmlID);
    	        
    			System.out.println("Job run response: "+runResponse);
    			logger.info("Run job Jenkins response: "+runResponse);
    			System.out.println("JOBRUN: "+runResponse);
    			if(runResponse.equals("HTTP/1.1 201 Created"))
    			{
    				return "success";
    			}
    			mmsUtil.delete(mmsServer, projectID, refID, jobInstanceElementID);
    			return runResponse + " Jenkins"; // jenkins response when running job
    			
    		}
    		logger.info("MMS Element creation response: "+elementCreationResponse);
    		return elementCreationResponse +" MMS"; // mms issue when creating job instance
    	}
    	else
    	{
    		logger.info(jobResponse);
    		return jobResponse; 
    	}
  

	}
	
	
	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs/{jobSysmlID}", method = RequestMethod.DELETE)
	@ResponseBody
	public String deleteJob(@PathVariable String projectID, @PathVariable String refID, @PathVariable String jobSysmlID,@RequestParam String alf_ticket,@RequestParam String mmsServer) {
		System.out.println("job" + "\n" + projectID + "\n" + refID + "\n");
		
		logger.info("Delete Job was called");
		logger.info( "projectID: "+ projectID + "\n" +"refID: "+ refID+ "\n"+"Job SysmlID: "+jobSysmlID+ "\n"+"alf_ticket: "+alf_ticket+ "\n"+"mmsServer: "+mmsServer);
		
		
		// Delete job element on MMS.
		MMSUtil mmsUtil = new MMSUtil(alf_ticket);
		String elementDeleteResponse = mmsUtil.delete(mmsServer, projectID, refID, jobSysmlID);
		System.out.println("Element delete response: "+elementDeleteResponse);
		logger.info( "Element delete response: "+elementDeleteResponse);
		
		if(!elementDeleteResponse.equals("HTTP/1.1 200 OK"))
		{
			logger.info( "MMS Element delete response: "+elementDeleteResponse);
			return elementDeleteResponse+" MMS";
		}
		
		// delete job on jenkins
    	JenkinsEngine je = login();
    	String jenkinsDeleteResponse = je.deleteJob(jobSysmlID);
    	System.out.println("Jenkins delete response: "+jenkinsDeleteResponse);
    	
    	if(!jenkinsDeleteResponse.equals("HTTP/1.1 302 Found"))
		{
    		logger.info( "Jenkins delete response: "+jenkinsDeleteResponse);
			return jenkinsDeleteResponse+" Jenkins";
		}
    	logger.info("Delete Succesfull");
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