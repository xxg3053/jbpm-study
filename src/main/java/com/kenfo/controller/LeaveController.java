package com.kenfo.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.jbpm.api.Configuration;
import org.jbpm.api.Execution;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
	IdentityService identityService = processEngine.getIdentityService();
	
	@RequestMapping(value="/index",method=RequestMethod.GET)
	public String index(HttpSession session,  Map<String,Object> model){
		String userName = (String)session.getAttribute("userName");
		if(StringUtils.isEmpty(userName)){
			//登录
			return "redirect:/user/login";
		}
		return "leave/index";
	}
	
	
	@RequestMapping(value="/history",method=RequestMethod.GET)
	public String history(HttpSession session,  Map<String,Object> model){
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
		return "leave/all";
	}
	
	@RequestMapping(value="/start",method=RequestMethod.GET)
	public String start(String id,String userName,Model model){
		//查询用户是否存在未完成的流程		
	   List<Task> taskList = taskService.findPersonalTasks(userName);
		if(taskList.size() == 0){
			 //定义
			 List<ProcessDefinition> pdList = repositoryService.createProcessDefinitionQuery().list();
			 if(pdList.size()==0){
				//流程发布
				 repositoryService.createDeployment().addResourceFromClasspath("leave.jpdl.xml").deploy();
			 }
			
			 ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().list().get(0);
			//如果没有则流程开始
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("owner", userName);
			//开始一个实例
			List<ProcessInstance> piList = executionService.createProcessInstanceQuery().list();
			if(piList.size()==0){
				executionService.startProcessInstanceById(pd.getId(), map);
			}
			

		}
		
		RepositoryService repositoryService = processEngine.getRepositoryService();
		ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().list().get(0);
		ProcessDefinitionImpl definitionimpl = (ProcessDefinitionImpl)definition;
		List<? extends Activity> list = definitionimpl.getActivities();
		for (Activity activity : list) {
			if(activity.getType()=="task"){
				System.out.println(activity.getName());
			}
		}
		
		List<Task> tasks = taskService.findPersonalTasks(userName);
		model.addAttribute("taskId",tasks.get(0).getId());
		
		
		return "leave/start2";
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
	/**
	 * 工作流中配置的form
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/request",method=RequestMethod.GET)
	public String request(String taskId,Map<String,Object> model){
		//密码验证
		model.put("taskId", taskId);
		Task task = taskService.findPersonalTasks("aa").get(0);
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
	@RequestMapping(value="/doRequest",method=RequestMethod.POST)
	public String doRequest(String taskId,HttpSession session,Map<String,Object> model){
		//System.out.println("taskId:"+taskId);
		String result = "to 手机号验证";//transitions的name
		taskService.completeTask(taskId,result);
		
		String userName = (String)session.getAttribute("userName");
		List<Task> taskList = taskService.findPersonalTasks(userName);
		model.put("taskList", taskList);
		model.put("taskId", taskList.get(0).getId());
		//return "leave/doManager";
		return "phone/manager";
	}
	
	@RequestMapping(value="/doManager",method=RequestMethod.POST)
	public String doManager(String taskId,String phoneNo,HttpSession session,
			Map<String,Object> model){
		//经理审批
		System.out.println("taskId："+taskId);
		System.out.println("手机号验证："+phoneNo);
		String result = "to 身份验证";
		taskService.completeTask(taskId,result);
		
		String userName = (String)session.getAttribute("userName");
		List<Task> taskList = taskService.findPersonalTasks(userName);
		model.put("taskId", taskList.get(0).getId());
		
		return "card/doBoss";
	}
	
	/**
	 * 工作流中配置的form
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/boss",method=RequestMethod.GET)
	public String boss(String taskId,Map<String,Object> model){
		System.out.println("boss taskId:"+taskId);
		//老板审批
		model.put("taskId", taskId);
		return "card/boss";
	}
	
	@RequestMapping(value="/end",method=RequestMethod.GET)
	public String end(String taskId,HttpSession session,Map<String,Object> model){
		//老板审批
		taskService.completeTask(taskId);
		
		return "leave/start2";
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
