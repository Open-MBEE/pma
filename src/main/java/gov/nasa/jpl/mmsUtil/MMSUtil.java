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
import org.apache.http.client.methods.HttpDelete;
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

	public ArrayNode buildClassElement(String id, String ownerID,String name)
	{
		ObjectMapper mapper = new ObjectMapper();

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
		

		return elements;
	}
	
	/**
	 * Builds a part property json object. Values are strings by default.
	 * 
	 * @param ownerID Sysml ID of owner
	 * @param name property name
	 * @param value value of property. 
	 * @return
	 */
	public ObjectNode buildPropertyNode(String ownerID,String name,String value)
	{
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String propertyID = ownerID+"_instance_"+timestamp.getTime()+Math.random()* 50 + 1;
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode classElement = mapper.createObjectNode();
		ObjectNode nullNode = null;
		
		classElement.put("type", "Property");
		classElement.put("documentation", "");
		classElement.put("_appliedStereotypeIds", mapper.createArrayNode());
		classElement.put("id", propertyID);
		classElement.put("mdExtensionsIds", mapper.createArrayNode());
		classElement.put("ownerId", ownerID);
		classElement.put("syncElementId", nullNode);
		classElement.put("appliedStereotypeInstanceId", nullNode);
		classElement.put("clientDependencyIds", mapper.createArrayNode());
		classElement.put("supplierDependencyIds", mapper.createArrayNode());
		classElement.put("name", name);
		classElement.put("nameExpression", nullNode);
		classElement.put("visibility", nullNode);
		classElement.put("isLeaf", Boolean.FALSE);
		classElement.put("isStatic", Boolean.FALSE);
		classElement.put("typeId", nullNode);
		classElement.put("isOrdered", Boolean.FALSE);
		classElement.put("isUnique", Boolean.TRUE);
		classElement.put("lowerValue", nullNode);
		classElement.put("upperValue", nullNode);
		classElement.put("isReadOnly", Boolean.FALSE);
		classElement.put("templateParameterId", nullNode);
		classElement.put("endIds", mapper.createArrayNode());
		classElement.put("deploymentIds", mapper.createArrayNode());
		classElement.put("aggregation", "composite");
		classElement.put("associationEndId", nullNode);
		classElement.put("qualifierIds", mapper.createArrayNode());
		classElement.put("datatypeId", nullNode);
		
		ObjectNode defaultValue = mapper.createObjectNode(); // value element
		
		defaultValue.put("type","LiteralString");
		defaultValue.put("documentation","");
		defaultValue.put("_appliedStereotypeIds",mapper.createArrayNode());
		
		defaultValue.put("id",propertyID+"_value");
		defaultValue.put("mdExtensionsIds",mapper.createArrayNode());
		
		defaultValue.put("ownerId",propertyID);
		defaultValue.put("syncElementId",nullNode);
		defaultValue.put("appliedStereotypeInstanceId",nullNode);
		defaultValue.put("clientDependencyIds",mapper.createArrayNode());
		defaultValue.put("supplierDependencyIds",mapper.createArrayNode());
		defaultValue.put("name","");
		defaultValue.put("nameExpression",nullNode);
		defaultValue.put("visibility","public");
		defaultValue.put("templateParameterId",nullNode);
		defaultValue.put("typeId",nullNode);
		defaultValue.put("value",value);
		
		classElement.put("defaultValue", defaultValue);
		
		
		classElement.put("interfaceId", nullNode);
		classElement.put("isDerived", Boolean.FALSE);
		classElement.put("isDerivedUnion", Boolean.FALSE);
		classElement.put("isID", Boolean.FALSE);
		classElement.put("redefinedPropertyIds", mapper.createArrayNode());
		classElement.put("subsettedPropertyIds", mapper.createArrayNode());
		classElement.put("associationId", nullNode);
		
		return classElement;
	}

	public ObjectNode buildJobElementJSON(String id, String ownerID,String name) {
		ObjectMapper mapper = new ObjectMapper();

		ObjectNode payload = mapper.createObjectNode();
		ArrayNode elements = buildClassElement(id,ownerID,name);
		
		elements.add(buildPropertyNode(id,"command","docweb"));
		elements.add(buildPropertyNode(id,"associatedElementID","12345"));
		elements.add(buildPropertyNode(id,"schedule","* * * *"));
		elements.add(buildPropertyNode(id,"arguments","tempValue,merpmerp"));
		
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
		    System.out.println("link: "+"https://"+server+"/alfresco/service/projects/"+project+"/refs/"+refID+"/elements?alf_ticket="+alfrescoToken);
//		    System.out.println(response.getStatusLine());
//		    System.out.println(response.toString());
		    return response.getStatusLine().toString();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		return "Exception Occured"; 
	}
	
	/**
	 * Used for deleting elements to MMS
	 * 
	 * @param server mms server (ex. opencae-uat.jpl.nasa.gov)
	 * @param project magicdraw project (ex.PROJECT-cea59ec3-7f4a-4619-8577-17bbeb9f1b)
	 * @param elementID ID of element to be deleted 
	 * @return Response message from the post.
	 */
	public String delete(String server,String project,String refID,String elementID){
		
		server = server.replace("https://", ""); 
		server = server.replace("/", "");
		HttpClient httpClient = HttpClientBuilder.create().build();
		try {
		    HttpDelete request = new HttpDelete("https://"+server+"/alfresco/service/projects/"+project+"/refs/"+refID+"/elements/"+elementID+"?alf_ticket="+alfrescoToken);
		    HttpResponse response = httpClient.execute(request);

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
		String token = "TICKET_1d4f13445f8f360e25e55b1646d080e34a8df338";
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
