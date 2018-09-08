<html>
<head>
<meta charset="UTF-8">
<title>Message Stats</title>
<script type="text/javascript" src="jquery-easyui-1.6.2/jquery.min.js"></script>
<script type="text/javascript"
	src="jquery-easyui-1.6.2/jquery.easyui.min.js"></script>
	
<link rel="stylesheet" type="text/css" href="jquery-easyui-1.6.2/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="jquery-easyui-1.6.2/themes/icon.css">	

<script type="text/javascript"
	src="https://www.gstatic.com/charts/loader.js"></script>

<style>
	.verticalLine {
  		<%--border-left: thick solid #ff0000;--%>
	}
	
	svg > g > g:last-child { pointer-events: none }
</style>

<script type="text/javascript">
	

	var waitInterval; //ms
	var intervalTimer;
	
	var chart1 = null;
	var chart2 = null;
	var chart3 = null;

	var sampleCount = 2;
	
	var peakStep = -1;

	$(document).ready(function() {
		//alert($('#refreshFrequency').combobox('getValue'));
		waitInterval = $('#refreshFrequency').combobox('getValue') * 1000;
		
		sampleCount = $('#sampleCount').slider('getValue');
		//alert(sampleCount);
		
		$('#sampleCount').slider({
		   // mode: 'h',
		   min: 2,
		   max: 100,
		   showTip: true,
		   range: false,
		    
		   tipFormatter: function(value){
		        return 'Samples: ' + value;
		   },
		
		   onComplete: function(newValue)
		   {
			   //alert(newValue + ' -- ' + oldValue);
			   
			   if (newValue != sampleCount)
			   {
				  // alert(newValue);
				   sampleCount = newValue;
				   drawRangeChart(sampleCount)
			   }
			   
			   
		   }
		    
		    
		});
		
		$("#reset").click(function() {
			$.messager.confirm('Please confirm', 'Cleanup all history and recount received messages from now?', function(r){
                if (r){
                	$.post("<%=this.getServletContext().getContextPath()%>/Stat", 
          			{
          				action : "reset",
          			}, 
          			
          			function(data, status) {
          				//alert(data);
          				//$.messager.alert('Result', data);
          				
          				$.messager.show({
	  		                title:'Result',
	  		                msg:data + "<br>New original time retrieved.",
	  		                timeout:5000,
	  		                showType:'fade'
	  		            });
          				
          				var newBaseTime = $.ajax({
          					url: "<%=this.getServletContext().getContextPath()%>/Stat?action=getBaseTime",
          					async : false
          				}).responseText;
          				
          				//alert(newBaseTime);
          				
          				$('#bt').textbox("setValue", newBaseTime);
          				
          				$('#update').click();
          			});
                }
            });
			
			
		});
		
		<%--
		$('#pause').on('click', function(e) {
			  e.preventDefault();
			  isPaused = true;
			});

		$('#play').on('click', function(e) {
		  e.preventDefault();
		  isPaused = false;
		}); 
		--%>
		
		
        $('#switch').switchbutton({
            checked: true,
            onChange: function(checked){
            	//alert(checked);
            	if (checked)
           		{
            		//isPaused = false;
            		
            		intervalTimer = setInterval(function() {
          				  doDraw();
          			}, waitInterval);
            		
           		}
            	else
           		{
            		//isPaused = true;
            		
            		window.clearInterval(intervalTimer);
           		}
            }
        });
        
        
        $('#refreshFrequency').combobox({
        	editable : false,
        	panelHeight : 'auto',
            
           	onSelect: function(rec){
                waitInterval = rec.value * 1000; //ms
              	//alert(waitInterval);
              	
              	window.clearInterval(intervalTimer);
              	
              	//alert($('#switch').switchbutton('options').checked);
              	
              	if ($('#switch').switchbutton('options').checked)
              	{
              		intervalTimer = setInterval(function() {
              			doDraw();
        			}, waitInterval);
              	}
              	
              	
             }
        });
        
        
        $('#bt').textbox({
       	 icons:[{
      	        iconCls:'icon-reload',
      	        
      	        handler:function(e){
      	            var oldBaseTime = $(e.data.target).textbox('getValue');
      	            //alert(val);
      	            
	      	        var newBaseTime = $.ajax({
	  					url: "<%=this.getServletContext().getContextPath()%>/Stat?action=getBaseTime",
	  					async : false
	  				}).responseText;
	      	        
	      	      if (typeof newBaseTime == "undefined")
	      	      	{
	  					$('#bt').textbox("setValue", newBaseTime);
	    	        	
	    	        	$.messager.show({
	  		                title:'Result',
	  		                msg:'Can not retrieve original time!',
	  		                timeout:2000,
	  		                showType:'fade'
	  		            });
	    	        }
	      	        else if (oldBaseTime == newBaseTime)
	      	        {
	      	        	$.messager.show({
	  		                title:'Result',
	  		                msg:'No change of original time.',
	  		                timeout:1500,
	  		                showType:'fade'
	  		            });
	      	        }
	      	        
	      	        else
	    	        {
	  					$('#bt').textbox("setValue", newBaseTime);
	    	        	
	    	        	$.messager.show({
	  		                title:'Result',
	  		                msg:'New original time retrieved.',
	  		                timeout:2000,
	  		                showType:'fade'
	  		            });
	    	        }
	      	    }
      	     }]
       });
        
        
        $("#update").click(function() {
        	window.clearInterval(intervalTimer);
        	
        	doDraw();
        	
        	if ($('#switch').switchbutton('options').checked)
          	{
          		intervalTimer = setInterval(function() {
          			doDraw();
    			}, waitInterval);
          	}
        });
        
        
        $("#search").click(function() {
        	
        	var from = new Date($("#from").datebox('getValue'));
        	var to = new Date($("#to").datebox('getValue'));
        	
        	//$('#sampleCount').slider({max:100});
        	$('#sampleCount').slider({value:2});
        	sampleCount = 2;
        	
        	if (from.toString().toLowerCase().includes("invalid"))
        	{
        		//alert("Invalid \"From\" time format!");
        		//$.messager.alert('Input error', 'Invalid "From" time format!');
        		
        		$.messager.show({
        			timeout:2500,
	                title:'Input error',
	                msg:'Invalid "From" time format!',
	                showType:'slide',
	                //icon : 'error',
	                style:{
	                    right:'',
	                    top:document.body.scrollTop+document.documentElement.scrollTop,
	                    bottom:''
	                }
	            });
        		return;
        	}
        	
        	if (to.toString().toLowerCase().includes("invalid"))
        	{
        		//alert("Invalid \"To\" time format!");
        		//$.messager.alert('Input error', 'Invalid "To" time format!');
        		
        		$.messager.show({
        			timeout:2500,
	                title:'Input error',
	                msg:'Invalid "To" time format!',
	                showType:'slide',
	                style:{
	                    right:'',
	                    top:document.body.scrollTop+document.documentElement.scrollTop,
	                    bottom:''
	                }
	            });
        		return;
        	}
        	
        	var newBaseTime = $.ajax({
				url: "<%=this.getServletContext().getContextPath()%>/Stat?action=getBaseTime",
				async : false
			}).responseText;
			
			$('#bt').textbox("setValue", newBaseTime);
			
			//alert($("#bt").val());
        	
        	var originalTime = new Date($("#bt").val()).getTime();
        	
        	if (from.getTime() < originalTime)
        	{
        		//alert("\"From\" time should be later than original time!");
        		//$.messager.alert('Input error', '"From" time should be later than original time!');
        		
        		$.messager.show({
        			timeout:2500,
	                title:'Input error',
	                msg:'"From" time should be later than original time!',
	                showType:'slide',
	                style:{
	                    right:'',
	                    top:document.body.scrollTop+document.documentElement.scrollTop,
	                    bottom:''
	                }
	            });
        		return;
        	}
        	
        	if (from > new Date().getTime())
        	{
        		//alert("\"From\" time can not be in the future!");
        		//$.messager.alert('Input error', '"From" time can not be in the future!');
        		
        		$.messager.show({
        			timeout:2500,
	                title:'Input error',
	                msg:'"From" time can not be in the future!',
	                showType:'slide',
	                style:{
	                    right:'',
	                    top:document.body.scrollTop+document.documentElement.scrollTop,
	                    bottom:''
	                }
	            });
        		return;
        	}
        	
        	<%-- 
        	if (to.getTime() < originalTime)
        	{
        		//alert("\"To\" time should be later than original time!");
        		//$.messager.alert('Input error', '"To" time should be later than original time!');
        		
        		$.messager.show({
        			timeout:2500,
	                title:'Input error',
	                msg:'"To" time should be later than original time!',
	                showType:'slide',
	                style:{
	                    right:'',
	                    top:document.body.scrollTop+document.documentElement.scrollTop,
	                    bottom:''
	                }
	            });
        		return;
        	}
        	--%>
        	
        	if (to > new Date().getTime())
        	{
        		//alert("\"To\" time can not be in the future!");
        		//$.messager.alert('Input error', '"To" time can not be in the future!');
        		
        		$.messager.show({
        			timeout:2500,
	                title:'Input error',
	                msg:'"To" time can not be in the future!',
	                showType:'slide',
	                style:{
	                    right:'',
	                    top:document.body.scrollTop+document.documentElement.scrollTop,
	                    bottom:''
	                }
	            });
        		return;
        	}
        	
        	if (to <= from)
        	{
        		//alert("\"To\" time should be later than \"From\" time!");
        		//$.messager.alert('Input error', '"To" time should be later than "From" time!');
        		
        		$.messager.show({
        			timeout:2500,
	                title:'Input error',
	                msg:'"To" time should be later than "From" time!',
	                showType:'slide',
	                style:{
	                    right:'',
	                    top:document.body.scrollTop+document.documentElement.scrollTop,
	                    bottom:''
	                }
	            });
        		return;
        	}
        	
        	
        	
        	$('#range').dialog({
        		//id: 'rangeResult',
    		    title: 'Result',
    		    width: 1200,
    		    height: 600,
    		    iconCls:'icon-filter',
    		    closed: false,
    		    cache: false,
    		    
    		    backgroundColor: 'transparent',
    		    
    		    
    		    //content: '<div id="rangechart" style="height: 100%; overflow: hidden"></div>',
    		   
    		    
    		    modal: false,
    		    resizable: false,
    		    collapsible: true,
    		    minimizable: false,
    		    maximizable: false,
    		    //zIndex: 3000,
    		    border: 'thin',  //true,false,'thin','thick'.
    		    //inline: true,
    		    //constrain: true,
    		    //left: '15%',
    		    //top: '5%',
    		    
    		    tooltip: {
				    	//trigger: 'selection',
    		    		position: 'left',
    		    		
				 	 	 },
    		    
    		});
        	
        	$('#range').dialog('center');
    		
        	<%--
    		var jsonData = $.ajax({
 		       url: "<%=this.getServletContext().getContextPath()%>/Stat?action=search",
 				dataType : "json",
 				async : false
 			}).responseText;
    		--%>
    		
    		drawRangeChart(sampleCount);
        	
	 		
        });  // end for $("#search").click(function()...
        		
        		
        		
        		
   		function drawRangeChart(sampleCountStr)
        {
			$.post("<%=this.getServletContext().getContextPath()%>/Stat", 
   			{
   				action : "search",
   				timeFrom : $("#from").datebox('getValue'),
   				timeTo : $("#to").datebox('getValue'),
   				sampleCount: sampleCountStr,
   			}, 
   			
   			function(data, status) {
   				//alert($("#to").datebox('getValue'));
   				//var jObj = $.parseJSON(jsonData);
   	    		var jStr = JSON.stringify(data.totalCounts);
   	    		
   	    		var realSampleCount = JSON.stringify(data.realSampleCount);
   	    		var maxSampleCount = JSON.stringify(data.maxSampleCount);
   	    		var increaseCount = JSON.stringify(data.increaseCount);
   	    		var duration = JSON.stringify(data.duration);
   	    		
   	    		//alert(duration);
   	    		//alert(increaseCount);
   	    		
   	    		$('#duration').text(duration.replace(new RegExp('"', 'g'), ''));
   	    		$('#increase').text(increaseCount.replace(new RegExp('"', 'g'), ''));
   	    		
   	    		//alert(realSampleCount);
   	    		
   	    		$('#sampleCount').slider({max: maxSampleCount});
   	    		
   	    		//rule:[2,'|',25,'|',50,'|',75,'|',100]
   	    		//$('#sampleCount').slider({rule: [2, '|', parseInt(maxSampleCount/4), '|', parseInt(maxSampleCount/2), '|',
   	    		                                // parseInt(maxSampleCount * 3/4), '|', maxSampleCount]});
   	    		
   	    		$('#sampleCount').slider({rule: [2, maxSampleCount]});
   	    		
   	    		if (realSampleCount != sampleCount)
   	    		{
   	    			 //$('#sampleCount').slider.setValue('max', realSampleCount);
   	    			// alert(sampleCount);
   	    			// alert ("real sample count not more then " + realSampleCount);
   	    			
   	    			$('#sampleCount').slider({value: realSampleCount});
   	    			
   	    			sampleCount = realSampleCount;
   	    		}
   	    		
   	    	//var jsonData1 = '{"cols":[{"id":"","pattern":"","label":"","type":"string"},{"id":"","pattern":"","label":"Amount","type":"number"}],"rows":[{"c":[{"f":null,"v":"Wed Apr 19 13:15:47 2017"},{"f":null,"v":4649}]},{"c":[{"f":null,"v":"Wed Apr 19 13:15:52 2017"},{"f":null,"v":17570}]}]}';
   	  		
   	    		//var jObj = $.parseJSON(jsonData);
   	    		//var jStr = JSON.stringify(jObj.totalCounts)
   	    		
   		 		// Create the data table.
   		 		var data = new google.visualization.DataTable(jStr);
   		 	
   		 		// Set chart options
   		 		var options = {
   		 			'title' : '',
   		 			
   		 			pointShape: { 
   		 				
   		 				//type: 'triangle', 
   		 				//rotation: 180,
   		 				
   		 				
   		 			},
   		 			
   		 			'pointSize' : 4,
   		 			
   		 			chartArea : {
   		 				top: '5%',
   		 				left: '6%',
   		 				//'width': '60%', 
   		 				//'height': '50%'
   		 			},
   		 			
	   		 		'backgroundColor': {
		   		         'fill': '#fcfcfc',
		   		         'opacity': 100
	   		      	 },
   		 			
   		 			//curveType: 'function',
   		 			
   		 			animation:{
				        duration: 2000,
				        easing: 'out',
				        startup: true,
				    },
   		 			
   		 			
   		 			'width' : 1150,
   		 			'height' : 600,
   		 			
   		 			
   					
   					//'is3D' : true,
   					'legend': { position: 'none' },
   					
   					//'colors': ['green'],
   				      
   					'vAxis' : {
   								//baselineColor: '#fff',
   		                		//gridlines: {color: 'transparent'},  
   		                		//title: "Total Count" , 
   								'format' : 'short'
   							  },
   							  
   					 hAxis: {
   						 		//title: "Timestamp" , 
   						 		//direction:-1, 
   						 		//slantedText:true, 
   						 		//slantedTextAngle:90,
   						 		
   								//textPosition: 'none',
   								
   								//'viewWindow.min': 15,
   								
   								//showTextEvery: data.getNumberOfRows() - 1,
   								showTextEvery: 2,
   								
   								textStyle:{color: 'blue'}
   								
   								
   						 	},
   					
   					 tooltip: {
   						    	//trigger: 'selection'
   						 	  },
   					 
   					 selectionMode: 'multiple',
   						 	
   		 		
   		 		};
   		 		
   		 		
   		 		if (chart3 != null)
   		 		{
   		 			chart3.clearChart();
   		 			chart3 = null;
   		 		}
   		 	
   		 		
   		 		
   		 		// Instantiate and draw our chart, passing in some options.
   		 		var chart = new google.visualization.LineChart(document.getElementById('rangechart'));
   		 		
   		 		google.visualization.events.addListener(chart, 'ready', function(e) {
	   				var rowNum = data.getNumberOfRows();
	   				//alert(rowNum);
	   			    chart.setSelection([{row:0,column:null},{row:rowNum - 1,column:null}]);
   				});
   		 		
   		 		chart.draw(data, options);
   		 		
   		 		chart3 = chart;
   				
   			});
        }
        
        
        
        
	   	// Load the Visualization API and the corechart package.
	   	 google.charts.load('current', {'packages':['line', 'bar', 'corechart']});
	   	 
	   	 // Set a callback to run when the Google Visualization API is loaded.
	   	 google.charts.setOnLoadCallback(repeatDraw);
	   	
	   	 
	   	 
	   	 // Callback that creates and populates a data table,
	   	 // instantiates the pie chart, passes in the data and
	   	 // draws it.
	   	 function drawChart(jsonData) {
	   		// var jsonData = '{"cols":[{"id":"","pattern":"","label":"","type":"string"},{"id":"","pattern":"","label":"Amount","type":"number"}],"rows":[{"c":[{"f":null,"v":"Wed Apr 19 13:15:47 2017"},{"f":null,"v":4649}]},{"c":[{"f":null,"v":"Wed Apr 19 13:15:52 2017"},{"f":null,"v":17570}]}]}';
	   		
	   		<%--var jsonData = $.ajax({
	   		       url: "<%=this.getServletContext().getContextPath()%>/Stat",
	   				dataType : "json",
	   				async : false
	   			}).responseText;--%>
	   		
	   		
	   		// Create the data table.
	   		var data = new google.visualization.DataTable(jsonData);
	   	
	   		// Set chart options
	   		var options = {
	   			'title' : 'Received Messages from IoT',
	   			
	   			//chart: { title: 'Received Messages from IoT' },
	        		 
	   			//curveType: 'function',
	   			
	   			'width' : 2000,
	   			'height' : 520,
	   			
	   			'pointSize' : 6,
	   			
	   			//'is3D' : true,
	   			'legend': { position: 'none' },
	   			
	   		      
	   		      
	   			'vAxis' : {
	   						//baselineColor: '#fff',
	                   		//gridlines: {color: 'transparent'},  
	                   		title: "Total Count" , 
	   						'format' : 'short'
	   					  },
	   					  
	   			 hAxis: {
	   				 		title: "Timestamp" , 
	   				 		//direction:-1, 
	   				 		//slantedText:true, 
	   				 		//slantedTextAngle:90,
	   				 		
	   				 		showTextEvery:1,
	   				 	},
	   			
	   			 tooltip: {
	   				    	trigger: 'selection'
	   				 	  },
	   			 
	   			 //selectionMode: 'multiple'
	   		};
	   	
	   		// Instantiate and draw our chart, passing in some options.
	   		var chart = new google.visualization.LineChart(document.getElementById('chart_div'));
	   		//var chart = new google.charts.Line(document	.getElementById('chart_div'));
	   		
	   		//alert(data.getNumberOfRows());
	   		
	   		google.visualization.events.addListener(chart, 'ready', function(e) {
	   			var rowNum = data.getNumberOfRows();
	   			//alert(rowNum);
	   		    chart.setSelection([{row:rowNum - 1,column:1},]);
	   		});
	   		
	   		chart.draw(data, options);
	   		
	   		chart1 = chart;;
	   	 }
	   	 
	   	 
	   	
	   	 function drawChart2(jsonData) {
	   		 <%--var jsonData = $.ajax({
	   		       url: "<%=this.getServletContext().getContextPath()%>/Stat",
	   				dataType : "json",
	   				async : false
	   			}).responseText;--%>
	   		 
	   		// Create the data table.
	   		//alert (jsonData);
	   		var data = new google.visualization.DataTable(jsonData);
	   		
	   		// Set chart options
	   		var options = {
	   			chartArea:{left:250,top:50,width:1500,height:100},
	   			'title' : 'Incoming count every 5 sec long',
	   			//'subtitle': 'timestamp',
	   			//'width' : 1800,
	   			//'height' : 180,
	   			//'is3D' : true,
	   			'legend': { position: 'none' },
	   			
	   			//colors: ['green'],
	   			
	   			'vAxis' : {
	   				//baselineColor: '#fff',
	           		//gridlines: {color: 'transparent'},  
	           		//title: "Interval Count",
	   			  },
	   			  
	   			  bars: {
	   				  Type: 'vertical',
	   				  groupWidth : '10%'
	   		 	  },
	   		 	  
	   		 	 
	   		};
	   	        
	   		// Instantiate and draw our chart, passing in some options.
	   		var chart = new google.visualization.ColumnChart(document.getElementById('chart_div2'));
	   		
	   		//alert($('#chart_div2').attr('id'));
	   		
	   		chart.draw(data, options);
	   		
	   		chart2 = chart;
	   	 }
	   	 
	   	 
	   	//var isPaused = false;
	   	var firstRun = true;
	   	
	   	
	   	function doDraw()
	   	{
	   		
	   		
	   		
	   		var oldBaseTime = $('#bt').textbox('getValue');
	               //alert(val);
	               
   	        var newBaseTime = $.ajax({
   				url: "<%=this.getServletContext().getContextPath()%>/Stat?action=getBaseTime",
   				async : false
  				}).responseText;
  			
   	        
   	        //alert(newBaseTime);
   	        if (typeof newBaseTime !== "undefined" && oldBaseTime !== newBaseTime)
   	        {
   				$('#bt').textbox("setValue", newBaseTime);
    	        	
    	        	$.messager.show({
   	                title:'Result',
   	                msg:'New original time retrieved.',
   	                timeout:2000,
   	                showType:'fade'
   	            });
   	        }
	    	        
	    	        
    	    var jsonData = $.ajax({
   	       			url: "<%=this.getServletContext().getContextPath()%>/Stat?peakStep=" + peakStep,
   					dataType : "json",
   					async : false,
   					
   			}).responseText;
   	
   			var jsonResult = $.parseJSON(jsonData);
   			var newPeakStep = JSON.stringify(jsonResult.peakStep);
   			
   		  
   	 	    
   			if (newPeakStep != peakStep)
   			{
   				//alert(peakStep + " -- " + newPeakStep);
   				
   				var totalCountsData = JSON.stringify(jsonResult.totalCounts);
	   			var intervalCountsData = JSON.stringify(jsonResult.intervalCounts);
	   			
	   			if (chart1 != null)
		   		{
		   			chart1.clearChart();
		   			chart1 = null;
		   		}
		   		
		   		if (chart2 != null)
		   		{
		   			chart2.clearChart();
		   			chart2 = null;
		   		}
	   			
   				drawChart(totalCountsData);
	   		 	drawChart2(intervalCountsData);
	   		 	
	   		 	peakStep = newPeakStep;
	   		 	
	   		 	//alert(peakStep);
   			}
   			else
   			{
   				//alert(peakStep);
   			}
	   		 	
	   	}
	   	
	   		
	   	function repeatDraw() {
	   		if (firstRun)
	   		{
	   			doDraw();
	   		}
	   		
	   		intervalTimer = setInterval(function() {
	   			 // if(!isPaused) {
	   				  doDraw();
	   				  
	   			 // }
	   			}, waitInterval);
	   	
	   		//var t = setInterval("drawChart()", 5000)
	    }
        
        	
	});
        
   

	
	 
	
    
</script>

</head>

<body>
<br/>
	
	
	<div align="center">
	<%-- 
		<input type="reset" class="easyui-linkbutton" data-options="iconCls:'icon-reload'" id="reset" value="Clear" style="width:80px"></input>
	 --%>
		
		<a href="#" class="easyui-linkbutton" id="reset" data-options="iconCls:'icon-redo'" style="width:75px">Cleanup</a>
		
		<%-- 
		<input type="reset" class="easyui-linkbutton" data-options="iconCls:'icon-reload'" id="play" value="Play" style="width:80px"></input>
		<input type="reset" class="easyui-linkbutton" data-options="iconCls:'icon-reload'" id="pause" value="Pause" style="width:80px"></input>
	 	--%>
	 	
		&nbsp;&nbsp;&nbsp;&nbsp;
		<span class="verticalLine">
		Auto refresh&nbsp;<input class="easyui-switchbutton" id="switch" checked data-options="onText:'On',offText:'Off'">
		</span>
		
		&nbsp;
		<span title="Chart refresh frequency" class="easyui-tooltip">
			<select class="easyui-combobox" id="refreshFrequency" style="width:100px;">
	                <option value="5">5 seconds</option>
	                <option value="10" selected>10 seconds</option>
	                <option value="30">30 seconds</option>
	                <option value="60">1 minute</option>
	                <option value="600">10 minutes</option>
			</select>
		</span>
		
		
		&nbsp;
		<span title="Click to get up-to-date graph" class="easyui-tooltip">
			<a href="#" class="easyui-linkbutton" id="update" data-options="iconCls:'icon-large-chart',size:'large'"></a>
		</span>
				
		&nbsp;&nbsp;&nbsp;&nbsp;
		Original time&nbsp;<input type="text" class="easyui-textbox" id="bt" value='<%=application.getAttribute("baseTimeStr") %>' 
								  style="width:150px" editable="false" />
	
	
	
		&nbsp;&nbsp;&nbsp;&nbsp;
		<span title="Should be after original time and before now." class="easyui-tooltip">
			Time from&nbsp;<input class="easyui-datetimebox" id="from"
			data-options="required:false,showSeconds:true" value=""
			style="width: 150px">
		</span>
		
			
		&nbsp;&nbsp;
		<span title="Should be after &quot;From&quot; time and before now." class="easyui-tooltip">
			to&nbsp;<input class="easyui-datetimebox" id="to"
				data-options="required:false,showSeconds:true" value=""
				style="width: 150px">
		</span>
		
		&nbsp;&nbsp;
		<a href="#" class="easyui-linkbutton" id="search" data-options="iconCls:'icon-search'" style="width:60px">Go</a>
	
	</div>
	
	
	<div id="chart_div" align="center"></div>
	
	<div id="chart_div2" align="center"></div>
	
	
	
	
	<div id="range" class="easyui-dialog" title="Result" data-options="closed: true" align="center" style="height: 100%; width: 100%; overflow: hidden">
		<%-- 
		<table style="height: 100%; overflow: hidden">
			<tr>
				<td>
					<div id="rangechart" style="height: 100%; width: 100%; overflow: hidden">
					</div>
				</td>
				
				<td>
					<div id="rangeInfo" style="height: 100%; width: 100%; overflow: hidden">
						abcde
					</div>
				</td>
			</tr>
		</table>
		
		--%>
		
		
        <div class="easyui-layout" data-options="fit:true">
        	
        
            <div id="r1"data-options="region:'west',split:false" style="width:1600px;height:100%;padding:2px">
            	
            	
            	<div  style='width:900px; height:70px;margin:0px auto;'>
            		<br/>
            		
           			<input class="easyui-slider" value="10"  style="width:200px" id="sampleCount"
      						 data-options="showTip:true">
            		
            	</div>
            	
                
                <div id="rangechart" style='margin:0px auto;display: block;'></div>
            </div>
            
            <div align="center" data-options="region:'east'" style="width:300px;height:100%;padding:2px">
                <br/><br/><br/><br/><br/>
                
                <%-- 
                <font size="4" color="blue">From time:<br/>
                04/20/2017 21:12:36</font>
                
                <br/><br/><p><p>
                <font size="4" color="blue">To time:<br/>
                04/20/2017 22:43:31</font>
                --%>
                 
                
                <font size="5" color="blue">Duration:<br/><br/>
                <span id="duration">00:44:31</span>
                </font>
                
                <br/><br/><br/><br/><br/>
                
                
                <hr/>
                
                
                <br/><br/><br/><br/><br/><br/>
                <font size="5" color="blue">Increments:</font><br/><br/><br/>
                <font size="8" color="blue"><b><span id="increase">122,012,861</span></b></font>
            </div>
            
        </div>
		
		
		
	</div>
	
	
	
	
	

</body>
</html>