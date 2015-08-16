package com.kenfo.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kenfo.service.JBPMService;

@Controller
@RequestMapping("/sign")
public class CountersignController {
	private static final String resourceZipName ="huiqian.jpdl.xml";
	
	private List<Task> taskList;
	private JBPMService jBPMService;
	
	@RequestMapping(value="/index",method=RequestMethod.GET)
	public String index(HttpSession session,Map<String,Object> model){
		String userName = (String)session.getAttribute("userName");
		taskList = jBPMService.getTasksList(userName);
		model.put("taskList", taskList);
		model.put("userName", userName);
		return "sign/index";
	}
	
	@RequestMapping(value="/start",method=RequestMethod.GET)
	public String start(HttpSession session,Map<String,Object> model){
		String userName = (String)session.getAttribute("userName");
		if(userName.equals("manager")||userName.equals("boss")){
			System.out.println("当前登录用户为审核人！！！");
		}else{
			System.out.println("当前登录用户为申请人！！！");
			List<ProcessDefinition> pdList=jBPMService.getAllPd();
			if(pdList.size()==0){									//如果没有流程定义，则发布
				jBPMService.deployNew(resourceZipName);
			}
			pdList=jBPMService.getAllPd();
			jBPMService.startPI(userName,pdList.get(0).getId());	//取第一条流程定义，开始流程实例
			
		}
		return "redirect:index"; 
	}

	@RequestMapping(value="/reply",method=RequestMethod.GET)
	public String reply(String taskId,Map<String,Object> model){
		jBPMService.addReply(taskId);
		return "redirect:index";
	}
	@RequestMapping(value="/check",method=RequestMethod.GET)
	public String check(String taskId,Integer result,String userName,Map<String,Object> model){
		
		jBPMService.checkReply(taskId,result,userName);
		
		return "redirect:index";
	}

	public JBPMService getjBPMService() {
		return jBPMService;
	}
	@Autowired
	public void setjBPMService(JBPMService jBPMService) {
		this.jBPMService = jBPMService;
	}
	
}
