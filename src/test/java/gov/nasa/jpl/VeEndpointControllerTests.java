package gov.nasa.jpl;

import gov.nasa.jpl.controllers.VeEndpointController;
import gov.nasa.jpl.jenkinsUtil.JenkinsEngine;
import gov.nasa.jpl.model.JobFromVE;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URL;

import static org.mockito.BDDMockito.given;

/**
 * Created by dank on 5/1/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class VeEndpointControllerTests {


    private VeEndpointController veEndpointController = new VeEndpointController();

    @Test
    public void outputHeader() {
        System.out.println("\n----------------------- [ VeEndpoint Controller Tests] -----------------------\n");
    }

    @Test
    public void testCreateJob() {
        JobFromVE job = new JobFromVE();
        JenkinsEngine je = new JenkinsEngine();
        je.setCredentials();
        je.login();
        job.setMmsServer("https://opencae-int.jpl.nasa.gov");
        job.setJobName("PMA_1493825038894_5fcafd6e-6e5a-4d03-a6e2-f47ff29286de");
        job.setAlfrescoToken("TICKET_966561726f35a382c76fa36d3a0a53b471f2db0b");
        job.setArguments(new String[0]);
        job.setAssociatedElementID("");
        job.setCommand("");
        job.setSchedule("");

        String elementId = veEndpointController.createJob("PROJECT-921084a3-e465-465f-944b-61194213043e", "master", job);
        assert (!elementId.contains("Unauthorized MMS"));
        assert (elementId.contains("PMA"));
        elementId = elementId.replace("HTTP/1.1 200 OK ", "");
        assert(je.getJob(elementId) != null);
        je.deleteJob(elementId);
    }
}
