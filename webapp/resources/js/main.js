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
	
});
	