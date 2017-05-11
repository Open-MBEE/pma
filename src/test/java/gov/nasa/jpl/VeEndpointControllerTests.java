package gov.nasa.jpl;

import gov.nasa.jpl.controllers.ClientEndpointController;
import gov.nasa.jpl.jenkinsUtil.JenkinsEngine;
import gov.nasa.jpl.model.JobFromClient;
import gov.nasa.jpl.model.JobInstanceFromClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.constraints.Null;

/**
 * Created by dank on 5/1/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class VeEndpointControllerTests {


    private ClientEndpointController veEndpointController = new ClientEndpointController();
    JenkinsEngine je = new JenkinsEngine();
    private String alfTicket = "TICKET_966561726f35a382c76fa36d3a0a53b471f2db0b";
    private String testServer = "https://opencae-int.jpl.nasa.gov";
    private String jobName = "PMA_1493825038894_5fcafd6e-6e5a-4d03-a6e2-f47ff29286de";
    private JobFromClient job;
    private boolean isConfigured = false;

    private void configVeEndpointController()
    {
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

//    @Test
//    public void testCreateDeleteJob() {
//        System.out.println("\n----------------------- [ VeEndpointController CreateDeleteJob ] -----------------------\n");
//        if (!isConfigured) {
//            configVeEndpointController();
//        }
//        String elementId = veEndpointController.createJob("PROJECT-921084a3-e465-465f-944b-61194213043e", "master", job);
//        assert (!elementId.contains("Unauthorized MMS"));
//        assert (elementId.contains("PMA"));
//        elementId = elementId.replace("HTTP/1.1 200 OK ", "");
//        String output = veEndpointController.deleteJob("PROJECT-921084a3-e465-465f-944b-61194213043e", "master", elementId, alfTicket, testServer);
//        assert (output.contains("200 OK"));
//    }
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
