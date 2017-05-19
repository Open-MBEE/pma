package gov.nasa.jpl.jenkinsUtil;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

/**
 * JenkinsBuildConfig ----
 *
 * Builds xml to send to jenkins
 *
 * @author Dan Karlsson (dank) , Tommy Hang (hang)
 * @date 3/20/17
 *
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class JenkinsBuildConfig {

	static final String outputEncoding = "UTF-8";
	private static final boolean DEBUG = true;
	private String jobID = "job0000";
	private String targetElement = "_18_1111_111_111";
	// jdk might have to be (Default)
	private String jdkVersion = "jdk1.8.0_45";
	private String gitBranch = "*/develop";
	private String workspace = "master"; // Also known as refId
	// Teamwork projects can be found in any Teamwork Server and should be case
	// sensitive
	private String teamworkProject = ""; // Also known as projectId
	private String schedule = "";
	private String magicdrawSchedulingCommand = "";
	private String mmsUser = "mmsUser";
	private String mmsPassword = "mmsPassword";
	private String mmsServer = "mmsServer";
	private String teamworkUser = "teamworkUser";
	private String teamworkPassword = "teamworkPassword";
	private String teamworkServer = "teamworkServer";
	private String teamworkPort = "teamworkPort";
	private String gitURL = "gitURL";
	private String gitCredentials = "gitCredentials";
	private String buildAgent = "buildAgent";
	private String timeOutForJob = "60";

	private String jenkinsShellFile = "pmaTestJenkins2.sh";
	private String artifactFile = "artifact.txt";
	private String jobType = "docgen";
	private String environmentVariables = "";
	
	
//	this.environmentVariables = "JOB_ID=" + this.jobID + "\n"+
//			"DOCUMENTS=" + this.documentID + "\n"+
//			"CREDENTIALS=/opt/local/jenkins/credentials/mms.properties\n"+
//			"TEAMWORK_PROJECT=" + this.teamworkProject + "\n"+
//			"MMS_SERVER=" + this.mmsServer + "\n"+
//			"MMS_WORKSPACE=" + this.workspace + "\n";
	

	
	public JenkinsBuildConfig() {
		// TODO Auto-generated constructor stub
	}

	// You can reference the Jenkins XML Schema if you have questions
	// about any of the elements in this file at the following link:
	// http://javadoc.jenkins-ci.org/

	// It isn't exact because of the flexibility Jenkins allows but if
	// you want to change the configuration, that will be a good resource to use
	// for
	// reference and also looking to add additional elements
	public String generateBaseConfigXML() {

		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element tempElement;

			// Root Element
			Element rootElement = doc.createElement("project");
			// this would specify a maven plugin...
			// rootElement.setAttribute("plugin", "maven-plugin@2.10");
			doc.appendChild(rootElement);

			tempElement = doc.createElement("actions");
			rootElement.appendChild(tempElement);

			tempElement = doc.createElement("description");
			tempElement.appendChild(doc.createTextNode(" "));
			rootElement.appendChild(tempElement);

			tempElement = doc.createElement("keepDependencies");
			tempElement.appendChild(doc.createTextNode("false"));
			rootElement.appendChild(tempElement);

			tempElement = doc.createElement("properties");

			Element discardOldBuilds = doc.createElement("jenkins.model.BuildDiscarderProperty");

			Element logRotator = doc.createElement("strategy");
			logRotator.setAttribute("class", "hudson.tasks.LogRotator");

			Element daysToKeep = doc.createElement("daysToKeep");
			daysToKeep.appendChild(doc.createTextNode("-1"));
			Element numToKeep = doc.createElement("numToKeep");
			numToKeep.appendChild(doc.createTextNode("-1"));
			Element artifactDaysToKeep = doc.createElement("artifactDaysToKeep");
			artifactDaysToKeep.appendChild(doc.createTextNode("28"));
			Element artifactNumToKeep = doc.createElement("artifactNumToKeep");
			artifactNumToKeep.appendChild(doc.createTextNode("-1"));

			logRotator.appendChild(daysToKeep);
			logRotator.appendChild(numToKeep);
			logRotator.appendChild(artifactDaysToKeep);
			logRotator.appendChild(artifactNumToKeep);

			discardOldBuilds.appendChild(logRotator);

			tempElement.appendChild(discardOldBuilds);
			rootElement.appendChild(tempElement);

			Element throttlePlugin = doc.createElement("hudson.plugins.throttleconcurrents.ThrottleJobProperty");
			throttlePlugin.setAttribute("plugin", "throttle-concurrents@1.8.5");
			Element maxPerNode = doc.createElement("maxConcurrentPerNode");
			maxPerNode.appendChild(doc.createTextNode("0"));
			Element maxTotal = doc.createElement("maxConcurrentTotal");
			maxTotal.appendChild(doc.createTextNode("0"));
			Element categories = doc.createElement("categories");
			categories.setAttribute("class", "java.util.concurrent.CopyOnWriteArrayList");
			Element throttleEnabled = doc.createElement("throttleEnabled");
			throttleEnabled.appendChild(doc.createTextNode("false"));
			Element throttleOption = doc.createElement("throttleOption");
			throttleOption.appendChild(doc.createTextNode("category"));
			Element limit = doc.createElement("limitOneJobWithMatchingParams");
			limit.appendChild(doc.createTextNode("false"));
			Element paramUseForLimit = doc.createElement("limitOneJobWithMatchingParams");

			throttlePlugin.appendChild(paramUseForLimit);
			throttlePlugin.appendChild(limit);
			throttlePlugin.appendChild(throttleOption);
			throttlePlugin.appendChild(throttleEnabled);
			throttlePlugin.appendChild(categories);
			throttlePlugin.appendChild(maxTotal);
			throttlePlugin.appendChild(maxPerNode);
			tempElement.appendChild(throttlePlugin);

			rootElement.appendChild(tempElement);

			tempElement = doc.createElement("scm");
			tempElement.setAttribute("class", "hudson.scm.NullSCM");
			rootElement.appendChild(tempElement);

			tempElement = doc.createElement("assignedNode");
			tempElement.appendChild(doc.createTextNode(this.buildAgent));
			rootElement.appendChild(tempElement);

			tempElement = doc.createElement("canRoam");
			tempElement.appendChild(doc.createTextNode("false"));
			rootElement.appendChild(tempElement);

			tempElement = doc.createElement("disabled");
			tempElement.appendChild(doc.createTextNode("false"));
			rootElement.appendChild(tempElement);

			tempElement = doc.createElement("blockBuildWhenDownstreamBuilding");
			tempElement.appendChild(doc.createTextNode("false"));
			rootElement.appendChild(tempElement);

			tempElement = doc.createElement("blockBuildWhenUpstreamBuilding");
			tempElement.appendChild(doc.createTextNode("false"));
			rootElement.appendChild(tempElement);

			tempElement = doc.createElement("jdk");
			tempElement.appendChild(doc.createTextNode(this.jdkVersion));
			rootElement.appendChild(tempElement);

			tempElement = doc.createElement("triggers");
			
			/**
			 * Sets Schedule. Should be in cron format: * * * * *
			 */
			if ((getSchedule() != null && !getSchedule().isEmpty())) {
				Element hudTrig = doc.createElement("hudson.triggers.TimerTrigger");
				Element spec = doc.createElement("spec");
				spec.appendChild(doc.createTextNode(this.schedule));
				hudTrig.appendChild(spec);
				tempElement.appendChild(hudTrig);
			}
			rootElement.appendChild(tempElement);

			tempElement = doc.createElement("concurrentBuild");
			tempElement.appendChild(doc.createTextNode("false"));
			rootElement.appendChild(tempElement);

			/**
			 *
			 * Builder Section This is where the console / shell commands will
			 * be placed
			 */
			tempElement = doc.createElement("builders");
			Element hudson = doc.createElement("hudson.tasks.Shell");
			Element command = doc.createElement("command");

			// Get the script file and put the string contents in
			// jenkins execute shell.
			String content = null;
			
			setEnvironmentVariables(); // Sets Jenkins environment variables based on type of job.
			
			try {

				ResourceLoader resourceLoader = new DefaultResourceLoader();
				InputStream is = resourceLoader.getResource("classpath:templates/"+this.jenkinsShellFile).getInputStream();
				BufferedReader br = null;
				StringBuilder sb = new StringBuilder();

				String line;
				try {

					br = new BufferedReader(new InputStreamReader(is));
					while ((line = br.readLine()) != null) {
						sb.append(line);
						sb.append("\n");
					}

				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (br != null) {
						try {
							br.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				content = sb.toString();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Old way of reading in template. Does not work when project is exported as a jar.
//			try {
//				content = new String(Files.readAllBytes(Paths.get("src/main/resources/templates/pmaTestJenkins2.sh")));
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

			command.appendChild(doc.createTextNode(content));
			hudson.appendChild(command);
			tempElement.appendChild(hudson);
			rootElement.appendChild(tempElement);

			tempElement = doc.createElement("aggregatorStyleBuild");
			tempElement.appendChild(doc.createTextNode("false"));
			rootElement.appendChild(tempElement);

			tempElement = doc.createElement("incrementalBuild");
			tempElement.appendChild(doc.createTextNode("false"));
			rootElement.appendChild(tempElement);

			// This has a purposeful spelling error, Jenkins for some reason
			// uses
			// ignoreUpstrem instead of ignoreUpstream
			tempElement = doc.createElement("ignoreUpstremChanges");
			tempElement.appendChild(doc.createTextNode("false"));
			rootElement.appendChild(tempElement);

			tempElement = doc.createElement("archivingDisabled");
			tempElement.appendChild(doc.createTextNode("false"));
			rootElement.appendChild(tempElement);

			tempElement = doc.createElement("siteArchivingDisabled");
			tempElement.appendChild(doc.createTextNode("false"));
			rootElement.appendChild(tempElement);

			tempElement = doc.createElement("fingerprintingDisabled");
			tempElement.appendChild(doc.createTextNode("false"));
			rootElement.appendChild(tempElement);

			tempElement = doc.createElement("resolveDependencies");
			tempElement.appendChild(doc.createTextNode("false"));
			rootElement.appendChild(tempElement);

			tempElement = doc.createElement("processPlugins");
			tempElement.appendChild(doc.createTextNode("false"));
			rootElement.appendChild(tempElement);

			tempElement = doc.createElement("mavenValidationLevel");
			tempElement.appendChild(doc.createTextNode("-1"));
			rootElement.appendChild(tempElement);

			tempElement = doc.createElement("runHeadless");
			tempElement.appendChild(doc.createTextNode("false"));
			rootElement.appendChild(tempElement);

			tempElement = doc.createElement("aggregatorStyleBuild");
			tempElement.appendChild(doc.createTextNode("true"));
			rootElement.appendChild(tempElement);

			tempElement = doc.createElement("disableTriggerDownstreamProjects");
			tempElement.appendChild(doc.createTextNode("false"));
			rootElement.appendChild(tempElement);

			tempElement = doc.createElement("blockTriggerWhenBuilding");
			tempElement.appendChild(doc.createTextNode("true"));
			rootElement.appendChild(tempElement);

			tempElement = doc.createElement("aggregatorStyleBuild");
			tempElement.appendChild(doc.createTextNode("true"));
			rootElement.appendChild(tempElement);

			// Define the build configuration settings
			Element settingsElement = doc.createElement("settings");
			settingsElement.setAttribute("class", "jenkins.mvn.DefaultSettingsProvider");
			rootElement.appendChild(settingsElement);

			Element globalSettingsElement = doc.createElement("globalSettings");
			globalSettingsElement.setAttribute("class", "jenkins.mvn.FilePathGlobalSettingsProvider");
			tempElement = doc.createElement("path");
			tempElement.appendChild(doc.createTextNode("/opt/local/maven/conf/settings.xml"));
			globalSettingsElement.appendChild(tempElement);
			rootElement.appendChild(globalSettingsElement);

			Element infoElement = doc.createElement("info");
			Element buildWrappers = doc.createElement("buildWrappers");
			Element propertiesContent = doc.createElement("propertiesContent");
			Element injectEnvironmentVar = doc.createElement("EnvInjectBuildWrapper");
			propertiesContent.appendChild(doc.createTextNode("\n"));
			
			propertiesContent.appendChild(doc.createTextNode(this.environmentVariables));
			
//			propertiesContent.appendChild(doc.createTextNode("JOB_ID=" + this.jobID + "\n"));
//			propertiesContent.appendChild(doc.createTextNode("DOCUMENTS=" + this.documentID + "\n"));
//			propertiesContent
//					.appendChild(doc.createTextNode("CREDENTIALS=/opt/local/jenkins/credentials/mms.properties\n"));
//			propertiesContent.appendChild(doc.createTextNode("TEAMWORK_PROJECT=" + this.teamworkProject + "\n"));
//			propertiesContent.appendChild(doc.createTextNode("MMS_SERVER=" + this.mmsServer + "\n"));
//			propertiesContent.appendChild(doc.createTextNode("MMS_WORKSPACE=" + this.workspace + "\n"));

			Element hudsonTimeout = doc.createElement("hudson.plugins.build__timeout.BuildTimeoutWrapper");
			Element strategy = doc.createElement("strategy");
			Element timeOut = doc.createElement("timeoutMinutes");
			timeOut.appendChild(doc.createTextNode(timeOutForJob));
			Element operationList = doc.createElement("operationList");

			hudsonTimeout.setAttribute("plugin", "build-timeout@1.14.1");
			strategy.setAttribute("class", "hudson.plugins.build_timeout.impl.AbsoluteTimeOutStrategy");

			strategy.appendChild(operationList);
			strategy.appendChild(timeOut);
			hudsonTimeout.appendChild(strategy);
			buildWrappers.appendChild(hudsonTimeout);

			injectEnvironmentVar.setAttribute("plugin", "envinject@1.91.3");
			infoElement.appendChild(propertiesContent);
			injectEnvironmentVar.appendChild(infoElement);
			buildWrappers.appendChild(injectEnvironmentVar);
			rootElement.appendChild(buildWrappers);

			tempElement = doc.createElement("reporters");
			rootElement.appendChild(tempElement);
			tempElement = doc.createElement("publishers");
			Element artifactArchiver = doc.createElement("hudson.tasks.ArtifactArchiver");
			Element secondTempElem = doc.createElement("artifacts");

//			secondTempElem.appendChild(doc.createTextNode("MDNotificationWindowText.html"));
			secondTempElem.appendChild(doc.createTextNode(this.artifactFile));
			
			artifactArchiver.appendChild(secondTempElem);

			secondTempElem = doc.createElement("allowEmptyArchive");
			secondTempElem.appendChild(doc.createTextNode("true"));
			artifactArchiver.appendChild(secondTempElem);

			secondTempElem = doc.createElement("onlyIfSuccessful");
			secondTempElem.appendChild(doc.createTextNode("false"));
			artifactArchiver.appendChild(secondTempElem);

			secondTempElem = doc.createElement("fingerprint");
			secondTempElem.appendChild(doc.createTextNode("false"));
			artifactArchiver.appendChild(secondTempElem);

			secondTempElem = doc.createElement("defaultExcludes");
			secondTempElem.appendChild(doc.createTextNode("true"));
			artifactArchiver.appendChild(secondTempElem);

			tempElement.appendChild(artifactArchiver);
			rootElement.appendChild(tempElement);

			/*
			 * Comment this out and fill in appropriate values if you would like
			 * to add pre or post build steps to the job configuration.
			 * Currently have no use for these
			 *
			 * rootElement.appendChild(doc.createElement("prebuilders"));
			 * rootElement.appendChild(doc.createElement("postbuilders"));
			 *
			 * Element runPostStepsIfResultsElement =
			 * doc.createElement("runPostStepsIfResult");
			 *
			 * tempElement = doc.createElement("name");
			 * tempElement.appendChild(doc.createTextNode("FAILURE"));
			 * runPostStepsIfResultsElement.appendChild(tempElement);
			 *
			 * tempElement = doc.createElement("ordinal");
			 * tempElement.appendChild(doc.createTextNode("2"));
			 * runPostStepsIfResultsElement.appendChild(tempElement);
			 *
			 * runPostStepsIfResultsElement.appendChild(doc.createElement(
			 * "color").appendChild(doc.createTextNode("RED")));
			 * runPostStepsIfResultsElement.appendChild(doc.createElement(
			 * "completeBuild").appendChild(doc.createTextNode("true")));
			 *
			 * rootElement.appendChild(runPostStepsIfResultsElement);
			 */

			// Save this in case the above code does not work and an element
			// needs to be made for each individual build tag
			// Element Element = doc.createElement("");

			// TransformerFactory transformerFactory =
			// TransformerFactory.newInstance();
			// Transformer transformer = transformerFactory.newTransformer();
			// DOMSource source = new DOMSource(doc);
			// StringWriter stringWriter = new StringWriter();
			// StreamResult result = new StreamResult(stringWriter);
			//
			// System.out.println("source: "+source);
			// System.out.println("result: "+result);
			// transformer.transform(source, result);
			// System.out.println("String: "+stringWriter.toString());

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StringWriter stringWriter = new StringWriter();
			StreamResult result = new StreamResult(stringWriter);
			// System.out.println("source: "+source);
			// System.out.println("result: "+result);
			transformer.transform(source, result);
			// System.out.println("String: "+stringWriter.toString());

			return stringWriter.toString();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Sets Jenkins environment variables based on type of job.
	 * 
	 */
	public void setEnvironmentVariables()
	{
		if(this.jobType.equals("docweb"))
		{
			String pmaHost = "";
			try {
				pmaHost = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setJenkinsShellFile("docweb.sh");
			setEnvironmentVariables("JOB_ID=" + this.jobID + "\n" + 
			"TARGET_VIEW_ID=" + this.targetElement + "\n"+ 
			"PROJECT_ID="+ this.teamworkProject + "\n" + 
			"MMS_HOST=" + this.mmsServer + "\n" + 
			"REF_ID="+ this.workspace + "\n" + 
			"PMA_HOST=" + pmaHost+":8080"+ "\n");
		}
		else
		{
			setEnvironmentVariables("JOB_ID=" + this.jobID + "\n" + "DOCUMENTS=" + this.targetElement + "\n"
					+ "CREDENTIALS=/opt/local/jenkins/credentials/mms.properties\n" + "TEAMWORK_PROJECT="
					+ this.teamworkProject + "\n" + "MMS_SERVER=" + this.mmsServer + "\n" + "MMS_WORKSPACE="
					+ this.workspace + "\n");
		}
	}
	
	/*
	 * Setters and getters
	 * 
	 */
	
	
	/**
	 * @return the documentID
	 */
	public String getTargetElementID() {
		return targetElement;
	}

	/**
	 * @param targetElement
	 *            the targetElement to set
	 */
	public void setTargetElementID(String targetElement) {
		this.targetElement = targetElement;
	}

	/**
	 * @return the mmsServer
	 */
	public String getMmsServer() {
		return mmsServer;
	}

	/**
	 * @param mmsServer
	 *            the mmsServer to set
	 */
	public void setMmsServer(String mmsServer) {
		this.mmsServer = mmsServer;
	}

	/**
	 * @return the mmsUser
	 */
	public String getMmsUser() {
		return mmsUser;
	}

	/**
	 * @param mmsUser
	 *            the mmsUser to set
	 */
	public void setMmsUser(String mmsUser) {
		this.mmsUser = mmsUser;
	}

	/**
	 * @return the mmsPassword
	 */
	public String getMmsPassword() {
		return mmsPassword;
	}

	/**
	 * @param mmsPassword
	 *            the mmsPassword to set
	 */
	public void setMmsPassword(String mmsPassword) {
		this.mmsPassword = mmsPassword;
	}

	/**
	 * @return the teamworkProject
	 */
	public String getTeamworkProject() {
		return teamworkProject;
	}

	/**
	 * @param teamworkProject
	 *            the teamworkProject to set
	 */
	public void setTeamworkProject(String teamworkProject) {
		this.teamworkProject = teamworkProject;
	}

	/**
	 * @return the teamworkServer
	 */
	public String getTeamworkServer() {
		return teamworkServer;
	}

	/**
	 * @param teamworkServer
	 *            the teamworkServer to set
	 */
	public void setTeamworkServer(String teamworkServer) {
		this.teamworkServer = teamworkServer;
	}

	/**
	 * @return the teamworkPort
	 */
	public String getTeamworkPort() {
		return teamworkPort;
	}

	/**
	 * @param teamworkPort
	 *            the teamworkPort to set
	 */
	public void setTeamworkPort(String teamworkPort) {
		this.teamworkPort = teamworkPort;
	}

	/**
	 * @return the teamworkUser
	 */
	public String getTeamworkUser() {
		return teamworkUser;
	}

	/**
	 * @param teamworkUser
	 *            the teamworkUser to set
	 */
	public void setTeamworkUser(String teamworkUser) {
		this.teamworkUser = teamworkUser;
	}

	/**
	 * @return the teamworkPassword
	 */
	public String getTeamworkPassword() {
		return teamworkPassword;
	}

	/**
	 * @param teamworkPassword
	 *            the teamworkPassword to set
	 */
	public void setTeamworkPassword(String teamworkPassword) {
		this.teamworkPassword = teamworkPassword;
	}

	/**
	 * @return the workspace
	 */
	public String getWorkspace() {
		return workspace;
	}

	/**
	 * @param workspace
	 *            the workspace to set
	 */
	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	public void setJDK(String version) {
		this.jdkVersion = version;
	}

	public String getJDKVersion() {
		return this.jdkVersion;
	}

	public String getJobID() {
		return this.jobID;
	}

	public void setJobID(String jobID) {
		this.jobID = jobID;
	}

	public void setBuildAgent(String buildAgent) {
		this.buildAgent = buildAgent;
	}

	/**
	 * @return the schedule
	 */
	public String getSchedule() {
		return schedule;
	}

	/**
	 * @param schedule The schedule to set. In cron format. ex: * * * * *
	 */
	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getJobType() {
		return this.jobType;
	}

	/**
	 * @param jobType 
	 */
	public void setJobType(String jobType) {
		this.jobType = jobType;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getEnvironmentVariables() {
		return this.environmentVariables;
	}

	/**
	 * 
	 * @param environmentVariables
	 */
	public void setEnvironmentVariables(String environmentVariables) {
		this.environmentVariables = environmentVariables;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getJenkinsShellFile() {
		return this.jenkinsShellFile;
	}

	/**
	 * 
	 * @param environmentVariables
	 */
	public void setJenkinsShellFile(String jenkinsShellFile) {
		this.jenkinsShellFile = jenkinsShellFile;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getArtifactFile() {
		return this.artifactFile;
	}

	/**
	 * 
	 * @param artifactFile
	 */
	public void setArtifactFile(String artifactFile) {
		this.artifactFile = artifactFile;
	}
}
