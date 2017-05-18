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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

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
    private JobFromClient job;
    private boolean isConfigured = false;
    private DBUtil dbUtil = new DBUtil();

    private void configVeEndpointController() {
    	
        String user = System.getenv("JENKINS_TEST_USER");
        if (user == null) {
            je.setCredentials();
        }
        else {
            System.out.println("\n=======================================================\n FOUND ENV USER \n");
            String password = System.getenv("JENKINS_TEST_PASSWORD");
            je.setUsername(user);
            je.setPassword(password);
            dbUtil.updateDbCredentials(user, password, "https://cae-jenkins2-int.jpl.nasa.gov", "CAE-Analysis-Int");
            je.setURL("cae-jenkins2-int.jpl.nasa.gov");
        }
        System.out.println("GETTING ALF TICKET");
        String mmsUser = System.getenv("ADMIN_USER");
        String mmsPass = System.getenv("ADMIN_PASS");
        this.alfTicket = MMSUtil.getAlfrescoToken(testServer, mmsUser, mmsPass);
        System.out.println("ALF TICKET: "+alfTicket);
        
        je.login();
        job = new JobFromClient();
        job.setMmsServer(testServer);
        job.setJobName(jobName);
        job.setAlfrescoToken(alfTicket);
        job.setArguments(new String[0]);
        job.setAssociatedElementID("");
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
        assert (response.toString().contains("200 OK"));
        try {
            responseBody = new JSONObject(response.getBody());
            id = responseBody.getJSONArray("jobs").getJSONObject(0).getString("id");
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
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

    @Test
    public void testCreateDeleteJob() {
        System.out.println("\n----------------------- [ VeEndpointController CreateDeleteJob ] -----------------------\n");
        System.out.println("BEFORE: "+this.alfTicket);
        configVeEndpointController();
        System.out.println("AFTER: "+this.alfTicket);

        String id = createJobGetId("PROJECT-921084a3-e465-465f-944b-61194213043e", "master");
        assert (id != null);
        deleteJob("PROJECT-921084a3-e465-465f-944b-61194213043e", "master", id);
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

        String id = createJobGetId("PROJECT-921084a3-e465-465f-944b-61194213043e", "master");
        clientEndpointController.runJob("PROJECT-921084a3-e465-465f-944b-61194213043e", "master", id, jobInstanceFromClient);

        assert (id != null);
        deleteJob("PROJECT-921084a3-e465-465f-944b-61194213043e", "master", id);
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

        String id = createJobGetId("PROJECT-921084a3-e465-465f-944b-61194213043e", "master");
        assert (id != null);
        ResponseEntity<String> responseEntity = clientEndpointController.getJobs("PROJECT-921084a3-e465-465f-944b-61194213043e", "master", alfTicket, "opencae-int.jpl.nasa.gov");

        try {
            JSONObject jsonObject = new JSONObject(responseEntity.getBody());
            JSONArray jsonArray = jsonObject.getJSONArray("jobs");
            int len = jsonArray.length();
            boolean found = false;
            for(int i = 0; i < len; ++i) {
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

        deleteJob("PROJECT-921084a3-e465-465f-944b-61194213043e", "master", id);
        System.out.println("\n-------------------------------------------------------------------------------\n");

    }
}