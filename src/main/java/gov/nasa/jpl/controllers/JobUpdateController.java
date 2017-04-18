package gov.nasa.jpl.controllers;

import java.io.IOException;
import java.sql.Timestamp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nasa.jpl.jenkinsUtil.JenkinsEngine;
import gov.nasa.jpl.mmsUtil.MMSUtil;

@Controller
public class JobUpdateController 
{
		
	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs/{jobID}/instances/{buildNumber}/{propertyName}/{value}/{mmsServer}", method = RequestMethod.POST)
	@ResponseBody
	public String updateJobInstanceProperty(@PathVariable String projectID, @PathVariable String refID,@PathVariable String jobID,@PathVariable String buildNumber,@PathVariable String propertyName,@PathVariable String value,@PathVariable String mmsServer,@RequestBody String bodyContent) 
	{
		// recieve token from jenkins
		String token = "";
//		String server = "opencae-uat.jpl.nasa.gov";
		System.out.println("propertyName: "+propertyName);
		System.out.println("value: "+value);
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode fullJson = mapper.readTree(bodyContent);
			System.out.println(fullJson);
			JsonNode data = fullJson.get("data");
			if (data != null)  // elements will be null if the json passed in is incorrect
			{
				JsonNode alfrescoTicket = data.get("ticket");
				token = alfrescoTicket.toString().replace("\"", "");
				System.out.println(token);
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MMSUtil mmsUtil = new MMSUtil(token);
		String newPropertyValue = value;
		
		/*
		 * Finds the property and updates the value on mms. 
		 * If the job instance doesn't exist, one will be created for the jenkins run.
		 */
		String mmsResponse = mmsUtil.modifyPartPropertyValue(server, projectID, refID, jobID, buildNumber, propertyName, newPropertyValue, token);
		
		return mmsResponse;	
	}

}