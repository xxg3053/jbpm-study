<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<title>会签管理界面</title>
<link rel="stylesheet" href="//cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap.min.css" />
</head>
<body>
<div class="container">
    <ul class="nav nav-tabs" Style="margin-bottom:10px;">
	  <li role="presentation" class="active"><a href="<%=request.getContextPath()%>/sign/index">会签管理面板</a></li>
	  <li role="presentation"><a href="<%=request.getContextPath()%>/sign/start">发布</a></li>
	  
	  <li role="presentation" class="dropdown">
	    <a class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
	      	<span class="glyphicon glyphicon-user" aria-hidden="true"></span> 您好，${userName} <span class="caret"></span>
	    </a>
	    <ul class="dropdown-menu">
	      <li role="presentation"><a href="<%=request.getContextPath()%>/user/logout">退出</a></li>
	    </ul>
	  </li>
	  
	</ul>
	<div class="panel panel-primary">
	  <div class="panel-heading">待办任务</div>
		  <div class="panel-body">
				<table class="table table-striped table-bordered table-hover">
					<thead>
						<tr>
							<th>ID</th>
							<th>Name</th>
							<th>assignee</th>
							<th>当前操作用户</th>
							<th>操作</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="item" items="${taskList}">
							<tr>
								<td>${item.id}</td>
								<td>${item.name}</td>
								<td>${item.assignee}</td>
								<td>${userName}</td>
								<td>
									<c:if test="${ userName!='manager' && userName!='boss'}">
										<a href="<%=request.getContextPath()%>/sign/reply?taskId=${item.id}">申请</a>
									</c:if>
									<c:if test="${ userName=='manager' || userName=='boss'}">
										<a href="<%=request.getContextPath()%>/sign/check?taskId=${item.id}&result=1&userName=${userName}">同意</a>
										<a href="<%=request.getContextPath()%>/sign/check?taskId=${item.id}&result=2&userName=${userName}">拒绝</a>
									</c:if>
								</td>
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