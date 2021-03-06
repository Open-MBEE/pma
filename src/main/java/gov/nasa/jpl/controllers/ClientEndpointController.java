package gov.nasa.jpl.controllers;

import java.io.IOException;

/**
 * Endpoints for applications to interface with PMA.
 */

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nasa.jpl.jenkinsUtil.JenkinsEngine;
import gov.nasa.jpl.mmsUtil.MMSUtil;
import gov.nasa.jpl.model.JobFromClient;
import gov.nasa.jpl.model.JobInstanceFromClient;
import gov.nasa.jpl.pmaUtil.PMAUtil;
import gov.nasa.jpl.pmaUtil.PMAPostUtil;

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
		
		MMSUtil mmsUtil = new MMSUtil(alf_ticket);
		return mmsUtil.getJobElement(mmsServer, projectID, refID, jobSysmlID);
		
		
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
		
		MMSUtil mmsUtil = new MMSUtil(alf_ticket);
		return mmsUtil.getJobInstanceElements(mmsServer, projectID, refID, jobSysmlID);

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
	public ResponseEntity<String> createJob(@PathVariable String projectID, @PathVariable String refID, @RequestBody final JobFromClient jobFromClient) {
		
		logger.info("Create Job was called");
		logger.info("projectID: " + projectID + "\n" + "refID: " + refID + "\n" + "JSON input: " + "\n" + "Job Name: "
				+ jobFromClient.getJobName() + "\n" + "Type: " + jobFromClient.getType() + "\n" + "From Ref Id: "
				+ jobFromClient.getFromRefId() + "\n" + "Schedule: " + jobFromClient.getSchedule() + "\n"
				+ "Associated Element ID: " + jobFromClient.getAssociatedElementID() + "\n" + "MMS Server: "
				+ jobFromClient.getMmsServer() + "\n" + "Alfresco Token: " + jobFromClient.getAlfrescoToken() );
		
		String jobName = jobFromClient.getJobName();
		String alfrescoToken = jobFromClient.getAlfrescoToken();
		String mmsServer = jobFromClient.getMmsServer();
		String associatedElementID = jobFromClient.getAssociatedElementID();
		String schedule = jobFromClient.getSchedule();
		String type = jobFromClient.getType();
		String fromRefId = jobFromClient.getFromRefId();
		
		return PMAPostUtil.createJob(jobName, alfrescoToken, mmsServer, associatedElementID, schedule, type, projectID, refID, logger, fromRefId);
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
				+ "Alfresco Token: " + jobInstance.getAlfrescoToken() +"\n"+"fromRefId: "+jobInstance.getFromRefId()
				+ "comment" + jobInstance.getComment());
		
		ObjectMapper mapper = new ObjectMapper(); // Used to create JSON objects
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // Http status to be returned. 
		
		String alfrescoToken = jobInstance.getAlfrescoToken();
		String mmsServer = jobInstance.getMmsServer();
		String fromRefId = jobInstance.getFromRefId();
    	String comment = jobInstance.getComment();
		
		MMSUtil mmsUtil = new MMSUtil(alfrescoToken);
		String org = mmsUtil.getProjectOrg(mmsServer, projectID);
		JenkinsEngine je = login(org);

		String isJobDisabled = mmsUtil.isJobDisabled(mmsServer, projectID, refID, jobSysmlID);
		if(isJobDisabled.equals("true")||isJobDisabled.equals("false"))
		{
			
				if(PMAUtil.isJSON(org)||(org==null)) // Checking if mms response came back with an error.
			    {
					
			        logger.info("Get org response: "+org); 
			        System.out.println("Get org response: "+org);
		    		ObjectNode responseJSON = mapper.createObjectNode();
		    		if(org==null)
					{
						status = HttpStatus.NOT_FOUND;
						responseJSON.put("message", "Project not found on MMS");
					}
		    		else
		    		{
		    			responseJSON.put("message", org+" MMS");
		    		}
		    		
			        return new ResponseEntity<String>(responseJSON.toString(),status);
			    }
				
				// Check if job exists on jenkins first
		    	String jobResponse = je.getNestedJob(jobSysmlID, projectID+"/job/"+refID);
		    	if((!jobResponse.equals("Job not found on Jenkins"))&&(!jobResponse.equals("HTTP/1.1 404 Not Found"))) // Job exists on Jenkins
		    	{
		    		String jobUpdateResponse = PMAPostUtil.updateJenkinsJobFromMMS(logger,mmsUtil, je, projectID, refID, jobSysmlID, mmsServer); // checking differences between job block and Jenkins job
		    		if((!jobUpdateResponse.equals("No difference"))&&(!jobUpdateResponse.equals("HTTP/1.1 200 OK")))
		    		{
		    			ObjectNode responseJSON = mapper.createObjectNode();
	    				responseJSON.put("message", jobUpdateResponse);
	    				return new ResponseEntity<String>(responseJSON.toString(),status);
		    		}
		    		
		    	   	System.out.println("Running Job: "+jobSysmlID);
		        	logger.info("Running Job: "+jobSysmlID);
		        	
	    			if(isJobDisabled.equals("true"))
	    			{
	    				ObjectNode responseJSON = mapper.createObjectNode();
	    				responseJSON.put("message", "Job is disabled");
	    				return new ResponseEntity<String>(responseJSON.toString(),HttpStatus.METHOD_NOT_ALLOWED);
	    			}

		    		return PMAPostUtil.runJob(jobSysmlID, projectID, refID, alfrescoToken, mmsServer, je, logger,fromRefId,comment);
			        
		    	}
		    	else 
		    	{	
		    		System.out.println("JOB NOT FOUND ELSE");
		        	logger.info("First Run Job Response: "+jobResponse); // Jenkins issue when checking if job exists.
		        	if ((jobResponse.contains("HTTP/1.1 404 Not Found")||(jobResponse.equals("Job not found on Jenkins"))))  // Job doesn't exist on Jenkins
					{
		        		
		        		// Creating job on Jenkins if the job exists on MMS.
		        		String schedule = null;
		        		String type = null;
		        		String associatedElementID = null;
		        		try {
		        			// Checking if job element exists on MMS and retrieves the job information if it does.
							String jobJsonString = mmsUtil.getJobElement(mmsServer, projectID, refID, jobSysmlID).getBody();
							System.out.println("Job JSON String: "+jobJsonString);
							JsonNode fullJson = mapper.readTree(jobJsonString);
							if(fullJson!=null)
							{
								JsonNode jobJson = fullJson.get("jobs");
								if(jobJson!=null)
								{
									JsonNode job = jobJson.get(0);
									if(job!=null)
									{
										JsonNode scheduleNode = job.get("schedule");
										JsonNode typeNode = job.get("type");
										JsonNode associatedElementIDNode = job.get("associatedElementID");
										
										if (scheduleNode != null) {
											schedule = scheduleNode.toString().replace("\"", "");
										}
										if (typeNode != null) {
											type = typeNode.toString().replace("\"", "");
										}
										if (associatedElementIDNode != null) {
											associatedElementID = associatedElementIDNode.toString().replace("\"", "");
										}

									}
									else
									{
										ObjectNode responseJSON = mapper.createObjectNode();
										responseJSON.put("message", "Job Element Doesn't Exist on MMS"); 
										return new ResponseEntity<String>(responseJSON.toString(),HttpStatus.NOT_FOUND); // Job element was not found since jobs array was blank
									}
								}
								else
								{
									ObjectNode responseJSON = mapper.createObjectNode();
									responseJSON.put("message", jobJsonString); 
									return new ResponseEntity<String>(responseJSON.toString(),status); // If jobJson was null, then the job element must not exist or mms returned an error
								}
							}
							else
							{
								ObjectNode responseJSON = mapper.createObjectNode();
								responseJSON.put("message", jobJsonString); 
								return new ResponseEntity<String>(jobJsonString,status); // If fullJson was null, then the job element must not exist because mms returned an empty json or mms returned an error.
							}

						} catch (JsonProcessingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		        		
		        		logger.info("Creating Job on Jenkins using job element Information");
		        		System.out.println("Creating Job on Jenkins using job element Information");
		        		
		        		// Creating job on Jenkins with the job element info pulled from MMS.
		        		ResponseEntity<String> createJobOutput = PMAPostUtil.jenkinsJobPost(associatedElementID, mmsServer, projectID, refID, jobSysmlID, schedule, type, logger,org);
		        		
		        		logger.info("Create JOB OUTPUT: "+createJobOutput.getBody());
		        		System.out.println("Create JOB OUTPUT: "+createJobOutput.getBody());
		        		
		        		String jobCreationResponse = createJobOutput.getBody();
		    	        if(PMAUtil.isJSON(jobCreationResponse))
		    	        {
		    	        	return createJobOutput; // returning Jenkins error
		    	        }
		    	        
		    	        if(jobCreationResponse.equals("HTTP/1.1 200 OK")) // If job was created succesfully on Jenkins
		    	        {	
				    		String jobUpdateResponse = PMAPostUtil.updateJenkinsJobFromMMS(logger,mmsUtil, je, projectID, refID, jobSysmlID, mmsServer); // checking differences between job block and Jenkins job
				    		if((!jobUpdateResponse.equals("No difference"))&&(!jobUpdateResponse.equals("HTTP/1.1 200 OK")))
				    		{
				    			ObjectNode responseJSON = mapper.createObjectNode();
			    				responseJSON.put("message", jobUpdateResponse);
			    				return new ResponseEntity<String>(responseJSON.toString(),status);
				    		}
			    			if(isJobDisabled.equals("true"))
			    			{
			    				ObjectNode responseJSON = mapper.createObjectNode();
			    				responseJSON.put("message", "Job is disabled");
			    				return new ResponseEntity<String>(responseJSON.toString(),HttpStatus.METHOD_NOT_ALLOWED);
			    			}
		    	        	System.out.println("Running Job: "+jobSysmlID);
		    	        	logger.info("Running Job: "+jobSysmlID);
		    	        	return PMAPostUtil.runJob(jobSysmlID, projectID, refID, alfrescoToken, mmsServer, je, logger,fromRefId,comment);
		    	        }
		    	        else
		    	        {
		    	      		ObjectNode responseJSON = mapper.createObjectNode();
		    	    		responseJSON.put("message", jobCreationResponse + " Jenkins"); // Jenkins issue when creating job instance
		    		        return new ResponseEntity<String>(responseJSON.toString(),status);
		    	        }
		    	        
					}
		        	return new ResponseEntity<String>(jobResponse,status);
		    	}
		}
		else
		{
			ObjectNode responseJSON = mapper.createObjectNode();
			responseJSON.put("message", isJobDisabled);
			return new ResponseEntity<String>(responseJSON.toString(),status);
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
	public ResponseEntity<String> deleteJob(@PathVariable String projectID, @PathVariable String refID, @PathVariable String jobSysmlID,@RequestParam String alf_ticket,@RequestParam String mmsServer) {
		System.out.println("job" + "\n" + projectID + "\n" + refID + "\n");
		
		logger.info("Delete Job was called");
		logger.info( "projectID: "+ projectID + "\n" +"refID: "+ refID+ "\n"+"Job SysmlID: "+jobSysmlID+ "\n"+"alf_ticket: "+alf_ticket+ "\n"+"mmsServer: "+mmsServer);
		
		ObjectMapper mapper = new ObjectMapper(); // Used to create JSON objects
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // Http status to be returned. 
		
		// Delete job element on MMS.
		MMSUtil mmsUtil = new MMSUtil(alf_ticket);
		String elementDeleteResponse = 	mmsUtil.delete(mmsServer, projectID, refID, "jobs_bin_"+jobSysmlID);  // Delete Job Folder
		System.out.println("Element delete response: "+elementDeleteResponse);
		logger.info( "Element delete response: "+elementDeleteResponse);
		
		if(!elementDeleteResponse.equals("HTTP/1.1 200 OK"))
		{
    		ObjectNode responseJSON = mapper.createObjectNode();
    		responseJSON.put("message", elementDeleteResponse + " MMS"); // mms issue when creating job instance
	        return new ResponseEntity<String>(responseJSON.toString(),status);
//			return elementDeleteResponse+" MMS";
		}

		String org = mmsUtil.getProjectOrg(mmsServer, projectID);
		
		if(PMAUtil.isJSON(org)||(org==null)) // Checking if mms response came back with an error.
	    {
			
	        logger.info("Get org response: "+org); 
	        System.out.println("Get org response: "+org);
    		ObjectNode responseJSON = mapper.createObjectNode();
    		if(org==null)
			{
				status = HttpStatus.NOT_FOUND;
				responseJSON.put("message", "Project not found on MMS");
			}
    		else
    		{
    			responseJSON.put("message", org+" MMS");
    		}
    		
	        return new ResponseEntity<String>(responseJSON.toString(),status);
	    }
		
		// delete job on jenkins
    	JenkinsEngine je = login(org);
    	String jenkinsDeleteResponse = je.deleteNestedJob(jobSysmlID, projectID, refID);
    	System.out.println("Jenkins delete response: "+jenkinsDeleteResponse);
    	logger.info( "Jenkins delete response: "+jenkinsDeleteResponse);
    	if(!jenkinsDeleteResponse.equals("HTTP/1.1 302 Found"))
		{
			if (!jenkinsDeleteResponse.equals("HTTP/1.1 404 Not Found")) // Not founds are ok.
			{
		 		ObjectNode responseJSON = mapper.createObjectNode();
	    		responseJSON.put("message", jenkinsDeleteResponse); 
	    		return new ResponseEntity<String>(responseJSON.toString(),status); // returning error during Jenkins job delete 
			}
   
//			return jenkinsDeleteResponse+" Jenkins";
		}
    	
    	status = HttpStatus.OK; 
    	
    	logger.info("Delete Succesfull");
		ObjectNode responseJSON = mapper.createObjectNode();
		responseJSON.put("message", "Delete Succesfull"); 
        return new ResponseEntity<String>(responseJSON.toString(),status);
	}
	
	/**
	 * Disables job on Jenkins and sets the disabled job element property to true on mms
	 * 
	 * @param projectId
	 * @param refId
	 * @param jobSysmlId
	 * @param alf_ticket
	 * @param mmsServer
	 * @return
	 */
	@RequestMapping(value = "/projects/{projectId}/refs/{refId}/jobs/{jobSysmlId}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public ResponseEntity<String> disableJob(@PathVariable String projectId, @PathVariable String refId, @PathVariable String jobSysmlId,@RequestParam String alf_ticket,@RequestParam String mmsServer,@RequestParam(value="enable", required = false, defaultValue = "false") boolean enable) {
		System.out.println("job" + "\n" + projectId + "\n" + refId + "\n");
		
		logger.info("Disabled Job was called");
		logger.info( "projectID: "+ projectId + "\n" +"refID: "+ refId+ "\n"+"Job SysmlID: "+jobSysmlId+ "\n"+"alf_ticket: "+alf_ticket+ "\n"+"mmsServer: "+mmsServer);
		System.out.println("Enable: "+enable);
		
		ObjectMapper mapper = new ObjectMapper(); // Used to create JSON objects
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // Http status to be returned. 
		
		
		MMSUtil mmsUtil = new MMSUtil(alf_ticket);
		
		// Set Job element disabled flag to true
		String disableJobElementResponse = null;
		if(enable)
		{
			disableJobElementResponse = mmsUtil.modifyJobValueProperty(mmsServer, projectId, refId, jobSysmlId, "disabled", "false");  // set the part property value from false to true.
		}
		else
		{
			disableJobElementResponse = mmsUtil.modifyJobValueProperty(mmsServer, projectId, refId, jobSysmlId, "disabled", "true");  // set the part property value from false to true.
		}
		
		System.out.println("Job disable response: "+disableJobElementResponse);
		logger.info( "Job disable response: "+disableJobElementResponse);
		
		if(!disableJobElementResponse.equals("HTTP/1.1 200 OK"))
		{
    		ObjectNode responseJSON = mapper.createObjectNode();
    		responseJSON.put("message", disableJobElementResponse + " MMS"); // mms issue when modifying disabled value property
	        return new ResponseEntity<String>(responseJSON.toString(),status);
		}

		String org = mmsUtil.getProjectOrg(mmsServer, projectId);
		
		if(PMAUtil.isJSON(org)||(org==null)) // Checking if mms response came back with an error.
	    {
			
	        logger.info("Get org response: "+org); 
	        System.out.println("Get org response: "+org);
    		ObjectNode responseJSON = mapper.createObjectNode();
    		if(org==null)
			{
				status = HttpStatus.NOT_FOUND;
				responseJSON.put("message", "Project not found on MMS");
			}
    		else
    		{
    			responseJSON.put("message", org+" MMS");
    		}
    		
	        return new ResponseEntity<String>(responseJSON.toString(),status);
	    }
		
		// disable job on jenkins
    	JenkinsEngine je = login(org);
    	String jenkinsDisableResponse = null;
    			
    	if(enable)
    	{
    		jenkinsDisableResponse = je.enableNestedJob(jobSysmlId, projectId, refId);
    	}
    	else
    	{
    		jenkinsDisableResponse = je.disableNestedJob(jobSysmlId, projectId, refId);
    	}
    	
    	System.out.println("Jenkins job disable response: "+jenkinsDisableResponse);
    	logger.info( "Jenkins job disable response: "+jenkinsDisableResponse);
    	if(!jenkinsDisableResponse.equals("HTTP/1.1 302 Found"))
		{
			if (!jenkinsDisableResponse.equals("HTTP/1.1 404 Not Found")) // Not founds are ok. The job might not always exist on Jenkins because the job might have been created on a different branch.
			{
		 		ObjectNode responseJSON = mapper.createObjectNode();
	    		responseJSON.put("message", jenkinsDisableResponse); 
	    		return new ResponseEntity<String>(responseJSON.toString(),status); // returning error during Jenkins job disabled 
			}
		}
    	
    	status = HttpStatus.OK; 
    	
      	if(enable)
    	{
      		logger.info("Enable Succesfull");
    		ObjectNode responseJSON = mapper.createObjectNode();
    		responseJSON.put("message", "Enable Succesfull"); 
            return new ResponseEntity<String>(responseJSON.toString(),status);
    	}
    	else
    	{
    		logger.info("Disable Succesfull");
    		ObjectNode responseJSON = mapper.createObjectNode();
    		responseJSON.put("message", "Disable Succesfull"); 
            return new ResponseEntity<String>(responseJSON.toString(),status);
    	}
    
		        
	}
	
    public JenkinsEngine login(String org)
    {
        JenkinsEngine je = new JenkinsEngine();
        je.setCredentials(org);
        je.login();
    	return je;
    }

}