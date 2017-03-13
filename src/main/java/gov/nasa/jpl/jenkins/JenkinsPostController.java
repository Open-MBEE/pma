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
        String buildAgent = "Analysis01-UAT";
        String documentID = "_18_0_6_40a019f_1487977632857_843643_14194";
        String mmsServer = "https://cae-ems-uat-origin.jpl.nasa.gov";
        String teamworkProject = "PROJECT-ID_2_24_17_3_05_44_PM__4fbf6b8b_15a55999900__6e6f_cae_tw_uat_jpl_nasa_gov_128_149_18_101";
        String jobID = "MMS_1488993038923_ab2dc7f9-f902-47a0-a22d-86268d48d814";

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
        jbc.setBuildAgent(buildAgent);
        jbc.setDocumentID(documentID);
        jbc.setMmsServer(mmsServer);
        jbc.setTeamworkProject(teamworkProject);
        jbc.setJobID(jobID);
        System.out.println("Jenkins XML: "+jbc.generateBaseConfigXML());
        JenkinsEngine je = new JenkinsEngine();
        je.setUsername(lines.get(0));
        je.setPassword(lines.get(1));
        je.setURL(lines.get(2));
        je.login();
        Boolean returnStatus = je.postConfigXml(jbc, "testJob", true);
        System.out.println("Status: "+returnStatus);
    }

}