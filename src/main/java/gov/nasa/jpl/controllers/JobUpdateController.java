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
	
	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs/{jobID}/instances/{instanceID}/{status}", method = RequestMethod.POST)
	@ResponseBody
	public String createJobInstance(@PathVariable String projectID, @PathVariable String refID,@PathVariable String jobID,@PathVariable String instanceID) {

		return "";
	}
	
	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs/{jobID}/instances/{buildNumber}/{propertyName}/{value}", method = RequestMethod.POST)
	@ResponseBody
	public String updateJobInstanceProperty(@PathVariable String projectID, @PathVariable String refID,@PathVariable String jobID,@PathVariable String buildNumber,@PathVariable String propertyName,@PathVariable String value,@RequestBody String bodyContent) 
	{
		
		String token = "";
		String server = "opencae-uat.jpl.nasa.gov";
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
		
		
//		String projectID = "PROJECT-921084a3-e465-465f-944b-61194213043e";
//		String refID = "master";
		MMSUtil mmsUtil = new MMSUtil(token);
		
//		String elementID = "PMA_1491324925592";
//		String buildNumber = "1";
//		String propertyName = "jobStatus";
		String newPropertyValue = value;
		
		
		String mmsResponse = mmsUtil.modifyPartPropertyValue(server, projectID, refID, jobID, buildNumber, propertyName, newPropertyValue, token);
		
		return mmsResponse;	
	}

}