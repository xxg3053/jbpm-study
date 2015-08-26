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
import org.jbpm.api.IdentityService;
import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.RepositoryService;
import org.jbpm.api.TaskService;
import org.jbpm.api.history.HistoryTask;
import org.jbpm.api.model.Activity;
import org.jbpm.api.model.ActivityCoordinates;
import org.jbpm.api.task.Task;
import org.jbpm.pvm.internal.model.ExecutionImpl;
import org.jbpm.pvm.internal.model.ProcessDefinitionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.inject.internal.Lists;
import com.google.inject.internal.Maps;
import com.kenfo.service.JBPMService;
import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
@RequestMapping("/leave")
@Api(basePath = "/",value = "/leave", description = "请假流程", position = 3)
@ApiIgnore
public class LeaveController {

	private static Logger log = LoggerFactory.getLogger(LeaveController.class);
	
	private JBPMService jBPMService;
	
	public JBPMService getjBPMService() {
		return jBPMService;
	}
	@Autowired
	public void setjBPMService(JBPMService jBPMService) {
		this.jBPMService = jBPMService;
	}
	
	@RequestMapping(value="/index",method=RequestMethod.GET)
	public String index(HttpSession session,  Map<String,Object> model){
		String userName = (String)session.getAttribute("userName");
		String flowName = (String)session.getAttribute("flowName");
		if(StringUtils.isEmpty(userName)){
			//登录
			return "redirect:/user/login";
		}
		
		model.put("userName", userName);
		model.put("flowName", flowName);
		return "leave/index";
	}
	@RequestMapping(value="/setting",method=RequestMethod.GET)
	public String setting(HttpSession session,  Map<String,Object> model){
		
		return "leave/setting";
	}
	@RequestMapping(value="/doSetting",method=RequestMethod.POST)
	public String doSetting(String flowName,HttpSession session,  Map<String,Object> model){
		session.setAttribute("flowName", flowName);
		List<ProcessDefinition> pdList = jBPMService.getPdByKey(flowName);
		for(ProcessDefinition df:pdList){
			jBPMService.deleteDeploymentCascade(df.getDeploymentId());
		}
		 jBPMService.deployNew(flowName+".jpdl.xml");
		return "redirect:index";
	}
	
	@RequestMapping(value="/history",method=RequestMethod.GET)
	public String history(HttpSession session,  Map<String,Object> model){
		String userName = (String)session.getAttribute("userName");
		if(StringUtils.isEmpty(userName)){
			//登录
			return "redirect:/user/login";
		}
		//流程定义
		List<ProcessDefinition> pdList = jBPMService.getAllPd();
		//流程实例
		List<ProcessInstance> piList = jBPMService.getAllPI();
		//
		List<Task> taskList = jBPMService.getTaskListByPerson(userName);
		
		List<HistoryTask> hTaskList = jBPMService.getHistoryTaskListByPerson(userName);
		
		model.put("processDef", pdList);
		model.put("piList", piList);
		model.put("taskList", taskList);
		model.put("hTaskList", hTaskList);
		model.put("userName", userName);
		return "leave/all";
	}
	
	/*******流程启动**************************/
	@ResponseBody
	@RequestMapping(value="/start",method=RequestMethod.GET)
	@ApiOperation(value = "开始一个流程", httpMethod = "GET", response = Map.class, 
		notes = "根据用户和流程名称开启流程，目前流程有两种：三成认证(three)和两次认证(two)")
	public Map<String,Object> start(
			@ApiParam(required = true, name = "userName", value = "用户名") String userName,
			@ApiParam(required = true, name = "flowName", value = "流程名称") String flowName,
			Model model){
		if(StringUtils.isEmpty(flowName)){
			flowName = "three";
		}
		//查询用户是否存在未完成的流程		
	   List<Task> taskList = jBPMService.getTaskListByPerson(userName);
		if(taskList.size() == 0){
			 //定义
			 List<ProcessDefinition> pdList = jBPMService.getPdByKey(flowName);
			 if(pdList.size()==0){
				//流程发布
				 jBPMService.deployNew(flowName+".jpdl.xml");
			 }
			
			 ProcessDefinition pd = jBPMService.getPdByKey(flowName).get(0);
			//如果没有则流程开始
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("owner", userName);
			//开始一个实例
			List<ProcessInstance> piList = jBPMService.getPiByKey(flowName);
			if(piList.size()==0){
				jBPMService.startPI(pd.getId(), map);
			}
		}
		List<Task> tasks = jBPMService.getTaskListByPerson(userName);
		
		Map<String,Object> result = Maps.newHashMap();
		Task ct = tasks.get(0);
		//System.out.println(ct);
		
		result.put("taskId", ct.getId());
		result.put("activityName", ct.getActivityName());
		result.put("description", ct.getDescription());
		result.put("formResourceName", ct.getFormResourceName());
		
		return result;
	}
	/*******流程导航**************************/
	@ResponseBody
	@RequestMapping(value="/tasks/{userName}",method=RequestMethod.GET)
	@ApiOperation(value = "获取流程导航", httpMethod = "GET", response = Map.class, notes = "获取流程导航")
	public Map<String,Object> getActivitiesJson(
			@ApiParam(required = true, name = "userName", value = "用户名") @PathVariable String userName,
			HttpSession session
			){
		
		
		return  jBPMService.getAssigneeActivities(userName,(String)session.getAttribute("flowName"));
	}
	
	
	/*******流程判断**************************/
	@RequestMapping(value="/request",method=RequestMethod.GET)
	public String request(String taskId,Map<String,Object> model){
		///查询出当前流程的状态
		//根据状态跳转到不同界面
		model.put("taskId", taskId);
		Task task = jBPMService.getTaskListByPerson("aa").get(0);
		
		
		String activityName = task.getActivityName();
		System.out.println("activityName:"+activityName);
		if("手机号验证".equals(activityName)){
			return "phone/manager";
		}else if("身份验证".equals(activityName)){
			return "card/doBoss";
		}else{
			return "password/request";
		}
	}
	
	/*******手机号验证流程开始**************************/
	@RequestMapping(value="/doRequest",method=RequestMethod.POST)
	public String doRequest(String taskId,HttpSession session,Map<String,Object> model){
		//System.out.println("taskId:"+taskId);
		String result = "to 手机号验证";//transitions的name
		jBPMService.addReply(taskId,result);
		String userName = (String)session.getAttribute("userName");
		List<Task> taskList = jBPMService.getTaskListByPerson(userName);
		model.put("taskList", taskList);
		model.put("taskId", taskList.get(0).getId());
		return "phone/manager";
	}
	
	
	/*******身份验证流程开始**************************/
	@ResponseBody
	@RequestMapping(value="/doManager",method=RequestMethod.GET)
	public Map<String,Object> doManager(String taskId,String phoneNo,
			HttpSession session,
			Map<String,Object> model){
		//
		System.out.println("taskId："+taskId);
		System.out.println("手机号验证："+phoneNo);
		String flowName = (String)session.getAttribute("flowName");
		System.out.println("flowName"+flowName);
		Map<String,Object> result = Maps.newHashMap();
		if("three".equals(flowName)){
			//身份验证
			jBPMService.addReply(taskId, "to 身份验证");
			
			String userName = (String)session.getAttribute("userName");
			List<Task> taskList = jBPMService.getTaskListByPerson(userName);
			taskId =  taskList.get(0).getId();
			model.put("taskId",taskId);
			
			
			result.put("isEnd", "false");
			result.put("url", "leave/doBoss?taskId="+taskId);
			
			return result;
		}else if("two".equals(flowName)){
			jBPMService.addReply(taskId, "to end1");
			result.put("isEnd", "true");
			result.put("url", "");
			return result;
		}else{
			return result;
		}
		//结束
		
	}
	@RequestMapping(value="/doBoss",method=RequestMethod.GET)
	public String doBoss(String taskId,Map<String,Object> model){
		//
		model.put("taskId", taskId);
		return "card/doBoss";
	}
	@RequestMapping(value="/boss",method=RequestMethod.GET)
	public String boss(String taskId,Map<String,Object> model){
		System.out.println("boss taskId:"+taskId);
		//
		model.put("taskId", taskId);
		return "card/boss";
	}
	/*******身份验证流程结束**************************/
	
	//流程结束调用
	@RequestMapping(value="/end/{taskId}",method=RequestMethod.GET)
	public @ResponseBody String end(@PathVariable String taskId,Map<String,Object> model){
		//身份认证
		jBPMService.addReply(taskId);
		//判断流程中这一步是不是最后一步
		
		return "end";
	}
	
	@RequestMapping(value="/view",method=RequestMethod.GET)
	public String view(String id,Map<String,Object> model){
		
		ProcessInstance processInstance = jBPMService.getPIById(id);
		Set<String> activityNames = processInstance.findActiveActivityNames(); // 获取实例执行到的当前节点的名称
		
		ActivityCoordinates ac = jBPMService.getActivityCoordinates(processInstance.getProcessDefinitionId(), activityNames);
		model.put("ac", ac);
		
		return "leave/view";
	}
	

	@RequestMapping(value="/del",method=RequestMethod.GET)
	public String del(String deploymentId,Map<String,Object> model){
		//流程删除
		log.debug("删除流程deploymentId:"+deploymentId);
		if(StringUtils.isNotEmpty(deploymentId)){ 
			jBPMService.deleteDeploymentCascade(deploymentId);
		}
		
		return "redirect:index";
	}
	
	@RequestMapping(value="/delpi",method=RequestMethod.GET)
	public String delpi(String id,Map<String,Object> model){
		//流程删除
		log.debug("删除流程实例id:"+id);
		if(StringUtils.isNotEmpty(id)){
			jBPMService.deleteProcessInstanceCascade(id);
		}
		
		return "redirect:index";
	}
	
}
