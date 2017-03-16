package gov.nasa.jpl.controllers;

import java.sql.Timestamp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
	public String getJobInstances(@PathVariable String projectID, @PathVariable String refID, @PathVariable String jobSysmlID) {
		return "job instance" + "\n" + projectID + "\n" + refID + "\n" + jobSysmlID;
	}

	/**
	 * Creates job element on mms and job on Jenkins.
	 * @param projectID
	 * @param refID
	 * @param jobjobFromVE
	 * @return
	 */
	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs", method = RequestMethod.POST)
	@ResponseBody
	public String createJob(@PathVariable String projectID, @PathVariable String refID, @RequestBody final JobFromVE jobFromVE) {
		ObjectMapper mapper = new ObjectMapper();
		
		String alfrescoToken = jobFromVE.getAlfrescoToken();
		String mmsServer = jobFromVE.getMmsServer(); 
		String associatedElementID = jobFromVE.getAssociatedElementID();
		
		MMSUtil mmsUtil = new MMSUtil(alfrescoToken);

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		ObjectNode on2 = mmsUtil.buildJobElementJSON("PMA_"+timestamp.getTime(),associatedElementID,"tempJob");
		System.out.println(on2.toString());
		String response = mmsUtil.post(mmsServer, projectID,refID, on2);
		
		try {
			return "Response: "+response+"\n"+mapper.writeValueAsString(jobFromVE);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Error";
	}

	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs/instances", method = RequestMethod.POST)
	@ResponseBody
	public Job runJob(@PathVariable String projectID, @PathVariable String refID, @RequestBody final Job job) {
		System.out.println("job" + "\n" + projectID + "\n" + refID + "\n");
		return job;
	}

	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs/{jobSysmlID}", method = RequestMethod.DELETE)
	@ResponseBody
	public String deleteJob(@PathVariable String projectID, @PathVariable String refID, @PathVariable String jobSysmlID) {
		System.out.println("job" + "\n" + projectID + "\n" + refID + "\n");
		return "job deleted" + "\n" + projectID + "\n" + refID + "\n" + jobSysmlID;
	}

}