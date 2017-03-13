package gov.nasa.jpl.jenkins;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;
import java.io.*;


@RestController
public class JenkinsPostController {

    @RequestMapping("/jenkinsPost")
    public String post() {
        String configFile = "config.txt";
        //config/alfresco/mms.properties

        List<String> lines = new ArrayList();
        try {
            Scanner sc = new Scanner(new File(configFile));

            while (sc.hasNextLine()) {
                lines.add(sc.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        JenkinsBuildConfig jbc = new JenkinsBuildConfig();
        System.out.println(jbc.generateBaseConfigXML());
        JenkinsEngine je = new JenkinsEngine();
        je.setUsername(lines.get(0));
        je.setPassword(lines.get(1));
        je.setURL(lines.get(2));
        je.login();
        Boolean returnStatus = je.postConfigXml(jbc, "testJob", true);
        System.out.println("Status: "+returnStatus);
        return lines.toString();
    }

}