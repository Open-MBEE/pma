package gov.nasa.jpl.jenkinsUtil;

/**
 * JenkinsEngine ----
 *
 * Implements the ExecutionEngine as a way to execute jobs (events) on the
 * Jenkins server.
 *
 * @author Dan Karlsson (dank), Tommy Hang (hang)
 * @date 3/20/17
 *
 */

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import gov.nasa.jpl.dbUtil.DBUtil;
import gov.nasa.jpl.pmaUtil.PMAUtil;

/**
 * Implements the ExecutionEngine as a way to execute jobs (events) on the
 * Jenkins server.
 * <p>
 * Example jenkins api queries:
 * <ul>
 * <li>https://some-jenkins-server.someorganization.com/job/
 * MMS_1460067117709_b5f26105-8581-406e-b54d-8525012044c5/lastBuild/api/json?
 * pretty=true
 * <li>https://some-jenkins-server.someorganization.com/job/
 * MMS_1460074360091_e6271b6a-0bb6-46d3-8283-0c2aaa7d1866/lastBuild/api/json?
 * tree=building,result&pretty=true
 * <li>https://some-jenkins-server.someorganization.com/api/json?tree=jobs[name,
 * description,color,url,lastCompletedBuild[duration,timestamp,estimatedDuration
 * ]]&pretty=true
 * </ul>
 *
 */
public class JenkinsEngine {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String username = ""; // User name to be used to connect to jenkins

	private String passwordOrToken = ""; // Token or password
											// that is associated
											// with the user name

	private String url = ""; // URL of the
								// Jenkins server
								// to execute the
								// job on
	private String jenkinsToken = "build"; // The build the token associated
											// with the build configuration on
											// the Jenkins server.
	public String jobName = ""; // Build name - the name of the job to be
								// executed on the Jenkins server.
	public String jenkinsApiURL = "/api/json?depth=";
	public int apiCallDepth = 1;
	public String executeUrl;
	public DefaultHttpClient jenkinsClient; //
	private long executionTime;
	public JSONObject jsonResponse; //
	public ArrayList<JSONObject> jenkinsQueue = new ArrayList<JSONObject>();
	public Map<String, String> detailResultMap;

	private BasicScheme basicAuth;
	private BasicHttpContext context;

	public enum detail {
		NAME, COLOR, URL, DURATION, EST_DURATION, TIMESTAMP, DESCRIPTION, LAST_SUCCESSFULL_BUILD, LAST_FAILED_BUILD, LAST_COMPLETED_BUILD, LAST_UNSUCCESFULL_BUILD, LAST_BUILD
	}

	private boolean DEBUG = false;

	/**
	 * This is the main constructor for using the JenkinsEngine interface.
	 */
	public JenkinsEngine() {

	}

	/**
	 * This method will create the initial connection to the server that is
	 * specified before calling 'new' on JenkinsEngine. It is required that the
	 * JenkinesEngine is initialized before attempting to make any queries to
	 * the Jenkins server because Jenkins will require any calls made to be
	 * authenticated before completing.
	 */

	public void login() {

		// Credentials
		String username = this.username;
		String password = this.passwordOrToken;
		String jenkinsUrl;

		jenkinsUrl = url + jenkinsApiURL + apiCallDepth;

		// Create your httpclient
		this.jenkinsClient = new DefaultHttpClient();

		// Then provide the right credentials
		this.jenkinsClient.getCredentialsProvider().setCredentials(
				new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
				new UsernamePasswordCredentials(username, password));

		// Generate BASIC scheme object and stick it to the execution
		// context
		this.basicAuth = new BasicScheme();
		this.context = new BasicHttpContext();

		this.context.setAttribute("preemptive-auth", basicAuth);

		// Add as the first (because of the zero) request interceptor
		// It will first intercept the request and preemptively
		// initialize the authentication scheme if there is not
		this.jenkinsClient.addRequestInterceptor(new PreemptiveAuth(), 0);

		// // You get request that will start the build
		// // Example for setting a build REST call:
		// // String getUrl = jenkinsUrl + "/job/" + jobName + "/build?token="
		// // + buildToken;
		// if (DEBUG) {
		//
		// String getUrl = jenkinsUrl;
		// System.out.println("The Build url is " + getUrl);
		// HttpGet get = new HttpGet(getUrl);
		//
		// try {
		// HttpResponse response = this.jenkinsClient.execute(get,
		// this.context);
		// HttpEntity entity = response.getEntity();
		// String retSrc = EntityUtils.toString(entity);
		// jsonResponse = new JSONObject(retSrc);
		// System.out.println("Content of the JSON Object is " +
		// jsonResponse.toString());
		// EntityUtils.consume(entity);
		// } catch (IOException e) {
		// e.printStackTrace();
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
	}

	/**
	 * Preemptive authentication intercepter
	 *
	 */
	static class PreemptiveAuth implements HttpRequestInterceptor {

		/*
		 * @see org.apache.http.HttpRequestInterceptor#process(org.apache.
		 * http.HttpRequest, org.apache.http.protocol.HttpContext)
		 */
		public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
			// Get the AuthState
			AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);

			// If no auth scheme available yet, try to initialize it
			// preemptively
			if (authState.getAuthScheme() == null) {
				AuthScheme authScheme = (AuthScheme) context.getAttribute("preemptive-auth");
				CredentialsProvider credsProvider = (CredentialsProvider) context
						.getAttribute(ClientContext.CREDS_PROVIDER);
				HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
				if (authScheme != null) {
					Credentials creds = credsProvider
							.getCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()));
					if (creds == null) {
						throw new HttpException("No credentials for preemptive authentication");
					}
					authState.setAuthScheme(authScheme);
					authState.setCredentials(creds);
				}
			}

		}

	}

	/**
	 * Sets the username to be used with the connection on Jenkins
	 *
	 * @param name
	 */
	public void setUsername(String name) {
		this.username = name;
	}

	/**
	 * Sets the password that is associated with the username that will be
	 * connected with Jenkins
	 *
	 * @param pass
	 */
	public void setPassword(String pass) {
		this.passwordOrToken = pass;
	}

	/**
	 * This method will set the job name
	 *
	 * @param job
	 */
	public void setJobName(String job) {
		this.jobName = job;
	}

	/**
	 * This method will set the jenkins URL
	 *
	 * @param job
	 */
	public void setURL(String url) {
		this.url = url;
	}

	/**
	 * This method is used to set the token that is required when attempting to
	 * execute a build on the jenkins server.
	 *
	 * @param token
	 */
	public void setJobToken(String token) {
		this.jenkinsToken = token;
	}

	/**
	 * Creates an instance of the Jenkins Engine
	 */
	public JenkinsEngine createEngine() {
		JenkinsEngine instance = new JenkinsEngine();
		return instance;
	}

	public String execute() {
		// This sets the URL to an Object specifically for making GET calls
		HttpGet get = new HttpGet(this.executeUrl);
		String entityString = null;

		try {
			// This will tell the Jenkins HTTP Client to execute the GET
			// call with the context that was set during the instantiation
			// of the Jenkins HttpClient.
			HttpResponse response = jenkinsClient.execute(get, this.context);

			// Takes the HttpResponse and turns it into an Entity that can
			// be manipulated into a string.
			HttpEntity entity = response.getEntity();
			entityString = EntityUtils.toString(entity);

			// this means there is no proper response... comes in as HTML?
			// returning will prevent json errors
			if (entityString.contains("<html>")) {
				System.out.println("Error " + response.getStatusLine().toString());
				return response.getStatusLine().toString();
			}

			// Converts the HttpEntity String from the response of the GET
			// call into a JSON object then consumes the entity to close the
			// connection.

			if ((entityString != null && !entityString.isEmpty())) {
				jsonResponse = new JSONObject(entityString);
			}

			// Will throw an error if the execution fails from either incorrect
			// setup or if the jenkinsClient has not been instantiated.
		} 
		catch(java.lang.IllegalStateException e)
		{
			if(e.toString().equals("java.lang.IllegalStateException: Target host is null"))
			{
				return "Jenkins url in configuration invalid.";
			}
			return e.toString();
		}
		catch(java.net.UnknownHostException e)
		{
			return e.toString();
		}
		catch (IOException e) {
			System.out.println(
					"JenkinsEngine.execute(): response \"" + entityString + "\" failed to parse as a JSONObject");
			System.out.println(e.toString());
			return e.toString();
//			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			System.out.println(e.toString());
			return e.toString();
		}

		return null;
	}

	/**
	 * Sends a post call with the executeUrl
	 * @return response from post
	 */
	public String build() {
		// This sets the URL to an Object specifically for making GET calls
		HttpPost post = new HttpPost(this.executeUrl);
		System.out.println("Execute URL: "+this.executeUrl);
		try {
			HttpResponse response = this.jenkinsClient.execute(post, this.context);

			EntityUtils.consume(response.getEntity());
			// Will throw an error if the execution fails from either incorrect
			// setup or if the jenkinsClient has not been instantiated.
			System.out.println("Response to string: "+response.toString());
			return response.getStatusLine().toString();
		} catch (IOException e) {
			return e.toString();
		}

	}

	// calling this will close the current HTTP connection
	// if it is closed, you would need to start up authentication again
	// or you would need to create a new JenkinsEngine instance
	public void closeConn(HttpEntity entity) throws IOException {
		EntityUtils.consume(entity);
	}

	/**
	 * This method is used to find the job that the user specifies within
	 * <b>jobName</b> and specifying which detail they would like from the job.
	 * <b>detailName</b> These are the parameters it accepts:
	 * <ul>
	 * <li>name
	 * <li>url
	 * <li>failed
	 * <li>successful
	 * <li>unsuccessful
	 * <li>stable
	 * <li>unstable
	 * </ul>
	 *
	 * @param String
	 *            jobName, String detailName
	 * @return Event details in a string form
	 * @Override
	 */
	public String getEventDetail(String jobName, String detailName) {
		String returnString = null;

		if (!detailName.isEmpty() && jsonResponse != null) {
			try {

			} catch (Exception e) {

			}
		}

		return returnString;
	}

	public String getMagicDrawLogFromJob(String jobId) {
		String url;

		if (!jobId.startsWith("/")) {
			jobId = "/" + jobId;
		}
		url = "/job" + jobId;

		if (!url.endsWith("/")) {
			url = url + "/";
		}

		url = this.url + url + "lastBuild/artifact/MDNotificationWindowText.html";
		return url;
	}

	/**
	 * Private method for constructing urls to be executed on Jenkins.
	 *
	 * Allowed Arguments for Detail Property:
	 * <ul>
	 * <li>NAME
	 * <li>URL
	 * <li>COLOR
	 * <li>LAST_COMPLETED_BUILD
	 * <li>LAST_FAILED_BUILD
	 * <li>LAST_SUCCESSFULL_BUILD
	 * <li>LAST_UNSUCCESFULL_BUILD
	 * <li>DESCRIPTION
	 * <li>LAST_BUILD
	 * </ul>
	 *
	 * @param jobUrl
	 * @param property
	 */
	public void constructJobUrl(detail property) {
		String url;

		url = "/api/json?tree=jobs";

		switch (property) {
		case NAME:
			url = url + "[name]";
			break;
		case URL:
			url = url + "[url]";
			break;
		case COLOR:
			url = url + "[color]";
			break;
		case LAST_COMPLETED_BUILD:
			url = url + "[lastCompletedBuild]";
			break;
		case LAST_FAILED_BUILD:
			url = url + "[lastFailedBuild]";
			break;
		case LAST_SUCCESSFULL_BUILD:
			url = url + "[lastSuccessfullBuild]";
			break;
		case LAST_UNSUCCESFULL_BUILD:
			url = url + "[lastUnsuccesfullBuild]";
			break;
		case DESCRIPTION:
			url = url + "[description]";
			break;
		case LAST_BUILD:
			url = url + "[lastBuild]";
		default:
			break;
		}
		this.executeUrl = this.url + url;
		System.out.println("Execution url is " + this.executeUrl);
	}

	public void constructBuildUrl(String jobUrl, detail property) {

		String url;

		if (!jobUrl.startsWith("/")) {
			jobUrl = "/" + jobUrl;
		}
		url = "/job" + jobUrl;

		if (!url.endsWith("/")) {
			url = url + "/";
		}

		url = url + "api/json?tree=";

		System.out.println("Current constuction url is " + url);

		switch (property) {
		case NAME:
			url = url + "displayName";
			break;
		case URL:
			url = url + "url";
			break;
		case DURATION:
			url = url + "lastCompletedBuild[duration]";
			break;
		case EST_DURATION:
			url = url + "lastCompletedBuild[estimatedDuration]";
			break;
		case TIMESTAMP:
			url = url + "lastCompletedBuild[timestamp]";
			break;
		case DESCRIPTION:
			url = url + "description";
			break;
		default:
			url = "";
		}
		this.executeUrl = this.url + url;
		System.out.println("Execution url is " + this.executeUrl);
	}

	/**
	 * Posting a config xml to jenkins to create or update a job
	 * @param config JenkinsBuildConfig object containing xml of job to be created
	 * @param jobName Name of Jenkins Job
	 * @param newConfig When creating a new job, newConfig will be true. If updating a job, then newConfig is false.
	 * @return
	 */
	public String postConfigXml(JenkinsBuildConfig config, String jobName, boolean newConfig)
	{
		return postNestedJobConfigXml(config, jobName,"","", newConfig);
	}
	
	/**
	 * Posting a config xml to jenkins to create or update a job
	 * @param config JenkinsBuildConfig object containing xml of job to be created
	 * @param jobName Name of Jenkins Job
	 * @param projectID ID of the magicdraw project, should also be a folder on Jenkins
	 * @param refID ID of the mms branch, should also be a folder on Jenkins
	 * @param newConfig When creating a new job, newConfig will be true. If updating a job, then newConfig is false.
	 * @return
	 */
	public String postNestedJobConfigXml(JenkinsBuildConfig config, String jobName,String projectID,String refID, boolean newConfig) {
		String postUrl = null;
		
		String nestedLocation = "";
		
		if((!projectID.equals(""))&&(!refID.equals("")))
		{
			nestedLocation = "job/"+projectID+"/job/"+refID+"/";	
		}
		
		if (newConfig) {
			postUrl = this.url + "/job/PMA/"+nestedLocation+"createItem?name=" + jobName; // Jenkins 2
		} else {
			postUrl = this.url + "/job/PMA/"+nestedLocation+"job/" + jobName + "/config.xml"; // Jenkins 2
		}

		String configFile = generateConfigXML(config);

		if (configFile == null) {
			System.out.println("FAILED TO CREATE JOB: " + jobName);
			return "Failed to create job. Config file is null";
		}

		try {
			HttpEntity xmlEntity = (HttpEntity) new StringEntity(configFile);

			HttpPost post = new HttpPost(postUrl);
			post.setHeader("Content-Type", "application/xml");
			post.setEntity(xmlEntity);
			HttpResponse response = this.jenkinsClient.execute(post, this.context);
			System.out.println("Response: " + response);
			EntityUtils.consume(response.getEntity());
			return response.getStatusLine().toString();
		} catch (Exception e) {
//			e.printStackTrace();
			logger.error(e.toString()); // job element not created on mms. 
			System.out.println(e.toString());
			return(e.toString());
		}
	}

	/**
	 * Gets job information from Jenkins.
	 * 
	 * @param jobName
	 *            Name of job
	 * @return Returns a JSON object from Jenkins with color of last job run.
	 *         Ex. {"color":"red","name":"PMA_1490223990977"}
	 */
	public String getJob(String jobName) 
	{
		return getNestedJob(jobName,"");
	}

	/**
	 * Gets job information from Jenkins.
	 * 
	 * @param jobName Name of job
	 * @param jobParentFolderName Name of folder inside the pma folder in Jenkins
	 * @return Returns a JSON object from Jenkins with color of last job run.
	 *         Ex. {"color":"red","name":"PMA_1490223990977"}
	 */
	public String getNestedJob(String jobName,String jobParentFolderName) 
	{
		JSONObject json = null;
		System.out.println("jenkins get job");
		String allJobResponse = getAllJobs(jobParentFolderName);
//		System.out.println("ALL JOB RESPONSE: "+allJobResponse);
		if (allJobResponse != null&&allJobResponse.startsWith("{")) {
			try {
				JSONObject allJobs = new JSONObject(allJobResponse);

				JSONArray jobs = allJobs.optJSONArray("jobs");
				if (jobs == null || jobs.length() <= 0)
					return "Job not found on Jenkins";
				for (int i = 0; i < jobs.length(); ++i) {
					JSONObject job = jobs.optJSONObject(i);
					if (job == null)
						continue;
					String name = job.optString("name");
					if ((name != null && !name.isEmpty()) && name.equals(jobName)) {
						json = job;
						break;
					}
				}
				if (json != null) {
					return json.toString();
				}
				else
				{
					return "Job not found on Jenkins";
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				 e.printStackTrace();
				logger.error("Jenkins error: "+e.toString()); // job not found on jenkins
				System.out.println("Jenkins error:: "+e.toString());
//				return e.toString();
			}
		}
		return allJobResponse;

	}
	
	/**
	 * Gets a list of all jobs in the PMA folder on Jenkins.
	 * @param jobParentFolderName Name of folder inside the pma folder on Jenkins 
	 * 
	 * @return
	 */
	public String getAllJobs(String jobParentFolderName) 
	{
		String nestedPMAFolder = "";
		if(!jobParentFolderName.equals(""))
		{
			nestedPMAFolder=jobParentFolderName+"/";
		}
		String url = this.url + "/job/PMA/job/"+nestedPMAFolder+"api/json?tree=jobs[name,color]"; // Jenkins 2

//		System.out.println("Current construction url is " + url);
		this.executeUrl = url;
//		System.out.println("Execution url is " + this.executeUrl);
		
//		System.out.println("before execute");
		String allJobs = execute(); // execute returns not null when theres an error
//		System.out.println("all jobs inside getAllJobs");
		if (allJobs != null) {
			return allJobs;
		}
		if(jsonResponse==null)
		{
			return null;
		}
		return jsonResponse.toString();
	}

	public String generateConfigXML(JenkinsBuildConfig config) {
		String xml = config.generateBaseConfigXML();

		if (xml != null)
			return xml;

		return null;
	}

	public String executeJob(String jobName) 
	{
		return executeNestedJob(jobName,"","");
	}
	public String executeNestedJob(String jobName,String projectID, String refID) 
	{
		String nestedLocation = "";
		
		if((!projectID.equals(""))&&(!refID.equals("")))
		{
			nestedLocation = projectID+"/job/"+refID+"/job/";	
		}
		try {
			this.setJobToken("build");
			this.executeUrl = this.url + "/job/PMA/job/"+nestedLocation+ jobName + "/build?token=" + this.jenkinsToken; // Jenkins 2
			System.out.println("Execute url: "+executeUrl);
			String response = this.build();
			return response;
		} catch (Exception e) {
			return e.toString();
		}
	}

	public String deleteJob(String jobName) 
	{
		return deleteNestedJob(jobName,"","");
	}
	public String deleteNestedJob(String jobName,String projectID,String refID)
	{
		String nestedLocation = "";
		
		if((!projectID.equals(""))&&(!refID.equals("")))
		{
			nestedLocation = projectID+"/job/"+refID+"/job/";	
		}
		
		try {
			this.executeUrl = this.url + "/job/PMA/job/"+nestedLocation + jobName + "/doDelete"; // Jenkins2
			System.out.println("Delete url: "+executeUrl);
			String response = this.build(); // Sends a post call 
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Disables a job on Jenkins
	 * @param jobName name of Job
	 * @param projectId MD Project Id
	 * @param refId branch Id
	 * @return
	 */
	public String disableNestedJob(String jobName,String projectId,String refId)
	{
		String nestedLocation = "";
		
		if((!projectId.equals(""))&&(!refId.equals("")))
		{
			nestedLocation = projectId+"/job/"+refId+"/job/";	
		}
		
		try {
			this.executeUrl = this.url + "/job/PMA/job/"+nestedLocation + jobName + "/disable"; // Jenkins2
			System.out.println("Disable url: "+executeUrl);
			String response = this.build();
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Enables a job on Jenkins
	 * @param jobName name of Job
	 * @param projectId MD Project Id
	 * @param refId branch Id
	 * @return
	 */
	public String enableNestedJob(String jobName,String projectId,String refId)
	{
		String nestedLocation = "";
		
		if((!projectId.equals(""))&&(!refId.equals("")))
		{
			nestedLocation = projectId+"/job/"+refId+"/job/";	
		}
		
		try {
			this.executeUrl = this.url + "/job/PMA/job/"+nestedLocation + jobName + "/enable"; // Jenkins2
			System.out.println("Disable url: "+executeUrl);
			String response = this.build();
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public JSONObject isJobInQueue(JSONObject jenkinsJobJson) {
		try {
			this.executeUrl = this.url + "/queue/api/json";
			execute();
		} catch (Exception e) {
			e.printStackTrace();
		}

		String sysmlid = jenkinsJobJson.optString("name");

		// items are the jobs that are in the queue of Jenkins
		if (this.jsonResponse != null) {
			JSONArray jobs = this.jsonResponse.optJSONArray("items");

			if (jobs != null) {
				for (int i = 0; i < jobs.length(); i++) {
					JSONObject job = jobs.optJSONObject(i);

					if (job != null) {
						// append jobs into this queue
						jenkinsQueue.add(i, job);

						String jenkinsJobName = job.optJSONObject("task").optString("name");

						// found the job, so return it
						if (jenkinsJobName.equals(sysmlid)) {
							return job;
						}
					}
				}
			}
		}

		return null;
	}

	public boolean isJobInQueue(String jobName) {
		try {
			this.executeUrl = this.url + "/queue/api/json";
			execute();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// items are the jobs that are in the queue of Jenkins
		if (this.jsonResponse != null) {
			JSONArray jobs = this.jsonResponse.optJSONArray("items");

			if (jobs != null) {
				for (int i = 0; i < jobs.length(); i++) {
					JSONObject job = jobs.optJSONObject(i);

					if (job != null) {
						// append jobs into this queue
						jenkinsQueue.add(i, job);

						String jenkinsJobName = job.optJSONObject("task").optString("name");

						// found the job, so return it
						if (jenkinsJobName.equals(jobName)) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public int numberInQueue(JSONObject jobInQueue) {
		String sysmlid = jobInQueue.optString("name");

		int position = -1;

		if (sysmlid != null) {
			position = this.jenkinsQueue.indexOf(jobInQueue);
		}
		return position;
	}

	public void cancelJob(String jobName, String cancelId, boolean isInQueue) {
		try {
			if (!isInQueue) { // If job is running; Stop it
				this.executeUrl = this.url + "/job/" + jobName + "/" + cancelId + "/stop";

				// this has to be a GET
				execute();
			} else { // If job has not yet start; Cancel it

				// TODO --
				// jobName is not what we want. we want the 'id' from the queue
				// which will have to be
				// handled in a different function (similar to isJobInQueue(
				// jenkinsJob ) )

				this.executeUrl = this.url + "/queue/cancelItem?id=" + cancelId;

				// this has to be a POST
				this.build();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the current build number
	 * @param jobName
	 * @return
	 */
	public String getBuildNumber(String jobName,String projectID, String refID) {
		try {
			this.executeUrl = this.url + "/job/PMA/job/"+projectID+"/job/"+refID+"/job/" + jobName + "/api/json?tree=builds[number]";
			System.out.println("Get build number url: "+this.executeUrl);
			execute();

			if (this.jsonResponse != null) {
				
				JSONArray builds = this.jsonResponse.optJSONArray("builds");

				if (builds != null && builds.length() > 0) {
					JSONObject build = builds.optJSONObject(0);

					if (build != null) {
						String buildNumber = build.optString("number");
						return buildNumber;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * Gets the next build number
	 * @param jobName name of job.
	 * @return
	 */
	public String getNextBuildNumber(String jobName,String projectID, String refID) {
		try {
			this.executeUrl = this.url + "/job/PMA/job/"+projectID+"/job/"+refID+"/job/"+ jobName + "/api/json?tree=nextBuildNumber";
			System.out.println("Get next build number url: "+this.executeUrl);
			execute();

			if (this.jsonResponse != null) {
				
				JSONObject response = this.jsonResponse;
				return(response.get("nextBuildNumber").toString());
				
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}

		return null;
	}
	
	public String getQueueId(String jobName) {

		try {
			this.executeUrl = this.url + "/queue/api/json";
			execute();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// items are the jobs that are in the queue of Jenkins
		if (this.jsonResponse != null) {
			JSONArray jobs = this.jsonResponse.optJSONArray("items");

			if (jobs != null) {
				for (int i = 0; i < jobs.length(); i++) {
					JSONObject job = jobs.optJSONObject(i);

					if (job != null) {
						String jenkinsJobName = job.optJSONObject("task").optString("name");

						// found the job, so return it
						if (jenkinsJobName.equals(jobName)) {
							String queueId = job.optString("id");

							if (queueId != null) {
								return queueId;
							}
						}
					}
				}
			}
		}

		return null;
	}

	public int getTotalNumberOfJobsInQueue() {
		int total = 0;

		try {
			this.executeUrl = this.url + "/queue/api/json";
			execute();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// items are the jobs that are in the queue of Jenkins
		if (this.jsonResponse != null) {
			JSONArray jobs = this.jsonResponse.optJSONArray("items");

			if (jobs != null) {
				total = jobs.length();
				return total;
			}
		}

		return total;
	}

	/**
	 * Retrieves config.xml file of the job.
	 * 
	 * @param jobId name of the job on jenkins (Usually the mms jobId)
	 * @return returns xml object of job
	 */
	public Document getConfigXML(String projectId, String refId,String jobId) {
		String getUrl = this.url + "/job/PMA/job/"+projectId+"/job/"+refId+"/job/" + jobId + "/config.xml";

		HttpGet get = new HttpGet(getUrl);

		try {
			HttpResponse response = this.jenkinsClient.execute(get, this.context);
			HttpEntity entity = response.getEntity();
			String xml = EntityUtils.toString(entity);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new InputSource(new StringReader(xml)));
			return doc;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * Posts config of job to Jenkins.
	 * 
	 * @param jobId name of the job on jenkins (Usually the mms jobId)
	 * @return returns post response
	 */
	public String postModifiedConfigXML(String projectId, String refId,String jobId, String xmlConfigString) {
		String postUrl = this.url + "/job/PMA/job/"+projectId+"/job/"+refId+"/job/" + jobId + "/config.xml";

		try {
			HttpEntity xmlEntity = (HttpEntity) new StringEntity(xmlConfigString);

			HttpPost post = new HttpPost(postUrl);
			post.setHeader("Content-Type", "application/xml");
			post.setEntity(xmlEntity);
			HttpResponse response = this.jenkinsClient.execute(post, this.context);
			System.out.println("Response: " + response);
			EntityUtils.consume(response.getEntity());
			return response.getStatusLine().toString();
		} catch (Exception e) {
//			e.printStackTrace();
			logger.error(e.toString()); // job element not created on mms. 
			System.out.println(e.toString());
			return(e.toString());
		}
	}
	
	/**
	 * Replaces propertyName variable value in the config xml.
	 * 
	 * @param doc contains job configuration in xml format
	 * @param propertyName environment variable in Jenkins config. ex: TARGET_VIEW_ID,PROJECT_ID
	 * @param newPropertyValue new value of the selected property.
	 * @return
	 */
	public Document replaceEnvironmentValueInConfigXML(Document doc,String propertyName, String newPropertyValue) {
		NodeList nodeList = doc.getElementsByTagName("*");

		// iterates through all the nodes to find the one which contains the
		// environment variables
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {

				// System.out.println(node.getNodeName());
				if (node.getNodeName().equals("EnvInjectBuildWrapper")) {
					
					NodeList envInjectNodeChildren = node.getChildNodes();
					for(int k =0;k<envInjectNodeChildren.getLength();k++)
					{
						Node nestedSearchNode = envInjectNodeChildren.item(k);
						if(nestedSearchNode.getNodeName().equals("info"))
						{
							NodeList infoNodeChildren = nestedSearchNode.getChildNodes();
							for(int l =0;l<infoNodeChildren.getLength();l++)
							{
								if(infoNodeChildren.item(l).getNodeName().equals("propertiesContent"))
								{
									Node propertiesContent = infoNodeChildren.item(l);
									
									String[] environmentVariables = propertiesContent.getTextContent().split("\n"); // retrieving the environment variables
									
									for (int j = 0; j < environmentVariables.length; j++) {
										String environmentVariable = environmentVariables[j];
										if (environmentVariable.contains(propertyName)) {
											// System.out.println("Changed property");
											 environmentVariables[j]=propertyName+"="+newPropertyValue;
											// Changes property to new value
										}
									}
									// System.out.println(Arrays.toString(environmentVariables));
									String variablesToString = "";
									for (String environmentVariable : environmentVariables) {
										variablesToString = variablesToString + environmentVariable + "\n";
									}
									propertiesContent.setTextContent(variablesToString);
								}
							}
						}
					}
				}
			}
		}
		return doc;

	}
	
	public Map<String,String> getEnvironmentVariablesFromConfigXml(Document doc) 
	{
		Map<String,String> jenkinsVariables = new HashMap();
		
		NodeList nodeList = doc.getElementsByTagName("*");
		// iterates through all the nodes to find the one which contains the environment variables
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				
				if(node.getNodeName().equals("disabled"))
				{
					jenkinsVariables.put("disabled", node.getTextContent()); // Disabled property
				}
				if(node.getNodeName().equals("triggers"))
				{
					NodeList triggerNodes = node.getChildNodes();
					for(int j=0;j<triggerNodes.getLength();j++)
					{
						if(triggerNodes.item(j).getNodeName().equals("hudson.triggers.TimerTrigger"))
						{
							NodeList timerTriggerNodes = triggerNodes.item(j).getChildNodes();
							for(int k=0;k<timerTriggerNodes.getLength();k++)
							{
								if(timerTriggerNodes.item(k).getNodeName().equals("spec"))
								{
									jenkinsVariables.put("schedule", timerTriggerNodes.item(k).getTextContent()); // Schedule property
								}
							}
						}
					}
				}
				
				if (node.getNodeName().equals("EnvInjectBuildWrapper")) {

					NodeList envInjectNodeChildren = node.getChildNodes();
					for(int j =0;j<envInjectNodeChildren.getLength();j++)
					{
						Node nestedSearchNode = envInjectNodeChildren.item(j);
						if(nestedSearchNode.getNodeName().equals("info"))
						{
							NodeList infoNodeChildren = nestedSearchNode.getChildNodes();
							for(int k =0;k<infoNodeChildren.getLength();k++)
							{
								if(infoNodeChildren.item(k).getNodeName().equals("propertiesContent"))
								{							
									Node propertiesContent = infoNodeChildren.item(k);
									String[] environmentVariables = propertiesContent.getTextContent().split("\n"); // retrieving the environment variables
									for(String environmentVariable:environmentVariables)
									{
										String[] environmentVariableSplitArray = environmentVariable.split("=");
										if(environmentVariableSplitArray.length==2)
										{
											jenkinsVariables.put(environmentVariableSplitArray[0], environmentVariableSplitArray[1]);
										}
									}
									return jenkinsVariables;
								}
							}
						}
					}
				}
			}
		}
		return null; // Jenkins config didn't have the correct keys.
	}
	
	/**
	 * Turns an xml document into a string
	 * @param doc
	 * @return
	 */
	public String xmlDocToString(Document doc)
	{
		try {
			// turns document object to a string.
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(doc);
			transformer.transform(source, result);
			return result.getWriter().toString();
		} catch (TransformerException ex) {
			ex.printStackTrace();
		}
		return "Error occured";
	}

	/**
	 * Retrieves credentials and jenkins server from the database.
	 */
	public void setCredentials(String org) {

		DBUtil dbUtil = new DBUtil();
		dbUtil.getCredentials(org);
		System.out.println(dbUtil.getJenkinsUsername());
		System.out.println(dbUtil.getJenkinsPassword());
		System.out.println("JenkinsURL: "+dbUtil.getJenkinsURL());
		
		this.setUsername(dbUtil.getJenkinsUsername());
		this.setPassword(dbUtil.getJenkinsPassword());
		this.setURL(dbUtil.getJenkinsURL());

	}

	/*
	 * Don't see much use in these functions at the moment may be subject to be
	 * removed from the interface so the code isn't cluttered in the
	 * JenkinsEngine
	 */

	public JSONObject configXmlToJson(String jobUrl) throws SAXException, ParserConfigurationException {
		String getUrl = jobUrl + "config.xml";

		JSONObject o = new JSONObject();

		HttpGet get = new HttpGet(getUrl);

		try {
			HttpResponse response = this.jenkinsClient.execute(get, this.context);
			HttpEntity entity = response.getEntity();
			String xml = EntityUtils.toString(entity);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new InputSource(new StringReader(xml)));

			// get the first element
			Element element = doc.getDocumentElement();

			// if there is a schedule for the job, add the property
			if (element.getElementsByTagName("spec").getLength() > 0)
				o.put("schedule", element.getElementsByTagName("spec").item(0).getTextContent().replaceAll("\\n", " "));
			else
				o.put("schedule", JSONObject.NULL);

			// NOTE: THIS WILL LEAVE THE CONNECTION OPEN, WE MIGHT NOT WANT
			// THIS.
			// EntityUtils.consume( entity );
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return o;
	}

	public JSONArray getJobUrls() {
		constructJobUrl(detail.URL);
		execute();
		try {
			return jsonResponse.getJSONArray("jobs");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public JSONArray getJobNames() {
		constructJobUrl(detail.NAME);
		execute();
		try {
			return jsonResponse.getJSONArray("jobs");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public JSONArray getJobColor() {
		constructJobUrl(detail.COLOR);
		execute();
		try {
			return jsonResponse.getJSONArray("jobs");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public JSONArray getLastSuccessfullBuild() {
		constructJobUrl(detail.LAST_SUCCESSFULL_BUILD);
		execute();
		try {
			return jsonResponse.getJSONArray("jobs");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public JSONArray getLastUnsuccesfullBuild() {
		constructJobUrl(detail.LAST_UNSUCCESFULL_BUILD);
		execute();
		try {
			return jsonResponse.getJSONArray("jobs");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public JSONArray getLastBuild() {
		constructJobUrl(detail.LAST_BUILD);
		execute();
		try {
			return jsonResponse.getJSONArray("jobs");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public JSONArray getLastFailedBuild() {
		constructJobUrl(detail.LAST_FAILED_BUILD);
		execute();
		try {
			return jsonResponse.getJSONArray("jobs");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public JSONArray getLastCompletedBuild() {
		constructJobUrl(detail.LAST_COMPLETED_BUILD);
		execute();
		try {
			return jsonResponse.getJSONArray("jobs");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public JSONArray getJobDescription() {
		constructJobUrl(detail.DESCRIPTION);
		execute();
		try {
			return jsonResponse.getJSONArray("jobs");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public JSONArray getBuildName(String jobConfigUrl) {
		constructBuildUrl(jobConfigUrl, detail.NAME);
		execute();
		try {
			return jsonResponse.getJSONArray("jobs");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public JSONArray getBuildDuration(String jobConfigUrl) {
		constructBuildUrl(jobConfigUrl, detail.DURATION);
		execute();
		try {
			return jsonResponse.getJSONArray("jobs");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public JSONArray getBuildEstimatedDuration(String jobConfigUrl) {
		constructBuildUrl(jobConfigUrl, detail.EST_DURATION);
		execute();
		try {
			return jsonResponse.getJSONArray("jobs");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public JSONArray getBuildTimestamp(String jobConfigUrl) {
		constructBuildUrl(jobConfigUrl, detail.TIMESTAMP);
		execute();
		try {
			return jsonResponse.getJSONArray("jobs");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public JSONArray getBuildDescription(String jobConfigUrl) {
		constructBuildUrl(jobConfigUrl, detail.DESCRIPTION);
		execute();
		try {
			return jsonResponse.getJSONArray("jobs");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Creates a folder inside the PMA folder
	 * @param folderName name of folder
	 * @return response from Jenkins during folder creation
	 */
	public String createFolder(String folderName)
	{
		return createFolderWithParent(folderName,"");
	}
	
	/**
	 * Creates a folder inside another specified folder inside the PMA folder
	 * @param folderName Name of folder to be created
	 * @param folderParentName Name of parent folder to put the new folder. If it is empty string, then it is ignored.
	 * @return
	 */
	public String createFolderWithParent(String folderName,String folderParentName)
	{
		try {
			String folderParent = "";
			if(!folderParentName.equals(""))
			{
				folderParent = "job/"+folderParentName+"/";
			}
			this.executeUrl = this.url + "/job/PMA/"+folderParent+"createItem?name="+folderName+"&mode=com.cloudbees.hudson.plugins.folder.Folder&from=&json=%7B%22name%22%3A%22FolderName%22%2C%22mode%22%3A%22com.cloudbees.hudson.plugins.folder.Folder%22%2C%22from%22%3A%22%22%2C%22Submit%22%3A%22OK%22%7D&Submit=OK";
			System.out.println("Create Folder url: "+executeUrl);
			HttpPost post = new HttpPost(this.executeUrl);
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			System.out.println("Execute URL: "+this.executeUrl);
			try {
				HttpResponse response = this.jenkinsClient.execute(post, this.context);

				EntityUtils.consume(response.getEntity());
				// Will throw an error if the execution fails from either incorrect
				// setup or if the jenkinsClient has not been instantiated.
				System.out.println("Response to string: "+response.toString());
				return response.getStatusLine().toString();
			} catch (IOException e) {
				return e.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return e.toString();
		}
	}
	
}