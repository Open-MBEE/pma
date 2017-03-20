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

@Controller
public class VeEndpointContoller {

	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs/{jobSysmlID}", method = RequestMethod.GET)
	@ResponseBody
	public String getJob(@PathVariable String projectID, @PathVariable String refID, @PathVariable String jobSysmlID) {
		return "job" + "\n" + projectID + "\n" + refID + "\n" + jobSysmlID;
	}

	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs/{jobSysmlID}/instances", method = RequestMethod.GET)
	@ResponseBody
	public String getJobInstances(@PathVariable String projectID, @PathVariable String refID,
			@PathVariable String jobSysmlID) {
		return "job instance" + "\n" + projectID + "\n" + refID + "\n" + jobSysmlID;
	}

	/**
	 * Creates job element on mms and job on Jenkins.
	 * 
	 * @param projectID
	 * @param refID
	 * @param jobjobFromVE
	 * @return
	 */
	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs", method = RequestMethod.POST)
	@ResponseBody
	public String createJob(@PathVariable String projectID, @PathVariable String refID,
			@RequestBody final JobFromVE jobFromVE) {
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
		
		if (elementCreationResponse.equals("HTTP/1.1 200 OK"))
		{
			System.out.println("Element Created");
			
			// Post to jenkins using jobElementID as the job name
	        String buildAgent = "Analysis01-UAT";
//	        String teamworkProject = "PROJECT-ID_2_24_17_3_05_44_PM__4fbf6b8b_15a55999900__6e6f_cae_tw_uat_jpl_nasa_gov_128_149_18_101";
	        
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
	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs/instances", method = RequestMethod.POST)
	@ResponseBody
	public Job runJob(@PathVariable String projectID, @PathVariable String refID, @RequestBody final Job job) {
		System.out.println("job" + "\n" + projectID + "\n" + refID + "\n");
		return job;
	}
	
	
	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs/{jobSysmlID}", method = RequestMethod.DELETE)
	@ResponseBody
	public String deleteJob(@PathVariable String projectID, @PathVariable String refID,
			@PathVariable String jobSysmlID) {
		System.out.println("job" + "\n" + projectID + "\n" + refID + "\n");
		return "job deleted" + "\n" + projectID + "\n" + refID + "\n" + jobSysmlID;
	}
	
    public JenkinsEngine login()
    {
    	String configFile = "config.txt";
        List<String> lines = new ArrayList();
        try {
            Scanner sc = new Scanner(new File(configFile));

            while (sc.hasNextLine()) {
                lines.add(sc.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JenkinsEngine je = new JenkinsEngine();
        je.setUsername(lines.get(0));
        je.setPassword(lines.get(1));
        je.setURL(lines.get(2));
        je.login();
    	return je;
    }

}