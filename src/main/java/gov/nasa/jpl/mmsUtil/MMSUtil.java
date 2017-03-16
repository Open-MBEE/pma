package gov.nasa.jpl.mmsUtil;
/**
 * Class used for interacting with MMS
 * @author hang
 *
 */

import java.io.IOException;
import java.sql.Timestamp;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class MMSUtil {
	String alfrescoToken = "";

	public MMSUtil(String alfToken) {
		alfrescoToken = alfToken;
	}

	public ObjectNode buildPackageJSON(String id, String ownerID) {
		ObjectMapper mapper = new ObjectMapper();

		ObjectNode payload = mapper.createObjectNode();
		ArrayNode elements = mapper.createArrayNode();
		ObjectNode packageElement = mapper.createObjectNode();
		ObjectNode nullNode = null;
		packageElement.put("type", "Package");
		packageElement.put("documentation", "");
		packageElement.put("_appliedStereotypeIds", mapper.createArrayNode());
		packageElement.put("_isSite", Boolean.FALSE);
		packageElement.put("id", id);
		packageElement.put("mdExtensionsIds", mapper.createArrayNode());
		packageElement.put("ownerId", ownerID);
		packageElement.put("syncElementId", nullNode);
		packageElement.put("appliedStereotypeInstanceId", nullNode);
		packageElement.put("clientDependencyIds", mapper.createArrayNode());
		packageElement.put("supplierDependencyIds", mapper.createArrayNode());
		packageElement.put("name", "jobs_bin");
		packageElement.put("nameExpression", nullNode);
		packageElement.put("visibility", "public");
		packageElement.put("templateParameterId", nullNode);
		packageElement.put("elementImportIds", mapper.createArrayNode());
		packageElement.put("packageImportIds", mapper.createArrayNode());
		packageElement.put("templateBindingIds", mapper.createArrayNode());
		packageElement.put("URI", "");
		packageElement.put("packageMergeIds", mapper.createArrayNode());
		packageElement.put("profileApplicationIds", mapper.createArrayNode());
		elements.add(packageElement);
		payload.put("elements",elements);
		payload.put("source","pma");
		payload.put("pmaVersion","1.0");
		
		return payload;
	} 


	public ObjectNode buildJobElementJSON(String id, String ownerID,String name) {
		ObjectMapper mapper = new ObjectMapper();

		ObjectNode payload = mapper.createObjectNode();
		ArrayNode elements = mapper.createArrayNode();
		ObjectNode classElement = mapper.createObjectNode();
		ObjectNode nullNode = null;
		classElement.put("type", "Class");
		classElement.put("documentation", "");
		classElement.put("_appliedStereotypeIds", mapper.createArrayNode().add("_18_0_5_407019f_1458258829038_313297_14086"));
		classElement.put("id", id);
		classElement.put("mdExtensionsIds", mapper.createArrayNode());
		classElement.put("ownerId", ownerID);
		classElement.put("syncElementId", nullNode);
		classElement.put("appliedStereotypeInstanceId", id+"_asi");
		classElement.put("clientDependencyIds", mapper.createArrayNode());
		classElement.put("supplierDependencyIds", mapper.createArrayNode());
		classElement.put("name", name);
		classElement.put("nameExpression", nullNode);
		classElement.put("visibility", "public");
		classElement.put("templateParameterId", nullNode);
		classElement.put("elementImportIds", mapper.createArrayNode());
		classElement.put("packageImportIds", mapper.createArrayNode());
		classElement.put("isLeaf", Boolean.FALSE);
		classElement.put("templateBindingIds", mapper.createArrayNode());
		classElement.put("useCaseIds", mapper.createArrayNode());
		classElement.put("representationId", nullNode);
		classElement.put("collaborationUseIds", mapper.createArrayNode());
		classElement.put("generalizationIds", mapper.createArrayNode());
		classElement.put("powertypeExtentIds", mapper.createArrayNode());
		classElement.put("isAbstract", Boolean.FALSE);
		classElement.put("isFinalSpecialization", Boolean.FALSE);
		classElement.put("redefinedClassifierIds", mapper.createArrayNode());
		classElement.put("substitutionIds", mapper.createArrayNode());
		classElement.put("ownedAttributeIds", mapper.createArrayNode());
		classElement.put("classifierBehaviorId", nullNode);
		classElement.put("interfaceRealizationIds", mapper.createArrayNode());
		classElement.put("ownedOperationIds", mapper.createArrayNode());
		classElement.put("isActive", Boolean.FALSE);
		classElement.put("nestedClassifierIds", mapper.createArrayNode());
		
		elements.add(classElement);
		
		ObjectNode instanceSpecificationElement = mapper.createObjectNode();
		
		instanceSpecificationElement.put("type", "InstanceSpecification");
		instanceSpecificationElement.put("documentation", "");
		instanceSpecificationElement.put("_appliedStereotypeIds",mapper.createArrayNode());
		instanceSpecificationElement.put("id", id+"_asi");
		instanceSpecificationElement.put("mdExtensionsIds", mapper.createArrayNode());
		instanceSpecificationElement.put("ownerId", id);
		instanceSpecificationElement.put("syncElementId", nullNode);
		instanceSpecificationElement.put("appliedStereotypeInstanceId", nullNode);
		instanceSpecificationElement.put("clientDependencyIds", mapper.createArrayNode());
		instanceSpecificationElement.put("supplierDependencyIds", mapper.createArrayNode());
		instanceSpecificationElement.put("name", name);
		instanceSpecificationElement.put("nameExpression", nullNode);
		instanceSpecificationElement.put("visibility", nullNode);
		instanceSpecificationElement.put("templateParameterId", nullNode);
		instanceSpecificationElement.put("deploymentIds", mapper.createArrayNode());
		instanceSpecificationElement.put("slotIds", mapper.createArrayNode());
		instanceSpecificationElement.put("specification", nullNode);
		instanceSpecificationElement.put("classifierIds", mapper.createArrayNode().add("_18_0_5_407019f_1458258829038_313297_14086"));
		instanceSpecificationElement.put("stereotypedElementId", id);
		
		elements.add(instanceSpecificationElement);
		
		payload.put("elements",elements);
		payload.put("source","pma");
		payload.put("pmaVersion","1.0");
		
		return payload;
	}
	
	public void buildJobInstanceJSON()
	{
		
	}

	
	/**
	 * Used for posting elements to MMS
	 * 
	 * @param server mms server (ex. opencae-uat.jpl.nasa.gov)
	 * @param project magicdraw project (ex.PROJECT-cea59ec3-7f4a-4619-8577-17bbeb9f1b)
	 * @param token alfresco authentication token
	 * @param on Jackson json object node. Should contain element(s) to send
	 * @return Response message from the post.
	 */
	public String post(String server,String project,String refID,ObjectNode on){
		
		server = server.replace("https://", ""); 
		server = server.replace("/", "");
		HttpClient httpClient = HttpClientBuilder.create().build();
		try {
		    HttpPost request = new HttpPost("https://"+server+"/alfresco/service/projects/"+project+"/refs/"+refID+"/elements?alf_ticket="+alfrescoToken);
		    StringEntity params = new StringEntity(on.toString());
		    request.addHeader("content-type", "application/json");
		    request.setEntity(params);
		    HttpResponse response = httpClient.execute(request);
		    System.out.println(response.getStatusLine());
		    System.out.println(response.toString());
		    return response.getStatusLine().toString();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		return "Exception Occured"; 
	}
	
	public static void main(String[] args) 
	{
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String sysmlID = "PMA_"+timestamp.getTime();
		String ownerID = "PROJECT-cea59ec3-7f4a-4619-8577-17bbeb9f1b1c_pm";
		String token = "TICKET_98936cf233fbd6884ec876d4ba272394c0639f50";
		String server = "opencae-uat.jpl.nasa.gov";
		String projectID = "PROJECT-cea59ec3-7f4a-4619-8577-17bbeb9f1b1c";
		String refID = "master";
		MMSUtil mmsUtil = new MMSUtil(token);

//		ObjectNode on = mmsUtil.buildPackageJSON(sysmlID,ownerID);
//		System.out.println(on.toString());
//		mmsUtil.post(server, projectID, token, on);
		
		ObjectNode on2 = mmsUtil.buildJobElementJSON("PMA_"+timestamp.getTime(),ownerID,"tempJob");
		System.out.println(on2.toString());
		mmsUtil.post(server, projectID,refID, on2);
		
	}
}
