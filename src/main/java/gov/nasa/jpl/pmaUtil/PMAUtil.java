package gov.nasa.jpl.pmaUtil;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PMAUtil 
{
	public PMAUtil()
	{
		
	}
	
	public void createJobsJSON()
	{
		ObjectMapper mapper = new ObjectMapper();
		
		ArrayNode jobs = mapper.createArrayNode();
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
	
	public ObjectNode createJobJSON(String id, String name, String command, String associatedElementID,String schedule, String arguments)
	{
		ObjectMapper mapper = new ObjectMapper();
		
		ObjectNode jobElement = mapper.createObjectNode();
		jobElement.put("id",id);
		jobElement.put("name",name);
		jobElement.put("command",command);
		jobElement.put("associatedElementID",associatedElementID);
		jobElement.put("schedule",schedule);
		jobElement.put("arguments",arguments);
		return jobElement;
		
	}
	
	public ObjectNode createJobInstanceJSON(String id, String jobStatus,String jenkinsLog,String created,String completed)
	{
		ObjectMapper mapper = new ObjectMapper();
		
		ObjectNode jobInstanceElement = mapper.createObjectNode();
		jobInstanceElement.put("id",id);
		jobInstanceElement.put("jobStatus",jobStatus);
		jobInstanceElement.put("jenkinsLog",jenkinsLog);
		jobInstanceElement.put("created",created);
		jobInstanceElement.put("completed",completed);
		return jobInstanceElement;
	}
	
}
