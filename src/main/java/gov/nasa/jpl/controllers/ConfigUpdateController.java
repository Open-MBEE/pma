package gov.nasa.jpl.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nasa.jpl.dbUtil.DBUtil;

@Controller
public class ConfigUpdateController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * To test if pma is up
	 * @param projectID
	 * @param refID
	 * @return
	 */
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity getJobs(HttpServletRequest request) {
		
		System.out.println("Client IP: "+request.getRemoteAddr());
		logger.info("Client IP: "+request.getRemoteAddr());
		
		System.out.println("Host IP: "+request.getLocalAddr());
		logger.info("Host IP: "+request.getLocalAddr());
		
		HttpStatus httpStatus = HttpStatus.NOT_FOUND;
		ObjectMapper mapper = new ObjectMapper();

		ObjectNode jobElement = mapper.createObjectNode();
		jobElement.put("id", "");
		jobElement.put("name", "");

		String json = jobElement.toString();

		return new ResponseEntity<>("Not Found", httpStatus);
	}
	
	/**
	 * Used for updating the credentials used to interact with Jenkins.
	 * Expects a json with username,password,jenkins url, and jenkins agent.
	 * @param bodyContent
	 * @return
	 */
	@RequestMapping(value = "/dbUpdate", method = RequestMethod.POST)
	@ResponseBody
	public String dbUpdate(@RequestBody String bodyContent, HttpServletRequest request) 
	{
		String clientIp = request.getRemoteAddr();
		String hostIp = request.getLocalAddr();
		
		System.out.println("Client IP: "+clientIp);
		logger.info("Client IP: "+clientIp);
		
		System.out.println("Host IP: "+hostIp);
		logger.info("Host IP: "+hostIp);
		
		if(hostIp.equals(clientIp))
		{
			logger.info("Updating credentials db");
			
			String jenkinsUsername = "";
			String jenkinsPassword = "";
			String jenkinsURL = "";
			String jenkinsAgent = "";
			String org = "";
			
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode fullJson = mapper.readTree(bodyContent);
				logger.info(fullJson.toString());
				if ((fullJson.get("username") != null)&&(fullJson.get("password") != null) &&(fullJson.get("url") != null) ) 
				{
					jenkinsUsername = fullJson.get("username").toString().replace("\"", "");
					jenkinsPassword = fullJson.get("password").toString().replace("\"", "");
					jenkinsURL = fullJson.get("url").toString().replace("\"", "");
					jenkinsAgent = fullJson.get("agent").toString().replace("\"", "");
					org = fullJson.get("org").toString().replace("\"", "");
					
					logger.info(jenkinsUsername);
					logger.info(jenkinsPassword);
					logger.info(jenkinsURL);
					logger.info(jenkinsAgent);
				}
				
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return e.toString();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return e.toString();
			}
			
			
			DBUtil dbUtil = new DBUtil();
			
			dbUtil.updateDbCredentials(jenkinsUsername, jenkinsPassword, jenkinsURL, jenkinsAgent, org);
			
			logger.info("Credentials Updated");
			
			return "Credentials Updated";
		}
		else
		{
			return "Can only be modified locally";
		}
	}
	
	/**
	 * Used for deleting the row in the credentials table associated with an org.
	 * @param bodyContent
	 * @return
	 */
	@RequestMapping(value = "/dbDelete", method = RequestMethod.POST)
	@ResponseBody
	public String dbDelete(@RequestBody String bodyContent, HttpServletRequest request) 
	{
		
		String clientIp = request.getRemoteAddr();
		String hostIp = request.getLocalAddr();
		
		System.out.println("Client IP: "+clientIp);
		logger.info("Client IP: "+clientIp);
		
		System.out.println("Host IP: "+hostIp);
		logger.info("Host IP: "+hostIp);
		
		if(hostIp.equals(clientIp))
		{
			
			String org = "";
			
			// Retrieving org parameter from accepted json
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode fullJson = mapper.readTree(bodyContent);
				logger.info(fullJson.toString());
				if ((fullJson.get("org") != null) ) 
				{
					org = fullJson.get("org").toString().replace("\"", "");
					logger.info(org);
				}
				else
				{
					return "Invalid JSON";
				}
				
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return e.toString();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return e.toString();
			}
			DBUtil dbUtil = new DBUtil();
			return dbUtil.deleteDBCredential(org);
		}
		else
		{
			return "Can only be modified locally";
		}
	}
	
	/**
	 * Creates job element on mms and job on Jenkins.
	 * 
	 * @param projectID magicdraw project ID
	 * @param refID id of workspace
	 * @param jobjobFromVE 
	 * @return
	 */
	@RequestMapping(value = "/testing/{projectID}/refs/{refID}/jobs", method = RequestMethod.POST)
	@ResponseBody
	public String createJob(@PathVariable String projectID, @PathVariable String refID) {
		
		return refID;

	}
		
}