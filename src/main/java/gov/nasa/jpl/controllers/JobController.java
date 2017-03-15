package gov.nasa.jpl.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import gov.nasa.jpl.model.Job;

@Controller
@RequestMapping("/test")
public class JobController {

    @RequestMapping(method = RequestMethod.POST, value = "job")
    @ResponseBody
    public Job math(@RequestBody final Job request) {
        System.out.println(request.getBuildAgent());
        return request;
    }

}