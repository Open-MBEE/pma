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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nasa.jpl.dbUtil.DBUtil;
import gov.nasa.jpl.mmsUtil.MMSUtil;

@Controller
public class ConfigUpdateController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * To test if pma is up
	 * @param projectID
	 * @param refID
	 * @return
	 */
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity getJobs() {

		HttpStatus httpStatus = HttpStatus.NOT_FOUND;
		ObjectMapper mapper = new ObjectMapper();

		ObjectNode jobElement = mapper.createObjectNode();
		jobElement.put("id", "");
		jobElement.put("name", "");

		String json = jobElement.toString();

		return new ResponseEntity<>("Not Found", httpStatus);
	}
	

	@RequestMapping(value = "/dbUpdate", method = RequestMethod.POST)
	@ResponseBody
	public String dbUpdate(@RequestBody String bodyContent) 
	{
		
		String jenkinsUsername = "";
		String jenkinsPassword = "";
		String jenkinsURL = "";
		String jenkinsAgent = "";
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode fullJson = mapper.readTree(bodyContent);
			logger.info(fullJson.toString());
			if ((fullJson.get("username") != null)&&(fullJson.get("password") != null) &&(fullJson.get("url") != null) ) 
			{
				jenkinsUsername = fullJson.get("username").toString().replace("\"", "");
				jenkinsPassword = fullJson.get("password").toString().replace("\"", "");
				jenkinsURL = fullJson.get("url").toString().replace("\"", "");
				jenkinsAgent = fullJson.get("agent").toString().replace("\"", "");
				
				logger.info(jenkinsUsername);
				logger.info(jenkinsPassword);
				logger.info(jenkinsURL);
				logger.info(jenkinsAgent);
			}
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.toString();
		}
		
		
		DBUtil dbUtil = new DBUtil();
		
		JdbcTemplate jdbcTemplate = dbUtil.createJdbcTemplate();
		
		jdbcTemplate.execute("drop table if exists credentials"); // deletes the previous table if there is one.
		jdbcTemplate.execute("CREATE TABLE if not exists credentials (username TEXT, password TEXT, server TEXT, agent TEXT)"); //creates the table that will store the credentials
		jdbcTemplate.execute("insert into CREDENTIALS (username, password, server, agent) values ('tempUSER', 'tempPassword', 'tempURL' ,'tempAgent')"); // inserts temp values for first row
		
		jdbcTemplate.execute("UPDATE CREDENTIALS SET username='"+jenkinsUsername+"'");
		jdbcTemplate.execute("UPDATE CREDENTIALS SET password='"+jenkinsPassword+"'");
		jdbcTemplate.execute("UPDATE CREDENTIALS SET server='"+jenkinsURL+"'");
		jdbcTemplate.execute("UPDATE CREDENTIALS SET agent='"+jenkinsAgent+"'");
		
		logger.info("Credentials Updated");
		
		return "Credentials Updated";
	}
		
}