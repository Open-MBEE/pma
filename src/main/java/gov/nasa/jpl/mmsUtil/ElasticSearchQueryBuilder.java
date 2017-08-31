package gov.nasa.jpl.mmsUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ElasticSearchQueryBuilder 
{
	public static String getJobsQuery(String projectId,String refId)
	{ 
		
		ObjectMapper mapper = new ObjectMapper();
		
		ObjectNode payload = mapper.createObjectNode();
		ArrayNode sort = mapper.createArrayNode();
		ObjectNode sort0 = mapper.createObjectNode();
		ObjectNode query = mapper.createObjectNode();
		ObjectNode bool = mapper.createObjectNode();
		ArrayNode filter = mapper.createArrayNode();
		
		ObjectNode filter0 = mapper.createObjectNode();
		ObjectNode bool2 = mapper.createObjectNode();
		ArrayNode must = mapper.createArrayNode();
		
		ObjectNode condition = mapper.createObjectNode();
		ObjectNode condition1 = mapper.createObjectNode();
		ObjectNode condition2 = mapper.createObjectNode();
		ObjectNode condition3 = mapper.createObjectNode();
		
		ObjectNode term = mapper.createObjectNode();
		ObjectNode term1 = mapper.createObjectNode();
		ObjectNode term2 = mapper.createObjectNode();
		ObjectNode term3 = mapper.createObjectNode();
		
		sort.add("_score");
		ObjectNode modified = mapper.createObjectNode();
		modified.put("order", "desc");
		sort0.set("_modified",modified);
		sort.add(sort0);
		payload.set("sort",sort);
		
		
		term.put("_projectId", projectId);
		term1.put("_inRefIds", refId);
		term2.put("type", "Generalization");
		term3.put("generalId", MMSUtil.docgenJobBlockID);
		
		condition.set("term", term);
		condition1.set("term", term1); 
		condition2.set("term", term2); 
		condition3.set("term", term3); 
		
		must.add(condition);
		must.add(condition1);
		must.add(condition2);
		must.add(condition3);
		
		bool2.set("must", must);
		filter0.set("bool", bool2);
		filter.add(filter0);
		bool.set("filter", filter);
		query.set("bool", bool);
		payload.set("query", query);
		
		payload.put("from", 0);
		payload.put("size", 10000);
		
		System.out.println(payload.toString());
		return payload.toString();
		
	}
	
	public static String getJobInstancesQuery(String projectId,String refId,String jobId)
	{ 
		
		ObjectMapper mapper = new ObjectMapper();
		
		ObjectNode payload = mapper.createObjectNode();
		ArrayNode sort = mapper.createArrayNode();
		ObjectNode sort0 = mapper.createObjectNode();
		ObjectNode query = mapper.createObjectNode();
		ObjectNode bool = mapper.createObjectNode();
		ArrayNode filter = mapper.createArrayNode();
		
		ObjectNode filter0 = mapper.createObjectNode();
		ObjectNode bool2 = mapper.createObjectNode();
		ArrayNode must = mapper.createArrayNode();
		
		ObjectNode condition = mapper.createObjectNode();
		ObjectNode condition1 = mapper.createObjectNode();
		ObjectNode condition2 = mapper.createObjectNode();
		ObjectNode condition3 = mapper.createObjectNode();
		
		ObjectNode term = mapper.createObjectNode();
		ObjectNode term1 = mapper.createObjectNode();
		ObjectNode term2 = mapper.createObjectNode();
		ObjectNode term4 = mapper.createObjectNode();
		
		sort.add("_score");
		ObjectNode modified = mapper.createObjectNode();
		modified.put("order", "desc");
		sort0.set("_modified",modified);
		sort.add(sort0);
		payload.set("sort",sort);
		
		
		term.put("_projectId", projectId);
		term1.put("_inRefIds", refId);
		term2.put("type", "InstanceSpecification");
		term4.put("classifierIds", jobId);
		
		condition.set("term", term);
		condition1.set("term", term1); 
		condition2.set("term", term2); 
		condition3.set("term", term4); 
		
		must.add(condition);
		must.add(condition1);
		must.add(condition2);
		must.add(condition3);
		
		bool2.set("must", must);
		filter0.set("bool", bool2);
		filter.add(filter0);
		bool.set("filter", filter);
		query.set("bool", bool);
		payload.set("query", query);
		
		payload.put("from", 0);
		payload.put("size", 10000);
		
		System.out.println(payload.toString());
		return payload.toString();
		
	}
	
	public void buildTerm()
	{
		
	}
	
	public void buildExists()
	{
		
	}
	public static void main(String args[])
	{
		String projectId = "PROJECT-8e161354-b9c5-45a5-b032-db21395db80f";
		String refId = "master";
		getJobsQuery(projectId,refId);
	}
}
