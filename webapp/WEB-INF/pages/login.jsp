<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<title>用户登录</title>
<link rel="stylesheet" href="//cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap.min.css" />
</head>
<body>
<div class="container">
	<div class="col-sm-offset-4 col-sm-4" Style="margin-top:150px;">
   <c:if test="${not empty message }">
   		 <div class="alert alert-warning" role="alert"> ${message}</div>
   </c:if>
	<div class="panel panel-primary">
		<div class="panel-heading">用户登录</div>
	  <div class="panel-body">
		<form class="form-horizontal" action="<%=request.getContextPath()%>/user/doLogin" method="POST">
			<div class="form-group">
			    <label for="inputEmail3" class="col-sm-4 control-label">用户名</label>
			    <div class="col-sm-8">
			      <input type="text" class="form-control" id="inputEmail3" placeholder="kenfo" name="userName">
			    </div>
			  </div>
			  
			  <div class="form-group">
			    <div class="col-sm-offset-2 col-sm-10">
			      <button type="submit" class="btn btn-primary">登录</button>
			    </div>
			  </div>
			
		</form>
	</div>
	</div>
	</div>
</div>
<script src="//cdn.bootcss.com/jquery/2.1.4/jquery.min.js"></script>
<script src="//cdn.bootcss.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
</body>
</html>