package gov.nasa.jpl;

import gov.nasa.jpl.controllers.ClientEndpointController;
import gov.nasa.jpl.dbUtil.DBUtil;
import gov.nasa.jpl.jenkinsUtil.JenkinsEngine;
import gov.nasa.jpl.mmsUtil.MMSUtil;
import gov.nasa.jpl.model.JobFromClient;
import gov.nasa.jpl.model.JobInstanceFromClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;
import javax.validation.constraints.Null;

/**
 * Created by dank on 5/1/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ClientEndpointControllerTests {

    private ClientEndpointController clientEndpointController = new ClientEndpointController();
    private JenkinsEngine je = new JenkinsEngine();

    MMSUtil mmsUtil = new MMSUtil("");

    private String alfTicket = "tempTicket";
    private String testServer = "opencae-int.jpl.nasa.gov";
    private String jobName = "PMA_1493825038894_5fcafd6e-6e5a-4d03-a6e2-f47ff29286de";
    private String testProject = "PROJECT-c0208d0f-3907-4dee-8a8a-9629d5e27cf4";
    private String testDocument = "_18_5_2_40a019f_1506616041770_592405_165520";
    private JobFromClient job;
    private boolean isConfigured = false;
    private DBUtil dbUtil = new DBUtil();

    private void configVeEndpointController() {
        configVeEndpointController(null, null, null);
    }

    private void configVeEndpointController(String mmsUser, String mmsPass) {
        configVeEndpointController(mmsUser, mmsPass, null);
    }

    private void configVeEndpointController(String mmsUser, String mmsPass, String jenkinsUser) {
        String user = jenkinsUser;
        if (user == null) {
            user = System.getenv("JENKINS_TEST_USER");
            if (user == null) {
                je.setCredentials(null);
            } else {
                System.out.println("\n=======================================================\n FOUND ENV USER \n");
                String password = System.getenv("JENKINS_TEST_PASSWORD");
                je.setUsername(user);
                je.setPassword(password);
                dbUtil.updateDbCredentials(user, password, "https://cae-jenkins2-int.jpl.nasa.gov", "CAE-Analysis-Int","cae");
                je.setURL("cae-jenkins2-int.jpl.nasa.gov");
            }
        }
        if (mmsUser == null || mmsPass == null) {
            System.out.println("MMSUser or Pass is null");
            mmsUser = System.getenv("ADMIN_USER");
            mmsPass = System.getenv("ADMIN_PASS");
            System.out.println("User : " + mmsUser);
            System.out.println("Pass : " + mmsPass);
            if (mmsUser == null || mmsPass == null) {
                mmsUser = System.getProperty("ADMIN_USER");
                mmsPass = System.getProperty("ADMIN_PASS");
                System.out.println("User : " + mmsUser);
                System.out.println("Pass : " + mmsPass);
            }
        }
        System.out.println("GETTING ALF TICKET");
        this.alfTicket = MMSUtil.getAlfrescoToken(testServer, mmsUser, mmsPass);
        System.out.println("ALF TICKET: " + alfTicket);
        mmsUtil = new MMSUtil(alfTicket);
        
        je.login();
        job = new JobFromClient();
        job.setMmsServer(testServer);
        job.setJobName(jobName);
        job.setAlfrescoToken(alfTicket);
        job.setArguments(new String[0]);
        job.setAssociatedElementID(testDocument);
        job.setCommand("");
        job.setSchedule("");
        isConfigured = true;

//        System.out.println("TEST SERVER: "+testServer);
//        System.out.println("MMS USER: "+mmsUser);
//        System.out.println("MMS PASSWORD"+mmsPass);

    }

    private String createJobGetId(String projectId, String refId) {
        String id = null;
        JSONObject responseBody;

        ResponseEntity<String> response = clientEndpointController.createJob(projectId, refId, job);
//        assert (response.toString().contains("200 OK"));
        try {
            responseBody = new JSONObject(response.getBody());
            id = responseBody.getJSONArray("jobs").getJSONObject(0).getString("id");
        } catch (JSONException e) {
            System.out.println("[ JSONException ] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[ Exception ] " + e.getMessage());
        }
        return id;
    }

    private String deleteJob(String projectId, String refId, String elementId) {
        JSONObject responseBody = null;
        ResponseEntity<String> response = clientEndpointController.deleteJob(projectId, refId, elementId, alfTicket, testServer);
        try {
            responseBody = new JSONObject(response.getBody());
            assert (responseBody.getString("message").contains("Delete Succesfull"));
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return responseBody.toString();
    }
    

    /**
     * Passes in a refId string to the create job endpoint and see if the string causes an illegal character exception
     * @param refId
     */
    public void urlInjectionTestBuilder(String refId)
    {
        ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.PAYLOAD_TOO_LARGE);
        response = clientEndpointController.createJob("TestingJob", refId, job);
        try {
       
        response = clientEndpointController.createJob(testProject, refId, job);
        } catch (java.lang.IllegalArgumentException e) {
//        	e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("STATUS CODE: "+response.getStatusCodeValue());
        }
 
        assert(response.getStatusCodeValue() == 500);
        System.out.println(response.getStatusCodeValue() == 500);
        System.out.println("REFID: "+refId);
        System.out.println("STATUS CODE: "+response.getStatusCodeValue()); // Should be 500
        System.out.println("RESPONSE BODY: "+response.getBody());
        System.out.println(response.getBody().contains("java.lang.IllegalArgumentException"));
        assert(response.getBody().contains("java.lang.IllegalArgumentException"));
        
        
        try {
        	JSONObject responseBody;
            responseBody = new JSONObject(response.getBody());
            String id = responseBody.getJSONArray("jobs").getJSONObject(0).getString("id");
            deleteJob(testProject, refId, id);
        } catch (JSONException e) {
            System.out.println("[ JSONException ] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[ Exception ] " + e.getMessage());
        }
        
    }

    @Test
    public void testCreateDeleteJob() {
        System.out.println("\n----------------------- [ VeEndpointController CreateDeleteJob ] -----------------------\n");
        System.out.println("BEFORE: " + this.alfTicket);
        configVeEndpointController();
        System.out.println("AFTER: " + this.alfTicket);

        String id = createJobGetId(testProject, "master");
        assert (id != null);
        deleteJob(testProject, "master", id);
        System.out.println("\n----------------------------------------------------------------------------------------\n");
    }

    @Test
    public void testRunJob() {
        System.out.println("\n----------------------- [ VeEndpointController RunJob ] -----------------------\n");
        configVeEndpointController();

        JobInstanceFromClient jobInstanceFromClient = new JobInstanceFromClient();
        jobInstanceFromClient.setMmsServer(testServer);
        jobInstanceFromClient.setArguments(null);
        jobInstanceFromClient.setAlfrescoToken(alfTicket);

        String id = createJobGetId(testProject, "master");
        clientEndpointController.runJob(testProject, "master", id, jobInstanceFromClient);

        assert (id != null);
        deleteJob(testProject, "master", id);
        System.out.println("\n-------------------------------------------------------------------------------\n");
    }

    @Test
    public void testGetJobsInfo() {
        System.out.println("\n----------------------- [ VeEndpointController Get Jobs Info ] -----------------------\n");
        configVeEndpointController();

        JobInstanceFromClient jobInstanceFromClient = new JobInstanceFromClient();
        jobInstanceFromClient.setMmsServer(testServer);
        jobInstanceFromClient.setArguments(null);
        jobInstanceFromClient.setAlfrescoToken(alfTicket);

        String id = createJobGetId(testProject, "master");
        assert (id != null);
        ResponseEntity<String> responseEntity = clientEndpointController.getJobs(testProject, "master", alfTicket, testServer);

        try {
            JSONObject jsonObject = new JSONObject(responseEntity.getBody());
            JSONArray jsonArray = jsonObject.getJSONArray("jobs");
            int len = jsonArray.length();
            boolean found = false;
            for (int i = 0; i < len; ++i) {
                if (jsonArray.getJSONObject(i).getString("id").equals(id)) {
                    found = true;
                    break;
                }
            }
            assert (found);
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        deleteJob(testProject, "master", id);
        System.out.println("\n-------------------------------------------------------------------------------\n");

    }
    
	/**
	 * Tests parameter input of the create job post 
	 * Each of the inputs are supposed to cause a 500 status to return with a java.lang.IllegalArgumentException exception.
	 */
    @Test
    public void testURLParameterInjection() {

		System.out.println("\n----------------------- [ ClientEndpointController testURLParameterInjection ] -----------------------\n");
		System.out.println("BEFORE: " + this.alfTicket);
		configVeEndpointController();
		System.out.println("AFTER: " + this.alfTicket);
		

		urlInjectionTestBuilder("test test"); // test for space
		urlInjectionTestBuilder("\""); // test special character
		urlInjectionTestBuilder("\\"); // test special character
		urlInjectionTestBuilder("test \""); // test for space and special character
		urlInjectionTestBuilder("test \\"); // test for space and special character


        System.out.println("\n----------------------------------------------------------------------------------------\n");
    }

	/**
	 * Jobs Bin's owner should be projectID_pm because it is inside the md project
	 */
    @Test
    public void testCheckJobsBinLocation() {
    	
    	System.out.println("\n----------------------- [ ClientEndpointController testCheckJobsBinLocation ] -----------------------\n");
    	
        configVeEndpointController();

        JobInstanceFromClient jobInstanceFromClient = new JobInstanceFromClient();
        jobInstanceFromClient.setMmsServer(testServer);
        jobInstanceFromClient.setArguments(null);
        jobInstanceFromClient.setAlfrescoToken(alfTicket);

        String id = createJobGetId(testProject, "master");
        clientEndpointController.runJob(testProject, "master", id, jobInstanceFromClient);

        assert (id != null);

        deleteJob(testProject, "master", id);
        
     // checks if the jobs bin owner Id is projectId_pm
        
        String jobPackageLocationCheckResponse = mmsUtil.isJobPackgeInsideModel(testServer, testProject, "master");
        System.out.println("JOB PACKAGE INSIDE MODEL: "+jobPackageLocationCheckResponse);
        System.out.println("\n----------------------------------------------------------------------------------------\n");
        assert(jobPackageLocationCheckResponse.equals("Already inside model"));
    }

	/**
	 * Checks if the disabled value property is created with the job class
	 */
    @Test
    public void testCheckDisabledPropertyCreation() {
    	
    	System.out.println("\n----------------------- [ ClientEndpointController testCheckDisabledPropertyCreation ] -----------------------\n");
    	
        configVeEndpointController();

        JobInstanceFromClient jobInstanceFromClient = new JobInstanceFromClient();
        jobInstanceFromClient.setMmsServer(testServer);
        jobInstanceFromClient.setArguments(null);
        jobInstanceFromClient.setAlfrescoToken(alfTicket);

        String id = createJobGetId(testProject, "master");
        clientEndpointController.runJob(testProject, "master", id, jobInstanceFromClient);

        assert (id != null);

        
        
     // checks if the jobs bin owner Id is projectId_pm
        
        Boolean jobPackageLocationCheckResponse = mmsUtil.disabledPropertyExists(testServer, testProject, "master",id);
        
        System.out.println("Disabled Value Property Exists: "+jobPackageLocationCheckResponse);
        
        assert(jobPackageLocationCheckResponse);
        
        deleteJob(testProject, "master", id);
        
        System.out.println("\n----------------------------------------------------------------------------------------\n");
      
    }
    
//    @Test
//    public void testIncorrectMMSAuthentication() {
//        System.out.println("\n----------------------- [ Incorrect MMS Authentication ] -----------------------\n");
//        configVeEndpointController("wrongUser", "wrongPass");
//        JobInstanceFromClient jobInstanceFromClient = new JobInstanceFromClient();
//        jobInstanceFromClient.setMmsServer(testServer);
//        jobInstanceFromClient.setArguments(null);
//        jobInstanceFromClient.setAlfrescoToken(alfTicket);
//
//        try {
//            String id = createJobGetId(testProject, "master");
//            assert (false);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//        System.out.println("\n-------------------------------------------------------------------------------\n");
//    }
//
//    @Test
//    public void testIncorrectJenkinsCredential() {
//        System.out.println("\n----------------------- [ Incorrect Jenkins Credentials ] -----------------------\n");
//        configVeEndpointController(null,null, "someUser");
//
//        String id = createJobGetId(testProject, "master");
//
//        assert (id == null);
//        System.out.println("\n-------------------------------------------------------------------------------\n");
//    }
}
