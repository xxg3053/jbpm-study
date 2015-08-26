package com.kenfo.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpm.api.Configuration;
import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.RepositoryService;
import org.jbpm.api.history.HistoryTask;
import org.jbpm.api.model.Activity;
import org.jbpm.api.model.ActivityCoordinates;
import org.jbpm.api.task.Task;
import org.jbpm.pvm.internal.model.ProcessDefinitionImpl;
import org.springframework.stereotype.Service;

import com.google.inject.internal.Lists;
import com.google.inject.internal.Maps;
import com.kenfo.service.JBPMService;
import com.kenfo.util.JBPMUtil;

@Service("JBPMService")
public class JBPMServiceImpl implements JBPMService {
	
	private JBPMUtil jBPMUtil = new JBPMUtil(Configuration.getProcessEngine());

	
	//发布流程
	public String  deployNew(String resourceName) {
		return jBPMUtil.deployNew(resourceName);
	}
	

	//发布流程
	public String  deployZipNew(String resourceZipName) {
		return jBPMUtil.deployZipNew(resourceZipName);
	}

	//开始流程实例
	public ProcessInstance startPI(String id,Map<String,Object> map) {
		return jBPMUtil.startPIById(id, map);
		
	}

	//根据id完成
	public void addReply(String taskId) {
		jBPMUtil.completeTask(taskId);
	}
	
	
	
	@Override
	public void addReply(String taskId, String outcome) {
		jBPMUtil.completeTask(taskId, outcome);
		
	}


	//由manager或者boss完成会签
	public void checkReply(String taskId, int result,String name) {
		System.out.println("name="+name);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		if(result==1){
			map.put("RESULT", "同意");
		}else{
			map.put("RESULT", "不同意");
		}
		
		jBPMUtil.signalExecutionById(jBPMUtil.getTask(taskId).getExecutionId(), map);
	}


	public List<Task> getTaskListByPerson(String assignee) {
		
		return jBPMUtil.findPersonalTasks(assignee);
	}


	@Override
	public List<HistoryTask> getHistoryTaskListByPerson(String assignee) {
		// TODO Auto-generated method stub
		return jBPMUtil.findPersonalHistoryTasks(assignee);
	}


	public List<ProcessDefinition> getAllPd() {
		return jBPMUtil.getAllPdList();
	}


	@Override
	public List<ProcessInstance> getAllPI() {
		// TODO Auto-generated method stub
		return jBPMUtil.getAllPiList();
	}


	@Override
	public ProcessInstance getPIById(String id) {
		// TODO Auto-generated method stub
		return jBPMUtil.getExecutionService().findProcessInstanceById(id);
	}


	@Override
	public ActivityCoordinates getActivityCoordinates(
			String processDefinitionId, Set<String> activityNames) {
		// TODO Auto-generated method stub
		return jBPMUtil.getRepositoryService().getActivityCoordinates(processDefinitionId, activityNames.iterator().next());
	}


	@Override
	public void deleteDeploymentCascade(String deploymentId) {
		jBPMUtil.getRepositoryService().deleteDeploymentCascade(deploymentId);
	}


	@Override
	public void deleteProcessInstanceCascade(String processInstanceId) {
		jBPMUtil.getExecutionService().deleteProcessInstanceCascade(processInstanceId);
		
	}


	@Override
	public Map<String, Object> getAssigneeActivities(String assignee) {
		RepositoryService repositoryService = jBPMUtil.getRepositoryService();
		ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().list().get(0);
		ProcessDefinitionImpl definitionimpl = (ProcessDefinitionImpl)definition;
		List<? extends Activity> list = definitionimpl.getActivities();
		Map<String,Object> result = Maps.newHashMap();
		List<String> as = Lists.newArrayList();
		for (Activity activity : list) {
			if(activity.getType()=="task"){
				as.add(activity.getName());
			}
		}
		Task task = jBPMUtil.findPersonalTasks(assignee).get(0);
		String activityName = task.getActivityName();
		
		result.put("active", activityName); //当前活动节点
		result.put("activities", as); //该用户所有节点
		
		return result;
	}
  
	
	
	
}
