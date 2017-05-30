package com.mmall.controller.testcontroller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 这是一个测试类
 * 
 * @author Administrator
 * 
 */
@Controller
@RequestMapping(value = "/test/")
public class Natapp_test {

	public Logger logger = LoggerFactory.getLogger(Natapp_test.class);

	@RequestMapping(value = "test.do", method = RequestMethod.GET)
	@ResponseBody
	public String test(String str) {

		logger.info("testinfo");
		logger.warn("testwarn");
		logger.error("testerror");

		return "testValue:" + str;
	}
}
