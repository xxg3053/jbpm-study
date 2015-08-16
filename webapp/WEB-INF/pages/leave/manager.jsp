<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
	  <li role="presentation"><a href="<%=request.getContextPath()%>/leave/index">流程管理面板</a></li>
	  <li role="presentation"  class="active"><a href="#">经理审批</a></li>
	 
	   <li role="presentation" class="dropdown">
	    <a class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
	      	您好，${userName} <span class="caret"></span>
	    </a>
	    <ul class="dropdown-menu">
	      <li role="presentation"><a href="<%=request.getContextPath()%>/user/logout">退出</a></li>
	    </ul>
	  </li>
	</ul>
	<div class="panel panel-primary">
		<div class="panel-heading">申请假期</div>
	  <div class="panel-body">
		<form class="form-horizontal" action="<%=request.getContextPath()%>/leave/doManager" method="POST">
			<input type="hidden" name="taskId" value="${param.id}">
		  <div class="form-group">
		    <label for="inputEmail3" class="col-sm-2 control-label">申请人</label>
		    <div class="col-sm-10">
		     ${owner}
		    </div>
		  </div>
		  <div class="form-group">
		    <label for="dayInput" class="col-sm-2 control-label">申请天数</label>
		    <div class="col-sm-10">
		      ${day}
		    </div>
		  </div>
		  <div class="form-group">
		    <label for="reasonInput" class="col-sm-2 control-label">请假原因</label>
		    <div class="col-sm-10">
		       ${reason}
		    </div>
		  </div>
		  <div class="form-group">
		    <div class="col-sm-offset-2 col-sm-10">
		      <button type="submit" class="btn btn-success">批准</button>
		      <a href="<%=request.getContextPath()%>/leave/doBack?taskId=${param.id}" class="btn btn-danger">驳回</a>
		    </div>
		  </div>
		</form>
	</div>
	</div>
</div>
<script src="//cdn.bootcss.com/jquery/2.1.4/jquery.min.js"></script>
<script src="//cdn.bootcss.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
</body>
</html>