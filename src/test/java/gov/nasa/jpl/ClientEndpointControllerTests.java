package gov.nasa.jpl;

import gov.nasa.jpl.controllers.ClientEndpointController;
import gov.nasa.jpl.jenkinsUtil.JenkinsEngine;
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
    JenkinsEngine je = new JenkinsEngine();
    private String alfTicket = "TICKET_ab8259b2cafd61e03db736721df7b0b953533d8a";
    private String testServer = "https://opencae-int.jpl.nasa.gov";
    private String jobName = "PMA_1493825038894_5fcafd6e-6e5a-4d03-a6e2-f47ff29286de";
    private JobFromClient job;
    private boolean isConfigured = false;

    private void configVeEndpointController() {
        je.setCredentials();
        je.login();
        job = new JobFromClient();
        job.setMmsServer(testServer);
        job.setJobName(jobName);
        job.setAlfrescoToken(alfTicket);
        job.setArguments(new String[0]);
        job.setAssociatedElementID("");
        job.setCommand("");
        job.setSchedule("");
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
        if (!isConfigured) {
            configVeEndpointController();
        }

        String id = createJobGetId("PROJECT-921084a3-e465-465f-944b-61194213043e", "master");
        assert (id != null);
        deleteJob("PROJECT-921084a3-e465-465f-944b-61194213043e", "master", id);
        System.out.println("\n----------------------------------------------------------------------------------------\n");
    }

    @Test
    public void testRunJob() {
        System.out.println("\n----------------------- [ VeEndpointController RunJob ] -----------------------\n");
        if (!isConfigured) {
            configVeEndpointController();
        }

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
        if (!isConfigured) {
            configVeEndpointController();
        }

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
