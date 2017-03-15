package gov.nasa.jpl.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import test.Request;
import test.Result;

@Controller
@RequestMapping("/test")
public class TestController {

    @RequestMapping(method = RequestMethod.POST, value = "math")
    @ResponseBody
    public Result math(@RequestBody final Request request) {
        final Result result = new Result();
        result.setAddition(request.getLeft() + request.getRight());
        result.setSubtraction(request.getLeft() - request.getRight());
        result.setMultiplication(request.getLeft() * request.getRight());
        return result;
    }

}