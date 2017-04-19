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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
public class JenkinsEngine implements ExecutionEngine {

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
	 * This method will set the job url
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
	@Override
	public JenkinsEngine createEngine() {
		JenkinsEngine instance = new JenkinsEngine();
		return instance;
	}

	@Override
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
		} catch (IOException e) {
			System.out.println(
					"JenkinsEngine.execute(): response \"" + entityString + "\" failed to parse as a JSONObject");
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public String build() {
		// This sets the URL to an Object specifically for making GET calls
		HttpPost post = new HttpPost(this.executeUrl);

		try {
			HttpResponse response = this.jenkinsClient.execute(post, this.context);

			EntityUtils.consume(response.getEntity());
			// Will throw an error if the execution fails from either incorrect
			// setup or if the jenkinsClient has not been instantiated.
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

	public void constructAllJobs() {
//		String url = this.url + "/view/PMA/api/json?tree=jobs[name,color]"; // Jenkins 1
		String url = this.url + "/job/PMA/api/json?tree=jobs[name,color]"; // Jenkins 2

		System.out.println("Current constuction url is " + url);
		this.executeUrl = url;
		System.out.println("Execution url is " + this.executeUrl);
	}

	// This should be called when you change the name, status, schedule of a job
	public String postConfigXml(JenkinsBuildConfig config, String jobName, boolean newConfig) {
		String postUrl = null;
		if (newConfig) {
//			postUrl = this.url + "/view/PMA/createItem?name=" + jobName; // Jenkins 1
			postUrl = this.url + "/job/PMA/createItem?name=" + jobName; // Jenkins 2
		} else {
//			postUrl = this.url + "/job/" + jobName + "/config.xml"; // Jenkins 1
			postUrl = this.url + "/job/PMA/job/" + jobName + "/config.xml"; // Jenkins 2
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
			e.printStackTrace();
		}
		return "Failed to create job";
	}

	/**
	 * Gets job information from Jenkins.
	 * 
	 * @param jobName
	 *            Name of job
	 * @return Returns a JSON object from Jenkins with color of last job run.
	 *         Ex. {"color":"red","name":"PMA_1490223990977"}
	 */
	public String getJob(String jobName) {
		JSONObject json = null;

		String allJobResponse = getAllJobs();

		try {
			JSONObject allJobs = new JSONObject(allJobResponse);

			JSONArray jobs = allJobs.optJSONArray("jobs");
			if (jobs == null || jobs.length() <= 0)
				return null;
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
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Job Not Found";

	}

	/**
	 * Gets a list of all jobs in the PMA view on Jenkins.
	 * 
	 * @return
	 */
	public String getAllJobs() {
		constructAllJobs();
		String allJobs = execute(); // execute returns not null when theres an
									// error
		if (allJobs != null) {
			return allJobs;
		}
		return jsonResponse.toString();
	}

	public String generateConfigXML(JenkinsBuildConfig config) {
		String xml = config.generateBaseConfigXML();

		if (xml != null)
			return xml;

		return null;
	}

	public String executeJob(String jobName) {
		try {
			this.setJobToken("build");
//			this.executeUrl = this.url + "/job/" + jobName + "/build?token=" + this.jenkinsToken; // Jenkins 1
			this.executeUrl = this.url + "/job/PMA/job/" + jobName + "/build?token=" + this.jenkinsToken; // Jenkins 2
			System.out.println("Execute url: "+executeUrl);
			String response = this.build();
			return response;
		} catch (Exception e) {
			return e.toString();
		}
	}

	public String deleteJob(String jobName) {
		try {
//			this.executeUrl = this.url + "/job/" + jobName + "/doDelete"; // Jenkins 1
			this.executeUrl = this.url + "/job/PMA/job/" + jobName + "/doDelete"; // Jenkins2
			System.out.println("Delete url: "+executeUrl);
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
	public String getBuildNumber(String jobName) {
		try {
//			this.executeUrl = this.url + "/job/" + jobName + "/api/json?tree=builds[number]";
			this.executeUrl = this.url + "/job/PMA/job/" + jobName + "/api/json?tree=builds[number]";
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
	public String getNextBuildNumber(String jobName) {
		try {
			this.executeUrl = this.url + "/job/PMA/job/" + jobName + "/api/json?tree=nextBuildNumber";

			System.out.println("Get next build number url: "+this.executeUrl);
			execute();

			if (this.jsonResponse != null) {
				
				JSONObject response = this.jsonResponse;
				return(response.get("nextBuildNumber").toString());
				
			}
		} catch (Exception e) {
			e.printStackTrace();
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
	 * Retrieves config.xml file of the job. Modifies the job id variable.
	 * 
	 * @param jobName
	 *            name of the job
	 * @return returns xml object of job
	 */
	public Document getConfigXML(String jobName) throws SAXException, ParserConfigurationException {
		String getUrl = this.url + "/job/" + jobName + "/config.xml";

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
		}

		return null;
	}

	/**
	 * Replaces jobID variable in the config xml
	 * 
	 * @param doc
	 *            contains job configuration
	 * @return
	 */
	public String replaceJobIDInConfigXML(Document doc, String newJobID) {
		NodeList nodeList = doc.getElementsByTagName("*");

		// iterates through all the nodes to find the one which contains the
		// environment variables
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {

				// System.out.println(node.getNodeName());
				if (node.getNodeName().equals("EnvInjectBuildWrapper")) {
					String[] environmentVariables = node.getTextContent().split("\n");
					// System.out.println(Arrays.toString(environmentVariables));
					for (int j = 0; j < environmentVariables.length; j++) {
						String environmentVariable = environmentVariables[j];
						if (environmentVariable.contains("JOB_ID")) {
							// System.out.println("Changed ID");
							// environmentVariables[j]="JOB_ID="+newJobID;
							// //Changes job id to new id
						}
					}
					// System.out.println(Arrays.toString(environmentVariables));
					String variablesToString = "";
					for (String environmentVariable : environmentVariables) {
						variablesToString = variablesToString + environmentVariable + "\n";
					}
					// System.out.println(variablesToString);
					// System.out.println(variablesToString.equals(node.getTextContent()));
					node.setTextContent(variablesToString);
					// System.out.println(node.getTextContent());
				}
			}
		}

		try {
			// turns document object to a string.
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(doc);
			transformer.transform(source, result);
			System.out.println(result.getWriter().toString());

			return result.getWriter().toString();
		} catch (TransformerException ex) {
			ex.printStackTrace();
		}
		return "Error occured";
	}

	/**
	 * Retrives credentials and jenkins server
	 * line 1 = user name 
	 * line 2 = password
	 * line 3 = jenkins server
	 */
	public void setCredentials() {
		String configFile = "config-jenkins2.txt";
		List<String> lines = new ArrayList();
		try {
			Scanner sc = new Scanner(new File(configFile));

			while (sc.hasNextLine()) {
				lines.add(sc.nextLine());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		this.setUsername(lines.get(0));
		this.setPassword(lines.get(1));
		
		String server = lines.get(2);
		if(server.substring(server.length()-1).equals("/"))
			server = server.substring(0, server.length()-1);
		this.setURL(server);

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
	 * DO NOT USE --- Exception Handling Not Implemented!
	 *
	 * @param detailName
	 * @return
	 */
	public String getEventDetails(List<String> detailName) {
		String returnString = "";
		// if ( !detailName.isEmpty() && jsonResponse != null ) {
		// for ( String det : detailName ) {
		// System.out.println( "Detail name : "
		// + jsonResponse.get( det ).toString() );
		// detailResultMap.put( det, jsonResponse.get( det ).toString() );
		// returnString += jsonResponse.getString( det ).toString() + ", ";
		// }
		// }
		return returnString;
	}

	@Override
	public void setEvent(String event) {
	}

	@Override
	public void setEvents(List<String> events) {

	}

	@Override
	public boolean stopExecution() {
		return false;
	}

	@Override
	public boolean removeEvent(String event) {
		return false;
	}

	@Override
	public void updateEvent(String event) {
		// TODO Auto-generated method stub
	}

	@Override
	public long getExecutionTime() {
		return executionTime;
	}
	public static void main(String[] args) 
	{
        JenkinsEngine je = new JenkinsEngine();
        je.setCredentials();
        je.login();
        
        String buildAgent = "CAE-Jenkins2-AgentL01-UAT";
        String associatedElementID = "ELEMENT_1234567";
        String mmsServer = "opencae-test.jpl.nasa.gov";
        String projectID = "PROJECT_1234567";
        String jobElementID = "PMA_1492637627350";
        String schedule = "H/2 * * * *";
        
        JenkinsBuildConfig jbc = new JenkinsBuildConfig();
        jbc.setBuildAgent(buildAgent);
        jbc.setDocumentID(associatedElementID);
        jbc.setMmsServer(mmsServer);
        jbc.setTeamworkProject(projectID);
        jbc.setJobID(jobElementID);
        jbc.setSchedule(schedule); 
        
//        String jobExecuteResponse = je.executeJob(jobElementID);
//        System.out.println(jobExecuteResponse);
        
//        String jobCreationResponse = je.postConfigXml(jbc, jobElementID, true);      
//        System.out.println(jobCreationResponse);
        
//    	String jenkinsDeleteResponse = je.deleteJob(jobElementID);
//        System.out.println(jenkinsDeleteResponse);
        
//        System.out.println(je.getAllJobs());
        System.out.println(je.getBuildNumber(jobElementID));
    	
	}
}