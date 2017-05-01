package gov.nasa.jpl.mmsUtil;
/**
 * Class used for interacting with MMS
 * @author hang
 *
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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

	/**
	 * Builds the class element with its instance specification element.
	 * @param id 
	 * @param ownerID Owner of ID.
	 * @param name element name
	 * @return Array containing class and instance specification element. 
	 */
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
		
		String propertyID = createId();
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

	public ObjectNode buildJobElementJSON(String id, String ownerID,String name,String command,String schedule) {
		ObjectMapper mapper = new ObjectMapper();

		ObjectNode payload = mapper.createObjectNode();
		ArrayNode elements = buildClassElement(id,ownerID,name);
		
		elements.add(buildPropertyNode(id,"command",command));
		elements.add(buildPropertyNode(id,"associatedElementID",ownerID));
		elements.add(buildPropertyNode(id,"schedule",schedule));
		elements.add(buildPropertyNode(id,"arguments","tempValue,tempValue2"));
		
		payload.put("elements",elements);
		payload.put("source","pma");
		payload.put("pmaVersion","1.0");
		
		return payload;
	}
	
	public ObjectNode buildJobInstanceJSON(String id, String ownerID,String name,String buildNumber,String jobStatus) 
	{
		
		ObjectMapper mapper = new ObjectMapper();
		
		ObjectNode payload = mapper.createObjectNode();
		ArrayNode elements = buildClassElement(id,ownerID,name);
		
		elements.add(buildPropertyNode(id,"buildNumber",buildNumber));
		elements.add(buildPropertyNode(id,"jobStatus",jobStatus));
		elements.add(buildPropertyNode(id,"jenkinsLog",""));
		elements.add(buildPropertyNode(id,"timeCreated",""));
		elements.add(buildPropertyNode(id,"completionTime",""));
		
		payload.put("elements",elements);
		payload.put("source","pma");
		payload.put("pmaVersion","1.0");
		
		return payload;
		
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
		} 
		catch (java.net.UnknownHostException e) {
		      System.out.println("Unknown Host Exception");
		}catch (IOException e) 
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
		} 
		catch (java.net.UnknownHostException e) {
		      System.out.println("Unknown Host Exception");
		}catch (IOException e) 
		{
			e.printStackTrace();
		}
		return "Exception Occured"; 
	}
	
	/**
	 * Used for getting elements to MMS
	 * 
	 * @param server mms server (ex. opencae-uat.jpl.nasa.gov)
	 * @param project magicdraw project (ex.PROJECT-cea59ec3-7f4a-4619-8577-17bbeb9f1b)
	 * @param elementID ID of element to be retrieved 
	 * @param recurse 
	 * @return json string of element.
	 */
	public String get(String server,String project,String refID,String elementID,Boolean recurse){
		
		String recurseString = "";
		if(recurse)
		{
			recurseString = "recurse=true&";
		}
		server = server.replace("https://", ""); 
		server = server.replace("/", "");
		HttpClient httpClient = HttpClientBuilder.create().build();
		try {
			String url = "https://"+server+"/alfresco/service/projects/"+project+"/refs/"+refID+"/elements/"+elementID+"?"+recurseString+"alf_ticket="+alfrescoToken;
			System.out.println("URL: "+url);
		    HttpGet request = new HttpGet(url);
			request.setHeader("Accept", "application/json");
			request.setHeader("Content-type", "application/json");
		    HttpResponse response = httpClient.execute(request);
		    
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			
			String result = "";
			String line = "";
			while ((line = rd.readLine()) != null) {
				result = result+line.trim();
			}
			return result;
			
		}
		catch (java.net.UnknownHostException e) {
		      System.out.println("Unknown Host Exception");
		      return "Unknown Host exception: "+e.toString();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "Exception occured ";
	}

	
	/**
	 * Should get the current value of the property, change it and send it back to mms
	 * @param server MMS server. Ex. opencae-uat.jpl.nasa.gov
	 * @param projectID Magicdraw project id
	 * @param refID
	 * @param elementID ID of job element (Should be the owner of the job instance element)
	 * @param buildNumber Build number of the jenkins job. Starts from 1. 
	 * @param propertyName Name of the part property. Ex: buildNumber,jobStatus,jenkinsLog,etc
	 * @param newPropertyValue New value of the part property
	 * @param token Alfresco token.
	 * @return Status code returned from mms.
	 */
	public String modifyPartPropertyValue(String server,String projectID,String refID,String elementID,String buildNumber,String propertyName,String newPropertyValue,String token)
	{
		
		// finding the part property
		MMSUtil mmsUtil = new MMSUtil(token);
		
		String jsonString = mmsUtil.get(server, projectID,refID, elementID,true);
		System.out.println("Modify Part Property JSON String: "+jsonString);
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			JsonNode fullJson = mapper.readTree(jsonString);
			JsonNode elements = fullJson.get("elements");
			String jobInstanceId = ""; // owner of the instance part properties
			if (elements != null)  // elements will be null if the json returned with error
			{
				for (JsonNode element : elements) {
					// Find the ID of the job instance element.
					if((element.get("type").toString().equals("\"Property\""))&&(element.get("defaultValue").get("value").toString().equals("\""+buildNumber+"\"")))
					{
						jobInstanceId = element.get("ownerId").toString();
					}
				}
				ObjectNode propertyElement = null;
				for (JsonNode element : elements) {
					/*
					 * Find the property element that contains the value to be replaced.
					 */
					if((element.get("type").toString().equals("\"Property\""))&&(element.get("ownerId").toString().equals(jobInstanceId))&&(element.get("name").toString().equals("\""+propertyName+"\"")))
					{
						System.out.println("Found: "+propertyName);
						System.out.println("Value: "+element.get("defaultValue").get("value").toString());
						propertyElement = (ObjectNode) element;
					}
				}
				if(propertyElement!=null) // will be null if the property element isn't found
				{
					/*
					 * Replace the value in the json object
					 */
					System.out.println("Before: "+propertyElement);
					ObjectNode propertyElementValue = (ObjectNode) propertyElement.get("defaultValue");
					propertyElementValue.put("value", newPropertyValue);
					propertyElement.put("defaultValue", propertyElementValue);
					
					// puts the new json object in an elements array that will be sent to mms
					ObjectNode payload = mapper.createObjectNode();
					ArrayNode arrayElements = mapper.createArrayNode();
					arrayElements.add(propertyElement);
					payload.put("elements",arrayElements);
					
					// send element to MMS
					System.out.println("Payload: "+payload);
					String response = mmsUtil.post(server, projectID, refID, payload); // sending element to MMS . Expecting 200 OK response
					System.out.println("Response: "+response);
					return response;
				}
				else 
				{
					
					if(propertyName.equals("jobStatus")) // creates the job instance
					{
			    		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			          	String jobInstanceElementID = createId();
			    		ObjectNode on = mmsUtil.buildJobInstanceJSON(jobInstanceElementID, elementID, elementID+"_instance_"+timestamp.getTime(),buildNumber,newPropertyValue); //job element will be the owner of the instance element
			    		String elementCreationResponse = mmsUtil.post(server, projectID, refID, on);
			    		return elementCreationResponse;
					}
				}
			}
			else
			{
				return jsonString; // Returns status from mms. Should be an error if the elements were null.
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonString;
	}
	
	// finds all the job elements in a project
	public String getJobElements(String server,String projectID,String refID)
	{
		// find package
		// look for command part property
		// put owner of part property in a list. Owner should be the job element
		// return the list.
		return null;
	}
	
	/**
	 * Gets a job element from MMS. Recursively gets the element to include all the part properties and job instances.
	 * 
	 * @param server mms server
	 * @param project magic draw project ID
	 * @param refID
	 * @param jobElementID element ID of job. 
	 * @return
	 */
	public String getJobElement(String server, String project,String refID,String jobElementID)
	{
		return get(server,project,refID,jobElementID,true);
	}
	
	
	public String getJobInstanceElements(String server, String project, String refID, String jobElementID)
	{
		// recursive get job sysmlid
		
		String jsonString = get(server, project,refID, jobElementID, true);
		
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode instanceElements = mapper.createArrayNode();
		
		try {
			JsonNode fullJson = mapper.readTree(jsonString);
			JsonNode elements = fullJson.get("elements");
			if (elements != null)  // elements will be null if the json returned with error
			{
				for (JsonNode element : elements) {
					// Find the ID of the job instance element.
					if((element.get("type").toString().equals("\"Property\""))&&(element.get("name").toString().equals("\"jobStatus\"")))
					{
						String jobInstanceId = element.get("ownerId").toString().replace("\"", "");// owner of the instance part properties
						instanceElements.add(mapper.createObjectNode().put("id", jobInstanceId));
					}
				}
				return instanceElements.toString();
			}
			else
			{
				return jsonString; // Returns status from mms. Should be an error if the elements were null.
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Element not found";
	}
	
<<<<<<< HEAD
    private String createId() {
    	String id = "PMA_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString();
    	return id;
    }
=======
	public String createId() {
		String id = "PMA_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString();
		return id;

	}
>>>>>>> jenkins2
	
	public static void main(String[] args) 
	{
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String sysmlID = "PMA_"+timestamp.getTime();
		String ownerID = "PROJECT-921084a3-e465-465f-944b-61194213043e_pm";
		String token = "TICKET_cbdbba79ae146a419938fb76f250bb89a9953174";
		String server = "opencae-test.jpl.nasa.gov";
		String projectID = "PROJECT-921084a3-e465-465f-944b-61194213043e";
		String refID = "master";
		MMSUtil mmsUtil = new MMSUtil(token);

		
//		ObjectNode on = mmsUtil.buildPackageJSON(sysmlID,ownerID);
//		System.out.println(on.toString());
//		mmsUtil.post(server, projectID, token, on);
		
		
//		String jobElementID = "PMA_"+timestamp.getTime();
//		ObjectNode on2 = mmsUtil.buildJobElementJSON("PMA_"+timestamp.getTime(),ownerID,"jobEle");
//		System.out.println(on2.toString());
//		mmsUtil.post(server, projectID,refID, on2);
//		
//		timestamp = new Timestamp(System.currentTimeMillis());
//		ObjectNode on3 = mmsUtil.buildJobInstanceJSON("PMA_"+timestamp.getTime()+"_instance",jobElementID,"jobInstance","1");
//		System.out.println(on3.toString());
//		mmsUtil.post(server, projectID,refID, on3);
		
		String elementID = "PMA_1491324925592";
		String buildNumber = "55";
		String propertyName = "jobStatus";
		String newPropertyValue = "completed";
		
		System.out.println(mmsUtil.get(server, projectID, refID, elementID, true));
//		System.out.println(mmsUtil.modifyPartPropertyValue(server, projectID, refID, elementID, buildNumber, propertyName, newPropertyValue, token));
		
	}
}
