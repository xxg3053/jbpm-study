<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<title>请假管理界面</title>
<link rel="stylesheet" href="//cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap.min.css" />
</head>
<body>
<div class="container">
    <ul class="nav nav-tabs" Style="margin-bottom:10px;">
	  <li role="presentation" class="active"><a href="<%=request.getContextPath()%>/leave/index">流程管理面板</a></li>
	  <li role="presentation"><a href="<%=request.getContextPath()%>/leave/deploy">发布</a></li>
	 
	   <li role="presentation" class="dropdown">
	    <a class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
	      	您好，${userName} <span class="caret"></span>
	    </a>
	    <ul class="dropdown-menu">
	      <li role="presentation"><a href="<%=request.getContextPath()%>/user/logout">退出</a></li>
	    </ul>
	  </li>
	</ul>
	
		
	
	<!-- 流程定义 -->
	<div class="panel panel-primary">
		<div class="panel-heading">流程定义</div>
	  <div class="panel-body">
		<table class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th>ID</th>
					<th>Name</th>
					<th>版本</th>
					<th>操作</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="item" items="${processDef}">
					<tr>
						<td>${item.id}</td>
						<td>${item.name}</td>
						<td>${item.version })</td>
						<td><a href="<%=request.getContextPath()%>/leave/del?deploymentId=${item.deploymentId}">删除</a>
						<a href="<%=request.getContextPath()%>/leave/start?id=${item.id}">发起</a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	</div>
<!-- 流程实例 -->
	<div class="panel panel-success">
		<div class="panel-heading">流程实例</div>
	  <div class="panel-body">
		<table class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th>ID</th>
					<th>Activity</th>
					<th>state</th>
					<th>操作</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="item" items="${piList}">
					<tr>
						<td>${item.id}</td>
						<td>${item.activityName}</td>
						<td>${item.state}</td>
						<td><a href="<%=request.getContextPath()%>/leave/delpi?id=${item.id}">删除</a>
						<a href="<%=request.getContextPath()%>/leave/view?id=${item.id}">查看</a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	</div>
	
		<!-- 待办任务 -->	
	<div class="panel panel-danger">
		<div class="panel-heading">待办任务</div>
	  <div class="panel-body">
		<table class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th>ID</th>
					<th>Name</th>
					<th>操作</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="item" items="${taskList}">
					<tr>
						<td>${item.id}</td>
						<td>${item.name}</td>
						<td><a href="<%=request.getContextPath()%>/${item.formResourceName}?id=${item.id}">处理</a></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	</div>


		<!-- 历史记录 -->	
	<div class="panel panel-info">
		<div class="panel-heading">历史记录</div>
	  <div class="panel-body">
		<table class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th>ID</th>
					<th>用户</th>
					<th>开始</th>
					<th>结束</th>
					<th>时长(s)</th>
					<th>状态</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="item" items="${hTaskList}">
					<tr>
						<td>${item.id}</td>
						<td>${item.assignee}</td>
						<td>${item.createTime}</td>
						<td>${item.endTime}</td>
						<td>${item.duration}</td>
						<td>${item.state}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	</div>
	
</div>
<script src="//cdn.bootcss.com/jquery/2.1.4/jquery.min.js"></script>
<script src="//cdn.bootcss.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
</body>
</html>