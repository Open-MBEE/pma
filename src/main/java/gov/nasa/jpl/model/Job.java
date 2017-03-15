package gov.nasa.jpl.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Job implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String buildAgent;
	private String documentID;
	private String mmsServer;
	private String teamworkProject;
	private String jobID;
	private String jobName;


	public String getBuildAgent() {
		return buildAgent;
	}

	public void setBuildAgent(String buildAgent) {
		this.buildAgent = buildAgent;
	}
	public String getDocumentID() {
		return documentID;
	}

	public void setDocumentID(String documentID) {
		this.documentID = documentID;
	}
	
	public String getMmsServer() {
		return mmsServer;
	}

	public void setMmsServer(String mmsServer) {
		this.mmsServer = mmsServer;
	}
	public String getTeamworkProject() {
		return teamworkProject;
	}

	public void setTeamworkProject(String teamworkProject) {
		this.teamworkProject = teamworkProject;
	}
	public String getJobID() {
		return jobID;
	}

	public void setJobID(String jobID) {
		this.jobID = jobID;
	}
	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

}
