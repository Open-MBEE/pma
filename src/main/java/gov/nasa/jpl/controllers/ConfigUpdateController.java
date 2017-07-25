package gov.nasa.jpl.controllers;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nasa.jpl.dbUtil.DBUtil;

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
	
	/**
	 * Used for updating the credentials used to interact with Jenkins.
	 * Expects a json with username,password,jenkins url, and jenkins agent.
	 * @param bodyContent
	 * @return
	 */
	@RequestMapping(value = "/dbUpdate", method = RequestMethod.POST)
	@ResponseBody
	public String dbUpdate(@RequestBody String bodyContent) 
	{
		logger.info("Updating credentials db");
		
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
		
		String updateJenkinsUsernameSQL = "UPDATE CREDENTIALS SET username = ?";
		String updateJenkinsPassword = "UPDATE CREDENTIALS SET password = ?";
		String updateJenkinsURL = "UPDATE CREDENTIALS SET server = ?";
		String updateJenkinsAgent = "UPDATE CREDENTIALS SET agent = ?";
		
		executeSanitizedQueury(jdbcTemplate,updateJenkinsUsernameSQL,jenkinsUsername);
		executeSanitizedQueury(jdbcTemplate,updateJenkinsPassword,jenkinsPassword);
		executeSanitizedQueury(jdbcTemplate,updateJenkinsURL,jenkinsURL);
		executeSanitizedQueury(jdbcTemplate,updateJenkinsAgent,jenkinsAgent);
		
		logger.info("Credentials Updated");
		
		return "Credentials Updated";
	}
	
	public void executeSanitizedQueury(JdbcTemplate jdbcTemplate,String sqlQuery,String value)
	{
		try {
			PreparedStatement ps = jdbcTemplate.getDataSource().getConnection().prepareStatement(sqlQuery);
			ps.setString(1, value);
			ps.execute();
		} catch (SQLException e) {
			logger.info(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates job element on mms and job on Jenkins.
	 * 
	 * @param projectID magicdraw project ID
	 * @param refID id of workspace
	 * @param jobjobFromVE 
	 * @return
	 */
	@RequestMapping(value = "/testing/{projectID}/refs/{refID}/jobs", method = RequestMethod.POST)
	@ResponseBody
	public String createJob(@PathVariable String projectID, @PathVariable String refID) {
		
		return refID;

	}
		
}