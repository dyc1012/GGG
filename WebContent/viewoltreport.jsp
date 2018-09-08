<html>
<head>
<meta charset="UTF-8">
<title>OLT Reports</title>
<link rel="stylesheet" type="text/css"
	href="jquery-easyui-1.6.2/themes/default/easyui.css">
<link rel="stylesheet" type="text/css"
	href="jquery-easyui-1.6.2/themes/icon.css">
<link rel="stylesheet" type="text/css"
	href="jquery-easyui-1.6.2/demo/demo.css">
<script type="text/javascript" src="jquery-easyui-1.6.2/jquery.min.js"></script>
<script type="text/javascript"
	src="jquery-easyui-1.6.2/jquery.easyui.min.js"></script>

<style type="text/css">
#searchresult {
	width: 130px;
	position: absolute;
	z-index: 9999;
	left: 130px;
	top: 71px;
	background: #E0E0E0;
	border-top: none;
	overflow: scroll;
	height: 160px;
}

.line {
	font-size: 12px;
	background: #E0E0E0;
	width: 130px;
	padding: 2px;
}

.hover {
	background: #007ab8;
	width: 130px;
	color: #fff;
}

.std {
	width: 150px;
}
</style>

<script type="text/javascript">
	$(document).ready(function() {
		$('#tt').datagrid({'pageList': [20,50,100,200], 'loadMsg' : 'loading...'});
		
		$("#c1").click(function() {
			$("#genFrom").datebox('setValue', '');
			$("#genTo").datebox('setValue', '');
		});
		
		
		$('#tt').datagrid('getColumnOption', 'rptLink').formatter = function(value, rowData, rowIndex)
		{
			return "<a class='item' href='javascript:void(0)' title='" + value + "'>view</a>";
		};
		
		$('#tt').datagrid('getColumnOption', 'jobName').formatter = function(value, rowData, rowIndex)
		{
			return "<span title='" + value + "'>" + value + "</a>";
		};
		
		
		
		$("#search").click(function() {
			//alert($("#genFrom").datebox('getValue'));
			$.post("<%=this.getServletContext().getContextPath()%>/OLTRpt", 
				{
					jobName : $("#jobNameParam").val(),
					genFromTime : $("#genFrom").datebox('getValue'),
					genToTime : $("#genTo").datebox('getValue'),
					dbLocation : $("#dblocate").combobox("getValue")
				}, 
				
				function(data, status) {
					$('#tt').datagrid({loadFilter:pagerFilter}).datagrid('loadData', data);
					
					$("a.item").click(function() {
						//alert(this.title);
						$.post("<%=this.getServletContext().getContextPath()%>/OLTRpt",
						{
							reportLink : this.title
						},

						function(data,status) {
							//alert(data);
							$("#d11").html(data);
						});

					});
				});

			});

						function pagerFilter(data) {
							
							if (typeof data.length == 'number'
									&& typeof data.splice == 'function') {
								data = {
									total : data.length,
									rows : data
								}
							}

							var dg = $(this);
							var opts = dg.datagrid('options');
							var pager = dg.datagrid('getPager');

							pager.pagination({
								onSelectPage : function(pageNum, pageSize) {
									opts.pageNumber = pageNum;
									opts.pageSize = pageSize;

									dg.datagrid('loadData', data);
									
									$("a.item").click(function() {
										//alert(this.title);
										$.post("<%=this.getServletContext().getContextPath()%>/OLTRpt",
										{
											reportLink : this.title
										},

										function(data,status) {
											$("#d11").html(data);
										});

									});
								}

							});

							if (!data.originalRows) {
								data.originalRows = (data.rows);
							}

							var start = (opts.pageNumber - 1)
									* parseInt(opts.pageSize);
							var end = start + parseInt(opts.pageSize);
							data.rows = (data.originalRows.slice(start, end));
							return data;

						}

					});
	
	$(function () { 
		
		 $("#jobNameParam").dblclick(function (evt) {
			   ChangeCoords(); 
	          
               $.ajax({  
                   type: 'post',
                   dataType: "json",  
                   url: "<%=this.getServletContext().getContextPath()%>/OLTRpt",
                   data: 'jobNameQuery=' + $.trim($("#jobNameParam").val()) + "&action=dbclick&dbLocation=" + $("#dblocate").combobox("getValue"),
                   
                   error: function (msg) {
                       alert("load data error!");  
                   },  
                   
                   success: function (data) {
                       var objData = data.d; 
                       
                       if (objData.length > 0) {  
                           var layer = "";  
                           layer = "<table id='aa'>";  
                           $.each(objData, function (idx, item) {  
                               layer += "<tr class='line'><td class='std'>" + item.jobName + "</td></tr>";  
                           });  
                           layer += "</table>";  
                                 
                           $("#searchresult").empty();  
                           $("#searchresult").append(layer);  
                           $(".line:first").addClass("hover");  
                           $("#searchresult").css("display", "block");
                           
						// move
                           $(".line").hover(function () {  
                               $(".line").removeClass("hover");  
                               $(this).addClass("hover");  
                           }, function () {  
                               $(this).removeClass("hover");  
                               //$("#searchresult").css("display", "none");  
                           });  
                           
                           //click  
                           $(".line").click(function () {  
                               $("#jobNameParam").val($(this).text());  
                               $("#searchresult").css("display", "none");  
                           });  
                       } else {  
                           $("#searchresult").empty();  
                           $("#searchresult").css("display", "none");  
                       }  
                   }  
               });  
	            
		 });
	});
	
	$(function () {  
        $("#jobNameParam").keyup(function (evt) {
            ChangeCoords();
            var k = window.event ? evt.keyCode : evt.which;  
            
            if ($.trim($("#jobNameParam").val()) != "" && k != 38 && k != 40 && k != 13) {
                $.ajax({  
                    type: 'post',
                    dataType: "json",  
                    url: "<%=this.getServletContext().getContextPath()%>/OLTRpt",
                    data: 'jobNameQuery=' + $.trim($("#jobNameParam").val()) + "&dbLocation=" + $("#dblocate").combobox("getValue"),
                    
                    error: function (msg) {
                        alert("load data error!");  
                    },  
                    
                    success: function (data) {
                        var objData = data.d; 
                        
                        if (objData.length > 0) {  
                            var layer = "";  
                            layer = "<table id='aa'>";  
                            $.each(objData, function (idx, item) {  
                                layer += "<tr class='line'><td class='std'>" + item.jobName + "</td></tr>";  
                            });  
                            layer += "</table>";  
                                  
                            $("#searchresult").empty();  
                            $("#searchresult").append(layer);  
                            $(".line:first").addClass("hover");  
                            $("#searchresult").css("display", "");  
                            
							// move
                            $(".line").hover(function () {  
                                $(".line").removeClass("hover");  
                                $(this).addClass("hover");  
                            }, function () {  
                                $(this).removeClass("hover");  
                                $("#searchresult").css("display", "none");  
                            });  
                            
                            //click  
                            $(".line").click(function () {  
                                $("#jobNameParam").val($(this).text());  
                                $("#searchresult").css("display", "none");  
                            });  
                        } else {  
                            $("#searchresult").empty();  
                            $("#searchresult").css("display", "none");  
                        }  
                    }  
                });  
            }  
            else if (k == 38) {//key up  
                $('#aa tr.hover').prev().addClass("hover");  
                $('#aa tr.hover').next().removeClass("hover");  
                $('#jobNameParam').val($('#aa tr.hover').text());  
            } else if (k == 40) {//key down 
                $('#aa tr.hover').next().addClass("hover");  
                $('#aa tr.hover').prev().removeClass("hover");  
                $('#jobNameParam').val($('#aa tr.hover').text());  
            }  
            else if (k == 13) {//key enter  
                $('#jobNameParam').val($('#aa tr.hover').text());  
                $("#searchresult").empty();  
                $("#searchresult").css("display", "none");  
            }  
            else {  
                $("#searchresult").empty();  
                $("#searchresult").css("display", "none");  
            }  
        });  
        $("#searchresult").bind("mouseleave", function () {  
            $("#searchresult").empty();  
            $("#searchresult").css("display", "none");  
        }); 
        
        
		var job = '<%=request.getParameter("job")%>';
		var timeFrom = '<%=request.getParameter("timefrom")%>';
		var timeTo = '<%=request.getParameter("timeto")%>';
		
		if (!(job == "null") || !(timeFrom == "null") || !(timeTo == "null")) {
			
			if (!(job == "null"))
			{
				//alert(job);
				$("#jobNameParam").val(job);
			}
				
			
			if (!(timeFrom == "null"))
			{
				//alert(timeFrom);
				//$("#genFrom").val(timeFrom);
				$("#genFrom").datetimebox("setValue", timeFrom);
			}
				
			
			if (!(timeTo == "null"))
			{
				//alert(timeTo);
				//$("#genTo").val(timeTo);
				$("#genTo").datetimebox("setValue", timeTo);
			}
			
			$("#search").click();
		}
	});

	function ChangeCoords() {
		var left = $("#jobNameParam").position().left;
		var top = $("#jobNameParam").position().top + 20;
		
		$("#searchresult").css("left", left + "px");
		$("#searchresult").css("top", top + "px");
	}
</script>

</head>
<body class="easyui-layout">
	<div data-options="region:'west',split:true,title:'Report Select'"
		style="width: 450px; padding: 10px;">
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'north',title:'Report Filter'"
				style="width: 450px; height: 200px; padding: 10px;">

				<form id="ff" action="" method="post">
					<table>
						<tr>
							<td colspan="2">Job Name: <input type="text" class="easyui-validatebox" id="jobNameParam" style="width:150px">
								<div id="searchresult" style="display: none;"></div>
							</td>
						</tr>
						<tr>
							<td>Time from:<input class="easyui-datetimebox" id="genFrom"
								data-options="required:false,showSeconds:false" value=""
								style="width: 120px">&nbsp;&nbsp;</td>
							<td>to:<input class="easyui-datetimebox" id="genTo"
								data-options="required:false,showSeconds:false" value=""
								style="width: 120px"></td>
						</tr>
						
						<tr>
							<td colspan="2">
								Hudson Source: <select id="dblocate" class="easyui-combobox" name="dblocate" style="width:150px;">
								    <option value="slcn03vmf0248.us.oracle.com" selected>slcn03vmf0248</option>
								    <option value="slcn03vmf0247.us.oracle.com">slcn03vmf0247</option>
								</select>
							</td>
						</tr>
					</table>
					<br /> <br />
					<div align="center">
						<input type="reset" class="easyui-linkbutton" data-options="iconCls:'icon-reload'" id="c1" value="Clear" style="width:80px"></input>&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="button" class="easyui-linkbutton" data-options="iconCls:'icon-search'" id="search" value="Search" style="width:80px"></input>
					</div>
				</form>

			</div>

			<div data-options="region:'center',title:'Report List'"
				style="width: 450px; padding: 10px;">
				<table id="tt" class="easyui-datagrid"
					style="width: 400px; height: 650px"
					data-options="singleSelect: true,rownumbers:true,autoRowHeight:false,pagination:true,pageSize:20">
					<thead>
						<tr>
							<th data-options="field:'buildId',width:60">Build ID</th>
							<th data-options="field:'jobName',width:100">Job Name</th>
							<th data-options="field:'genTime',width:140">Generate Time</th>
							<th data-options="field:'rptLink',width:60,align:'center'">Report</th>
						</tr>
					</thead>
				</table>
			</div>
		</div>
	</div>

	<div data-options="region:'center',title:'Report Detail'">
		<div id="d11" class="easyui-layout" style="padding: 15px">View
			olt report page...</div>
	</div>


</body>
</html>