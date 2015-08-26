$(document).ready(function(){
	$("#indexForm input,textarea").attr("readOnly",true);
	$(".delete").click(function(){
		var durl = $(this).data("url");
		if(durl){
			$('#delModel').modal('show');
			$('#delModel').find('.yes').click(function(){
				$.ajax({
					url:durl,
					success:function(){
						location.reload();
					}
				});
			});

		}
	});
	$("#start").bind('click',function(){
		$.ajax({
			url:$(this).data("url"),
			type:'GET',
			data:{},
			success:function(data){
				var url = "http://localhost:18083/jbpm-study/leave/request?taskId="+data.taskId;
				$('#dealModal').find('iframe').attr("src",url);
				$('#dealModal').modal('show');
			}
		})
   });
	
	
});

////流程结束之后的回调函数
var closeModel = function(end){
	$('#dealModal').modal('hide');
	if(end){
		$("#indexForm input,textarea").attr("readOnly",false);
		$("#start").attr("href","#");
		$("#start").unbind("click");
	}
}
	