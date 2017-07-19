/**
 * Class used to interact with the sql database.
 * 
 * @author hang
 */
package gov.nasa.jpl.dbUtil;

import java.io.File;
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
import org.springframework.jdbc.core.JdbcTemplate;

public class DBUtil 
{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	String jenkinsUsername = "";
	String jenkinsPassword = "";
	String jenkinsURL = "";
	String jenkinsAgent = "";
			
	public DBUtil()
	{
		
	}
	public void setJenkinsUsername(String username)
	{
		this.jenkinsUsername=username;
	}
	public String getJenkinsUsername()
	{
		return this.jenkinsUsername;
	}
	
	public void setJenkinsPassword(String password)
	{
		this.jenkinsPassword=password;
	}
	public String getJenkinsPassword()
	{
		return this.jenkinsPassword;
	}
	
	public void setJenkinsURL(String url)
	{
		this.jenkinsURL=url;
	}
	public String getJenkinsURL()
	{
		return this.jenkinsURL;
	}
	
	public void setJenkinsAgent(String agent)
	{
		this.jenkinsAgent=agent;
	}
	public String getJenkinsAgent()
	{
		return this.jenkinsAgent;
	}
	
	/**
	 * Creates a JdbcTemplate object that is used to interact with the sql database.
	 * @return JdbcTemplate object that is used to interact with the sql database
	 */
	public JdbcTemplate createJdbcTemplate()
	{
		logger.info("Creating db connection object");
		Parameters params = new Parameters();
		// Read database login from the properties file
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
			System.out.println("[ERROR] Unable to read Application Properties file.");
		}
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate();
		DataSource ds = new DataSource();
		
		ds.setUrl(url);
		ds.setUsername(username);
		ds.setPassword(password);
		jdbcTemplate.setDataSource(ds);
		return jdbcTemplate;
	}
	
	/**
	 * Retrieving the jenkins username, jenkins password, jenkins url, and jenkins agent from the credentials table in the database.
	 */
	public void getCredentials()
	{
		System.out.println("Retrieving credentials from DB");
		logger.info("Retrieving credentials from DB");
		JdbcTemplate jdbcTemplate = createJdbcTemplate();

		String sql = "SELECT * FROM CREDENTIALS";
		try
		{
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql); // Retrieving the CREDENTIALS table.

		if(!list.isEmpty())
		{
			/*
			 * Getting first row of the CREDENTIALS table.
			 * Contains the Jenkins username, password, server url, and agent.
			 * Example values of the first row: tempUSER, tempPassword, tempURL ,tempAgent
			 */
			Map<String, Object> firstRow = list.get(0); 
			
			ArrayList valueList = new ArrayList();
			valueList.addAll(firstRow.values());
//			System.out.println("Value List: "+String.join(", ", valueList));
//			System.out.println("Value Size: "+valueList.size());
			if(valueList.size()==4)
			{
				System.out.println("Setting Credentials");
				this.setJenkinsUsername((String) valueList.get(0));
				this.setJenkinsPassword((String) valueList.get(1));
				this.setJenkinsURL((String) valueList.get(2));
				this.setJenkinsAgent((String) valueList.get(3));
			}

			}
		} catch (Exception e) {
			logger.info(e.toString());
			e.printStackTrace();
		}
	}

	public void updateDbCredentials(String username, String password, String url, String agent)
	{
		JdbcTemplate jdbcTemplate = createJdbcTemplate();

		jdbcTemplate.execute("drop table if exists credentials"); // deletes the previous table if there is one.
		jdbcTemplate.execute("CREATE TABLE if not exists credentials (username TEXT, password TEXT, server TEXT, agent TEXT)"); //creates the table that will store the credentials
		jdbcTemplate.execute("insert into CREDENTIALS (username, password, server, agent) values ('tempUSER', 'tempPassword', 'tempURL' ,'tempAgent')"); // inserts temp values for first row

		jdbcTemplate.execute("UPDATE CREDENTIALS SET username='"+username+"'");
		jdbcTemplate.execute("UPDATE CREDENTIALS SET password='"+password+"'");
		jdbcTemplate.execute("UPDATE CREDENTIALS SET server='"+url+"'");
		jdbcTemplate.execute("UPDATE CREDENTIALS SET agent='"+agent+"'");
	}
}
