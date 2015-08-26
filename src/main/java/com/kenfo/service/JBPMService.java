package com.kenfo.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.history.HistoryTask;
import org.jbpm.api.model.Activity;
import org.jbpm.api.model.ActivityCoordinates;
import org.jbpm.api.task.Task;
import org.springframework.stereotype.Service;

@Service
public interface JBPMService {
	public String deployZipNew(String resourceName);		//发布这条流程定义
	
	public String deployNew(String resourceZipName);		//发布这条流程定义
	
	public ProcessInstance startPI(String id,Map<String,Object> map);		//开始一个流程实例
	
	public List<Task> getTaskListByPerson(String assignee);	//所有任务列表
	
	public Task getTask(String taskId);
	
	public Activity getActivity(String activityName,String key);
	
	public List<HistoryTask> getHistoryTaskListByPerson(String assignee);//历史任务
	
	public void checkReply(String taskId,int result,String name);		//经理完成任务，返回一个result结果
	
	public void addReply(String taskId);			//按照任务Id完成该任务
	
	public void addReply(String taskId,String outcome);
	
	public List<ProcessDefinition> getAllPd(); //获得所有流程定义
	public List<ProcessDefinition> getPdByKey(String key);
	public ProcessDefinition getPdById(String id);
	
	public List<ProcessInstance> getAllPI();//获取所有流程实例
	public List<ProcessInstance> getPiByKey(String key);
	
	public ProcessInstance getPIById(String id);//根据id获得流程实例
	
	public ActivityCoordinates getActivityCoordinates(String processDefinitionId,Set<String> activityNames);
	
	public void deleteDeploymentCascade(String deploymentId);
	
	public void deleteProcessInstanceCascade(String processInstanceId);
	
	public Map<String,Object> getAssigneeActivities(String assignee,String key);
}
