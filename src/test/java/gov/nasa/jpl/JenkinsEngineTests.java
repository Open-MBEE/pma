package gov.nasa.jpl;

import gov.nasa.jpl.controllers.ConfigUpdateController;
import gov.nasa.jpl.jenkinsUtil.JenkinsBuildConfig;
import gov.nasa.jpl.jenkinsUtil.JenkinsEngine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Created by dank on 4/26/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class JenkinsEngineTests {

    @Test
    public void outputHeader() {
        System.out.println("\n----------------------- [ JenkinsEngine Tests] -----------------------\n");
    }

    @Test
    public void testLogin() {
        System.out.println("\n----------------------------[ Test Login ] ---------------------------\n");
        JenkinsEngine je = new JenkinsEngine();
        je.setCredentials();
        je.login();
        System.out.println(je.getJobNames());
    }

    @Test
    public void testPostConfigXML() {
        System.out.println("\n---------------------- [ Test Post Config XML] -----------------------\n");
        JenkinsEngine je = new JenkinsEngine();
        je.setCredentials();
        je.login();
        JenkinsBuildConfig buildConfig = new JenkinsBuildConfig();
        buildConfig.generateBaseConfigXML();
        je.postConfigXml(buildConfig, "PMAUnitTest-PostConfig", true);
        assert(!je.getJob("PMAUnitTest-PostConfig").contains("Job Not Found"));
        je.deleteJob("PMAUnitTest-PostConfig");
//        assert(je.getJob("PMAUnitTest-PostConfig").contains("Job Not Found") || je.getJob("PMAUnitTest-PostConfig") == null);
    }

    @Test
    public void testExecuteJob()
    {
        System.out.println("\n------------------------- [ Test Execute Job] ------------------------\n");
        JenkinsEngine je = new JenkinsEngine();
        je.setCredentials();
        je.login();
        JenkinsBuildConfig buildConfig = new JenkinsBuildConfig();
        buildConfig.generateBaseConfigXML();
        je.postConfigXml(buildConfig, "PMAUnitTest-ExecuteJob", true);
        assert(!je.getJob("PMAUnitTest-ExecuteJob").contains("Job Not Found"));
        System.out.println(je.executeJob("PMAUnitTest-ExecuteJob"));

        assert(je.isJobInQueue("PMAUnitTest-ExecuteJob"));
        je.deleteJob("PMAUnitTest-ExecuteJob");
//        assert(je.getJob("PMAUnitTest-ExecuteJob").contains("Job Not Found"));
    }
}
