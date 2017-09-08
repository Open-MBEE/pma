package gov.nasa.jpl.mmsUtil;
/**
 * Class used for interacting with MMS
 * @author hang
 *
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nasa.jpl.jmsUtil.JmsConnection;
import gov.nasa.jpl.pmaUtil.PMAUtil;

import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;


public class MMSUtil {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/*
	 * Hardcoded element ID's from sysml extensions
	 */
	public static final String valuePropertyStereotypeID = "_12_0_be00301_1164123483951_695645_2041";
	
	public static final String docgenJobBlockID = "_18_5_1_8bf0285_1501191821803_878234_16095";
	public static final String typePropertyID = "_18_5_1_8bf0285_1501191821884_107041_16126";
	public static final String schedulePropertyID = "_18_5_1_8bf0285_1501191821884_399553_16127";
	public static final String buildNumberPropertyID = "_18_5_1_8bf0285_1501191821884_977257_16128";
	public static final String jobStatusID = "_18_5_1_8bf0285_1501191821884_370214_16129";
	public static final String logUrlPropertyID = "_18_5_1_8bf0285_1501191821885_707280_16130";
	public static final String startedPropertyID = "_18_5_1_8bf0285_1501191821885_603965_16131";
	public static final String completedPropertyID = "_18_5_1_8bf0285_1501191821885_292854_16132";
	public static final String associatedElementIdPropertyID = "_18_5_1_8bf0285_1501191821885_586435_16133";
	public static final String refIdPropertyID = "_18_5_1_8bf0285_1501191821885_436667_16134";
	public static final String projectIdPropertyID = "_18_5_1_8bf0285_1501191821885_300964_16135";
	
	
	
	String alfrescoToken = "";
	
	
	public MMSUtil(String alfToken) {
		alfrescoToken = alfToken;
	}

	public ObjectNode buildPackageJSON(String id, String ownerID,String name) {
		ObjectMapper mapper = new ObjectMapper();

		ObjectNode payload = mapper.createObjectNode();
		ArrayNode elements = mapper.createArrayNode();
		ObjectNode packageElement = mapper.createObjectNode();
		ObjectNode nullNode = null;
		packageElement.set("_appliedStereotypeIds", mapper.createArrayNode());
		packageElement.put("documentation", "");
		packageElement.put("_isSite", Boolean.FALSE);
		packageElement.put("type", "Package");
		packageElement.put("id", id);
		packageElement.set("mdExtensionsIds", mapper.createArrayNode());
		packageElement.put("ownerId", ownerID);
		packageElement.set("syncElementId", nullNode);
		packageElement.set("appliedStereotypeInstanceId", nullNode);
		packageElement.set("clientDependencyIds", mapper.createArrayNode());
		packageElement.set("supplierDependencyIds", mapper.createArrayNode());
		packageElement.put("name",name);
		packageElement.set("nameExpression", nullNode);
		packageElement.put("visibility", "public");
		packageElement.set("templateParameterId", nullNode);
		packageElement.set("elementImportIds", mapper.createArrayNode());
		packageElement.set("packageImportIds", mapper.createArrayNode());
		packageElement.set("templateBindingIds", mapper.createArrayNode());
		packageElement.put("URI", "");
		packageElement.set("packageMergeIds", mapper.createArrayNode());
		packageElement.set("profileApplicationIds", mapper.createArrayNode());
		elements.add(packageElement);
		payload.set("elements",elements);
		payload.put("source","pma");
		payload.put("pmaVersion","3.1");
		
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
		classElement.set("_appliedStereotypeIds", mapper.createArrayNode().add("_11_5EAPbeta_be00301_1147424179914_458922_958"));
		classElement.put("documentation", "");
		classElement.put("type", "Class");
		classElement.put("id", id);
		classElement.set("mdExtensionsIds", mapper.createArrayNode());
		classElement.put("ownerId", ownerID);
		classElement.set("syncElementId", nullNode);		
		classElement.put("appliedStereotypeInstanceId", id+"_asi");
		classElement.set("clientDependencyIds", mapper.createArrayNode());
		classElement.set("supplierDependencyIds", mapper.createArrayNode());
		classElement.put("name", name);
		classElement.set("nameExpression", nullNode);
		classElement.set("visibility", nullNode);
		classElement.set("templateParameterId", nullNode);
		classElement.set("elementImportIds", mapper.createArrayNode());
		classElement.set("packageImportIds", mapper.createArrayNode());
		classElement.put("isLeaf", Boolean.FALSE);
		classElement.set("templateBindingIds", mapper.createArrayNode());
		classElement.set("useCaseIds", mapper.createArrayNode());
		classElement.set("representationId", nullNode);
		classElement.set("collaborationUseIds", mapper.createArrayNode());
		classElement.set("generalizationIds", mapper.createArrayNode());
		classElement.set("powertypeExtentIds", mapper.createArrayNode());
		classElement.put("isAbstract", Boolean.FALSE);
		classElement.put("isFinalSpecialization", Boolean.FALSE);
		classElement.set("redefinedClassifierIds", mapper.createArrayNode());
		classElement.set("substitutionIds", mapper.createArrayNode());
		classElement.set("ownedAttributeIds", mapper.createArrayNode());
		classElement.set("classifierBehaviorId", nullNode);
		classElement.set("interfaceRealizationIds", mapper.createArrayNode());
		classElement.set("ownedOperationIds", mapper.createArrayNode());
		classElement.put("isActive", Boolean.FALSE);
		
		elements.add(classElement);
		
		ObjectNode instanceSpecificationElement = mapper.createObjectNode();
		
		instanceSpecificationElement.set("_appliedStereotypeIds",mapper.createArrayNode());
		instanceSpecificationElement.put("documentation", "");
		instanceSpecificationElement.put("type", "InstanceSpecification");
		instanceSpecificationElement.put("id", id+"_asi");
		instanceSpecificationElement.set("mdExtensionsIds", mapper.createArrayNode());
		instanceSpecificationElement.put("ownerId", id);
		instanceSpecificationElement.set("syncElementId", nullNode);
		instanceSpecificationElement.set("appliedStereotypeInstanceId", nullNode);
		instanceSpecificationElement.set("clientDependencyIds", mapper.createArrayNode());
		instanceSpecificationElement.set("supplierDependencyIds", mapper.createArrayNode());
		instanceSpecificationElement.put("name", name);
		instanceSpecificationElement.set("nameExpression", nullNode);
		instanceSpecificationElement.set("visibility", nullNode);
		instanceSpecificationElement.set("templateParameterId", nullNode);
		instanceSpecificationElement.set("deploymentIds", mapper.createArrayNode());
		instanceSpecificationElement.set("slotIds", mapper.createArrayNode());
		instanceSpecificationElement.set("specification", nullNode);
		instanceSpecificationElement.set("classifierIds", mapper.createArrayNode().add("_11_5EAPbeta_be00301_1147424179914_458922_958"));
		instanceSpecificationElement.put("stereotypedElementId", id);
		
		elements.add(instanceSpecificationElement);
		

		return elements;
	}
	
	/**
	 * Used for building instance specifications
	 * @param ownerID Owner id of instance specification
	 * @param classifierID ID of classifying element
	 * @param name Name of instance specification
	 * @param stereotypesElement If the instance stereotypes an element, then it will be true.
	 * @return
	 */
	public ObjectNode buildInstanceSpecificationNode(String ownerID,String classifierID,String name,Boolean stereotypesElement)
	{
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode nullNode = null;
		
		ObjectNode instanceSpecificationElement = mapper.createObjectNode();
		
		instanceSpecificationElement.set("_appliedStereotypeIds",mapper.createArrayNode());
		instanceSpecificationElement.put("documentation", "");
		instanceSpecificationElement.put("type", "InstanceSpecification");
		
		if(stereotypesElement)
		{
			instanceSpecificationElement.put("id", ownerID+"_asi");
		}
		else
		{
			instanceSpecificationElement.put("id", createId());
		}
		
		instanceSpecificationElement.set("mdExtensionsIds", mapper.createArrayNode());
		instanceSpecificationElement.put("ownerId", ownerID);
		instanceSpecificationElement.set("syncElementId", nullNode);
		instanceSpecificationElement.set("appliedStereotypeInstanceId", nullNode);
		instanceSpecificationElement.set("clientDependencyIds", mapper.createArrayNode());
		instanceSpecificationElement.set("supplierDependencyIds", mapper.createArrayNode());
		instanceSpecificationElement.put("name", name);
		instanceSpecificationElement.set("nameExpression", nullNode);
		instanceSpecificationElement.set("visibility", nullNode);
		instanceSpecificationElement.set("templateParameterId", nullNode);
		instanceSpecificationElement.set("deploymentIds", mapper.createArrayNode());
		instanceSpecificationElement.set("slotIds", mapper.createArrayNode());
		instanceSpecificationElement.set("specification", nullNode);
		instanceSpecificationElement.set("classifierIds", mapper.createArrayNode().add(classifierID));
		
		if(stereotypesElement)
		{
			instanceSpecificationElement.put("stereotypedElementId", ownerID);
		}
		else
		{
			instanceSpecificationElement.set("stereotypedElementId", nullNode);
		}

		return instanceSpecificationElement;
	}
	
	/**
	 * Creates the json of a slot to send to mms.
	 * @param ownerID Owner of the slot element
	 * @param value value of slot
	 * @param definingFeatureId Defining feature ID is the ID of a property. 
	 * @return
	 */
	public ObjectNode buildSlotNode(String ownerID,String value, String definingFeatureId)
	{
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode nullNode = null;
		
		ObjectNode slotElement = mapper.createObjectNode();
		
		String slotElementID = ownerID+"-slot-"+definingFeatureId;
		slotElement.set("_appliedStereotypeIds",mapper.createArrayNode());
		slotElement.put("documentation", "");
		slotElement.put("type", "Slot");
		slotElement.put("id", ownerID+"-slot-"+definingFeatureId);
		slotElement.set("mdExtensionsIds", mapper.createArrayNode());
		
		slotElement.put("ownerId", ownerID);
		slotElement.set("syncElementId", nullNode);
		slotElement.set("appliedStereotypeInstanceId", nullNode);
		
		ArrayNode valueNode = mapper.createArrayNode();
		ObjectNode nestedValue = mapper.createObjectNode();
		nestedValue.set("_appliedStereotypeIds", mapper.createArrayNode());
		nestedValue.put("documentation", "");
		nestedValue.put("type", "LiteralString");
		nestedValue.put("id",ownerID+"-slot-"+definingFeatureId+"-slotvalue-0-literalstring");
		nestedValue.set("mdExtensionsIds", mapper.createArrayNode());
		nestedValue.put("ownerId",slotElementID);
		nestedValue.set("syncElementId", nullNode);
		nestedValue.set("appliedStereotypeInstanceId", nullNode);
		nestedValue.set("clientDependencyIds", mapper.createArrayNode());
		nestedValue.set("supplierDependencyIds", mapper.createArrayNode());
		nestedValue.put("name", "");
		nestedValue.set("nameExpression", nullNode);
		nestedValue.set("visibility", nullNode);
		nestedValue.set("templateParameterId", nullNode);
		nestedValue.set("typeId", nullNode);
		nestedValue.put("value", value);
		
		valueNode.add(nestedValue);
		
		slotElement.set("value", valueNode);
		
		slotElement.put("definingFeatureId",definingFeatureId);
		
		return slotElement;
	}
	
	/**
	 * Builds a part property json object. Values are strings by default.
	 * 
	 * @param ownerID Sysml ID of owner
	 * @param name property name
	 * @param value value of property. 
	 * @return
	 */
	public ObjectNode buildPropertyNode(String ownerID,String name,String value, String redefinedPropertyId)
	{
		
		String propertyID = createId();
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode classElement = mapper.createObjectNode();
		ObjectNode nullNode = null;
		
		classElement.set("_appliedStereotypeIds", mapper.createArrayNode().add("_12_0_be00301_1164123483951_695645_2041"));
		classElement.put("documentation", "");
		classElement.put("type", "Property");
		classElement.put("id", propertyID);
		classElement.set("mdExtensionsIds", mapper.createArrayNode());
		classElement.put("ownerId", ownerID);
		classElement.set("syncElementId", nullNode);
		classElement.put("appliedStereotypeInstanceId", propertyID+"_asi");
		classElement.set("clientDependencyIds", mapper.createArrayNode());
		classElement.set("supplierDependencyIds", mapper.createArrayNode());
		classElement.put("name", name);
		classElement.set("nameExpression", nullNode);
		classElement.set("visibility", nullNode);
		classElement.put("isLeaf", Boolean.FALSE);
		classElement.put("isStatic", Boolean.FALSE);
		classElement.put("typeId", "_16_5_1_12c903cb_1245415335546_479030_4092");
		classElement.put("isOrdered", Boolean.FALSE);
		classElement.put("isUnique", Boolean.TRUE);
		classElement.set("lowerValue", nullNode);
		classElement.set("upperValue", nullNode);
		classElement.put("isReadOnly", Boolean.FALSE);
		classElement.set("templateParameterId", nullNode);
		classElement.set("endIds", mapper.createArrayNode());
		classElement.set("deploymentIds", mapper.createArrayNode());
		classElement.put("aggregation", "composite");
		classElement.set("associationEndId", nullNode);
		classElement.set("qualifierIds", mapper.createArrayNode());
		classElement.set("datatypeId", nullNode);
		
		if (value != null) 
		{
			ObjectNode defaultValue = mapper.createObjectNode(); // value element

			defaultValue.set("_appliedStereotypeIds", mapper.createArrayNode());
			defaultValue.put("documentation", "");
			defaultValue.put("type", "LiteralString");
			defaultValue.put("id", createId());
			defaultValue.set("mdExtensionsIds", mapper.createArrayNode());
			defaultValue.put("ownerId", propertyID);
			defaultValue.set("syncElementId", nullNode);
			defaultValue.set("appliedStereotypeInstanceId", nullNode);
			defaultValue.set("clientDependencyIds", mapper.createArrayNode());
			defaultValue.set("supplierDependencyIds", mapper.createArrayNode());
			defaultValue.put("name", "");
			defaultValue.set("nameExpression", nullNode);
			defaultValue.put("visibility", "public");
			defaultValue.set("templateParameterId", nullNode);
			defaultValue.set("typeId", nullNode);
			defaultValue.put("value", value);

			classElement.set("defaultValue", defaultValue);
		}
		else
		{
			classElement.set("defaultValue", nullNode);
		}
		
		
		classElement.set("interfaceId", nullNode);
		classElement.put("isDerived", Boolean.FALSE);
		classElement.put("isDerivedUnion", Boolean.FALSE);
		classElement.put("isID", Boolean.FALSE);
		
		if(redefinedPropertyId!=null)
		{
			classElement.set("redefinedPropertyIds", mapper.createArrayNode().add(redefinedPropertyId));
		}
		else
		{
			classElement.set("redefinedPropertyIds", mapper.createArrayNode());
		}
		classElement.set("subsettedPropertyIds", mapper.createArrayNode());
		classElement.set("associationId", nullNode);
		
		return classElement;
	}
	
	public ObjectNode buildGeneralizationNode(String ownerID,String sourceID,String targetID)
	{
		String generalizationID = createId();
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode generalization = mapper.createObjectNode();
		ObjectNode nullNode = null;
		generalization.set("_appliedStereotypeIds", mapper.createArrayNode());
		generalization.put("documentation", "");
		generalization.put("type", "Generalization");
		generalization.put("id", generalizationID);
		generalization.set("mdExtensionsIds", mapper.createArrayNode());
		generalization.put("ownerId", ownerID);
		generalization.set("syncElementId", nullNode);
		generalization.set("appliedStereotypeInstanceId", nullNode);
		generalization.set("_sourceIds", mapper.createArrayNode().add(sourceID));
		generalization.set("_targetIds", mapper.createArrayNode().add(targetID));
		generalization.put("generalId", targetID);
		generalization.set("generalizationSetIds", mapper.createArrayNode());
		generalization.put("isSubstitutable", Boolean.TRUE);
		generalization.put("specificId", ownerID);
		
		return generalization;
	}
	
	/**
	 * 
	 * @param sysmlID Sysml ID of job element
	 * @param ownerID owner of the job element
	 * @param name name of the job element
	 * @param associatedElementID
	 * @param type
	 * @param schedule
	 * @param refID
	 * @param projectID
	 * @return
	 */
	public ObjectNode buildDocgenJobElementJSON(String sysmlID, String ownerID,String name, String associatedElementID, String type,String schedule, String refID, String projectID) 
	{
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode payload = mapper.createObjectNode();
		ArrayNode elements = buildClassElement(sysmlID,ownerID,name);
		
		ObjectNode generalizationNode = buildGeneralizationNode(sysmlID, sysmlID, docgenJobBlockID);
		ObjectNode typePropertyNode = buildPropertyNode(sysmlID,"type",type,typePropertyID);
		ObjectNode typePropertyInstanceSpecification = buildInstanceSpecificationNode(typePropertyNode.get("id").toString().replace("\"", ""), valuePropertyStereotypeID,"",true);
		ObjectNode schedulePropertyNode = buildPropertyNode(sysmlID,"schedule",schedule,schedulePropertyID);
		ObjectNode schedulePropertyInstanceSpecification = buildInstanceSpecificationNode(schedulePropertyNode.get("id").toString().replace("\"", ""), valuePropertyStereotypeID,"",true);	
		ObjectNode buildNumberPropertyNode = buildPropertyNode(sysmlID,"buildNumber",null,buildNumberPropertyID);
		ObjectNode buildNumberPropertyInstanceSpecification = buildInstanceSpecificationNode(buildNumberPropertyNode.get("id").toString().replace("\"", ""), valuePropertyStereotypeID,"",true);
		ObjectNode jobStatusPropertyNode = buildPropertyNode(sysmlID,"jobStatus",null,jobStatusID);
		ObjectNode jobStatusPropertyInstanceSpecification = buildInstanceSpecificationNode(jobStatusPropertyNode.get("id").toString().replace("\"", ""), valuePropertyStereotypeID,"",true);
		ObjectNode logUrlPropertyNode = buildPropertyNode(sysmlID,"logUrl",null,logUrlPropertyID);
		ObjectNode logUrlPropertyInstanceSpecification = buildInstanceSpecificationNode(logUrlPropertyNode.get("id").toString().replace("\"", ""), valuePropertyStereotypeID,"",true);
		ObjectNode startedPropertyNode = buildPropertyNode(sysmlID,"started",null,startedPropertyID);
		ObjectNode startedPropertyInstanceSpecification = buildInstanceSpecificationNode(startedPropertyNode.get("id").toString().replace("\"", ""), valuePropertyStereotypeID,"",true);
		ObjectNode completedPropertyNode = buildPropertyNode(sysmlID,"completed",null,completedPropertyID);
		ObjectNode completedPropertyInstanceSpecification = buildInstanceSpecificationNode(completedPropertyNode.get("id").toString().replace("\"", ""), valuePropertyStereotypeID,"",true);
		ObjectNode associatedElementIdPropertyNode = buildPropertyNode(sysmlID,"associatedElementId",associatedElementID,associatedElementIdPropertyID);
		ObjectNode associatedElementIdPropertyInstanceSpecification = buildInstanceSpecificationNode(associatedElementIdPropertyNode.get("id").toString().replace("\"", ""), valuePropertyStereotypeID,"",true);
		ObjectNode refIdPropertyNode = buildPropertyNode(sysmlID,"refId",null,refIdPropertyID);
		ObjectNode refIdPropertyInstanceSpecification = buildInstanceSpecificationNode(refIdPropertyNode.get("id").toString().replace("\"", ""), valuePropertyStereotypeID,"",true);
		ObjectNode projectIdPropertyNode = buildPropertyNode(sysmlID,"projectId",projectID,projectIdPropertyID);
		ObjectNode projectIdPropertyInstanceSpecification = buildInstanceSpecificationNode(projectIdPropertyNode.get("id").toString().replace("\"", ""), valuePropertyStereotypeID,"",true);

		elements.add(generalizationNode);
		elements.add(typePropertyNode);
		elements.add(typePropertyInstanceSpecification);
		elements.add(schedulePropertyNode);
		elements.add(schedulePropertyInstanceSpecification);
		elements.add(buildNumberPropertyNode);
		elements.add(buildNumberPropertyInstanceSpecification);
		elements.add(jobStatusPropertyNode);
		elements.add(jobStatusPropertyInstanceSpecification);
		elements.add(logUrlPropertyNode);
		elements.add(logUrlPropertyInstanceSpecification);	
		elements.add(startedPropertyNode);
		elements.add(startedPropertyInstanceSpecification);
		elements.add(completedPropertyNode);
		elements.add(completedPropertyInstanceSpecification);
		elements.add(associatedElementIdPropertyNode);
		elements.add(associatedElementIdPropertyInstanceSpecification);
		elements.add(refIdPropertyNode);
		elements.add(refIdPropertyInstanceSpecification);
		elements.add(projectIdPropertyNode);
		elements.add(projectIdPropertyInstanceSpecification);
		
		/*
		 * Adding the property id's to the ownedAttributes key in the job class JSON
		 * Also adding generalization id to the job class node.
		 */
		
		ObjectNode jobClass = (ObjectNode) elements.get(0);
		ArrayNode ownedAttributes = mapper.createArrayNode();
		
		ownedAttributes.add(typePropertyNode.get("id"));
		ownedAttributes.add(schedulePropertyNode.get("id"));
		ownedAttributes.add(buildNumberPropertyNode.get("id"));
		ownedAttributes.add(jobStatusPropertyNode.get("id"));
		ownedAttributes.add(logUrlPropertyNode.get("id"));
		ownedAttributes.add(startedPropertyNode.get("id"));
		ownedAttributes.add(completedPropertyNode.get("id"));
		ownedAttributes.add(associatedElementIdPropertyNode.get("id"));
		ownedAttributes.add(refIdPropertyNode.get("id"));
		ownedAttributes.add(projectIdPropertyNode.get("id"));
				
		jobClass.set("ownedAttributeIds",ownedAttributes);
		jobClass.set("generalizationIds",mapper.createArrayNode().add(generalizationNode.get("id")));

		elements.set(0, jobClass);
		
		payload.set("elements",elements);
		payload.put("source","pma");
		payload.put("pmaVersion","3.1");
		
		return payload;
	}
	
	/**
	* Creating instance specification with slots
	 * @param id SysmlID of the instance specification
	 * @param ownerID 
	 * @param name name of job instance
	 * @param buildNumber Jenkins build number
	 * @param jobStatus
	 * @param server
	 * @param projectID
	 * @param refID
	 * @param jobID
	 * @return
	 */
	public ObjectNode buildDocGenJobInstanceJSON(String id, String ownerID,String name,String buildNumber,String jobStatus,String server, String projectID, String refID,String jobID) 
	{
		
		ObjectMapper mapper = new ObjectMapper();	
		
		String schedule = "";
		String type = "";
		String associatedElementID = "";
		String jobName = "";
		
		try {
			String jobJsonString = getJobElement(server, projectID, refID, jobID).getBody();
			JsonNode fullJson = mapper.readTree(jobJsonString);
			if(fullJson!=null)
			{
				JsonNode jobJson = fullJson.get("jobs");
				if (jobJson != null)
				{
					JsonNode job = jobJson.get(0);
					if (job != null) 
					{
						String scheduleValue = job.get("schedule").toString();
						String typeValue = job.get("command").toString();
						String associatedElementIDValue = job.get("associatedElementID").toString();
						String jobValue = job.get("name").toString();

						if (scheduleValue != null) {
							schedule = scheduleValue.replace("\"", "");
						}
						if (typeValue != null) {
							type = typeValue.replace("\"", "");
						}
						if (associatedElementIDValue != null) {
							associatedElementID = associatedElementIDValue.replace("\"", "");
						}
						if (jobValue != null) {
							jobName = jobValue.replace("\"", "");
						}
					}
					else
					{
						ObjectNode responseJSON = mapper.createObjectNode();
						responseJSON.put("message", "Job Element Doesn't Exist on MMS"); 
						return(responseJSON); // Job element was not found since jobs array was blank
					}
				}
				else
				{

					ObjectNode responseJSON = mapper.createObjectNode();
					responseJSON.put("message", jobJsonString); 

					return(responseJSON); // If jobJson was null, then the job element must not exist or mms returned an error
				}
			}
			else
			{
				ObjectNode responseJSON = mapper.createObjectNode();
				responseJSON.put("message", jobJsonString); 

				return(responseJSON); // If fullJson was null, then the job element must not exist because mms returned an empty json or mms returned an error.
			}

		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		ObjectNode payload = mapper.createObjectNode();
		ArrayNode elements = mapper.createArrayNode();
		ObjectNode instanceSpecificationNode = buildInstanceSpecificationNode(ownerID, jobID,jobName+" - "+name, false);
		instanceSpecificationNode.put("id",id);
		
		String typeDefiningFeatureId = getDefiningFeatureID(server, projectID, refID, jobID, "type");
		ObjectNode typeSlotNode = buildSlotNode(instanceSpecificationNode.get("id").toString().replace("\"", ""), type,typeDefiningFeatureId);
		
		String scheduleDefiningFeatureId = getDefiningFeatureID(server, projectID, refID, jobID, "schedule");
		ObjectNode scheduleSlotNode = buildSlotNode(instanceSpecificationNode.get("id").toString().replace("\"", ""), schedule,scheduleDefiningFeatureId);
		
		String buildNumberDefiningFeatureId = getDefiningFeatureID(server, projectID, refID, jobID, "buildNumber");
		ObjectNode buildNumberSlotNode = buildSlotNode(instanceSpecificationNode.get("id").toString().replace("\"", ""), buildNumber,buildNumberDefiningFeatureId);
		
		String jobStatusDefiningFeatureId = getDefiningFeatureID(server, projectID, refID, jobID, "jobStatus");
		ObjectNode jobStatusSlotNode = buildSlotNode(instanceSpecificationNode.get("id").toString().replace("\"", ""), jobStatus,jobStatusDefiningFeatureId);
		
		String logUrlDefiningFeatureId = getDefiningFeatureID(server, projectID, refID, jobID, "logUrl");
		ObjectNode logUrlSlotNode = buildSlotNode(instanceSpecificationNode.get("id").toString().replace("\"", ""), "",logUrlDefiningFeatureId);
		
		String currentTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date()); //ex. 2017-06-08T13:37:19.483-0700
		String startedDefiningFeatureId = getDefiningFeatureID(server, projectID, refID, jobID, "started");
		ObjectNode startedSlotNode = buildSlotNode(instanceSpecificationNode.get("id").toString().replace("\"", ""), currentTimestamp,startedDefiningFeatureId);
		
		String completedDefiningFeatureId = getDefiningFeatureID(server, projectID, refID, jobID, "completed");
		ObjectNode completedSlotNode = buildSlotNode(instanceSpecificationNode.get("id").toString().replace("\"", ""), "",completedDefiningFeatureId);
		
		String associatedElementDefiningFeatureId = getDefiningFeatureID(server, projectID, refID, jobID, "associatedElementId");
		ObjectNode associatedElementIDSlotNode = buildSlotNode(instanceSpecificationNode.get("id").toString().replace("\"", ""), associatedElementID ,associatedElementDefiningFeatureId);
		
		String projectIdDefiningFeatureId = getDefiningFeatureID(server, projectID, refID, jobID, "projectId");
		ObjectNode projectIdSlotNode = buildSlotNode(instanceSpecificationNode.get("id").toString().replace("\"", ""), projectID,projectIdDefiningFeatureId);
		
		String refIdDefiningFeatureId = getDefiningFeatureID(server, projectID, refID, jobID, "refId");
		ObjectNode refIdSlotNode = buildSlotNode(instanceSpecificationNode.get("id").toString().replace("\"", ""), refID,refIdDefiningFeatureId);
		
		/*
		 * Adding the property id's to the ownedAttributes key in the instance specification JSON
		 */
		ArrayNode slotIds = mapper.createArrayNode();
		slotIds.add(typeSlotNode.get("id"));
		slotIds.add(scheduleSlotNode.get("id"));
		slotIds.add(buildNumberSlotNode.get("id"));
		slotIds.add(jobStatusSlotNode.get("id"));
		slotIds.add(logUrlSlotNode.get("id"));
		slotIds.add(startedSlotNode.get("id"));
		slotIds.add(completedSlotNode.get("id"));
		slotIds.add(associatedElementIDSlotNode.get("id"));
		slotIds.add(projectIdSlotNode.get("id"));
		slotIds.add(refIdSlotNode.get("id"));
		
		instanceSpecificationNode.set("slotIds",slotIds);
		
		elements.add(instanceSpecificationNode);
		
		elements.add(typeSlotNode);
		elements.add(scheduleSlotNode);
		elements.add(buildNumberSlotNode);
		elements.add(jobStatusSlotNode);
		elements.add(logUrlSlotNode);
		elements.add(startedSlotNode);
		elements.add(completedSlotNode);
		elements.add(associatedElementIDSlotNode);
		elements.add(projectIdSlotNode);
		elements.add(refIdSlotNode);

		
		payload.set("elements",elements);
		payload.put("source","pma");
		payload.put("pmaVersion","3.1");
		
		
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
		      logger.info("Unknown Host Exception");
		      return e.toString();
		}
		catch (java.lang.IllegalArgumentException e) {
			logger.info("Illegal argument during Post");
			System.out.println("Illegal argument during Post");
			return e.toString();
		}catch (IOException e) 
		{
			e.printStackTrace();
			return e.toString();
		}
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
		      return (e.toString());
		}catch (IOException e) 
		{
			e.printStackTrace();
			return (e.toString());
		}
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
			logger.info("Unknown Host Exception During Get");
			System.out.println("Unknown Host Exception During Get");
			return e.toString();
		}
		catch (java.lang.IllegalArgumentException e) {
			logger.info("Illegal argument during Get");
			System.out.println("Illegal argument during Get");
			return e.toString();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.toString();
		}

	}
	
	/**
	 * Used for getting bulk elements from MMS
	 * 
	 * @param server mms server (ex. opencae-uat.jpl.nasa.gov)
	 * @param projectId magicdraw project (ex.PROJECT-cea59ec3-7f4a-4619-8577-17bbeb9f1b)
	 * @param elementArray Array of elementId's to be retrieved. Ex: {"elements": [{"id": "ELEMENT_ID1"}, {"id":"ELEMENT_ID2" }]}
	 * @param recurse 
	 * @return json string containing elements element.
	 */
	public String put(String server,String projectId,String refId,ObjectNode elementArray,Boolean recurse){
		
		String recurseString = "";
		if(recurse)
		{
			recurseString = "depth=-1&";
		}
		server = server.replace("https://", ""); 
		server = server.replace("/", "");
		HttpClient httpClient = HttpClientBuilder.create().build();
		try {
			String url = "https://"+server+"/alfresco/service/projects/"+projectId+"/refs/"+refId+"/elements"+"?"+recurseString+"alf_ticket="+alfrescoToken;
			System.out.println("URL: "+url);
		    HttpPut request = new HttpPut(url);
		    StringEntity params = new StringEntity(elementArray.toString());
			request.setHeader("Accept", "application/json");
			request.setHeader("Content-type", "application/json");
		    request.setEntity(params);
		    HttpResponse response = httpClient.execute(request);
		    
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			
			String result = "";
			String line = "";
			while ((line = rd.readLine()) != null) {
				result = result+line.trim();
			}
//			System.out.println("RESULT:"+result);
			return result;
			
		}
		catch (java.net.UnknownHostException e) {
			logger.info("Unknown Host Exception During Bulk element get");
			System.out.println("Unknown Host Exception During Bulk element get");
			return e.toString();
		}
		catch (java.lang.IllegalArgumentException e) {
			logger.info("Illegal argument during Bulk element get");
			System.out.println("Illegal argument during Bulk element get");
			return e.toString();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.toString();
		}

	}
	
	/**
	 * Used for getting the JSON of a project
	 * 
	 * @param server mms server (ex. opencae-uat.jpl.nasa.gov)
	 * @param project magicdraw project (ex.PROJECT-cea59ec3-7f4a-4619-8577-17bbeb9f1b)
	 * @return json string of project.
	 */
	public String getProjectJson(String server,String project){
		
		server = server.replace("https://", ""); 
		server = server.replace("/", "");
		HttpClient httpClient = HttpClientBuilder.create().build();
		try {
			String url = "https://"+server+"/alfresco/service/projects/"+project+"?alf_ticket="+alfrescoToken;
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
			logger.info("Unknown Host Exception During Get");
			System.out.println("Unknown Host Exception During Get");
			return e.toString();
		}
		catch (java.lang.IllegalArgumentException e) {
			logger.info("Illegal argument during Get");
			System.out.println("Illegal argument during Get");
			return e.toString();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.toString();
		}
	}
	
	/**
	 * Used for getting the org a project is in.
	 * 
	 * @param server mms server (ex. opencae-uat.jpl.nasa.gov)
	 * @param project magicdraw project (ex.PROJECT-cea59ec3-7f4a-4619-8577-17bbeb9f1b)
	 * @return json string of element.
	 */
	public String getProjectOrg(String server, String project)
	{
		
		String projectJson = getProjectJson(server, project);
		ObjectMapper mapper = new ObjectMapper();
		
		ArrayNode jobInstanceArray = mapper.createArrayNode();
		try {
			JsonNode fullJson = mapper.readTree(projectJson);
			JsonNode projects = fullJson.get("projects");
			if (projects == null)  // instances will be null if the json returned with error
			{
				return projectJson;
			}
			else
			{
				if(projects.size()==0)
				{
					return null;
				}
				for(JsonNode projectNode:projects)
				{
					String org = projectNode.get("orgId").toString().replace("\"","");
					return org;
				}
			}
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return projectJson;
	}
	
	/**
	 * Should get the current value of the slot, change it and send it back to mms
	 * Looking for instance specifications using the latest created one in the ref.
	 * @param server MMS server. Ex. opencae-uat.jpl.nasa.gov
	 * @param projectId Magicdraw project id
	 * @param refId
	 * @param jobId ID of job element
	 * @param buildNumber Build number of the jenkins job. Starts from 1. 
	 * @param propertyName Name of the part property. Ex: buildNumber,jobStatus,logUrl,etc
	 * @param newSlotValue New value of the slot
	 * @param token Alfresco token.
	 * @return Status code returned from mms.
	 */
	public String modifyInstanceSpecificationValue(String server,String projectId,String refId,String jobId,String buildNumber,String propertyName,String newSlotValue)
	{
		
		// Get all the job instances and the job element.
		String mmsReturnString = getJobInstancesJson(server, projectId, refId, jobId); 
		
		Map<String,String> jobInstanceInformationMap = null;
		ObjectMapper mapper = new ObjectMapper();
		
		if(isElementJSON(mmsReturnString)) // It will be an error if the json string is not an element JSON.
		{
			// looking for job instance element
			ArrayList<Map<String,String>> jobInstancesmapList = PMAUtil.generateJobInstanceIDMapJSON(mmsReturnString,jobId); // map contains slot id's with their values
			for(Map jobInstanceMap:jobInstancesmapList)
			{
				if(jobInstanceMap.get("refId").equals(refId))
				{
					jobInstanceInformationMap = jobInstanceMap;
					System.out.println(jobInstanceInformationMap);
					break; // Assuming job instance is the first instance in the jobInstancesmapList
				}
			}
			if(jobInstanceInformationMap!=null) // Instance was found. 
			{
				String jobInstanceSlotID = jobInstanceInformationMap.get(propertyName+"ID");
				if(jobInstanceSlotID!=null)
				{
					// Modify slot json and send back to MMS
					try {
						JsonNode fullJson = mapper.readTree(mmsReturnString);
						JsonNode elements = fullJson.get("elements");
						ObjectNode instanceSlotElement = null;
						for(JsonNode element:elements)
						{
							if(element.get("id").toString().replace("\"", "").equals(jobInstanceSlotID))
							{
								// Found slot
								logger.info("Found Slot for: "+propertyName);
								System.out.println("Found Slot for: "+propertyName);
								instanceSlotElement=(ObjectNode) element;
								
							}
						}
						if(instanceSlotElement!=null)
						{
							// Modify slot 
							ObjectNode valueNode = (ObjectNode) instanceSlotElement.get("value").get(0);
							System.out.println("Old Value: "+valueNode.get("value"));
							valueNode.put("value", newSlotValue);
							System.out.println("New Value: "+valueNode.get("value"));
							
							ArrayNode valueArray = mapper.createArrayNode();
							valueArray.add(valueNode);
							instanceSlotElement.set("value", valueArray);
							
							// puts the new json object in an elements array that will be sent to mms
							ObjectNode payload = mapper.createObjectNode();
							ArrayNode arrayElements = mapper.createArrayNode();
							arrayElements.add(instanceSlotElement);
							payload.put("elements",arrayElements);
							payload.put("source","pma");
							
							// send element to MMS
//							System.out.println("Payload: "+payload);
							
							String response = post(server, projectId, refId, payload); // sending element to MMS
							System.out.println("MMS Update Element Response: "+response);
							/*
							 * Sending jms messsage with job instance object
							 */
				    		if (response.equals("HTTP/1.1 200 OK"))
				    		{
						    	return sendJobInstanceJMS(jobInstanceInformationMap, propertyName, newSlotValue, server, refId, projectId);
				    		}
				    		else
				    		{
				    			return response;
				    		}
						}
						else
						{
							return "Error during Job Instance Modification. Slot element not found on MMS";
						}
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return(e.toString());
					}catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return(e.toString());
					}
						
				}
				else
				{
					return "Error during Job Instance Modification. No slot with this property name: "+propertyName;
				}
			}
			else // Instance isn't found, a new one will be created. Happens when a job is ran without being triggered by PMA. EX. (Manual run on Jenkins or a scheduled run.)
			{
				System.out.println("INSIDE ELSE");
				if(!propertyName.equals("jobStatus")) // By default the job status will be pending.
				{
					newSlotValue = "pending";
				}
				String createJobInstanceElementResponse = createJobInstanceElement(jobId, projectId, refId, server, buildNumber, newSlotValue);
				System.out.println("CREATE JOB INSTANCE RESPONSE: "+createJobInstanceElementResponse);
				return createJobInstanceElementResponse;
				
			}
		}
		
		return mmsReturnString;
	}
	
	/**
	 * Should get the current value of the slot, change it and send it back to mms
	 * Looking for instance specifications using the latest created one in the ref.
	 * @param server MMS server. Ex. opencae-uat.jpl.nasa.gov
	 * @param projectId Magicdraw project id
	 * @param refId
	 * @param jobId ID of job element
	 * @param buildNumber Build number of the jenkins job. Starts from 1. 
	 * @param propertyName Name of the part property. Ex: buildNumber,jobStatus,logUrl,etc
	 * @param newSlotValue New value of the slot
	 * @param token Alfresco token.
	 * @return Status code returned from mms.
	 */
	public String modifyBulkInstanceSpecificationValue(String server,String projectId,String refId,String jobId,String buildNumber,Map<String,String> newJobInstanceValues)
	{
		
		// Get all the job instances and the job element.
		String mmsReturnString = getJobInstancesJson(server, projectId, refId, jobId); 
		Map<String,String> jobInstanceInformationMap = null;
		ObjectMapper mapper = new ObjectMapper();
		
		if(isElementJSON(mmsReturnString)) // It will be an error if the json string is not an element JSON.
		{
//			System.out.println("JOBID: "+jobId);
//			System.out.println("beforeFor: "+PMAUtil.generateJobInstanceIDMapJSON(mmsReturnString,jobId));
			// looking for job instance element
			ArrayList<Map<String,String>> jobInstancesmapList = PMAUtil.generateJobInstanceIDMapJSON(mmsReturnString,jobId); // map contains slot id's with their values
			for(Map jobInstanceMap:jobInstancesmapList)
			{
				if(jobInstanceMap.get("refId").equals(refId))
				{
					jobInstanceInformationMap = jobInstanceMap;
//					System.out.println("INFOMAP: "+jobInstanceInformationMap);
					break; // Assuming job instance is the first instance in the jobInstancesmapList
				}
			}
	
//		    System.out.println("beforeif");
//		    System.out.println(jobInstanceInformationMap!=null);
			if(jobInstanceInformationMap!=null) // Instance was found. 
			{
				ArrayNode arrayElements = mapper.createArrayNode();
				Iterator it = newJobInstanceValues.entrySet().iterator();
			    while (it.hasNext()) {
			        Map.Entry pair = (Map.Entry)it.next();
			        String propertyName = (String) pair.getKey();
			        String newSlotValue = (String) pair.getValue();
			        
			        System.out.println("PropertyName: "+propertyName);
			        System.out.println("newSlotValue: "+newSlotValue);
			        String jobInstanceSlotID = jobInstanceInformationMap.get(propertyName+"ID");
					if(jobInstanceSlotID!=null)
					{
						// Modify slot json and send back to MMS
						try {
							JsonNode fullJson = mapper.readTree(mmsReturnString);
							JsonNode elements = fullJson.get("elements");
							ObjectNode instanceSlotElement = null;
							for(JsonNode element:elements)
							{
								if(element.get("id").toString().replace("\"", "").equals(jobInstanceSlotID))
								{
									// Found slot
									logger.info("Found Slot for: "+propertyName);
									System.out.println("Found Slot for: "+propertyName);
									instanceSlotElement=(ObjectNode) element;
									
								}
							}
							if(instanceSlotElement!=null)
							{
								// Modify slot 
								ObjectNode valueNode = (ObjectNode) instanceSlotElement.get("value").get(0);
								System.out.println("Old Value: "+valueNode.get("value"));
								valueNode.put("value", newSlotValue);
								System.out.println("New Value: "+valueNode.get("value"));
								
								ArrayNode valueArray = mapper.createArrayNode();
								valueArray.add(valueNode);
								instanceSlotElement.set("value", valueArray);
								
								arrayElements.add(instanceSlotElement);
								jobInstanceInformationMap.put(propertyName, newSlotValue);
							}
							else
							{
								return "Error during Job Instance Modification. Slot element not found on MMS";
							}
						} catch (JsonProcessingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return(e.toString());
						}catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return(e.toString());
						}
							
					}
					else
					{
						return "Error during Job Instance Modification. No slot with this property name: "+propertyName;
					}
					it.remove(); // avoids a ConcurrentModificationException
			    }
			    
			    // puts the new json object in an elements array that will be sent to mms
				ObjectNode payload = mapper.createObjectNode();
				
				payload.put("elements",arrayElements);
				payload.put("source","pma");
				
				// send element to MMS
//				System.out.println("Payload: "+payload);
				
				String response = post(server, projectId, refId, payload); // sending element to MMS
//				System.out.println("MMS Update Element Response: "+response);
				/*
				 * Sending jms messsage with job instance object
				 */
	    		if (response.equals("HTTP/1.1 200 OK"))
	    		{
			    	return sendJobInstanceJMS(jobInstanceInformationMap,"Bulk", "Update", server, refId, projectId);
	    		}
	    		else
	    		{
	    			return response;
	    		}
				
			}
			else // Instance isn't found, a new one will be created. Happens when a job is ran without being triggered by PMA. EX. (Manual run on Jenkins or a scheduled run.)
			{
				String createJobInstanceElementResponse = createJobInstanceElement(jobId, projectId, refId, server, buildNumber, "pending");
//				System.out.println("CREATE JOB INSTANCE RESPONSE: "+createJobInstanceElementResponse);
				return createJobInstanceElementResponse;
				
			}
		}
		
		return mmsReturnString;
	}
	
	/**
	 * Sending jms update message when updating job instances
	 * @param jobInstanceInformationMap contains a mapping of the job instances' ids with their properties
	 * @param propertyName name of slot being modified
	 * @param newSlotValue new value for the slot being modified
	 * @param server
	 * @param refId
	 * @param projectId
	 * @return
	 */
	public String sendJobInstanceJMS(Map<String,String> jobInstanceInformationMap,String propertyName,String newSlotValue, String server, String refId,String projectId)
	{
		try
    	{
    		
    		jobInstanceInformationMap.put(propertyName, newSlotValue);
    		String jobJson = PMAUtil.createJobInstanceJSON(jobInstanceInformationMap).toString();
    		
		 	// build job instance element json to be sent
		 	JSONObject jobInstanceJSON = new JSONObject(jobJson);	
		 	
	    	// Sending job instance element to jms.
	    	JmsConnection jmc = new JmsConnection();
	    	String jmsSettings = MMSUtil.getJMSSettings(server);
	    	JSONObject connectionJson = new JSONObject(jmsSettings);
	    	jmc.ingestJson(connectionJson);
	    	
	    	JSONObject jmsJSON = new JSONObject();	
	    	jmsJSON.put("updatedJobs", jobInstanceJSON);
	    	jmc.publish(jmsJSON, jmc.TYPE_DELTA, refId, projectId);
	    	logger.info("Sent JMS json: "+jmsJSON.toString());
	    	System.out.println("Sent JMS json: "+jmsJSON.toString());
	    	return "Instance Specification Updated. Property: "+propertyName+", Value: "+newSlotValue;
    	}
    	catch(JSONException e)
    	{
    		e.printStackTrace();
    		logger.info(e.toString());
    		return e.toString();
    	}
	}
	
	/**
	 * Creates a new job instance element and sends a jms update message.
	 * @param propertyName
	 * @param jobId
	 * @param projectId
	 * @param refId
	 * @param server
	 * @param buildNumber jenkins build number
	 * @param jobStatus
	 * @return
	 */
	public String createJobInstanceElement(String jobId,String projectId,String refId, String server,String buildNumber, String jobStatus)
	{
		// Creating job instance for the job run because it doesn't currently exist.

		System.out.println("inside job status");
		String jobInstanceElementID = createId();
		ObjectNode on = buildDocGenJobInstanceJSON(jobInstanceElementID, "jobs_bin_" + jobId, jobId + "_instance", buildNumber, jobStatus, server, projectId, refId, jobId); // jobs bin will be the owner of the instance element 
			
		if (on == null) {
			logger.info("buildDocGenJobInstanceJSON output was null");
			return "Error during Job Instance Modification. Job Element doesn't exist on MMS";
		}

		String elementCreationResponse = this.post(server, projectId, refId, on);

		System.out.println("ELEMENT CREATION RESPONSE: " + elementCreationResponse);
		System.out.println(elementCreationResponse.contains("HTTP/1.1 200 OK"));
		System.out.println(elementCreationResponse.equals("HTTP/1.1 200 OK"));
		/*
		 * Sending jms messsage with job instance object
		 */
		if (elementCreationResponse.contains("HTTP/1.1 200 OK")) {
			System.out.println("inside if");
			try {
				/*
				 * When the job instance is first created, it will have these
				 * values by default. Couldn't retrieve the job instance part
				 * property values from MMS, since the job instance was just
				 * created a couple lines above.
				 *
				 */
				JmsConnection jmc = new JmsConnection();
				String jmsSettings = MMSUtil.getJMSSettings(server);
				JSONObject connectionJson = new JSONObject(jmsSettings);
				jmc.ingestJson(connectionJson);

				JSONObject jobInstanceJSON = new JSONObject();
				jobInstanceJSON.put("id", jobInstanceElementID);
				jobInstanceJSON.put("jobId", jobId);
				jobInstanceJSON.put("buildNumber", buildNumber);
				jobInstanceJSON.put("jobStatus", jobStatus);
				jobInstanceJSON.put("jenkinsLog", "");
				jobInstanceJSON.put("created", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date())); // ex. 2017-06-08T13:37:19.483-0700);
				jobInstanceJSON.put("completed", "");

				JSONObject jmsJSON = new JSONObject();
				jmsJSON.put("updatedJobs", jobInstanceJSON);

				jmc.publish(jobInstanceJSON, jmc.TYPE_DELTA, refId, projectId);
				logger.info("Sent JMS json: " + jobInstanceJSON.toString());
				System.out.println("Sent JMS json: " + jobInstanceJSON.toString());
			} catch (JSONException e) {
				e.printStackTrace();
				logger.info(e.toString());
				return e.toString();
			}
			
		}
		return elementCreationResponse;
	
	}
	
		
	// finds all the job elements in a project
	public ResponseEntity<String> getJobElements(String server,String projectId,String refId)
	{
		
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		String returnJSONString = "";
		String getJobsQuery = ElasticSearchQueryBuilder.getJobsQuery(projectId, refId);
		String elasticResponse = queryElastic(server, projectId, refId, getJobsQuery);
//		System.out.println(elasticResponse);
		ObjectMapper mapper = new ObjectMapper();


		JsonNode fullJson = PMAUtil.JSONStringToObject(elasticResponse);
		if(fullJson!=null)
		{
			JsonNode elements = fullJson.get("elements");
			if(elements!=null)
			{
				ArrayNode jobIds = mapper.createArrayNode();
				for(JsonNode element:elements)
				{
					JsonNode jobId = element.get("specificId");
					if(jobId!=null)
					{
						jobIds.add(mapper.createObjectNode().set("id",jobId)); 
					}		
				}
				
				ObjectNode elementArrayNode = mapper.createObjectNode();
				elementArrayNode.set("elements", jobIds);
				
				String bulkElementGetResponse = put(server, projectId, refId, elementArrayNode, true); // retrieving all job elements from mms.
				JsonNode bulkElementGetResponseNode = PMAUtil.JSONStringToObject(bulkElementGetResponse);
//				System.out.println(bulkElementGetResponse);
				if(bulkElementGetResponseNode!=null)
				{
					
					JsonNode bulkElements = bulkElementGetResponseNode.get("elements");
					if(bulkElements!=null)
					{
						
						logger.info("Jobs found successfully");
						String jobsArrayString = PMAUtil.generateJobArrayJSON(bulkElementGetResponse); // converting mms response into a more concise structure
//						System.out.println(jobArrayString);
	
						status=HttpStatus.OK;
						return new ResponseEntity<String>(jobsArrayString,status);
					}
					else
					{
						logger.info(" mms error during bulk element get: "+bulkElementGetResponse);
						return new ResponseEntity<String>(bulkElementGetResponse,status); // mms error during bulk element get
					}

					

				}
				else
				{
					// bulkElementGetResponse was not a JSON String
					logger.info("bulkElementGetResponse was not a JSON String: "+bulkElementGetResponse);
					return new ResponseEntity<String>(bulkElementGetResponse,status); // Returning the error
				}

				
				

				
			}
			else
			{
				// Error with mms elastic query or a blank return. 
				status=HttpStatus.OK;
				return new ResponseEntity<String>(elasticResponse,status);
			}
		}
		else
		{
			// fullJson was not a JSON String
			return new ResponseEntity<String>(elasticResponse,status);
		}
		
	}
	
	/**
	 * Gets a job element from MMS. Recursively gets the element to include all the part properties. Returns the element as a job json.
	 * 
	 * @param server mms server
	 * @param project magic draw project ID
	 * @param refID
	 * @param jobElementID element ID of job. 
	 * @return
	 */
	public ResponseEntity<String> getJobElement(String server, String project,String refID,String jobElementID)
	{
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		String jsonString = get(server,project,refID,jobElementID,true); //should contain job element information from mms
//		System.out.println("Get job elements string: "+jsonString);
		String returnJSONString = "";
		
		PMAUtil pmaUtil = new PMAUtil();
		if(isElementJSON(jsonString)) // It will be an error if the json string is not an element JSON.
		{
			System.out.println("is element json");
			status = HttpStatus.OK;
			return new ResponseEntity<String>(pmaUtil.generateJobArrayJSON(jsonString),status);
		}
		
		if (pmaUtil.isJSON(jsonString)) 
		{
			returnJSONString = jsonString;
		} 
		else 
		{
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode returnJSON = mapper.createObjectNode();
			returnJSON.put("message", jsonString);
			returnJSONString = returnJSON.toString();
		}
		
		logger.info("Get Job element return JSON: "+returnJSONString);
//		System.out.println("Get Job element return JSON: "+returnJSONString);
		return new ResponseEntity<String>(returnJSONString,status); // Returning the error
	}
	
	public String getJobInstanceElement(String server, String project, String refID, String jobInstanceElementID,String jobSysmlID)
	{
		// recursive get job sysmlid
				
		String jobInstanceElements = getJobInstanceElements(server, project, refID, jobSysmlID).getBody();
		
		ObjectMapper mapper = new ObjectMapper();
		
		ObjectNode returnInstance = mapper.createObjectNode();
		ArrayNode jobInstanceArray = mapper.createArrayNode();
		try {
			JsonNode fullJson = mapper.readTree(jobInstanceElements);
			JsonNode jobInstances = fullJson.get("jobInstances");
			if (jobInstances == null)  // instances will be null if the json returned with error
			{
				return jobInstanceElements;
			}
			else
			{
				for(JsonNode instance:jobInstances)
				{
					String instanceId = instance.get("id").toString().replace("\"", "");
					System.out.println("Instance Id: "+instanceId);
					if(instanceId.equals(jobInstanceElementID))
					{
						jobInstanceArray.add(instance);
						returnInstance.set("jobInstances", jobInstanceArray);
						return returnInstance.toString();
					}
				}
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

		return "";
	}
	
	/**
	 * Recursively retrieves all the instance specifications of a Job element and the job element with its part properties.
	 * @param server MMS server
	 * @param projectId MagicDraw project ID
	 * @param refId 
	 * @param jobElementId ID of job element. Used to look up the job instances.
	 * @return
	 */
	public String getJobInstancesJson(String server, String projectId, String refId, String jobElementId)
	{

		String getJobsQuery = ElasticSearchQueryBuilder.getJobInstancesQuery(projectId, refId,jobElementId);
		
		String elasticResponse = queryElastic(server, projectId, refId, getJobsQuery);
		ObjectMapper mapper = new ObjectMapper();


		JsonNode fullJson = PMAUtil.JSONStringToObject(elasticResponse);
		if(fullJson!=null)
		{
			JsonNode elements = fullJson.get("elements");
			if(elements!=null)
			{
				ArrayNode jobInstanceIds = mapper.createArrayNode();
				for(JsonNode element:elements)
				{
					JsonNode jobInstanceId = element.get("id");
					if(jobInstanceId!=null)
					{	
						jobInstanceIds.add(mapper.createObjectNode().set("id",jobInstanceId)); 
					}
				}
				
				ObjectNode elementArrayNode = mapper.createObjectNode();
				jobInstanceIds.add(mapper.createObjectNode().put("id",jobElementId)); // need to retrieve the Job class to get job property names.
				elementArrayNode.set("elements", jobInstanceIds);
				
				//bulk element get
				String bulkElementGetResponse = put(server, projectId, refId, elementArrayNode, true);
				JsonNode bulkElementGetResponseNode = PMAUtil.JSONStringToObject(bulkElementGetResponse);
				
				if(bulkElementGetResponseNode!=null)
				{
					JsonNode bulkElements = bulkElementGetResponseNode.get("elements");
					if(bulkElements!=null)
					{
						logger.info("Job Instances found successfully");
						return bulkElementGetResponse;
					}
					else
					{
						logger.info(" mms error during bulk element get: "+bulkElementGetResponse);
						return bulkElementGetResponse; // mms error during bulk element get
					}
				}
				else
				{
					// bulkElementGetResponse was not a JSON String
					logger.info("bulkElementGetResponse was not a JSON String: "+bulkElementGetResponse);
					return bulkElementGetResponse; // Returning the error
				}
			}
			else
			{
				// Error with mms elastic query or a blank return. 
				logger.info("Error with mms elastic query or a blank return: "+elasticResponse);
				return elasticResponse;
			}
		}
		else
		{
			// fullJson was not a JSON String
			logger.info("fullJson was not a JSON String: "+elasticResponse);
			return elasticResponse; // Returning the error
		}
		
	}
	
	/**
	 * Retrieves the instance specifications ID for a job element
	 * @param server MMS server
	 * @param projectId MagicDraw project ID
	 * @param refId 
	 * @param jobElementId ID of job element. Used to look up the job instances.
	 * @return
	 */
	public String getJobInstanceID(String server, String projectId, String refId, String jobElementId)
	{

		String bulkElementGetResponse = getJobInstancesJson(server, projectId, refId, jobElementId);
		
		JsonNode bulkElementGetResponseNode = PMAUtil.JSONStringToObject(bulkElementGetResponse);
		System.out.println("before null check");
		if(bulkElementGetResponseNode!=null)
		{
			System.out.println("before second null check");
			JsonNode bulkElements = bulkElementGetResponseNode.get("elements");
			if(bulkElements!=null)
			{
				System.out.println("Job Instances found successfully");
				logger.info("Job Instances found successfully");
				for(JsonNode element:bulkElements)
				{
					if((element.get("type").toString().equals("\"InstanceSpecification\""))&&(element.get("classifierIds").get(0).toString().replace("\"", "").equals(jobElementId)))
					{
						return element.get("id").toString().replace("\"","");
					}
				}
				return null; // job instance wasn't found
				
			}
			else
			{
				System.out.println("before null check");
				logger.info(" mms error during bulk element get: "+bulkElementGetResponse);
				return bulkElementGetResponse; // mms error during bulk element get
			}
		}
		else
		{
			// bulkElementGetResponse was not a JSON String
			logger.info("bulkElementGetResponse was not a JSON String: "+bulkElementGetResponse);
			return bulkElementGetResponse; // Returning the error
		}
		
	}
	
	
	/**
	 * Retrieves all the instance specifications for a job element
	 * @param server MMS server
	 * @param projectId MagicDraw project ID
	 * @param refId 
	 * @param jobElementId ID of job element. Used to look up the job instances.
	 * @return
	 */
	public ResponseEntity<String> getJobInstanceElements(String server, String projectId, String refId, String jobElementId)
	{
		
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

		String bulkElementGetResponse = getJobInstancesJson(server, projectId, refId, jobElementId);
		
		JsonNode bulkElementGetResponseNode = PMAUtil.JSONStringToObject(bulkElementGetResponse);
		System.out.println("before null check");
		if(bulkElementGetResponseNode!=null)
		{
			System.out.println("before second null check");
			JsonNode bulkElements = bulkElementGetResponseNode.get("elements");
			if(bulkElements!=null)
			{
				System.out.println("Job Instances found successfully");
				logger.info("Job Instances found successfully");
				String jobInstanceArrayString = PMAUtil.generateJobInstanceArrayJSON(bulkElementGetResponse,jobElementId,refId);
				status = HttpStatus.OK;
				return new ResponseEntity<String>(jobInstanceArrayString,status);
			}
			else
			{
				System.out.println("before null check");
				logger.info(" mms error during bulk element get: "+bulkElementGetResponse);
				return new ResponseEntity<String>(bulkElementGetResponse,status); // mms error during bulk element get
			}
		}
		else
		{
			// bulkElementGetResponse was not a JSON String
			logger.info("bulkElementGetResponse was not a JSON String: "+bulkElementGetResponse);
			return new ResponseEntity<String>(bulkElementGetResponse,status); // Returning the error
		}
		
	}
	
    public String createId() {
    	String id = "PMA_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString();
    	return id;
    }
	
	/**
	 *  Checks if job package exists on server. True if package exists. 
	 *  Job package should have the id jobs_bin_PACKAGEID
	 * @param server mmsServer
	 * @param projectID md project ID
	 * @param refID workspaceID
	 * @return
	 */
	public Boolean jobPackageExists(String server,String projectID,String refID)
	{
		// finds a package with id projectID_job
		String packageID = "jobs_bin_"+projectID;
		String jsonReturnString = this.get(server,projectID,refID,packageID,true);
//		System.out.println("JSON RETURN STRING: "+jsonReturnString);
		
		try {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode fullJson = mapper.readTree(jsonReturnString);
		JsonNode elements = fullJson.get("elements");
		if (elements == null)  // elements will be null if the json returned with error
		{
			return false;
		}
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
	 * Checks if a string is an element JSON
	 * @param jsonString
	 * @return
	 */
	public Boolean isElementJSON(String jsonString)
	{
		if(jsonString.equals("{}"))
		{
			return true;
		}
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode fullJson = mapper.readTree(jsonString);
			JsonNode elements = fullJson.get("elements");
			if(elements!=null)
			{
				return true;
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return false;
	}

	/**
	 * Retrieves alfresco token from mms.
	 * @param server mms server
	 * @param username mms username
	 * @param password mms password
	 * @return Alfresco Ticket String
	 */
	 public static String getAlfrescoToken(String server, String username, String password) {
		server = server.replace("https://", "");
		server = server.replace("/", "");
		HttpClient httpClient = HttpClientBuilder.create().build();

		ObjectMapper mapper = new ObjectMapper();

		ObjectNode payload = mapper.createObjectNode();

		payload.put("username",username);
		payload.put("password",password);

		try {

			String url = "https://"+server+"/alfresco/service/api/login";
			HttpPost request = new HttpPost(url);
			StringEntity params = new StringEntity(payload.toString());
			request.addHeader("Content-Type", "application/json");
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();

			JSONObject result = new JSONObject();
			try {
				result = new JSONObject(EntityUtils.toString(entity));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //Convert String to JSON Object

			try {
				return result.getJSONObject("data").getString("ticket");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				return e.toString();
			}
		}
		catch (java.net.UnknownHostException e) {
			System.out.println("Unknown Host Exception");
			return e.toString();
		}catch (IOException e)
		{
			e.printStackTrace();
			return e.toString();
		}
//		return "Exception Occurred";
	}
	 
	 /**
	  * Retrieves the jms settings for a mms server
	  * @param server mms server
	  * @return JSON String containing jms settings.
	  */
	 public static String getJMSSettings(String server) {
		server = server.replace("https://", "");
		server = server.replace("/", "");
		HttpClient httpClient = HttpClientBuilder.create().build();

		try {
			String url = "https://"+server+"/alfresco/service/connection/jms";
			HttpGet request = new HttpGet(url);
			request.addHeader("Content-Type", "application/json");

			HttpResponse response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();

			return EntityUtils.toString(entity);
		}
		catch (java.net.UnknownHostException e) {
			System.out.println("Unknown Host Exception");
			return e.toString();
		}catch (IOException e)
		{
			e.printStackTrace();
			return e.toString();
		}
	}
	 
	 /**
	  * Retrieves a defining feature ID used for creating slots. Defining feature ID is the ID of a property. 
	  * @param server MMS server ex:opencae-test.jpl.nasa.gov
	  * @param projectID MagicDraw project ID
	  * @param refID MMS branch or tag ID
	  * @param jobID ID of PMA job element
	  * @param propertyName Name of the property of the job element.
	  * @return
	  */
	 public String getDefiningFeatureID(String server, String projectID, String refID, String jobID,String propertyName)
	 {
		String definingFeatureID = null;
		String jsonString = this.get(server, projectID, refID, jobID, true);
		 // Parse through ownedAttributeIds
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode fullJSON = mapper.readTree(jsonString);
			JsonNode elements = fullJSON.get("elements");
			if ((elements == null)) // elements will be null if the json returned with error
			{
				// return false;
				System.out.println(jsonString);
			}
			else
			{
				for(JsonNode element: elements)
				{
					if(element.get("type").toString().replace("\"", "").equals("Class"))
					{
						ArrayNode ownedAttributeIds = (ArrayNode) element.get("ownedAttributeIds");
						for(JsonNode ownedAttribute:ownedAttributeIds)
						{
							String attributeID = ownedAttribute.toString().replace("\"", "");
							for(JsonNode nestedSearchElement: elements)
							{
								if(nestedSearchElement.get("id").toString().replace("\"", "").equals(attributeID))
								{
									String propertyNameString = nestedSearchElement.get("name").toString().replace("\"", "");
									if(propertyName.equals(propertyNameString))
									{
										System.out.println("Found PropertyName: "+propertyNameString);
										definingFeatureID = attributeID;
									}
								}
							}
						}
					}
				}
				
			}

			// for loop
			// get on each ownedAttributeId
			// if name matches element.getname, returnid
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// return false;
		}
//				return true;	
		return definingFeatureID;
	 }
	 
	 	public static Map getElementIdMap()
	 	{
	 		Map elementMap = new HashMap();
	 		elementMap.put(valuePropertyStereotypeID, "valueProperty");
	 		elementMap.put(docgenJobBlockID, "docgenJobBlock");
	 		elementMap.put(typePropertyID, "type");
	 		elementMap.put(schedulePropertyID, "schedule");
	 		elementMap.put(buildNumberPropertyID, "buildNumber");
	 		elementMap.put(jobStatusID, "jobStatus");
	 		elementMap.put(logUrlPropertyID, "logUrl");
	 		elementMap.put(startedPropertyID, "started");
	 		elementMap.put(completedPropertyID, "completed");
	 		elementMap.put(associatedElementIdPropertyID, "associatedElementId");
	 		elementMap.put(refIdPropertyID, "refId");
	 		elementMap.put(projectIdPropertyID, "projectId");	 		
	 		
	 		return elementMap;
	 	}
	 	
	 	public String queryElastic(String server,String project,String refID,String query){
			

			server = server.replace("https://", ""); 
			server = server.replace("/", "");
			HttpClient httpClient = HttpClientBuilder.create().build();
			try {
				 HttpPut request = new HttpPut("https://"+server+"/alfresco/service/projects/"+project+"/refs/"+refID+"/search?alf_ticket="+alfrescoToken);
				 StringEntity params = new StringEntity(query);
				 request.addHeader("content-type", "application/json");
				 request.setEntity(params);
				 HttpResponse response = httpClient.execute(request);
				 System.out.println("link: "+"https://"+server+"/alfresco/service/projects/"+project+"/refs/"+refID+"/elements?alf_ticket="+alfrescoToken);
				 BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

				String result = "";
				String line = "";
				while ((line = rd.readLine()) != null) {
					result = result + line.trim();
				}
				
				return result;
					
			}
			catch (java.net.UnknownHostException e) {
				logger.info("Unknown Host Exception During elastic query");
				System.out.println("Unknown Host Exception During elastic query");
				return e.toString();
			}
			catch (java.lang.IllegalArgumentException e) {
				logger.info("Illegal argument during elastic query");
				System.out.println("Illegal argument during elastic query");
				return e.toString();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return e.toString();
			}
	 		
	 	}
	 	
		public static void main(String[] args) 
		{
			String projectID = "PROJECT-cea59ec3-7f4a-4619-8577-17bbeb9f1b1c";
			String ownerID = "_18_5_1_40a019f_1498057623506_316834_18928";
			String token = "TICKET_7214d2fd8e9c2c09268da0a3ded72401ef98079a";
			String server = "opencae-int.jpl.nasa.gov";
			
//			String projectID = "PROJECT-cea59ec3-7f4a-4619-8577-17bbeb9f1b1c";
//			String ownerID = "jobs_bin_PROJECT-cea59ec3-7f4a-4619-8577-17bbeb9f1b1c";
			String refID = "master";
			String jobName = "testJob";
			String associatedElementID = "ASCELEMENT_123";
			String type = "docgen";
			String schedule = "* * * *";
			String targetID = "_18_5_1_40a019f_1499898145957_571809_17594";
			String buildNumber = "23";
			String jobStatus = "completed";
			String logUrl = "http://log.com";
			String started = "3:30pm";
			String completed = "4:00pm";
			
			MMSUtil mmsUtil = new MMSUtil(token);

			String sysmlID = mmsUtil.createId();
			
			String jobID = "_18_5_1_40a019f_1499898367904_56649_17676";
//			ObjectMapper mapper = new ObjectMapper();
//			try {
//				String jobJSON = mmsUtil.getJobElement(server, projectID, refID, jobID).getBody();
//				JsonNode fullJson = mapper.readTree(jobJSON).get("jobs").get(0);
//				if(fullJson!=null)
//				{
//					System.out.println(fullJson.get("associatedElementID"));
//					System.out.println(fullJson.get("command"));
//					System.out.println(fullJson.get("schedule").toString());
//				}
//
//			} catch (JsonProcessingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		
//			System.out.println(mmsUtil.getJobInstanceElements(server, projectID, refID, "PMA_1500329733457_0b985264-a4c6-448d-ba69-1da41efa92e0").getBody());
			System.out.println(mmsUtil.getJobInstanceElement(server, projectID, refID, "PMA_1500330591329_fe26ff4d-44a5-47aa-8ad6-5187e771a48a", "PMA_1500329733457_0b985264-a4c6-448d-ba69-1da41efa92e0"));
			
//			System.out.println(mmsUtil.get(server, projectID, refID, "PMA_1500329936596_f65fd93d-2b8a-4fa2-882f-759b5e0654a2", true));
//			System.out.println(mmsUtil.getDefiningFeatureID(server, projectID, refID, "_18_5_1_40a019f_1499898367904_56649_17676", "projectId"));
			
//			ObjectNode on2 = mmsUtil.buildDocgenJobElementJSON(sysmlID, ownerID, jobName, associatedElementID, type, schedule, refID, projectID);
//			System.out.println(on2.toString());
//			mmsUtil.post(server, projectID,refID, on2);
			
//			ObjectNode on3 = mmsUtil.buildDocGenJobInstanceJSON(sysmlID, ownerID, "test job instance", buildNumber, jobStatus, server, projectID, refID,"");
//			System.out.println("ON3: "+on3);
//			mmsUtil.post(server, projectID,refID, on3);
			
//			String elementID = "PMA_1491324925592";
//			String buildNumber = "55";
//			String propertyName = "jobStatus";
//			String newPropertyValue = "completed";
//			
//			System.out.println(mmsUtil.get(server, projectID, refID, elementID, true));
////			System.out.println(mmsUtil.modifyPartPropertyValue(server, projectID, refID, elementID, buildNumber, propertyName, newPropertyValue, token));
			
		}
	 
}
