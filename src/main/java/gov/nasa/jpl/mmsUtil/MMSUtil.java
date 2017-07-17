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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
		packageElement.put("_appliedStereotypeIds", mapper.createArrayNode());
		packageElement.put("documentation", "");
		packageElement.put("_isSite", Boolean.FALSE);
		packageElement.put("type", "Package");
		packageElement.put("id", id);
		packageElement.put("mdExtensionsIds", mapper.createArrayNode());
		packageElement.put("ownerId", ownerID);
		packageElement.put("syncElementId", nullNode);
		packageElement.put("appliedStereotypeInstanceId", nullNode);
		packageElement.put("clientDependencyIds", mapper.createArrayNode());
		packageElement.put("supplierDependencyIds", mapper.createArrayNode());
		packageElement.put("name", "Jobs Bin");
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
		classElement.put("_appliedStereotypeIds", mapper.createArrayNode().add("_11_5EAPbeta_be00301_1147424179914_458922_958"));
		classElement.put("documentation", "");
		classElement.put("type", "Class");
		classElement.put("id", id);
		classElement.put("mdExtensionsIds", mapper.createArrayNode());
		classElement.put("ownerId", ownerID);
		classElement.put("syncElementId", nullNode);		
		classElement.put("appliedStereotypeInstanceId", id+"_asi");
		classElement.put("clientDependencyIds", mapper.createArrayNode());
		classElement.put("supplierDependencyIds", mapper.createArrayNode());
		classElement.put("name", name);
		classElement.put("nameExpression", nullNode);
		classElement.put("visibility", nullNode);
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
		
		elements.add(classElement);
		
		ObjectNode instanceSpecificationElement = mapper.createObjectNode();
		
		instanceSpecificationElement.put("_appliedStereotypeIds",mapper.createArrayNode());
		instanceSpecificationElement.put("documentation", "");
		instanceSpecificationElement.put("type", "InstanceSpecification");
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
		instanceSpecificationElement.put("classifierIds", mapper.createArrayNode().add("_11_5EAPbeta_be00301_1147424179914_458922_958"));
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
		
		instanceSpecificationElement.put("_appliedStereotypeIds",mapper.createArrayNode());
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
		
		instanceSpecificationElement.put("mdExtensionsIds", mapper.createArrayNode());
		instanceSpecificationElement.put("ownerId", ownerID);
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
		instanceSpecificationElement.put("classifierIds", mapper.createArrayNode().add(classifierID));
		
		if(stereotypesElement)
		{
			instanceSpecificationElement.put("stereotypedElementId", ownerID);
		}
		else
		{
			instanceSpecificationElement.put("stereotypedElementId", nullNode);
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
		slotElement.put("_appliedStereotypeIds",mapper.createArrayNode());
		slotElement.put("documentation", "");
		slotElement.put("type", "Slot");
		slotElement.put("id", ownerID+"-slot-"+definingFeatureId);
		slotElement.put("mdExtensionsIds", mapper.createArrayNode());
		
		slotElement.put("ownerId", ownerID);
		slotElement.put("syncElementId", nullNode);
		slotElement.put("appliedStereotypeInstanceId", nullNode);
		
		ArrayNode valueNode = mapper.createArrayNode();
		ObjectNode nestedValue = mapper.createObjectNode();
		nestedValue.put("_appliedStereotypeIds", mapper.createArrayNode());
		nestedValue.put("documentation", "");
		nestedValue.put("type", "LiteralString");
		nestedValue.put("id",ownerID+"-slot-"+definingFeatureId+"-slotvalue-0-literalstring");
		nestedValue.put("mdExtensionsIds", mapper.createArrayNode());
		nestedValue.put("ownerId",slotElementID);
		nestedValue.put("syncElementId", nullNode);
		nestedValue.put("appliedStereotypeInstanceId", nullNode);
		nestedValue.put("clientDependencyIds", mapper.createArrayNode());
		nestedValue.put("supplierDependencyIds", mapper.createArrayNode());
		nestedValue.put("name", "");
		nestedValue.put("nameExpression", nullNode);
		nestedValue.put("visibility", nullNode);
		nestedValue.put("templateParameterId", nullNode);
		nestedValue.put("typeId", nullNode);
		nestedValue.put("value", value);
		
		valueNode.add(nestedValue);
		
		slotElement.put("value", valueNode);
		
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
		
		classElement.put("_appliedStereotypeIds", mapper.createArrayNode().add("_12_0_be00301_1164123483951_695645_2041"));
		classElement.put("documentation", "");
		classElement.put("type", "Property");
		classElement.put("id", propertyID);
		classElement.put("mdExtensionsIds", mapper.createArrayNode());
		classElement.put("ownerId", ownerID);
		classElement.put("syncElementId", nullNode);
		classElement.put("appliedStereotypeInstanceId", propertyID+"_asi");
		classElement.put("clientDependencyIds", mapper.createArrayNode());
		classElement.put("supplierDependencyIds", mapper.createArrayNode());
		classElement.put("name", name);
		classElement.put("nameExpression", nullNode);
		classElement.put("visibility", nullNode);
		classElement.put("isLeaf", Boolean.FALSE);
		classElement.put("isStatic", Boolean.FALSE);
		classElement.put("typeId", "_16_5_1_12c903cb_1245415335546_479030_4092");
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
		
		if (value != null) 
		{
			ObjectNode defaultValue = mapper.createObjectNode(); // value element

			defaultValue.put("_appliedStereotypeIds", mapper.createArrayNode());
			defaultValue.put("documentation", "");
			defaultValue.put("type", "LiteralString");
			defaultValue.put("id", createId());
			defaultValue.put("mdExtensionsIds", mapper.createArrayNode());
			defaultValue.put("ownerId", propertyID);
			defaultValue.put("syncElementId", nullNode);
			defaultValue.put("appliedStereotypeInstanceId", nullNode);
			defaultValue.put("clientDependencyIds", mapper.createArrayNode());
			defaultValue.put("supplierDependencyIds", mapper.createArrayNode());
			defaultValue.put("name", "");
			defaultValue.put("nameExpression", nullNode);
			defaultValue.put("visibility", "public");
			defaultValue.put("templateParameterId", nullNode);
			defaultValue.put("typeId", nullNode);
			defaultValue.put("value", value);

			classElement.put("defaultValue", defaultValue);
		}
		else
		{
			classElement.put("defaultValue", nullNode);
		}
		
		
		classElement.put("interfaceId", nullNode);
		classElement.put("isDerived", Boolean.FALSE);
		classElement.put("isDerivedUnion", Boolean.FALSE);
		classElement.put("isID", Boolean.FALSE);
		
		if(redefinedPropertyId!=null)
		{
			classElement.put("redefinedPropertyIds", mapper.createArrayNode().add(redefinedPropertyId));
		}
		else
		{
			classElement.put("redefinedPropertyIds", mapper.createArrayNode());
		}
		classElement.put("subsettedPropertyIds", mapper.createArrayNode());
		classElement.put("associationId", nullNode);
		
		return classElement;
	}
	
	public ObjectNode buildGeneralizationNode(String ownerID,String sourceID,String targetID)
	{
		String generalizationID = createId();
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode generalization = mapper.createObjectNode();
		ObjectNode nullNode = null;
		generalization.put("_appliedStereotypeIds", mapper.createArrayNode());
		generalization.put("documentation", "");
		generalization.put("type", "Generalization");
		generalization.put("id", generalizationID);
		generalization.put("mdExtensionsIds", mapper.createArrayNode());
		generalization.put("ownerId", ownerID);
		generalization.put("syncElementId", nullNode);
		generalization.put("appliedStereotypeInstanceId", nullNode);
		generalization.put("_sourceIds", mapper.createArrayNode().add(sourceID));
		generalization.put("_targetIds", mapper.createArrayNode().add(targetID));
		generalization.put("generalId", targetID);
		generalization.put("generalizationSetIds", mapper.createArrayNode());
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
		
		/*
		 * Hardcoded element ID's from sysml extensions
		 */
		String docgenJobBlockID = "_18_5_1_40a019f_1499898145957_571809_17594";
		String typePropertyID = "_18_5_1_40a019f_1499898145958_112751_17595";
		String schedulePropertyID = "_18_5_1_40a019f_1499898145959_694022_17596";
		String buildNumberPropertyID = "_18_5_1_40a019f_1499898145959_884244_17597";
		String jobStatusID = "_18_5_1_40a019f_1499898145959_630987_17598";
		String logUrlPropertyID = "_18_5_1_40a019f_1499898145959_58347_17599";
		String startedPropertyID = "_18_5_1_40a019f_1499898145959_366173_17600";
		String completedPropertyID = "_18_5_1_40a019f_1499898145959_892705_17601";
		String associatedElementIdPropertyID = "_18_5_1_40a019f_1499898210580_818758_17660";
		String refIdPropertyID = "_18_5_1_40a019f_1499898283219_617170_17669";
		String projectIdPropertyID = "_18_5_1_40a019f_1499898288594_457150_17672";
		String valuePropertyStereotypeID = "_12_0_be00301_1164123483951_695645_2041";
		
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
		ObjectNode refIdPropertyNode = buildPropertyNode(sysmlID,"refId",refID,refIdPropertyID);
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
				
		jobClass.put("ownedAttributeIds",ownedAttributes);
		jobClass.put("generalizationIds",mapper.createArrayNode().add(generalizationNode.get("id")));

		elements.set(0, jobClass);
		
		payload.put("elements",elements);
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
		
		try {
			String jobJSON = getJobElement(server, projectID, refID, jobID).getBody();
			JsonNode fullJson = mapper.readTree(jobJSON).get("jobs").get(0);
			if(fullJson!=null)
			{
				String scheduleValue = fullJson.get("schedule").toString();
				String typeValue = fullJson.get("command").toString();
				String associatedElementIDValue = fullJson.get("associatedElementID").toString();
				
				if(scheduleValue!=null)
				{
					schedule = scheduleValue.replace("\"", "");
				}
				if(typeValue!=null)
				{
					type = typeValue.replace("\"", "");
				}
				if(associatedElementIDValue!=null)
				{
					associatedElementID = associatedElementIDValue.replace("\"", "");
				}
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
		ObjectNode instanceSpecificationNode = buildInstanceSpecificationNode(ownerID, jobID,name, false);
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
		
		instanceSpecificationNode.put("slotIds",slotIds);
		
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

		
		payload.put("elements",elements);
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
	public String modifyPartPropertyValue(String server,String projectID,String refID,String elementID,String buildNumber,String propertyName,String newPropertyValue,String token , String jobId)
	{
		
		// finding the part property
		MMSUtil mmsUtil = new MMSUtil(token);
		
		String jsonString = mmsUtil.get(server, projectID,refID, elementID,true);
//		System.out.println("Modify Part Property JSON String: "+jsonString);
		logger.info("Modify Part Property JSON String: "+jsonString);
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
						jobInstanceId = element.get("ownerId").toString().replace("\"", "");
					}
				}
				ObjectNode propertyElement = null;
				for (JsonNode element : elements) {
					/*
					 * Find the property element that contains the value to be replaced.
					 */
					if((element.get("type").toString().equals("\"Property\""))&&(element.get("ownerId").toString().replace("\"", "").equals(jobInstanceId))&&(element.get("name").toString().equals("\""+propertyName+"\"")))
					{
//						System.out.println("Found: "+propertyName);
//						System.out.println("Value: "+element.get("defaultValue").get("value").toString());
						propertyElement = (ObjectNode) element;
						logger.info("Found: "+propertyName);
						logger.info("Value: "+element.get("defaultValue").get("value").toString());
					}
				}
				if(propertyElement!=null) // will be null if the property element isn't found
				{
					/*
					 * Replace the value in the json object
					 */
//					System.out.println("Before: "+propertyElement);
					ObjectNode propertyElementValue = (ObjectNode) propertyElement.get("defaultValue");
					propertyElementValue.put("value", newPropertyValue);
					propertyElement.put("defaultValue", propertyElementValue);
					
					// puts the new json object in an elements array that will be sent to mms
					ObjectNode payload = mapper.createObjectNode();
					ArrayNode arrayElements = mapper.createArrayNode();
					arrayElements.add(propertyElement);
					payload.put("elements",arrayElements);
					payload.put("source","pma");
					
					// send element to MMS
//					System.out.println("Payload: "+payload);
					String response = mmsUtil.post(server, projectID, refID, payload); // sending element to MMS . Expecting 200 OK response
//					System.out.println("Response: "+response);
					
					/*
					 * Sending jms messsage with job instance object
					 */
		    		if (response.equals("HTTP/1.1 200 OK"))
		    		{
				    	try
				    	{
				    		// Retrieving job instance values 
				    		Map<String,String> jobInstanceValues = new HashMap<String,String>();
							for (JsonNode element : elements) {
								/*
								 * Find property elements
								 */
								if((element.get("type").toString().equals("\"Property\""))&&(element.get("ownerId").toString().replace("\"", "").equals(jobInstanceId)))
								{
									String elementName = element.get("name").toString().replace("\"", "");
									String elementValue = element.get("defaultValue").get("value").toString().replace("\"", "");
									jobInstanceValues.put(elementName, elementValue);
									System.out.println("Found: "+elementName);
									System.out.println("Value: "+elementValue);
									logger.info("Found: "+elementName);
									logger.info("Value: "+elementValue);
								}
							}
							jobInstanceValues.put(propertyName, newPropertyValue); // overwrites the old value
				    		
							// build job instance element json to be sent
						 	JSONObject jobInstanceJSON = new JSONObject();			
					    	
					    	for (Map.Entry entry : jobInstanceValues.entrySet()) {
					    		jobInstanceJSON.put((String) entry.getKey(), entry.getValue());
					    		System.out.println("key: "+entry.getKey() + " value: "+entry.getValue());
					    	}
					    	
						 	jobInstanceJSON.put("id", jobInstanceId);
					    	jobInstanceJSON.put("jobId", jobId);
					    	
					    	// Sending job instance element to jms.
					    	JmsConnection jmc = new JmsConnection();
					    	String jmsSettings = MMSUtil.getJMSSettings(server);
					    	JSONObject connectionJson = new JSONObject(jmsSettings);
					    	jmc.ingestJson(connectionJson);
					    	
					    	JSONObject jmsJSON = new JSONObject();	
					    	jmsJSON.put("updatedJobs", jobInstanceJSON);
					    	jmc.publish(jmsJSON, jmc.TYPE_DELTA, refID, projectID);
					    	logger.info("Sent JMS json: "+jmsJSON.toString());
					    	System.out.println("Sent JMS json: "+jmsJSON.toString());
				    	}
				    	catch(JSONException e)
				    	{
				    		e.printStackTrace();
				    		logger.info(e.toString());
				    	}
		    		}
					return response;
				}
				else 
				{
					
					// Creating job instance for the job run because it doesn't currently exist.
					if(propertyName.equals("jobStatus")) // creates the job instance
					{
			    		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			          	String jobInstanceElementID = createId();
			          	
			          	/**
			          	 * TODO update the creation of job instance
			          	 */
			    		ObjectNode on = mmsUtil.buildDocGenJobInstanceJSON(jobInstanceElementID, "jobs_bin_"+projectID, elementID+"_instance_"+timestamp.getTime(),buildNumber,newPropertyValue, server, projectID, refID,elementID); //job folder will be the owner of the instance element
			    		String elementCreationResponse = mmsUtil.post(server, projectID, refID, on);
			    		
			    		System.out.println(elementCreationResponse);
				    	
						/*
						 * Sending jms messsage with job instance object
						 */
			    		if (elementCreationResponse.equals("HTTP/1.1 200 OK"))
			    		{
				    		try
					    	{
					    		/*
					    		 * When the job instance is first created, it will have these values by default. 
					    		 * Couldn't retrieve the job instance part property values from MMS, since the job instance was just created a couple lines above. 
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
						    	jobInstanceJSON.put("jobStatus", newPropertyValue);
						    	jobInstanceJSON.put("jenkinsLog", "");
						    	jobInstanceJSON.put("created", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date())); //ex. 2017-06-08T13:37:19.483-0700);
						    	jobInstanceJSON.put("completed", "");
						    	
						    	JSONObject jmsJSON = new JSONObject();	
						    	jmsJSON.put("updatedJobs", jobInstanceJSON);
						    	
						    	jmc.publish(jobInstanceJSON, jmc.TYPE_DELTA, refID, projectID);
						    	logger.info("Sent JMS json: "+jobInstanceJSON.toString());
						    	System.out.println("Sent JMS json: "+jobInstanceJSON.toString());
					    	}
					    	catch(JSONException e)
					    	{
					    		e.printStackTrace();
					    		logger.info(e.toString());
					    	}
			    		}
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
	public ResponseEntity<String> getJobElements(String server,String projectID,String refID)
	{
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		String returnJSONString = "";
		// find all elements inside the jobs bin package
		// recursive get job sysmlid
		String jsonString = get(server, projectID,refID, "jobs_bin_"+projectID, true);
		
//		System.out.println("Get job elements string: "+jsonString);
		
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
		
		String jsonString = get(server, project,refID, jobInstanceElementID, true);
		
		PMAUtil pmaUtil = new PMAUtil();
		
		return pmaUtil.generateJobInstanceArrayJSON(jsonString,jobSysmlID);
	}
	
	public ResponseEntity<String> getJobInstanceElements(String server, String project, String refID, String jobElementID)
	{
		
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		String returnJSONString = "";
		
		String jsonString = get(server, project,refID, jobElementID, true); // recursive get job sysmlid
		
		PMAUtil pmaUtil = new PMAUtil();
		if(isElementJSON(jsonString)) // It will be an error if the json string is not an element JSON.
		{
			System.out.println("is element json");
			status = HttpStatus.OK;
			return new ResponseEntity<String>(pmaUtil.generateJobInstanceArrayJSON(jsonString,jobElementID),status);
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
//		return pmaUtil.generateJobInstanceArrayJSON(jsonString);
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
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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


	 
		public static void main(String[] args) 
		{
			String projectID = "PROJECT-58b59e19-35d0-46e1-acb7-97f974823b1c";
			String ownerID = "_18_5_1_40a019f_1498057623506_316834_18928";
			String token = "TICKET_24cf71d3b692def570fbd828ce94e897ab3a6dab";
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
		
			
//			System.out.println(mmsUtil.getDefiningFeatureID(server, projectID, refID, "_18_5_1_40a019f_1499898367904_56649_17676", "projectId"));
			
//			ObjectNode on2 = mmsUtil.buildDocgenJobElementJSON(sysmlID, ownerID, jobName, associatedElementID, type, schedule, refID, projectID);
//			System.out.println(on2.toString());
//			mmsUtil.post(server, projectID,refID, on2);
			
			ObjectNode on3 = mmsUtil.buildDocGenJobInstanceJSON(sysmlID, ownerID, "test job instance", buildNumber, jobStatus, server, projectID, refID,jobID);
			System.out.println(on3.toString());
			mmsUtil.post(server, projectID,refID, on3);
			
//			String elementID = "PMA_1491324925592";
//			String buildNumber = "55";
//			String propertyName = "jobStatus";
//			String newPropertyValue = "completed";
//			
//			System.out.println(mmsUtil.get(server, projectID, refID, elementID, true));
////			System.out.println(mmsUtil.modifyPartPropertyValue(server, projectID, refID, elementID, buildNumber, propertyName, newPropertyValue, token));
			
		}
	 
}
