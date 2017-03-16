package gov.nasa.jpl.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import gov.nasa.jpl.model.Job;

@Controller
public class VeEndpointContoller {

	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs/{jobSysmlID}", method = RequestMethod.GET)
	@ResponseBody
	public String getJob(@PathVariable String projectID, @PathVariable String refID, @PathVariable String jobSysmlID) {
		return "job" + "\n" + projectID + "\n" + refID + "\n" + jobSysmlID;
	}

	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs/{jobSysmlID}/instances", method = RequestMethod.GET)
	@ResponseBody
	public String getJobInstances(@PathVariable String projectID, @PathVariable String refID, @PathVariable String jobSysmlID) {
		return "job instance" + "\n" + projectID + "\n" + refID + "\n" + jobSysmlID;
	}

	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs", method = RequestMethod.POST)
	@ResponseBody
	public Job createJob(@PathVariable String projectID, @PathVariable String refID, @RequestBody final Job job) {
		System.out.println("job" + "\n" + projectID + "\n" + refID + "\n");
		return job;
	}

	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs/instances", method = RequestMethod.POST)
	@ResponseBody
	public Job runJob(@PathVariable String projectID, @PathVariable String refID, @RequestBody final Job job) {
		System.out.println("job" + "\n" + projectID + "\n" + refID + "\n");
		return job;
	}

	@RequestMapping(value = "/projects/{projectID}/refs/{refID}/jobs/{jobSysmlID}", method = RequestMethod.DELETE)
	@ResponseBody
	public String deleteJob(@PathVariable String projectID, @PathVariable String refID, @PathVariable String jobSysmlID) {
		System.out.println("job" + "\n" + projectID + "\n" + refID + "\n");
		return "job deleted" + "\n" + projectID + "\n" + refID + "\n" + jobSysmlID;
	}

}