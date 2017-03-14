package test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import model.Job;

@Controller
@RequestMapping("/test")
public class JobController {

    @RequestMapping(method = RequestMethod.POST, value = "job")
    @ResponseBody
    public Job math(@RequestBody final Job request) {
        final Result result = new Result();
        System.out.println(request.getBuildAgent());
        return request;
    }

}