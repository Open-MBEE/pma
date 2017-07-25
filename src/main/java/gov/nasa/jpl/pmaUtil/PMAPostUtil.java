/**
 * Utility class for PMA Post endpoints.
 * @author hang
 */
package gov.nasa.jpl.pmaUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
	 *  Runs the Jenkins job and creates an Instance specification that will contain the run history.
	 * @param jobSysmlID
	 * @param projectID
	 * @param refID
	 * @param alfrescoToken
	 * @param mmsServer
	 * @param je
	 * @param logger
	 * @return
	 */
	public static ResponseEntity<String> runJob(String jobSysmlID,String projectID, String refID, String alfrescoToken, String mmsServer,JenkinsEngine je,Logger logger)
	{
		ObjectMapper mapper = new ObjectMapper(); // Used to create JSON objects
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // Http status to be returned. 
		
		String jobResponse = "";
		
		String nextBuildNumber = je.getNextBuildNumber(jobSysmlID, projectID, refID);
		
		// Create job instance element. Uses the job package as the owner.
		
		MMSUtil mmsUtil = new MMSUtil(alfrescoToken);
		String jobInstanceElementID = mmsUtil.createId();
		String currentTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date()); //ex. 2017-06-08T13:37:19.483-0700
		ObjectNode on = mmsUtil.buildDocGenJobInstanceJSON(jobInstanceElementID,"jobs_bin_"+jobSysmlID, jobSysmlID+"_instance_"+currentTimestamp,nextBuildNumber,"pending", mmsServer, projectID, refID,jobSysmlID); //job element will be the owner of the instance element
//		System.out.println("job instance JSON: "+on.toString());
		logger.info("job instance JSON: "+on);
		if(on.get("message")!=null)
		{
			jobResponse = on.toString();
			
			return new ResponseEntity<String>(jobResponse,status);
		}
		String elementCreationResponse = mmsUtil.post(mmsServer, projectID, refID, on);
		
//		System.out.println("job instance element creation response"+elementCreationResponse);
		logger.info("job instance element creation response"+elementCreationResponse);
		if (elementCreationResponse.equals("HTTP/1.1 200 OK"))
		{
			// run job on jenkins
	        String runResponse = je.executeNestedJob(jobSysmlID, projectID, refID); // job name should be the job sysmlID
	        
//			System.out.println("Job run response: "+runResponse);
			logger.info("Run job Jenkins response: "+runResponse);
//			System.out.println("JOBRUN: "+runResponse);
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
	 * @return
	 */
	public static ResponseEntity<String> createJob(String jobName, String alfrescoToken,String mmsServer, String associatedElementID,String schedule, String type, String projectID,String refID,Logger logger)
	{
		ObjectMapper mapper = new ObjectMapper();
		
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
		
		
		ObjectNode on = mmsUtil.buildDocgenJobElementJSON(jobElementID, "jobs_bin_"+jobElementID, jobName, associatedElementID, type, schedule, refID, projectID); // Job elements should be created in the jobs bin package
		
//		System.out.println("Job class JSON: "+on.toString());
//		logger.info("Job class JSON: "+on.toString());
		
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
	        jbc.setJobType(type);
//	        System.out.println("Jenkins XML: "+jbc.generateBaseConfigXML());
	        
	        
	        ResponseEntity<String> jobPostResponse = jenkinsJobPost(associatedElementID, mmsServer, projectID, refID, jobElementID, schedule, type, logger);
	        
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
		        
		        mmsUtil.delete(mmsServer, projectID, refID, jobElementID); // Delete the job element since the job wasn't created on Jenkins.
		        
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
	
	public static ResponseEntity<String> jenkinsJobPost(String associatedElementID, String mmsServer, String projectID, String refID, String jobElementID, String schedule, String type,Logger logger)
	{
	
		ObjectMapper mapper = new ObjectMapper();
		
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // By default, the return status code is 500 Internal Server error
		
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
        jbc.setJobType(type);
//        System.out.println("Jenkins XML: "+jbc.generateBaseConfigXML());
        
        JenkinsEngine je = new JenkinsEngine();
        je.setCredentials();
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
	
	public static void main(String args[])
	{

	}
}
