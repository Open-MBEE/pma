package gov.nasa.jpl;

import gov.nasa.jpl.controllers.ClientEndpointController;
import gov.nasa.jpl.jenkinsUtil.JenkinsEngine;
import gov.nasa.jpl.model.JobFromClient;
import gov.nasa.jpl.model.JobInstanceFromClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.constraints.Null;

/**
 * Created by dank on 5/1/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ClientEndpointControllerTests {


    private ClientEndpointController clientEndpointController = new ClientEndpointController();
    JenkinsEngine je = new JenkinsEngine();
    private String alfTicket = "TICKET_18d2fd354c4de32dfa5169d0c2eb0da8be1c95cd";
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

    @Test
    public void testCreateDeleteJob() {
        System.out.println("\n----------------------- [ VeEndpointController CreateDeleteJob ] -----------------------\n");
        String id = null;
        JSONObject responseBody;
        if (!isConfigured) {
            configVeEndpointController();
        }

        ResponseEntity<String> response = clientEndpointController.createJob("PROJECT-921084a3-e465-465f-944b-61194213043e", "master", job);
        try {
            responseBody = new JSONObject(response.getBody());
            id = responseBody.getJSONArray("jobs").getJSONObject(0).getString("id");
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        assert (response.toString().contains("200 OK"));
        assert (id != null);

        response = clientEndpointController.deleteJob("PROJECT-921084a3-e465-465f-944b-61194213043e", "master", id, alfTicket, testServer);
        try {
            responseBody = new JSONObject(response.getBody());
            assert(responseBody.getString("message").contains("Delete Succesfull"));
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
//
//    @Test
//    public void testRunJob() {
//        System.out.println("\n----------------------- [ VeEndpointController RunJob ] -----------------------\n");
//        if (!isConfigured) {
//            configVeEndpointController();
//        }
//
//        JobInstanceFromVE jobInstanceFromVE = new JobInstanceFromVE();
//        jobInstanceFromVE.setMmsServer(testServer);
//        jobInstanceFromVE.setArguments(null);
//        jobInstanceFromVE.setAlfrescoToken(alfTicket);
//
//        String elementId = veEndpointController.createJob("PROJECT-921084a3-e465-465f-944b-61194213043e", "master", job);
//        assert (!elementId.contains("Unauthorized MMS"));
//        assert (elementId.contains("PMA"));
//        elementId = elementId.replace("HTTP/1.1 200 OK ", "");
//        String output = veEndpointController.runJob("PROJECT-921084a3-e465-465f-944b-61194213043e", "master", elementId, jobInstanceFromVE);
//        System.out.println("\n------------------------ [ Run Job ] -------------------------\n");
//        System.out.println(output);
//        System.out.println("\n--------------------------------------------------------------\n");
//        output = veEndpointController.deleteJob("PROJECT-921084a3-e465-465f-944b-61194213043e", "master", elementId, alfTicket, testServer);
//        System.out.println("\n----------------------- [ Delete Job ] -----------------------\n");
//        System.out.println(output);
//        System.out.println("\n--------------------------------------------------------------\n");
//    }
}
