package gov.nasa.jpl.controllers;

/**
 * Endpoints for applications to interface with PMA.
 */

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import gov.nasa.jpl.model.JobFromClient;
import gov.nasa.jpl.model.JobInstanceFromClient;
import gov.nasa.jpl.pmaUtil.PMAUtil;

@CrossOrigin(origins = "*")
@Controller
public class ClientEndpointController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Returns all the jobs of a project.
	 * @param projectID
	 * @param refID
	 * @return
	 */
	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<String> getJobs(@PathVariable String projectID, @PathVariable String refID,@RequestParam String alf_ticket,@RequestParam String mmsServer) {
		
		logger.info("Get Jobs was called");
		logger.info( "projectID: "+ projectID + "\n" +"refID: "+ refID+ "\n"+"alf_ticket: "+alf_ticket+ "\n"+"mmsServer: "+mmsServer);
		System.out.println("Get JOBS was called");
		MMSUtil mmsUtil = new MMSUtil(alf_ticket);
		
		System.out.println("inside get jobs");
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
		
		PMAUtil pmaUtil = new PMAUtil();
		
		// Check if job exists on jenkins first
    	JenkinsEngine je = login();
    	String jobResponse = je.getNestedJob(jobSysmlID, projectID+"/job/"+refID);
    	System.out.println("Job Response: "+jobResponse);
    	HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    	if(pmaUtil.isJSON(jobResponse)) // The job response will be a json if the job exists.
    	{
    		MMSUtil mmsUtil = new MMSUtil(alf_ticket);
    		return mmsUtil.getJobElement(mmsServer, projectID, refID, jobSysmlID);
    	}
    	else
    	{
    		ObjectMapper mapper = new ObjectMapper();
    		ObjectNode jobJSON = mapper.createObjectNode();
    		jobJSON.put("message", jobResponse);
    		jobResponse = jobJSON.toString();
    		
    		if(!jobResponse.contains("Exception"))
    		{
    			status = HttpStatus.NOT_FOUND;
    		}
    	}
    	logger.info("Get Job Response: "+jobResponse); // Jenkins issue when checking if job exists
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
    	String jobResponse = je.getNestedJob(jobSysmlID, projectID+"/job/"+refID);
    	logger.info("Jenkins Get Job Response: "+jobResponse);
    	System.out.println("Jenkins Get Job Response: "+jobResponse);
    	HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    	PMAUtil pmaUtil = new PMAUtil();
    	if(pmaUtil.isJSON(jobResponse))
    	{
    		MMSUtil mmsUtil = new MMSUtil(alf_ticket);
    		
    		return mmsUtil.getJobInstanceElements(mmsServer, projectID, refID, jobSysmlID);
    	}
    	else
    	{
    		ObjectMapper mapper = new ObjectMapper();
    		ObjectNode jobJSON = mapper.createObjectNode();
    		jobJSON.put("message", jobResponse);
    		jobResponse = jobJSON.toString();
    		
    		if(!jobResponse.contains("Exception"))
    		{
    			status = HttpStatus.NOT_FOUND;
    		}
    	}
    	
      	logger.info("Get Job Response: "+jobResponse); // Jenkins issue when checking if job exists
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
	public ResponseEntity<String> createJob(@PathVariable String projectID, @PathVariable String refID, @RequestBody final JobFromClient jobFromVE) {
		
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
		
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // By default, the return status code is 500 Internal Server error
		
		MMSUtil mmsUtil = new MMSUtil(alfrescoToken);
		
		Boolean jobsBinExists = mmsUtil.jobPackageExists(mmsServer, projectID, refID);

		
		if(!jobsBinExists) // create jobs bin package if it doesn't exist.
		{
			logger.info("Jobs Bin Does not exist");
			System.out.println("Jobs Bin Does not exist");
//			ObjectNode packageNode = mmsUtil.buildPackageJSON("jobs_bin_"+projectID,projectID+"_pm","Jobs Bin"); // creating the package inside the project
			ObjectNode packageNode = mmsUtil.buildPackageJSON("jobs_bin_"+projectID,projectID,"Jobs Bin"); // creating the package one level above the package, wont get synced back to the model.
//			System.out.println(packageNode.toString());
			String binCreateResponse = mmsUtil.post(mmsServer, projectID, refID, packageNode);
			System.out.println("Bin Create Response: "+binCreateResponse);
			logger.info("Bin Create Response: "+binCreateResponse);
		}
		
		
		String jobElementID = mmsUtil.createId();
		
		/*
		 *  Create jobs package if it doesn't exist.
		 *  Jobs package is named after the job element ID.
		 *  It contains the job element and job instance specifications
		 */
		Boolean jobPackageExists = mmsUtil.jobPackageExists(mmsServer, jobElementID, refID);
		if(!jobPackageExists) 
		{
			logger.info("Job Package Does not exist");
			System.out.println("Job Package Does not exist");
			ObjectNode packageNode = mmsUtil.buildPackageJSON("jobs_bin_"+jobElementID,"jobs_bin_"+projectID,jobName+" - "+jobElementID); // Creating the package. The owner of the package is the Jobs Bin package.
//			System.out.println(packageNode.toString());
			String packageCreateResponse = mmsUtil.post(mmsServer, projectID, refID, packageNode);
			System.out.println("Package Create Response: "+packageCreateResponse);
			logger.info("Package Create Response: "+packageCreateResponse);
		}
		
		
		ObjectNode on = mmsUtil.buildDocgenJobElementJSON(jobElementID, "jobs_bin_"+jobElementID, jobName, associatedElementID, command, schedule, refID, projectID); // Job elements should be created in the jobs bin package
		
//		System.out.println("Job class JSON: "+on.toString());
		logger.info("Job class JSON: "+on.toString());
		
		String elementCreationResponse = mmsUtil.post(mmsServer, projectID, refID, on);
		System.out.println("MMS Job element response: "+elementCreationResponse);
		logger.info("MMS Job element response: "+elementCreationResponse);
		System.out.println("");
		if (elementCreationResponse.equals("HTTP/1.1 200 OK"))
		{
//			System.out.println("Created Job Element ID: "+jobElementID);
			
			// Post to jenkins using jobElementID as the job name
	       
			DBUtil dbUtil = new DBUtil();
			dbUtil.getCredentials();
			String jenkinsAgent = dbUtil.getJenkinsAgent();
	        
	        JenkinsBuildConfig jbc = new JenkinsBuildConfig();
	        jbc.setBuildAgent(jenkinsAgent);
	        jbc.setTargetElementID(associatedElementID);
	        jbc.setMmsServer(mmsServer);
	        jbc.setTeamworkProject(projectID);
	        jbc.setWorkspace(refID);
	        jbc.setJobID(jobElementID);
	        jbc.setSchedule(schedule); 
	        jbc.setJobType(command);
//	        System.out.println("Jenkins XML: "+jbc.generateBaseConfigXML());
	        
	        JenkinsEngine je = login();
	        
	        /*
	         *  Creating a folder for the projectID if it doesn't exist
	         */
	        String folderName = projectID;
	        String jobString = je.getJob(folderName);
	        
	        logger.info("JOB STRING: "+jobString);
	        System.out.println("Jenkins folder check string: "+jobString);
	        if(!PMAUtil.isJSON(jobString))
	        {
	        	if((jobString.equals("Job not found on Jenkins"))||(jobString.equals("HTTP/1.1 404 Not Found")))
	        	{
	        		System.out.println("Creating folder: "+folderName);
	        		logger.info("Creating folder: "+folderName);
	        		String jenkinsCreateFolderReturn = je.createFolder(folderName);
	        		System.out.println("Jenkins Create Folder Return String: "+jenkinsCreateFolderReturn);
	        		logger.info("Jenkins Create Folder Return String: "+jenkinsCreateFolderReturn);
	        	}
	        	else
	        	{
	        		ObjectNode responseJSON = mapper.createObjectNode();
	        		responseJSON.put("message", jobString+" Jenkins");
    		        return new ResponseEntity<String>(jobString,status);
	        	}
	        }
	        else
	        {
	        	System.out.println(folderName+" Folder already exists");
	        	logger.info(folderName+" Folder already exists");
	        }
	        
	        /*
	         *  Creating a folder for the ref if it doesn't exist
	         */
	        String nestedfolderName = refID;
	        jobString = je.getNestedJob(nestedfolderName, folderName);
	        System.out.println("Jenkins nested folder check string: "+jobString);
	        logger.info("JOB STRING: "+jobString);
	        if(!PMAUtil.isJSON(jobString))
	        {
	        	
	        	if((jobString.equals("Job not found on Jenkins"))||(jobString.equals("HTTP/1.1 404 Not Found")))
	        	{
	        		logger.info("Creating folder: "+nestedfolderName);
	        		String nestedFoldercreateReturn = je.createFolderWithParent(nestedfolderName, folderName);
	        		System.out.println("Jenkins Create Nested Folder Return String: "+nestedFoldercreateReturn);
	        		logger.info("Jenkins Create Nested Folder Return String: "+nestedFoldercreateReturn);
	        	}
	        	else
	        	{
	        		ObjectNode responseJSON = mapper.createObjectNode();
	        		responseJSON.put("message", jobString+" Jenkins");
    		        return new ResponseEntity<String>(jobString,status);
	        	}
	        }
	        else
	        {
	        	System.out.println(nestedfolderName+" Folder already exists");
	        	logger.info(nestedfolderName+" Folder already exists");
	        }
	        
	        // Creating the job
	        String jobCreationResponse = je.postNestedJobConfigXml(jbc, jobElementID,projectID ,refID, true);
	        System.out.println("Jenkins Job creation response: "+jobCreationResponse);
	        
	        if(jobCreationResponse.equals("HTTP/1.1 200 OK"))
	        {
	        	logger.info("Return message: "+jobCreationResponse + " " + jobElementID);
	    		
	        	return mmsUtil.getJobElement(mmsServer, projectID, refID, jobElementID);
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
//    		System.out.println("Return message: "+elementCreationResponse);
			logger.info("Return message: "+elementCreationResponse+" MMS"); // job element not created on mms. 
			return new ResponseEntity<String>(elementCreationResponse,status);
		}
	}
	
	/**
	 * This will run the job on jenkins and create an instance of a job
	 * @param projectID
	 * @param refID
	 * @param jobSysmlID
	 * @param jobInstance
	 * @return
	 */
	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs/{jobSysmlID}/instances", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<String> runJob(@PathVariable String projectID, @PathVariable String refID,@PathVariable String jobSysmlID, @RequestBody final JobInstanceFromClient jobInstance) {
		
		logger.info("Run job was called");
		
		logger.info("projectID: " + projectID + "\n" + "refID: " + refID + "\n" + "JSON input: " + "\n" + "Arguments: "
				+ jobInstance.getArguments() + "\n" + "MMS Server: " + jobInstance.getMmsServer() + "\n"
				+ "Alfresco Token: " + jobInstance.getAlfrescoToken());
		
		ObjectMapper mapper = new ObjectMapper(); // Used to create JSON objects
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // Http status to be returned. 
		
		// Check if job exists on jenkins first
    	JenkinsEngine je = login();
    	String jobResponse = je.getNestedJob(jobSysmlID, projectID+"/job/"+refID);
    	System.out.println("Job Response: "+jobResponse);
    	if((!jobResponse.equals("Job not found on Jenkins"))&&(!jobResponse.equals("HTTP/1.1 404 Not Found")))
    	{
    		System.out.println("");
    		String alfrescoToken = jobInstance.getAlfrescoToken();
    		String mmsServer = jobInstance.getMmsServer();
    		
    		String nextBuildNumber = je.getNextBuildNumber(jobSysmlID, projectID, refID);
    		
    		// Create job instance element. Use the jobs package as the owner.
    		
          	
    		MMSUtil mmsUtil = new MMSUtil(alfrescoToken);
    		String jobInstanceElementID = mmsUtil.createId();
    		String currentTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date()); //ex. 2017-06-08T13:37:19.483-0700
    		ObjectNode on = mmsUtil.buildDocGenJobInstanceJSON(jobInstanceElementID,"jobs_bin_"+jobSysmlID, jobSysmlID+"_instance_"+currentTimestamp,nextBuildNumber,"pending", mmsServer, projectID, refID,jobSysmlID); //job element will be the owner of the instance element
//    		System.out.println("job instance JSON: "+on.toString());
    		logger.info("job instance JSON: "+on);
    		if(on.get("message")!=null)
    		{
				jobResponse = on.toString();
				
				return new ResponseEntity<String>(jobResponse,status);
    		}
    		String elementCreationResponse = mmsUtil.post(mmsServer, projectID, refID, on);
    		
//    		System.out.println("job instance element creation response"+elementCreationResponse);
    		logger.info("job instance element creation response"+elementCreationResponse);
    		if (elementCreationResponse.equals("HTTP/1.1 200 OK"))
    		{
    			// run job on jenkins
    	        String runResponse = je.executeNestedJob(jobSysmlID, projectID, refID); // job name should be the job sysmlID
    	        
//    			System.out.println("Job run response: "+runResponse);
    			logger.info("Run job Jenkins response: "+runResponse);
//    			System.out.println("JOBRUN: "+runResponse);
    			if(runResponse.equals("HTTP/1.1 201 Created"))
    			{
    				status = HttpStatus.OK;
    				
    				String jobInstanceJSON = mmsUtil.getJobInstanceElement(mmsServer, projectID, refID, jobInstanceElementID,jobSysmlID);
    				
    		        return new ResponseEntity<String>(jobInstanceJSON,status);
    			}
    			mmsUtil.delete(mmsServer, projectID, refID, jobInstanceElementID);
	    		ObjectNode responseJSON = mapper.createObjectNode();
	    		responseJSON.put("message", runResponse + " Jenkins"); // jenkins error when running job
	    		runResponse = responseJSON.toString();
		        return new ResponseEntity<String>(runResponse,status);
    			
    		}
    		logger.info("MMS Element creation response: "+elementCreationResponse);
    		
    		ObjectNode responseJSON = mapper.createObjectNode();
    		responseJSON.put("message", elementCreationResponse + " MMS"); // mms issue when creating job instance
    		elementCreationResponse = responseJSON.toString();
	        return new ResponseEntity<String>(elementCreationResponse,status);
	        
    	}
    	else
    	{	
    		System.out.println("JOB NOT FOUND ELSE");
        	logger.info("Run Job Response: "+jobResponse); // Jenkins issue when checking if job exists.
        	if ((jobResponse.contains("HTTP/1.1 404 Not Found")||(jobResponse.equals("Job not found on Jenkins")))) 
			{
				status = HttpStatus.NOT_FOUND; 
				ObjectNode responseJSON = mapper.createObjectNode();
				responseJSON.put("message", jobResponse); 
				jobResponse = responseJSON.toString();
			}
        	return new ResponseEntity<String>(jobResponse,status);
    	}
  

	}
	
	/**
	 * Deletes job on Jenkins and Job element on MMS
	 * 
	 * @param projectID
	 * @param refID
	 * @param jobSysmlID
	 * @param alf_ticket
	 * @param mmsServer
	 * @return
	 */
	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs/{jobSysmlID}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public ResponseEntity<String> deleteJob(@PathVariable String projectID, @PathVariable String refID, @PathVariable String jobSysmlID,@RequestParam String alf_ticket,@RequestParam String mmsServer) {
		System.out.println("job" + "\n" + projectID + "\n" + refID + "\n");
		
		logger.info("Delete Job was called");
		logger.info( "projectID: "+ projectID + "\n" +"refID: "+ refID+ "\n"+"Job SysmlID: "+jobSysmlID+ "\n"+"alf_ticket: "+alf_ticket+ "\n"+"mmsServer: "+mmsServer);
		
		ObjectMapper mapper = new ObjectMapper(); // Used to create JSON objects
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // Http status to be returned. 
		
		// Delete job element on MMS.
		MMSUtil mmsUtil = new MMSUtil(alf_ticket);
		String elementDeleteResponse = mmsUtil.delete(mmsServer, projectID, refID, "jobs_bin_"+jobSysmlID);
		System.out.println("Element delete response: "+elementDeleteResponse);
		logger.info( "Element delete response: "+elementDeleteResponse);
		
		if(!elementDeleteResponse.equals("HTTP/1.1 200 OK"))
		{
			
    		ObjectNode responseJSON = mapper.createObjectNode();
    		responseJSON.put("message", elementDeleteResponse + " MMS"); // mms issue when creating job instance
	        return new ResponseEntity<String>(responseJSON.toString(),status);
//			return elementDeleteResponse+" MMS";
		}
		
		// delete job on jenkins
    	JenkinsEngine je = login();
    	String jenkinsDeleteResponse = je.deleteNestedJob(jobSysmlID, projectID, refID);
    	System.out.println("Jenkins delete response: "+jenkinsDeleteResponse);
    	logger.info( "Jenkins delete response: "+jenkinsDeleteResponse);
    	if(!jenkinsDeleteResponse.equals("HTTP/1.1 302 Found"))
		{
			if (jenkinsDeleteResponse.equals("HTTP/1.1 404 Not Found")) 
			{
				status = HttpStatus.NOT_FOUND; 
				jenkinsDeleteResponse="Job Not Found";
			}
    		ObjectNode responseJSON = mapper.createObjectNode();
    		responseJSON.put("message", jenkinsDeleteResponse); 
    		return new ResponseEntity<String>(responseJSON.toString(),status);
//			return jenkinsDeleteResponse+" Jenkins";
		}
    	
    	status = HttpStatus.OK; 
    	
    	logger.info("Delete Succesfull");
		ObjectNode responseJSON = mapper.createObjectNode();
		responseJSON.put("message", "Delete Succesfull"); 
        return new ResponseEntity<String>(responseJSON.toString(),status);
//		return "HTTP/1.1 200 OK";
	}
	
    public JenkinsEngine login()
    {
        JenkinsEngine je = new JenkinsEngine();
        je.setCredentials();
        je.login();
    	return je;
    }

}