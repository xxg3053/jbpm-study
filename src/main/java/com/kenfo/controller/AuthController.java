package com.kenfo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.history.HistoryTask;
import org.jbpm.api.model.Activity;
import org.jbpm.api.model.Transition;
import org.jbpm.api.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.inject.internal.Maps;
import com.kenfo.service.JBPMService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
@RequestMapping("/auth")
@Api(basePath = "/",value = "/auth", description = "认证流程", position = 1)
public class AuthController {
	
	private static Logger log = LoggerFactory.getLogger(AuthController.class);
	private JBPMService jBPMService;
	
	public JBPMService getjBPMService() {
		return jBPMService;
	}
	@Autowired
	public void setjBPMService(JBPMService jBPMService) {
		this.jBPMService = jBPMService;
	}
	
	@ResponseBody
	@RequestMapping(value="/setflow/{flowName}",method=RequestMethod.GET)
	@ApiOperation(value = "设置流程", httpMethod = "GET", response = Map.class, 
	notes = "目前流程有两种：三成认证(three)和两次认证(two)",position=1)
	public Map<String,Object> setFlow(
			@ApiParam(required = true, name = "flowName", value = "流程对应的名称") @PathVariable String flowName
			){
		//session.setAttribute("flowName", flowName);
		List<ProcessDefinition> pdList = jBPMService.getPdByKey(flowName);
		for(ProcessDefinition df:pdList){
			jBPMService.deleteDeploymentCascade(df.getDeploymentId());
		}
		 jBPMService.deployNew(flowName+".jpdl.xml");
		 
		Map<String,Object> result = Maps.newHashMap();
		result.put("flowName", flowName);
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value="/start/{flowName}/{userName}",method=RequestMethod.GET)
	@ApiOperation(value = "开始一个流程", httpMethod = "GET", response = Map.class, 
		notes = "根据用户和流程名称创建流程实例",position=3)
	public Map<String,Object> start(
			@ApiParam(required = true, name = "userName", value = "用户名") @PathVariable String userName,
			@ApiParam(required = true, name = "flowName", value = "流程名称") @PathVariable String flowName,
			Model model){
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
	@ResponseBody
	@RequestMapping(value="/tasks/{flowName}/{userName}",method=RequestMethod.GET)
	@ApiOperation(value = "获取流程导航", httpMethod = "GET", response = Map.class, 
	notes = "返回当前节点以及节点流程",position=2)
	public Map<String,Object> getActivitiesJson(
			@ApiParam(required = true, name = "flowName", value = "流程名称") @PathVariable String flowName,
			@ApiParam(required = true, name = "userName", value = "用户名") @PathVariable String userName

			){
		
		return  jBPMService.getAssigneeActivities(userName,flowName);
	}
	@ResponseBody
	@RequestMapping(value="/reply/{flowName}/{userName}/{taskId}",method=RequestMethod.GET)
	@ApiOperation(value = "完成一个流程", httpMethod = "GET", response = Map.class, 
	notes = "根据taskId完成一个任务，返回下一个任务url以及是否为最后一个任务",position=4)
	public Map<String,Object> reply(
			@ApiParam(required = true, name = "flowName", value = "流程名称") @PathVariable String flowName,
			@ApiParam(required = true, name = "userName", value = "用户名") @PathVariable String userName,
			@ApiParam(required = true, name = "taskId", value = "任务ID") @PathVariable String taskId
			
			){
		
		Task task = jBPMService.getTask(taskId);
		String activityName = task.getActivityName();
		Activity at = jBPMService.getActivity(activityName,flowName);
		Transition transition = at.getDefaultOutgoingTransition();
		System.out.println("==="+transition);
		jBPMService.addReply(taskId, transition.getName());
		String nextTaskId = "";
		boolean isEnd = false;
		if("to end1".equals(transition.getName())){
			isEnd = true;
		}else{
			List<Task> taskList = jBPMService.getTaskListByPerson(userName);
			nextTaskId =  taskList.get(0).getId();
		}
		
		Activity destination =  transition.getDestination();
		Map<String,Object> result = Maps.newHashMap();
		result.put("isEnd", isEnd);
		//result.put("url", "leave/doBoss?taskId="+taskId);
		result.put("url", destination.getName());
		result.put("newTaskId", nextTaskId);
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value="/history/all/{userName}",method=RequestMethod.GET)
	@ApiOperation(value = "获取所有历史数据", httpMethod = "GET", response = Map.class, 
	notes = "返回流程定义，流程实例，任务，历史任务",position=10)
	public  Map<String,Object> historyJson(
			@ApiParam(required = true, name = "userName", value = "用户名")  @PathVariable String userName){
		//流程定义
		List<ProcessDefinition> pdList = jBPMService.getAllPd();
		//流程实例
		List<ProcessInstance> piList = jBPMService.getAllPI();
		//
		List<Task> taskList = jBPMService.getTaskListByPerson(userName);
		
		List<HistoryTask> hTaskList = jBPMService.getHistoryTaskListByPerson(userName);
		
		Map<String,Object> model = Maps.newHashMap();
		model.put("processDef", pdList.size());
		model.put("piList", piList.size());
		model.put("taskList", taskList.size());
		model.put("hTaskList", hTaskList.size());
		model.put("userName", userName);
		return model;
	}
}
