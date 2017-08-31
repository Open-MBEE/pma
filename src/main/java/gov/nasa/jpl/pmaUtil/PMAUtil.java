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

import gov.nasa.jpl.mmsUtil.MMSUtil;

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
	public static ObjectNode createJobJSON(Map<String,String> jobMap)
	{
		ObjectMapper mapper = new ObjectMapper();
		
		ObjectNode jobElement = mapper.createObjectNode();
		jobElement.put("id",jobMap.get("id"));
		jobElement.put("name",jobMap.get("name"));
		jobElement.put("command",jobMap.get("type"));
		jobElement.put("associatedElementID",jobMap.get("associatedElementId"));
		jobElement.put("schedule",jobMap.get("schedule"));
		
		return jobElement;
		
	}
	
	/**
	 * Creates a json object that contains the job instance information
	 * @param jobInstancesMap
	 * @return
	 */
	public static ObjectNode createJobInstanceJSON(Map<String,String> jobInstancesMap)
	{
		ObjectMapper mapper = new ObjectMapper();
		
		ObjectNode jobInstanceElement = mapper.createObjectNode();
		jobInstanceElement.put("id",jobInstancesMap.get("id"));
		jobInstanceElement.put("jobId",jobInstancesMap.get("jobId"));
		jobInstanceElement.put("buildNumber",jobInstancesMap.get("buildNumber"));
		jobInstanceElement.put("jobStatus",jobInstancesMap.get("jobStatus"));
		jobInstanceElement.put("jenkinsLog",jobInstancesMap.get("logUrl"));
		jobInstanceElement.put("created",jobInstancesMap.get("started"));
		jobInstanceElement.put("completed",jobInstancesMap.get("completed"));
		
		jobInstanceElement.put("type",jobInstancesMap.get("type"));
		jobInstanceElement.put("refId",jobInstancesMap.get("refId"));
		return jobInstanceElement;
	}
	
	/**
	 * Generates a json array with job objects. 
	 * @param mmsJSONString Element data from MMS.
	 * @return
	 */
	public static String generateJobArrayJSON(String mmsJSONString)
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
					if((element.get("type").toString().equals("\"Property\""))&&(element.get("name").toString().equals("\"type\"")))
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
						if((element.get("type").toString().equals("\"Property\""))&&(elementOwner.equals(jobID))&&(element.get("defaultValue").get("value")!=null))
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
				System.out.println("Error or empty mms JSON String: "+mmsJSONString);
				if(mmsJSONString.equals("{}"))
				{
					jobJSON.put("jobs",jobElements);
					
					return jobJSON.toString();
				}
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
	 * Generates a map with job instance objects and their slot ids. 
	 * @param mmsJSONString Element data from MMS.
	 * @return
	 */
	public static ArrayList<Map<String,String>> generateJobInstanceIDMapJSON(String mmsJSONString,String jobID)
	{
		ObjectMapper mapper = new ObjectMapper();
		ArrayList<String> jobInstanceIDElementList = new ArrayList<String>();
		Map elementIdMap = MMSUtil.getElementIdMap();
		ArrayList<Map<String,String>> jobInstancesMapList = new ArrayList<Map<String, String>>();
		try {
			JsonNode fullJson = mapper.readTree(mmsJSONString);
			JsonNode elements = fullJson.get("elements");
			if (elements != null)  // elements will be null if the json returned with error
			{
				for (JsonNode element : elements) {
					/*
					 * Find the ID of job instance elements
					 */
					if((element.get("type").toString().equals("\"InstanceSpecification\""))&&(element.get("classifierIds").get(0).toString().replace("\"", "").equals(jobID)))
					{
						String jobInstanceID = element.get("id").toString().replace("\"", "");//id of owner of part property
//						System.out.println(jobInstanceID);
						jobInstanceIDElementList.add(jobInstanceID);// put owner of part property in a list. Owner should be the job element
					}
				}
				//putting the job instance information into an json object.
				for(String jobInstanceID:jobInstanceIDElementList)
				{
					Map<String,String> jobInstanceMap = new HashMap();
					jobInstanceMap.put("id", jobInstanceID);
					jobInstanceMap.put("jobId", jobID);
					for (JsonNode element : elements) 
					{	
						String elementOwner = element.get("ownerId").toString().replace("\"", "");
						if((element.get("type").toString().equals("\"Slot\""))&&(elementOwner.equals(jobInstanceID)))
						{
							String slotName = element.get("definingFeatureId").toString().replace("\"", "");
							String slotValue = element.get("value").get(0).get("value").toString().replace("\"", "");
							String slotID =  element.get("id").toString().replace("\"", "");
//							System.out.println(propertyName);
//							System.out.println(propertyValue);
							
							for(JsonNode nestedSearchElement : elements)
							{
								if(nestedSearchElement.get("id").toString().replace("\"", "").equals(slotName))
								{
									
									String redefinedPropertyID = nestedSearchElement.get("redefinedPropertyIds").get(0).toString().replace("\"", "");
									slotName=(String) elementIdMap.get(redefinedPropertyID);
//									System.out.println(propertyName);
								}
							}
							jobInstanceMap.put(slotName, slotValue);
							jobInstanceMap.put(slotName+"ID", slotID);
						}
					}
					jobInstancesMapList.add(jobInstanceMap);
				}
			}
			else
			{
				return null; // Returns status from mms. Should be an error or empty if the elements were null.
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jobInstancesMapList;
	}
	
	
	/**
	 * Generates a json array with job instance objects. 
	 * @param mmsJSONString Element data from MMS.
	 * @return
	 */
	public static String generateJobInstanceArrayJSON(String mmsJSONString,String jobId,String refId)
	{
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode jobJSON = mapper.createObjectNode();
		ArrayNode jobInstanceElements = mapper.createArrayNode();
		ArrayList<String> jobInstanceIDElementList = new ArrayList<String>();
		Map elementIdMap = MMSUtil.getElementIdMap();
		try {
			JsonNode fullJson = mapper.readTree(mmsJSONString);
			JsonNode elements = fullJson.get("elements");
			if (elements != null)  // elements will be null if the json returned with error
			{
				for (JsonNode element : elements) {
					/*
					 * Find the ID of job instance elements
					 */
					if((element.get("type").toString().equals("\"InstanceSpecification\""))&&(element.get("classifierIds").get(0).toString().replace("\"", "").equals(jobId)))
					{
						String jobInstanceID = element.get("id").toString().replace("\"", "");//id of owner of part property
//						System.out.println(jobInstanceID);
						jobInstanceIDElementList.add(jobInstanceID);// put owner of part property in a list. Owner should be the job element
					}
				}
				//putting the job instance information into an json object.
				for(String jobInstanceID:jobInstanceIDElementList)
				{
					Map<String,String> jobInstanceMap = new HashMap();
					jobInstanceMap.put("id", jobInstanceID);
					jobInstanceMap.put("jobId", jobId);
					for (JsonNode element : elements) 
					{	
						String elementOwner = element.get("ownerId").toString().replace("\"", "");
						if((element.get("type").toString().equals("\"Slot\""))&&(elementOwner.equals(jobInstanceID))) // Retrieving values from instance specification slots
						{
							String propertyName = element.get("definingFeatureId").toString().replace("\"", "");
							String propertyValue = element.get("value").get(0).get("value").toString().replace("\"", "");
//							System.out.println("PropertyName:"+propertyName);
							
							for(JsonNode nestedSearchElement : elements) // Looking for the property names inside the Job Class properties
							{
								if(nestedSearchElement.get("id").toString().replace("\"", "").equals(propertyName))
								{
	
									String redefinedPropertyID = nestedSearchElement.get("redefinedPropertyIds").get(0).toString().replace("\"", "");
									propertyName=(String) elementIdMap.get(redefinedPropertyID);
//									System.out.println("PropertyName:"+propertyName);
//									System.out.println("propertyValue: "+propertyValue);
								}
							}
							jobInstanceMap.put(propertyName, propertyValue);
						}
					}
					if(jobInstanceMap.get("refId").equals(refId))
					{
						jobInstanceElements.add(createJobInstanceJSON(jobInstanceMap));
					}
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
	public static Boolean isJSON(String jsonString)
	{
		if(jsonString==null)
		{
			return false;
		}
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode fullJson = mapper.readTree(jsonString);
//			System.out.println("jobs "+fullJson.get("jobs"));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Turns a json string to a JsonNode Object
	 * @param jsonString
	 * @return
	 */
	public static JsonNode JSONStringToObject(String jsonString)
	{
		if(isJSON(jsonString))
		{
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode fullJson = mapper.readTree(jsonString);
				return fullJson;
				
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null; // There was an exception
		}
		else
		{
			return null; // String wasn't a json.
		}
		
	}
	
	// Accepts the get job instances json and returns the most recent instance.
	// looks for the job instance with the highest build number
	public static String getLatestJobInstance(String jsonString)
	{
		ObjectMapper mapper = new ObjectMapper();
	
		int highestBuildNumber = 0;
		
		JsonNode latestJobInstance = mapper.createObjectNode();
		
		try {
			JsonNode fullJson = mapper.readTree(jsonString);
			JsonNode jobInstances = fullJson.get("jobInstances");
			
			if(jobInstances.get(0)!=null)
			{
				latestJobInstance = jobInstances.get(0);
				highestBuildNumber = Integer.parseInt(jobInstances.get(0).get("buildNumber").toString().replace("\"", ""));
			}
			int buildNumber = 0;
			for (JsonNode jobInstance : jobInstances) {
				buildNumber = Integer.parseInt(jobInstance.get("buildNumber").toString().replace("\"", ""));
				if(buildNumber>highestBuildNumber)
				{
					highestBuildNumber = buildNumber;
					latestJobInstance = jobInstance;
				}
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return(latestJobInstance.toString());
			
	}
	
	public static void main(String args[])
	{
		String jsonString = "{\"jobInstances\": [{\"id\": \"PMA_1493929692690_e9c15e52-1a21-4dd2-8c14-413ebd519c18\",\"buildNumber\": \"2\",\"jobStatus\": \"completed\",\"jenkinsLog\": \"\",\"created\": null,\"completed\": null},{\"id\": \"PMA_1493929332779_c035124d-06af-40b7-ad7f-ce7781b08a3e\",\"buildNumber\": \"1\",\"jobStatus\": \"completed\",\"jenkinsLog\": \"\",\"created\": null,\"completed\": null}]}";
//		String jsonString = "{\"jobInstances\": []}";
		System.out.println(getLatestJobInstance(jsonString));
	}
}
