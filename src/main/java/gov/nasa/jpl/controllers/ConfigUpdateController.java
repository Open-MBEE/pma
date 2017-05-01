package gov.nasa.jpl.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import gov.nasa.jpl.mmsUtil.MMSUtil;

@Controller
public class ConfigUpdateController {

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
	public class DBController {

	    @RequestMapping(value = "/db", method = RequestMethod.GET)
	    public String getDB() throws IOException {
	    	
			return null;
	    	
	    }

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

	@RequestMapping(value = "/db", method = RequestMethod.GET)
	public String getDB() throws IOException {

		JdbcTemplate jdbcTemplate = new JdbcTemplate();
		DataSource ds = new DataSource();
		ds.setUrl("jdbc:h2:./testdb");
		ds.setUsername("sa");
		ds.setPassword("sa");
		jdbcTemplate.setDataSource(ds);

		String sql = "SELECT * FROM INFORMATION_SCHEMA.TABLES";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : list) {
			System.out.println(row.toString());
		}
		return null;

	}

	
 
	
}