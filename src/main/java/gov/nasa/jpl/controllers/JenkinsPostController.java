package gov.nasa.jpl.controllers;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.nasa.jpl.jenkins.JenkinsBuildConfig;
import gov.nasa.jpl.jenkins.JenkinsEngine;

import java.util.*;
import java.io.*;


@RestController
public class JenkinsPostController {

    @RequestMapping("/jenkinsPost")
    public String post() {

        String buildAgent = "Analysis01-UAT";
        String documentID = "_18_0_6_40a019f_1487977632857_843643_14194";
        String mmsServer = "https://cae-ems-uat-origin.jpl.nasa.gov";
        String teamworkProject = "PROJECT-ID_2_24_17_3_05_44_PM__4fbf6b8b_15a55999900__6e6f_cae_tw_uat_jpl_nasa_gov_128_149_18_101";
        String jobID = "MMS_1488993038923_ab2dc7f9-f902-47a0-a22d-86268d48d814";
        String jobName = "testJob";

        JenkinsBuildConfig jbc = new JenkinsBuildConfig();
        jbc.setBuildAgent(buildAgent);
        jbc.setDocumentID(documentID);
        jbc.setMmsServer(mmsServer);
        jbc.setTeamworkProject(teamworkProject);
        jbc.setJobID(jobID);
        System.out.println("Jenkins XML: "+jbc.generateBaseConfigXML());
        
        JenkinsEngine je = login();

        Boolean returnStatus = je.postConfigXml(jbc, jobName, true);
        System.out.println("Status: "+returnStatus);
        return "posted";
    }

    @RequestMapping(value = "/jenkinsRun" , method = RequestMethod.GET)
    public String run(@RequestParam(value="jobName", defaultValue="testJob") String jobName) {
    	
    	JenkinsEngine je = login();
        je.executeJob(jobName);
        
        return "job runnning";
    }

    @RequestMapping("/jenkinsDelete")
    public String delete(@RequestParam(value="jobName", defaultValue="testJob") String jobName) {
    	JenkinsEngine je = login();
        je.deleteJob(jobName);
        return "job deleted";
    }
    
    
    public JenkinsEngine login()
    {
    	String configFile = "config.txt";
        List<String> lines = new ArrayList();
        try {
            Scanner sc = new Scanner(new File(configFile));

            while (sc.hasNextLine()) {
                lines.add(sc.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JenkinsEngine je = new JenkinsEngine();
        je.setUsername(lines.get(0));
        je.setPassword(lines.get(1));
        je.setURL(lines.get(2));
        je.login();
    	return je;
    }

}