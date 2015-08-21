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
 <ol class="breadcrumb">
          <li><a href="javascript:void(0)">申请</a></li>
		  <li  class="active">手机号验证</li>
		  <li><a href="javascript:void(0)">身份证验证</a></li>
		</ol>
		<form class="form-horizontal" action="<%=request.getContextPath()%>/leave/doManager" method="POST">
			<input type="hidden" name="taskId" value="${taskId}">
		  
		  <div class="form-group">
		    <label for="dayInput" class="col-sm-2 control-label">手机号验证</label>
		    <div class="col-sm-10">
		      <input type="text" name="phoneNo" />
		    </div>
		  </div>
		  <div class="form-group">
		    <div class="col-sm-offset-2 col-sm-10">
		      <button type="submit" class="btn btn-success">下一步</button>
		    </div>
		  </div>
		</form>
<script src="//cdn.bootcss.com/jquery/2.1.4/jquery.min.js"></script>
<script src="//cdn.bootcss.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
</body>
</html>