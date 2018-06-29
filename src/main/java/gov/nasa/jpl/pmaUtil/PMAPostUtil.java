/**
 * Utility class for PMA Post endpoints.
 * @author hang
 */
package gov.nasa.jpl.pmaUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.w3c.dom.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nasa.jpl.dbUtil.DBUtil;
import gov.nasa.jpl.jenkinsUtil.JenkinsBuildConfig;
import gov.nasa.jpl.jenkinsUtil.JenkinsEngine;
import gov.nasa.jpl.mmsUtil.MMSUtil;

public class PMAPostUtil 
{
	
	public PMAPostUtil()
	{
		
	}
	
	/**
	 *  Runs the Jenkins job and updates the job instance specification with the current run information.
	 * @param jobSysmlID
	 * @param projectId
	 * @param refId
	 * @param alfrescoToken
	 * @param mmsServer
	 * @param je
	 * @param logger
	 * @param fromRefId
	 * @return
	 */
	public static ResponseEntity<String> runJob(String jobSysmlID,String projectId, String refId, String alfrescoToken, String mmsServer,JenkinsEngine je,Logger logger, String fromRefId,String comment)
	{
		ObjectMapper mapper = new ObjectMapper(); // Used to create JSON objects
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // Http status to be returned. 
		
		String nextBuildNumber = je.getNextBuildNumber(jobSysmlID, projectId, refId); // next build number from Jenkins
		
		//	Modifies the job instance with current run information or creates a new one if it doesn't exist

		MMSUtil mmsUtil = new MMSUtil(alfrescoToken);
		
		// Checking if the Jobs Bin is inside the model
		String packageInsideModelCheckResponse = mmsUtil.isJobPackgeInsideModel(mmsServer, projectId, refId);
		System.out.println("packageInsideModelCheckResponse: "+packageInsideModelCheckResponse);
		logger.info("packageInsideModelCheckResponse: "+packageInsideModelCheckResponse);
		
		String currentTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date()); //ex. 2017-06-08T13:37:19.483-0700
		
		Map<String,String> newJobInstanceValues = new HashMap<String,String>();
		newJobInstanceValues.put("completed", "");
		newJobInstanceValues.put("logUrl", "");
		newJobInstanceValues.put("jobStatus", "pending");
		newJobInstanceValues.put("started", currentTimestamp);
		newJobInstanceValues.put("buildNumber", nextBuildNumber);
		
		if (fromRefId != null) {
			newJobInstanceValues.put("fromRefId", fromRefId);
		}
		
		String modifyJobInstanceSpecificationResponse =  mmsUtil.modifyBulkInstanceSpecificationValue(mmsServer, projectId, refId, jobSysmlID, nextBuildNumber,newJobInstanceValues);
		
		logger.info("modify job instance element response: "+modifyJobInstanceSpecificationResponse);
		if (modifyJobInstanceSpecificationResponse.contains("Instance Specification Updated.")||modifyJobInstanceSpecificationResponse.contains("HTTP/1.1 200 OK"))
		{
			
			// run job on jenkins
	        String runResponse = null;
	        
	        if (fromRefId != null)  // docmerge job
	        {
	        	HashMap<String,String> parameterMap = new HashMap<String,String>();
	        	parameterMap.put("ticket", alfrescoToken);
	        	parameterMap.put("fromRefId", fromRefId);
	        	parameterMap.put("comment", comment);
	        	runResponse = je.executeNestedParamerterizedJob(jobSysmlID, projectId, refId,parameterMap); // job name should be the job sysmlID
	        }
	        else
	        {
	        	runResponse = je.executeNestedJob(jobSysmlID, projectId, refId); // job name should be the job sysmlID
	        }
	        

			logger.info("Run job Jenkins response: "+runResponse);

			if(runResponse.equals("HTTP/1.1 201 Created"))
			{
				status = HttpStatus.OK;
				
				String jobInstanceElementId = mmsUtil.getJobInstanceID(mmsServer, projectId, refId, jobSysmlID);
				if(jobInstanceElementId==null)
				{
					ObjectNode responseJSON = mapper.createObjectNode();
		    		responseJSON.put("message", "Couldn't Find Job Instance Element To Return"); // couldn't find job instance element to return
		    		runResponse = responseJSON.toString();
					return new ResponseEntity<String>(runResponse,HttpStatus.NOT_FOUND);
				}
				
				System.out.println("Job instance Id: "+jobInstanceElementId);
				String jobInstanceJSON = mmsUtil.getJobInstanceElement(mmsServer, projectId, refId, jobInstanceElementId,jobSysmlID);
				System.out.println("ran succesfully!");
				System.out.println("Job Instance JSON: "+jobInstanceJSON);
		        return new ResponseEntity<String>(jobInstanceJSON,status);
			}
			
			mmsUtil.modifyInstanceSpecificationValue(mmsServer, projectId, refId, jobSysmlID, nextBuildNumber, "jobStatus", "Didn't Start Due To Jenkins Error");
			mmsUtil.modifyInstanceSpecificationValue(mmsServer, projectId, refId, jobSysmlID, nextBuildNumber, "logUrl", runResponse);
			currentTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date()); //ex. 2017-06-08T13:37:19.483-0700
			mmsUtil.modifyInstanceSpecificationValue(mmsServer, projectId, refId, jobSysmlID, nextBuildNumber, "completed", currentTimestamp);
			
    		ObjectNode responseJSON = mapper.createObjectNode();
    		responseJSON.put("message", runResponse + " Jenkins"); // jenkins error when running job
    		runResponse = responseJSON.toString();
	        return new ResponseEntity<String>(runResponse,status);
			
		}
		else
		{
			mmsUtil.modifyInstanceSpecificationValue(mmsServer, projectId, refId, jobSysmlID, nextBuildNumber, "jobStatus", "Didn't Start Due To MMS Error");
			mmsUtil.modifyInstanceSpecificationValue(mmsServer, projectId, refId, jobSysmlID, nextBuildNumber, "logUrl", modifyJobInstanceSpecificationResponse);
			currentTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date()); //ex. 2017-06-08T13:37:19.483-0700
			mmsUtil.modifyInstanceSpecificationValue(mmsServer, projectId, refId, jobSysmlID, nextBuildNumber, "completed", currentTimestamp);
		}
		System.out.println("didn't run succesfully!");
		
		ObjectNode responseJSON = mapper.createObjectNode();
		responseJSON.put("message", modifyJobInstanceSpecificationResponse + " (MMS)"); // mms issue when creating job instance
		modifyJobInstanceSpecificationResponse = responseJSON.toString();
        return new ResponseEntity<String>(modifyJobInstanceSpecificationResponse,status);
	}
	
	/**
	 * Creates a job on jenkins and a job element on mms
	 * @param jobName
	 * @param alfrescoToken
	 * @param mmsServer
	 * @param associatedElementID
	 * @param schedule
	 * @param type
	 * @param projectID
	 * @param refID
	 * @param logger
	 * @param onlyCreateJenkinsJob If it is true, this method will create the Jenkins job without creating the mms element.
	 * @param jobID SysmlID of job if it needs to be specified.
	 * @param fromRefId The id of the ref to merge with the current ref.
	 * @return
	 */
	public static ResponseEntity<String> createJob(String jobName, String alfrescoToken,String mmsServer, String associatedElementID,String schedule, String type, String projectID,String refID,Logger logger,String fromRefId)
	{
		ObjectMapper mapper = new ObjectMapper();
		
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // By default, the return status code is 500 Internal Server error
		
		MMSUtil mmsUtil = new MMSUtil(alfrescoToken);
		
		Boolean jobsBinExists = mmsUtil.jobPackageExists(mmsServer, projectID, refID);

		
		if(!jobsBinExists) // create jobs bin package if it doesn't exist.
		{
			logger.info("Jobs Bin Does not exist");
			System.out.println("Jobs Bin Does not exist");
			ObjectNode packageNode = mmsUtil.buildPackageJSON("jobs_bin_"+projectID,projectID+"_pm","Jobs Bin"); // creating the package inside the project. Will 
//			ObjectNode packageNode = mmsUtil.buildPackageJSON("jobs_bin_"+projectID,projectID,"Jobs Bin"); // creating the package one level above the package, wont get synced back to the model.
//			System.out.println(packageNode.toString());
			String binCreateResponse = mmsUtil.post(mmsServer, projectID, refID, packageNode);
			System.out.println("Bin Create Response: "+binCreateResponse);
			logger.info("Bin Create Response: "+binCreateResponse);
		}
		
		// Checking if the Jobs Bin is inside the model
		String packageInsideModelCheckResponse = mmsUtil.isJobPackgeInsideModel(mmsServer, projectID, refID);
		System.out.println("packageInsideModelCheckResponse: "+packageInsideModelCheckResponse);
		logger.info("packageInsideModelCheckResponse: "+packageInsideModelCheckResponse);
		
		
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
		
		// Creating the job element json to send to mms. Job elements should be created in the jobs bin package
		
		ObjectNode on;
		if(type.equals("docmerge"))
		{
			on = mmsUtil.buildDocMergeJobElementJSON(jobElementID, "jobs_bin_"+jobElementID, jobName, associatedElementID, type, refID, projectID,fromRefId); 
		}
		else
		{
			on = mmsUtil.buildDocgenJobElementJSON(jobElementID, "jobs_bin_"+jobElementID, jobName, associatedElementID, type, schedule, refID, projectID); 
		}
		
		String elementCreationResponse = mmsUtil.post(mmsServer, projectID, refID, on);
		System.out.println("MMS Job element response: "+elementCreationResponse);
		logger.info("MMS Job element response: "+elementCreationResponse);
		System.out.println("");
		if (elementCreationResponse.equals("HTTP/1.1 200 OK"))
		{
//			System.out.println("Created Job Element ID: "+jobElementID);
			
			// Post to jenkins using jobElementID as the job name
			
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
			
			DBUtil dbUtil = new DBUtil();
			dbUtil.getCredentials(org);
			String jenkinsAgent = dbUtil.getJenkinsAgent();
	        
	        JenkinsBuildConfig jbc = new JenkinsBuildConfig();
	        jbc.setBuildAgent(jenkinsAgent);
	        jbc.setTargetElementID(associatedElementID);
	        jbc.setMmsServer(mmsServer);
	        jbc.setTeamworkProject(projectID);
	        jbc.setWorkspace(refID);
	        jbc.setJobID(jobElementID);
	        jbc.setSchedule(schedule); 
	        jbc.setJobType(type);
//	        System.out.println("Jenkins XML: "+jbc.generateBaseConfigXML());
	        
	        
	        ResponseEntity<String> jobPostResponse = jenkinsJobPost(associatedElementID, mmsServer, projectID, refID, jobElementID, schedule, type, logger,org);
	        
	        String jobCreationResponse = jobPostResponse.getBody();
	        if(PMAUtil.isJSON(jobCreationResponse))
	        {
	        	return jobPostResponse; // returning Jenkins error
	        }
	        
	        if(jobCreationResponse.equals("HTTP/1.1 200 OK")) // If job was created succesfully on Jenkins
	        {
	        	logger.info("Return message: "+jobCreationResponse + " " + jobElementID);
	    		
	        	return mmsUtil.getJobElement(mmsServer, projectID, refID, jobElementID);
	        }
	        else
	        {	
		        
		        mmsUtil.delete(mmsServer, projectID, refID, "jobs_bin_"+jobElementID); // Delete the job element since the job wasn't created on Jenkins.
		        
		        logger.info("Return message: "+jobCreationResponse +" Jenkins"); // job not created on jenkins 
		        System.out.println("jobCreationResponse");
	    		ObjectNode responseJSON = mapper.createObjectNode();
	    		responseJSON.put("message", jobCreationResponse+" Jenkins");
	    		jobCreationResponse = responseJSON.toString();
		        return new ResponseEntity<String>(jobCreationResponse,status);
	        }

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
	
	public static ResponseEntity<String> jenkinsJobPost(String associatedElementID, String mmsServer, String projectID, String refID, String jobElementID, String schedule, String type,Logger logger,String org)
	{
	
		ObjectMapper mapper = new ObjectMapper();
		
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // By default, the return status code is 500 Internal Server error
		
		// Post to jenkins using jobElementID as the job name
	    
		DBUtil dbUtil = new DBUtil();
		dbUtil.getCredentials(org);
		String jenkinsAgent = dbUtil.getJenkinsAgent();
        
        JenkinsBuildConfig jbc = new JenkinsBuildConfig();
        jbc.setBuildAgent(jenkinsAgent);
        jbc.setTargetElementID(associatedElementID);
        jbc.setMmsServer(mmsServer);
        jbc.setTeamworkProject(projectID);
        jbc.setWorkspace(refID);
        jbc.setJobID(jobElementID);
        jbc.setSchedule(schedule); 
        jbc.setJobType(type);
//        System.out.println("Jenkins XML: "+jbc.generateBaseConfigXML());
        
        JenkinsEngine je = new JenkinsEngine();
        je.setCredentials(org);
        je.login();
        
        /*
         *  Creating a folder for the projectID if it doesn't exist
         */
        String folderName = projectID;
        String jobString = je.getJob(folderName);
        
        logger.info("JOB STRING: "+jobString);
        System.out.println("Jenkins folder check string: "+jobString);
        if(!PMAUtil.isJSON(jobString)) // When folder doesn't exist, the jobString won't be a json.
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
        
        return new ResponseEntity<String>(jobCreationResponse,status);
        
	}
	
	/**
	 * Checks if there are any differences between the job element on MMS and the job on Jenkins. 
	 * Will push the differences on MMS to Jenkins.
	 * Currently checks associatedElement(TARGET_VIEW_ID), schedule, and disabled
	 * @param mmsUtil
	 * @param je
	 * @param projectId
	 * @param refId
	 * @param jobId
	 * @param mmsServer
	 */
	public static String updateJenkinsJobFromMMS(Logger logger, MMSUtil mmsUtil,JenkinsEngine je,String projectId,String refId,String jobId,String mmsServer)
	{
		Document doc = je.getConfigXML(projectId, refId, jobId);

		if(doc==null)
		{
			logger.error("Error when retrieving xml from Jenkins ");
			return "Error when retrieving xml from Jenkins ";
			
		}
		ResponseEntity<String> re = mmsUtil.getJobElement(mmsServer, projectId, refId, jobId);
		
		if(re.getStatusCodeValue()==200) // successful job element get
		{
			// Get job elements
			String jobsJsonString = re.getBody();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jobElement = null;
			try {
				JsonNode fullJson = mapper.readTree(jobsJsonString);
				JsonNode jobsArray = fullJson.get("jobs");
				if(jobsArray!=null)
				{
					jobElement = jobsArray.get(0);  // Assuming theres only 1 element in the jobsArray
				}
				else
				{
					// error
					System.out.println("jobsArray is null");
					logger.error("jobsArray is null");
					return jobsJsonString;
				}

			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// do a diff , could probably put into its own method, params(map,jsonNode) returns a map of diff
			if(jobElement!=null)
			{
				
				Map<String,String> jenkinsEnvironmentVariables = je.getEnvironmentVariablesFromConfigXml(doc);
				
				if(jenkinsEnvironmentVariables!=null)
				{
					Map<String,String> jenkinsDifferences = new HashMap();
					
//					System.out.println("Values: "+jenkinsEnvironmentVariables.values());
					String jenkinsSchedule = jenkinsEnvironmentVariables.get("schedule");
					String jenkinsDisabled = jenkinsEnvironmentVariables.get("disabled");
					String jenkinsTargetViewId = jenkinsEnvironmentVariables.get("TARGET_VIEW_ID");
					
					JsonNode scheduleNode = jobElement.get("schedule");
					JsonNode disabledNode = jobElement.get("disabled");
					JsonNode associatedElementIDNode = jobElement.get("associatedElementID");
					
					if(scheduleNode!=null)
					{
						String mmsJobSchedule = scheduleNode.toString().replace("\"", "");
						if(jenkinsSchedule!=null&&(!jenkinsSchedule.equals(mmsJobSchedule)))
						{
							jenkinsDifferences.put("schedule", mmsJobSchedule);
							System.out.println("Difference Found. MMS: "+mmsJobSchedule+" Jenkins: "+jenkinsSchedule);
						}
						if(jenkinsSchedule==null)
						{
							if(!mmsJobSchedule.equals(""))
							{
								jenkinsDifferences.put("schedule", mmsJobSchedule);
								System.out.println("Difference Found. MMS: "+mmsJobSchedule+" Jenkins: "+jenkinsSchedule);
							}
						}
					}
					if(disabledNode!=null)
					{
						String mmsJobDisabled = disabledNode.toString().replace("\"", "");
						if(jenkinsDisabled==null||(!jenkinsDisabled.equals(mmsJobDisabled)))
						{
							jenkinsDifferences.put("disabled", mmsJobDisabled);
							System.out.println("Difference Found. MMS: "+mmsJobDisabled+" Jenkins: "+jenkinsDisabled);
						}
					}
					if(associatedElementIDNode!=null)
					{
						String mmsJobTargetViewId = associatedElementIDNode.toString().replace("\"", "");
						if(jenkinsTargetViewId==null||(!jenkinsTargetViewId.equals(mmsJobTargetViewId)))
						{
							jenkinsDifferences.put("TARGET_VIEW_ID", mmsJobTargetViewId);
							System.out.println("Difference Found. MMS: "+mmsJobTargetViewId+" Jenkins: "+jenkinsTargetViewId);
						}
					}

					System.out.println("Diffs on Jenkins: "+jenkinsDifferences+" "+jenkinsDifferences.size());
					logger.info("Diffs on Jenkins: "+jenkinsDifferences+" "+jenkinsDifferences.size());
					
					if(jenkinsDifferences.size()>0)
					{
						
						Iterator it = jenkinsDifferences.entrySet().iterator();
					    while (it.hasNext()) {
					        Map.Entry pair = (Map.Entry)it.next();
					        System.out.println(pair.getKey() + " = " + pair.getValue());
					        
					        // if diff update xml
							je.replaceEnvironmentValueInConfigXML(doc, pair.getKey().toString(),pair.getValue().toString());
							
					        it.remove(); // avoids a ConcurrentModificationException
					    }
						// send back xml
						String xmlString = je.xmlDocToString(doc);
						String postResponse = je.postModifiedConfigXML(projectId, refId, jobId, xmlString);
						System.out.println("Job Update Post Response: "+postResponse);
						logger.info("Job Update Post Response: "+postResponse);
						// return post response, should be 200 ok
						return postResponse;
					}
					else
					{
						return "No difference";
					}
				}
				else
				{
					// error jenkins xml didn't have the correct keys
					logger.error("Error: Jenkins XML didn't have the correct keys");
					return "Error: Jenkins XML didn't have the correct keys";
				}
			}
			else
			{
				// Job element not found
				logger.error("Error: Job element not found in jobs json");
				return "Error: Job element not found in jobs json";
			}
		}
		else
		{
			// MMS Error
			logger.debug("MMS ERROR: "+re.getBody().toString());
			System.out.println("MMS ERROR: "+re.getBody().toString());
			return "MMS ERROR: "+re.getBody().toString();
		}
		
	}
}
