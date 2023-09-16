$(function() {

	//run job once
    $(".btnRun").click(function() {
    	var jobId = $(this).parent().data("id");
    	console.log(jobId);
        $.ajax({
            url: "/api/runJob?id=" + jobId,
            type: "POST",
            success: function(res) {
                if (res.valid) {
                	console.log("run success!");  
                } else {
                	console.log(res.msg); 
                }
            }
        });
    });
    
    //pause job
    $(".btnPause").click(function() {
    	var jobId = $(this).parent().data("id");
        $.ajax({
            url: "/api/pauseJob?id=" + jobId,
            type: "POST",
            success: function(res) {
                if (res.valid) {
                	console.log("pause success!");
                	location.reload();
                } else {
                	console.log(res.msg); 
                }
            }
        });
    });
    
    //resume job
    $(".btnResume").click(function() {
    	var jobId = $(this).parent().data("id");
        $.ajax({
            url: "/api/resumeJob?id=" + jobId,
            type: "POST",
            success: function(res) {
                if (res.valid) {
                	console.log("resume success!");
                	location.reload();
                } else {
                	console.log(res.msg); 
                }
            }
        });
    });
    
    //delete job
    $(".btnDelete").click(function() {
    	var jobId = $(this).parent().data("id");
        $.ajax({
            url: "/api/deleteJob?id=" + jobId,
            type: "POST",
            data: {
                "jobName": $("#name_"+jobId).text(),
                "jobGroup": $("#group_"+jobId).text()
            },
            success: function(res) {
                if (res.valid) {
                	console.log("delete success!");
                	location.reload();
                } else {
                	console.log(res.msg); 
                }
            }
        });
    });
	
	// update cron expression
    $(".btnEdit").click(
    		function() {
    			$("#myModalLabel").html("cron edit");
    			var jobId = $(this).parent().data("id");
    			$("#jobId").val(jobId);
    			$("#edit_name").val($("#name_"+jobId).text());
    			$("#edit_group").val($("#group_"+jobId).text());
    			$("#edit_cron").val($("#cron_"+jobId).text());
    			$("#edit_status").val($("#status_"+jobId).text());
    			$("#edit_desc").val($("#desc_"+jobId).text());
    			$('#edit_name').attr("readonly","readonly"); 
    			$('#edit_group').attr("readonly","readonly");
    			$('#edit_desc').attr("readonly","readonly");
    			$("#myModal").modal("show");
    });
    
    $("#save").click(
	    function() {
	    	var strJobName = $("#edit_name").val();
	    	var strjobGroup = $("#edit_group").val();
	    	var strCronExpression = $("#edit_cron").val();
	    	var strJobStatus = $("#edit_status").val();
	    	var strJobDesc = $("#edit_desc").val();
	    	let jobDetails = {};
	    	jobDetails["jobName"] = strJobName;
	    	jobDetails["jobGroup"] = strjobGroup;
	    	if(strCronExpression != ""){
	    		jobDetails["cronExpression"] = strCronExpression;
	    	}
	    	jobDetails["jobStatus"] = strJobStatus;
	    	jobDetails["desc"] = strJobDesc;
	        console.log(JSON.stringify(jobDetails));
	    	$.ajax({
	            url: "/api/saveOrUpdate?t=" + new Date().getTime(),
	                headers: {
				        'Content-Type':'application/json'
				    },
	            type: "POST",
	            data:JSON.stringify(jobDetails),
	            success: function(res) {
	            	if (res.valid) {
	                	console.log("success!");
	                	location.reload();
	                } else {
	                	console.log(res.msg); 
	                }
	            }
	        });
    });


    // create job
    $("#createBtn").click(
    		function() {
    			$("#myModalLabel").html("Create Job");
    			
    			$("#jobId").val("");
    			$("#edit_name").val("");
    			$("#edit_group").val("");
    			$("#edit_cron").val("");
    			$("#edit_status").val("NORMAL");
    			$("#edit_desc").val("");
    			
    			$('#edit_name').removeAttr("readonly");
    			$('#edit_group').removeAttr("readonly");
    			$('#edit_desc').removeAttr("readonly");
    			
    			$("#myModal").modal("show");
    });
    
    
});