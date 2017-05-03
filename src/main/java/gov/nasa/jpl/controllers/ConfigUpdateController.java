package gov.nasa.jpl.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nasa.jpl.mmsUtil.MMSUtil;

@Controller
public class ConfigUpdateController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Returns all the jobs of a project.
	 * @param projectID
	 * @param refID
	 * @return
	 */
	@RequestMapping(value = "/admin/", method = RequestMethod.GET)
	@ResponseBody
	public String getJobs(@PathVariable String projectID, @PathVariable String refID,@RequestParam String alf_ticket,@RequestParam String mmsServer) {
		
		MMSUtil mmsUtil = new MMSUtil(alf_ticket);
		mmsUtil.getJobElements(mmsServer,projectID, refID);
		
		return "job" + "\n" + projectID + "\n" + refID+ "\n"+alf_ticket;
	}
	
	@RestController
	@EnableAutoConfiguration
	public class ResourcesController {
	    @Autowired
	    private ResourceLoader resourceLoader;

	    @RequestMapping(value = "/file", method = RequestMethod.GET)
	    public String getResources() throws IOException {
	        InputStream is = resourceLoader.getResource("classpath:templates/pmaTestJenkins2.sh").getInputStream();
	        BufferedReader br = null;
			StringBuilder sb = new StringBuilder();

			String line;
			try {

				br = new BufferedReader(new InputStreamReader(is));
				while ((line = br.readLine()) != null) {
					sb.append(line);
					sb.append("\n");
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
	        return "the content of resources:" + sb.toString();
//			return new String(Files.readAllBytes(Paths.get("src/main/resources/templates/pmaTestJenkins2.sh")));
	    }

	}
	@RequestMapping(value = "/dbUpdate", method = RequestMethod.POST)
	@ResponseBody
	public String dbUpdate(@RequestBody String bodyContent) 
	{
		
		String jenkinsUsername = "";
		String jenkinsPassword = "";
		String jenkinsURL = "";
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode fullJson = mapper.readTree(bodyContent);
			logger.info(fullJson.toString());
			if ((fullJson.get("username") != null)&&(fullJson.get("password") != null) &&(fullJson.get("url") != null) ) 
			{
				jenkinsUsername = fullJson.get("username").toString().replace("\"", "");
				jenkinsPassword = fullJson.get("password").toString().replace("\"", "");
				jenkinsURL = fullJson.get("url").toString().replace("\"", "");

				logger.info(jenkinsUsername);
				logger.info(jenkinsPassword);
				logger.info(jenkinsURL);
			}
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Parameters params = new Parameters();
		// Read data from this file
		File propertiesFile = new File("application.properties");
		FileBasedConfigurationBuilder<FileBasedConfiguration> builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class).configure(params.fileBased().setFile(propertiesFile));
		String dbUsername = "";
		String dbPassword = "";
		String dbUrl = "";
		
		try {
			Configuration config = builder.getConfiguration();
			dbUsername = (config.getString("spring.datasource.username"));
			dbPassword = (config.getString("spring.datasource.password"));
			dbUrl = (config.getString("spring.datasource.url"));
		} catch (ConfigurationException cex) {
			// loading of the configuration file failed
			logger.error("[ERROR] Unable to read Application Properties file.");
		}
		
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate();
		DataSource ds = new DataSource();
		
		ds.setUsername(dbUsername);
		ds.setPassword(dbPassword);
		ds.setUrl(dbUrl);
		jdbcTemplate.setDataSource(ds);

		jdbcTemplate.execute("UPDATE CREDENTIALS SET username='"+jenkinsUsername+"'");
		jdbcTemplate.execute("UPDATE CREDENTIALS SET password='"+jenkinsPassword+"'");
		jdbcTemplate.execute("UPDATE CREDENTIALS SET server='"+jenkinsURL+"'");
		
		logger.info("Credentials Updated");
		
		return "Credentials Updated";
	}
	
	@RequestMapping(value = "/db", method = RequestMethod.GET)
	@ResponseBody
	public String updateJobInstanceProperty() 
	{
		
		Parameters params = new Parameters();
		// Read data from this file
		File propertiesFile = new File("application.properties");
		FileBasedConfigurationBuilder<FileBasedConfiguration> builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class).configure(params.fileBased().setFile(propertiesFile));
		String username = "";
		String password = "";
		String url = "";
		
		try {
			Configuration config = builder.getConfiguration();
			username = (config.getString("spring.datasource.username"));
			password = (config.getString("spring.datasource.password"));
			url = (config.getString("spring.datasource.url"));
		} catch (ConfigurationException cex) {
			// loading of the configuration file failed
			logger.error("[ERROR] Unable to read Application Properties file.");
		}
		
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate();
		DataSource ds = new DataSource();
		
		logger.info("DB Username: "+username);
		logger.info("DB password: "+password);
		logger.info("DB url: "+url);
		
		ds.setUrl(url);
		ds.setUsername(username);
		ds.setPassword(password);
		jdbcTemplate.setDataSource(ds);

		String sql = "SELECT * FROM CREDENTIALS";
		String dbString = "";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql); // Retrieving the CREDENTIALS table.
		
//		for (Map<String, Object> row : list) {
//			System.out.println(row.toString());
//			System.out.println("Keyset: "+row.keySet().toString());
//			System.out.println("Values: "+row.values().toString());
//		}
		
		if(!list.isEmpty())
		{
			/*
			 * Getting first row of the CREDENTIALS table.
			 * Contains the Jenkins username, password, and the server url.
			 * Example values of the first row: tempUSER, tempPassword, tempURL
			 */
			
			Map<String, Object> firstRow = list.get(0); 
			
			ArrayList valueList = new ArrayList();
			valueList.addAll(firstRow.values());
			
			if(valueList.size()==3)
			{
				String jenkinsUsername = (String) valueList.get(0);
				String jenkinsPassword = (String) valueList.get(1);
				String jenkinsURL = (String) valueList.get(2);
				logger.info(jenkinsUsername+jenkinsPassword+jenkinsURL);
			}
			
		}
		
		return dbString;	
	}
	
}