package com.kenfo.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.jbpm.api.Configuration;
import org.jbpm.api.ExecutionService;
import org.jbpm.api.HistoryService;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.RepositoryService;
import org.jbpm.api.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/mail")
public class MailController {

private static Logger log = LoggerFactory.getLogger(MailController.class);
	
	@RequestMapping(value="/index",method=RequestMethod.GET)
	public String index(){
		
		
		return "mail/index";
	}
	
	@RequestMapping(value="/send",method=RequestMethod.POST)
	public String send(String mail,String subtext,HttpSession session){
		//流程引擎
		ProcessEngine processEngine = Configuration.getProcessEngine();
		ExecutionService executionService = processEngine.getExecutionService();
			//发布流程
		   /**/ String deploymentId = processEngine.getRepositoryService().createDeployment()
		    .addResourceFromClasspath("mail.jpdl.xml").deploy();
		    System.out.println("流程发布 ID:"+deploymentId);
		    
		    String addressee = mail;
		    String newspaper=subtext;
		    Date date=new Date();
		    String details="继续加油吧 ";
		    
		    Map<String,Object> variables =new HashMap<String,Object>();
		    variables.put("addressee", addressee);
		    variables.put("newspaper", newspaper);
		    variables.put("date", date);
		    variables.put("details", details);
		    
		    //启动一个流程实例
		    ProcessInstance processInstance = executionService.startProcessInstanceByKey("TemplateMail",variables);
		    System.out.println("流程实例 ID:" + processInstance.getId());
		
		    return "redirect:index";
	}
}
