package gov.nasa.jpl.model;

import java.io.Serializable;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class JobInstance implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private String[] arguments;
	private String mmsServer;
	private String alfrescoToken;
	
	public String[] getArguments() {
		return arguments;
	}

	public void setArguments(String[] arguments) {
		this.arguments = arguments;
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
