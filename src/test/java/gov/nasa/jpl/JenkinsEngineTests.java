package gov.nasa.jpl;

import gov.nasa.jpl.controllers.ConfigUpdateController;
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
    public void testLogin(){
        System.out.println("\n----------------------- [ Test Login ] -----------------------\n");
        JenkinsEngine je = new JenkinsEngine();
        je.login();
        assert (je.jenkinsClient != null);
        System.out.println(je.jenkinsClient.getRequestInterceptor(0).);
    }
}
