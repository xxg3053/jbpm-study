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

<form class="form-horizontal" action="<%=request.getContextPath()%>/leave/doRequest" method="POST">
	<input type="hidden" name="taskId" value="${param.id}">
  <div class="form-group">
    <label for="inputEmail3" class="col-sm-2 control-label">申请人</label>
    <div class="col-sm-10">
      <input type="text" class="form-control" name="owner" value="${sessionScope['userName']}">
    </div>
  </div>
  <div class="form-group">
    <label for="dayInput" class="col-sm-2 control-label">申请天数</label>
    <div class="col-sm-10">
      <input type="number" id="dayInput" class="form-control" placeholder="0" name="day">
    </div>
  </div>
  <div class="form-group">
    <label for="reasonInput" class="col-sm-2 control-label">请假原因</label>
    <div class="col-sm-10">
       <textarea name="reason" id="" cols="30" rows="5"></textarea>
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-offset-2 col-sm-10">
      <button type="submit" class="btn btn-success">申请</button>
    </div>
  </div>
</form>

<script src="//cdn.bootcss.com/jquery/2.1.4/jquery.min.js"></script>
<script src="//cdn.bootcss.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
</body>
</html>