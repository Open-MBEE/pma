package gov.nasa.jpl.model;

import java.io.Serializable;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * For handling job creation JSON recieved from VE.
 * @author hang
 *
 */
@Entity
public class JobFromClient implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private String jobName;
	private String type;
	private String schedule;
	private String associatedElementID;
	private String mmsServer;
	private String alfrescoToken;
	private String fromRefId;
	
	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getFromRefId() {
		return fromRefId;
	}

	public void setFromRefId(String fromRefId) {
		this.fromRefId = fromRefId;
	}
	
	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}
	
	public String getAssociatedElementID() {
		return associatedElementID;
	}

	public void setAssociatedElementID(String associatedElementID) {
		this.associatedElementID = associatedElementID;
	}
	
	public String getMmsServer() {
		return mmsServer;
	}

	public void setMmsServer(String mmsServer) {
		this.mmsServer = mmsServer;
	}
	
	public String getAlfrescoToken() {
		return alfrescoToken;
	}

	public void setAlfrescoToken(String alfrescoToken) {
		this.alfrescoToken = alfrescoToken;
	}




}
