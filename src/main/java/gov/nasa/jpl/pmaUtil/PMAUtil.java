/**
 * Utility class for creating JSON PMA returns.
 * @author hang
 */
package gov.nasa.jpl.pmaUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PMAUtil 
{
	public PMAUtil()
	{
		
	}
	
	/**
	 * Creates a job JSON using a map. Map contains the job information.
	 * @param jobMap
	 * @return
	 */
	public ObjectNode createJobJSON(Map<String,String> jobMap)
	{
		ObjectMapper mapper = new ObjectMapper();
		
		ObjectNode jobElement = mapper.createObjectNode();
		jobElement.put("id",jobMap.get("id"));
		jobElement.put("name",jobMap.get("name"));
		jobElement.put("command",jobMap.get("command"));
		jobElement.put("associatedElementID",jobMap.get("associatedElementID"));
		jobElement.put("schedule",jobMap.get("schedule"));
		jobElement.put("arguments",jobMap.get("arguments"));
		
		return jobElement;
		
	}
	
	/**
	 * 
	 * @param jobInstancesMap
	 * @return
	 */
	public ObjectNode createJobInstanceJSON(Map<String,String> jobInstancesMap)
	{
		ObjectMapper mapper = new ObjectMapper();
		
		ObjectNode jobInstanceElement = mapper.createObjectNode();
		jobInstanceElement.put("id",jobInstancesMap.get("id"));
		jobInstanceElement.put("buildNumber",jobInstancesMap.get("buildNumber"));
		jobInstanceElement.put("jobStatus",jobInstancesMap.get("jobStatus"));
		jobInstanceElement.put("jenkinsLog",jobInstancesMap.get("jenkinsLog"));
		jobInstanceElement.put("created",jobInstancesMap.get("created"));
		jobInstanceElement.put("completed",jobInstancesMap.get("completed"));
		return jobInstanceElement;
	}
	
	/**
	 * Generates a json array with job objects. 
	 * @param mmsJSONString Element data from MMS.
	 * @return
	 */
	public String generateJobArrayJSON(String mmsJSONString)
	{
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode jobJSON = mapper.createObjectNode();
		ArrayNode jobElements = mapper.createArrayNode();
		ArrayList<String> jobElementIDList = new ArrayList<String>();
		
		try {
			JsonNode fullJson = mapper.readTree(mmsJSONString);
			JsonNode elements = fullJson.get("elements");
			if (elements != null)  // elements will be null if the json returned with error
			{
				for (JsonNode element : elements) {
					/*
					 * Find the ID of job element by looking for the owner of the command property
					 * only job elements have the command part property
					 */
					if((element.get("type").toString().equals("\"Property\""))&&(element.get("name").toString().equals("\"command\"")))
					{
						String jobID = element.get("ownerId").toString().replace("\"", "");//id of owner of part property
						jobElementIDList.add(jobID);// put owner of part property in a list. Owner should be the job element
					}
				}
				//putting the job information into an json object.
				for(String jobID:jobElementIDList)
				{
					Map<String,String> jobMap = new HashMap();
					jobMap.put("id", jobID);
					for (JsonNode element : elements) 
					{							
						String elementOwner = element.get("ownerId").toString().replace("\"", "");
						if(element.get("id").toString().replace("\"", "").equals(jobID))
						{
							String jobName = element.get("name").toString().replace("\"", "");
							System.out.println("Job Name: "+jobName);
							jobMap.put("name", jobName);
						}
						if((element.get("type").toString().equals("\"Property\""))&&(elementOwner.equals(jobID)))
						{
							String propertyName = element.get("name").toString().replace("\"", "");
							String propertyValue = element.get("defaultValue").get("value").toString().replace("\"", "");
							System.out.println(propertyName);
							System.out.println(propertyValue);
							jobMap.put(propertyName, propertyValue);
						}					
					}				
					jobElements.add(createJobJSON(jobMap));					
				}
			}
			else
			{
				return mmsJSONString; // Returns status from mms. Should be an error or empty if the elements were null.
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jobJSON.put("jobs",jobElements);
		
		return jobJSON.toString();
	}
	
	/**
	 * Generates a json array with job instance objects. 
	 * @param mmsJSONString Element data from MMS.
	 * @return
	 */
	public String generateJobInstanceArrayJSON(String mmsJSONString)
	{
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode jobJSON = mapper.createObjectNode();
		ArrayNode jobInstanceElements = mapper.createArrayNode();
		ArrayList<String> jobInstanceIDElementList = new ArrayList<String>();
		
		try {
			JsonNode fullJson = mapper.readTree(mmsJSONString);
			JsonNode elements = fullJson.get("elements");
			if (elements != null)  // elements will be null if the json returned with error
			{
				for (JsonNode element : elements) {
					/*
					 * Find the ID of job element by looking for the owner of the command property
					 * only job elements have the command part property
					 */
					if((element.get("type").toString().equals("\"Property\""))&&(element.get("name").toString().equals("\"jobStatus\"")))
					{
						String jobInstanceID = element.get("ownerId").toString().replace("\"", "");//id of owner of part property
						jobInstanceIDElementList.add(jobInstanceID);// put owner of part property in a list. Owner should be the job element
					}
				}
				//putting the job instance information into an json object.
				for(String jobInstanceID:jobInstanceIDElementList)
				{
					Map<String,String> jobInstanceMap = new HashMap();
					jobInstanceMap.put("id", jobInstanceID);
					for (JsonNode element : elements) 
					{	
						String elementOwner = element.get("ownerId").toString().replace("\"", "");
						if((element.get("type").toString().equals("\"Property\""))&&(elementOwner.equals(jobInstanceID)))
						{
							String propertyName = element.get("name").toString().replace("\"", "");
							String propertyValue = element.get("defaultValue").get("value").toString().replace("\"", "");
							System.out.println(propertyName);
							System.out.println(propertyValue);
							jobInstanceMap.put(propertyName, propertyValue);
						}
					}
					jobInstanceElements.add(createJobInstanceJSON(jobInstanceMap));
				}
			}
			else
			{
				return mmsJSONString; // Returns status from mms. Should be an error or empty if the elements were null.
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jobJSON.put("jobInstances",jobInstanceElements);
		
		return jobJSON.toString();
	}
	
	
	/**
	 * Checks if a string is a JSON
	 * @param jsonString
	 * @return
	 */
	public Boolean isJSON(String jsonString)
	{
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode fullJson = mapper.readTree(jsonString);
			System.out.println("jobs "+fullJson.get("jobs"));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	

}
