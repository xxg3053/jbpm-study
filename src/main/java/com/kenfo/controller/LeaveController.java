package com.kenfo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.jbpm.api.Configuration;
import org.jbpm.api.ExecutionService;
import org.jbpm.api.HistoryService;
import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.RepositoryService;
import org.jbpm.api.TaskService;
import org.jbpm.api.history.HistoryTask;
import org.jbpm.api.model.ActivityCoordinates;
import org.jbpm.api.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.inject.internal.Maps;

@Controller
@RequestMapping("/leave")
public class LeaveController {

	private static Logger log = LoggerFactory.getLogger(LeaveController.class);
	
	//流程引擎
	ProcessEngine processEngine = Configuration.getProcessEngine();
	//流程仓库
	RepositoryService repositoryService = processEngine.getRepositoryService();
	
	ExecutionService executionService = processEngine.getExecutionService();
	TaskService taskService = processEngine.getTaskService();
	HistoryService historyService = processEngine.getHistoryService();
	
	@RequestMapping(value="/index",method=RequestMethod.GET)
	public String index(HttpSession session,  Map<String,Object> model){
		String userName = (String)session.getAttribute("userName");
		if(StringUtils.isEmpty(userName)){
			//登录
			return "redirect:/user/login";
		}
		//流程定义
		List<ProcessDefinition> pdList = repositoryService.createProcessDefinitionQuery().list();
		//流程实例
		List<ProcessInstance> piList = executionService.createProcessInstanceQuery().list();
		//
		List<Task> taskList = taskService.findPersonalTasks(userName);
		
		List<HistoryTask> hTaskList = historyService.createHistoryTaskQuery().assignee(userName).list();
		
		
		model.put("processDef", pdList);
		model.put("piList", piList);
		model.put("taskList", taskList);
		model.put("hTaskList", hTaskList);
		model.put("userName", userName);
		return "leave/index";
	}
	
	@RequestMapping(value="/deploy",method=RequestMethod.GET)
	public String deploy(Map<String,Object> model){
		//流程发布
		repositoryService.createDeployment().addResourceFromClasspath("leave.jpdl.xml").deploy();
		
		return "redirect:index";
	}
	
	@RequestMapping(value="/start",method=RequestMethod.GET)
	public String start(String id,HttpSession session,Map<String,Object> model){
		//流程开始
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("owner", session.getAttribute("userName"));
		map.put("address", "xxg3053@qq.com");
		executionService.startProcessInstanceById(id, map);
		
		return "redirect:index"; 
	}
	
	@RequestMapping(value="/del",method=RequestMethod.GET)
	public String del(String deploymentId,Map<String,Object> model){
		//流程删除
		log.debug("删除流程deploymentId:"+deploymentId);
		if(StringUtils.isNotEmpty(deploymentId)){ 
			repositoryService.deleteDeploymentCascade(deploymentId);
		}
		
		return "redirect:index";
	}
	
	@RequestMapping(value="/delpi",method=RequestMethod.GET)
	public String delpi(String id,Map<String,Object> model){
		//流程删除
		log.debug("删除流程实例id:"+id);
		if(StringUtils.isNotEmpty(id)){ 
			executionService.deleteProcessInstanceCascade(id);
		}
		
		return "redirect:index";
	}
	
	@RequestMapping(value="/request",method=RequestMethod.GET)
	public String request(String id,Map<String,Object> model){
		//申请
		
		return "leave/request";
	}
	@RequestMapping(value="/doRequest",method=RequestMethod.POST)
	public String doRequest(String taskId,String owner,Integer day,String reason,Map<String,Object> model){
		Map<String ,Object> map = Maps.newHashMap();
		map.put("day",day);
		map.put("reason", reason);
		//System.out.println("taskId:"+taskId);
		String result = "to 经理审批";//transitions的name 
		taskService.completeTask(taskId,result, map);
		return "redirect:index";
	}
	
	@RequestMapping(value="/manager",method=RequestMethod.GET)
	public String manager(String id,Map<String,Object> model){
		//经理审批
		model.put("day", taskService.getVariable(id, "day"));
		model.put("owner", taskService.getVariable(id, "owner"));
		model.put("reason", taskService.getVariable(id, "reason"));
		return "leave/manager";
	}
	
	@RequestMapping(value="/doManager",method=RequestMethod.POST)
	public String doManager(String taskId,Map<String,Object> model){
		//经理审批
		String result = "to exclusive1";
		taskService.completeTask(taskId,result);
		return "redirect:index";
	}
	@RequestMapping(value="/doBack",method=RequestMethod.GET)
	public String doBack(String taskId,Map<String,Object> model){
		//经理驳回
		String result = "驳回";
		taskService.completeTask(taskId,result);
		return "redirect:index";
	}
	@RequestMapping(value="/boss",method=RequestMethod.GET)
	public String boss(String id,Map<String,Object> model){
		//老板审批
		model.put("day", taskService.getVariable(id, "day"));
		model.put("owner", taskService.getVariable(id, "owner"));
		model.put("reason", taskService.getVariable(id, "reason"));
		return "leave/boss";
	}
	
	@RequestMapping(value="/doBoss",method=RequestMethod.POST)
	public String doBoss(String taskId,Map<String,Object> model){
		//老板审批
		taskService.completeTask(taskId);
		return "redirect:index";
	}
	
	@RequestMapping(value="/view",method=RequestMethod.GET)
	public String view(String id,Map<String,Object> model){
		
		ProcessInstance processInstance = executionService
				.findProcessInstanceById(id); // 根据ID获取流程实例
		Set<String> activityNames = processInstance
				.findActiveActivityNames(); // 获取实例执行到的当前节点的名称
		
		ActivityCoordinates ac = repositoryService.getActivityCoordinates(
				processInstance.getProcessDefinitionId(), activityNames
						.iterator().next());
		model.put("ac", ac);
		
		return "leave/view";
	}
	

	
}
