package gov.nasa.jpl.controllers;

/**
 * Endpoints for Jenkins to update elements on MMS.
 */

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nasa.jpl.mmsUtil.MMSUtil;

@Controller
public class JobUpdateController 
{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Used for updating part property values of Job instance elements. 
	 * 
	 * @param projectID Magicdraw Project ID
	 * @param refID Workspace ID
	 * @param jobID Job element ID
	 * @param buildNumber Jenkins build number
	 * @param propertyName Part property name
	 * @param value New value of part property
	 * @param mmsServer 
	 * @param bodyContent Alfresco ticket JSON
	 * @return
	 */
	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs/{jobID}/instances/{buildNumber}/{propertyName}/{value}", method = RequestMethod.POST)
	@ResponseBody
	public String updateJobInstanceProperty(@PathVariable String projectID, @PathVariable String refID,@PathVariable String jobID,@PathVariable String buildNumber,@PathVariable String propertyName,@PathVariable String value,@RequestParam String mmsServer,@RequestBody String bodyContent) 
	{
		logger.info("Update Jobs was called");
		logger.info( "projectID: "+ projectID + "\n" +"refID: "+ refID+ "\n"+"JobID: "+jobID+ "\n"+"Build Number: "+buildNumber+ "\n"+"Property admin: "+propertyName+ "\n"+"Value: "+value+ "\n"+"mmsServer: "+mmsServer+ "\n"+"Body Content: "+bodyContent);
		System.out.println( "projectID: "+ projectID + "\n" +"refID: "+ refID+ "\n"+"JobID: "+jobID+ "\n"+"Build Number: "+buildNumber+ "\n"+"Property admin: "+propertyName+ "\n"+"Value: "+value+ "\n"+"mmsServer: "+mmsServer+ "\n"+"Body Content: "+bodyContent);
		
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
		String mmsResponse = mmsUtil.modifyPartPropertyValue(mmsServer, projectID, refID, jobID, buildNumber, propertyName, newPropertyValue, token, jobID);
		logger.info("MMS Response: "+mmsResponse);
		if (propertyName.equals("jobStatus") && value.equals("completed")) {
			String currentTimestamp = new java.text.SimpleDateFormat("MM/dd/yyyy-HH:mm:ss").format(new java.util.Date());
			mmsResponse = mmsUtil.modifyPartPropertyValue(mmsServer, projectID, refID, jobID, buildNumber, "completed", currentTimestamp, token, jobID);
			logger.info(mmsResponse);
		}

		return mmsResponse;
	}

}