package gov.nasa.jpl;

import gov.nasa.jpl.controllers.VeEndpointContoller;
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
@WebMvcTest(VeEndpointContoller.class)
public class VeEndpointControllerTests {


    @Autowired
    private MockMvc mvc;

    @MockBean
    private VeEndpointContoller veEndpointContoller;

    @Test
    public void outputHeader() {
        System.out.println("\n----------------------- [ VeEndpoint Controller Tests] -----------------------\n");
    }

    @Test
    public void testGetJobInformation() throws exception {
        try {
            URL myURL = new URL("Https://cae-ems.jpl.nasa.gov/alfresco/s/api/login")
        }
        //given(this.veEndpointContoller.getJob("PROJECT-921084a3-e465-465f-944b-61194213043e", "master",""));
    }
}
