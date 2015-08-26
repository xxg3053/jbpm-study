package com.kenfo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.history.HistoryTask;
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
	@RequestMapping(value="/start",method=RequestMethod.GET)
	@ApiOperation(value = "开始一个流程", httpMethod = "GET", response = Map.class, 
		notes = "根据用户和流程名称开启流程，目前流程有两种：三成认证(leave)和两次认证(two)")
	public Map<String,Object> start(
			@ApiParam(required = true, name = "userName", value = "用户名") String userName,
			@ApiParam(required = true, name = "flowName", value = "流程名称") String flowName,
			Model model){
		if(StringUtils.isEmpty(flowName)){
			flowName = "leave";
		}
		//查询用户是否存在未完成的流程		
	   List<Task> taskList = jBPMService.getTaskListByPerson(userName);
		if(taskList.size() == 0){
			 //定义
			 List<ProcessDefinition> pdList = jBPMService.getAllPd();
			 if(pdList.size()==0){
				//流程发布
				 jBPMService.deployNew(flowName+".jpdl.xml");
			 }
			
			 ProcessDefinition pd = jBPMService.getAllPd().get(0);
			//如果没有则流程开始
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("owner", userName);
			//开始一个实例
			List<ProcessInstance> piList = jBPMService.getAllPI();
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
	@RequestMapping(value="/tasks/{userName}",method=RequestMethod.GET)
	@ApiOperation(value = "获取流程导航", httpMethod = "GET", response = Map.class, 
	notes = "返回当前节点以及节点流程")
	public Map<String,Object> getActivitiesJson(
			@ApiParam(required = true, name = "userName", value = "用户名") @PathVariable String userName){
		
		return  jBPMService.getAssigneeActivities(userName);
	}
	@ResponseBody
	@RequestMapping(value="/reply",method=RequestMethod.GET)
	@ApiOperation(value = "完成一个流程", httpMethod = "GET", response = Map.class, 
	notes = "根据taskId完成一个任务，返回下一个任务url以及是否为最后一个任务")
	public Map<String,Object> reply(
			@ApiParam(required = true, name = "taskId", value = "任务ID") String taskId
			){
		
		Map<String,Object> result = Maps.newHashMap();
		result.put("isEnd", "false");
		result.put("url", "leave/doBoss?taskId="+taskId);
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value="/history/all",method=RequestMethod.GET)
	@ApiOperation(value = "获取所有历史数据", httpMethod = "GET", response = Map.class, 
	notes = "返回流程定义，流程实例，任务，历史任务")
	public  Map<String,Object> historyJson(
			@ApiParam(required = true, name = "userName", value = "用户名") String userName){
		//流程定义
		List<ProcessDefinition> pdList = jBPMService.getAllPd();
		//流程实例
		List<ProcessInstance> piList = jBPMService.getAllPI();
		//
		List<Task> taskList = jBPMService.getTaskListByPerson(userName);
		
		List<HistoryTask> hTaskList = jBPMService.getHistoryTaskListByPerson(userName);
		
		Map<String,Object> model = Maps.newHashMap();
		model.put("processDef", pdList);
		model.put("piList", piList);
		model.put("taskList", taskList);
		model.put("hTaskList", hTaskList);
		model.put("userName", userName);
		return model;
	}
}
