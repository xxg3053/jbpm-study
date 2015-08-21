$(document).ready(function(){
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
	
	$("#start").click(function(){
		$.ajax({
			url:$(this).data("url"),
			type:'GET',
			data:{},
			success:function(data){
				console.log(data)
				data = data.replace(/(^\s*)|(\s*$)/g, "");
				var url = "http://localhost:18083/jbpm-study/leave/request?taskId="+data;
				$('#dealModal').find('iframe').attr("src",url);
				$('#dealModal').modal('show');
			}
		})
		
	});
	
	$("#end").click(function(){
		
		$.ajax({
			url:$(this).data("url"),
			type:'GET',
			data:{},
			success:function(data){
				console.log(data)
				//data = data.replace(/(^\s*)|(\s*$)/g, "");
				window.parent.closeModel(true);
			}
		})
		
	});
	
});

var closeModel = function(end){
	$('#dealModal').modal('hide');
	if(end){
		$('address').css("color","red");
	}
}
	