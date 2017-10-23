$(function(){
	var code=GetQueryString("code");
	var header=getHeader(code);
	if(header==null)
	{
		return false;
	}
	//获取同行params,并逐行渲染
	getSameRowParams(header);

	//值集全局变量
	globalVset={};
	globalValidateTables=[];
	isLovOnBlur=true;
	
});


/*********************************获取Header部分**************************************/

//获取headerInformation
function getHeaderInformation(code)
{
	var headerInformation={};
	$.ajax({
        url:  _basePath+"/rep/reportHeader/getByProgramName",
        type: 'POST',
        data: {
        	"programName":code
        },
        async: false,
        success: function (data) {
        	headerInformation=data;
        },
        error: function (jqXHR, textStatus, errorThrown) {
        	kendo.ui.showInfoDialog({
	        	message:error
	    	})
        }
    });
    return headerInformation;
}
//获取header
function getHeader(code)
{
	var headerInformation=getHeaderInformation(code);
	if(headerInformation.success==false)
	{
    	kendo.ui.showInfoDialog({
        	message:headerInformation.message
    	});
		return null;
	}
	else
	{
		var headers=headerInformation.rows;
		var header=headers[0];
		return header;
	}
}


//获取url参数
function GetQueryString(name)
{
	var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
	var search=window.location.search;
	search=decodeURI(search);
	var r = search.substr(1).match(reg);
	if(r!=null)return  unescape(r[2]); return null;
}
/********************************END-获取Header部分********************************/

/*********************************参数渲染部分**************************************/

//获取同行params,并逐行渲染
function getSameRowParams(data)
{
	//获取url中的code和参数字符串
	var code=GetQueryString("code");
	var paramsStr=GetQueryString("paramsStr");
	var linkParam={};
	if(paramsStr!=null && paramsStr!=undefined)
	{
		linkParam=analysisLinkParams(paramsStr);

	}

	//清空以前渲染的报表
	$("#formElementsWin").html("");
	
	//获取headerId
	var headerId=data.repHeaderId;
	
	//声明一个params信息对象并赋值
	var paramsInformation={};
	paramsInformation=getParams(headerId);
	
	//声明一个params数组，如果params信息获取成功，则赋值，否则弹出提示
	params=[];
	if(paramsInformation.success==true)
	{
		params=paramsInformation.rows;
	}
	else
	{
    	kendo.ui.showInfoDialog({
        	message:paramsInformation.message
    	});
		return false;
	}
	
	
	//声明一个用来存放参数框的div，并加到窗口上
	var $div='<div class="bounce" id="paramsArea"><div id="hiddenDivParent"><span>参数列表</span><div id="hiddenDiv" class="upStyle"></div><div style="clear:both;"></div></div><div id="blockDiv" style="width: 100%;height: 36px;"></div><div id="formElements" ></div><div id="buttons" ></div></div>';
    $("#formElementsWin").append($div);

    var $div='';
    $("#formElementsWin").append($div);

    //声明一个用来存放报表列表的div，并加到窗口上
	$div='<div id="report"></div>';
	$("#formElementsWin").append($div);


	$("#hiddenDivParent").click(function (){
		$("#formElements").slideToggle();
		if($("#hiddenDiv").prop("className")=="downStyle"){
			$("#hiddenDiv").removeClass("downStyle");
			$("#hiddenDiv").addClass("upStyle");
		}else{
			$("#hiddenDiv").removeClass("upStyle");
			$("#hiddenDiv").addClass("downStyle");
		}
	});
	
	//声明行号，剩余参数数组和同行参数数组，剩余参数数组赋值为所有参数
	var rowNum=0;
	var restParams=params;
	var sameRowParams=[];
	
	//开始循环所有参数
	while(restParams.length!=0)
	{
		//循环下一行
		rowNum++;
		//赋值参数数组为剩余参数数组
		params=restParams;
		//清空剩余参数数组和同行参数数组
		restParams=[];
		sameRowParams=[];
		
		//循环参数数组，划分为当前行数组和剩余数组
		for(var i=0;i<params.length;i++)
		{
			if(params[i].rowNumber==rowNum)
			{
				sameRowParams.push(params[i]);
			}
			else
			{
				restParams.push(params[i]);
			}
		}

		//判断当前行是否有参数
		if(sameRowParams.length!=0)
		{
			//为同一行的参数按列排序
			sameRowParams=sortParams(sameRowParams);
			//渲染同一行的参数
			if(paramsStr!=null && paramsStr!=undefined)
			{
				showLinkFormElements(sameRowParams, rowNum,linkParam);
			}
			else
			{
				showFormElements(data, sameRowParams, rowNum);
			}
		}
	}
	//为参数框前的文件加提示气泡
    $(".spanName").kendoTooltip({
        showOn: "click",
        content: function (e) {                   
            var target = e.target; // the element for which the tooltip is shown
            return target.text(); // set the element text as content of the tooltip
        },
        width: 80,               
        position: "bottom"
    });
    //添加查询按钮和换行
    var $search='<br><span id="searchBtn"><input class="btn btn-success" onclick="showReportContent();" type="button"  value="'+query+'" /></span>';
	$("#buttons").append($search);
	var $str="&nbsp;&nbsp;";
	$("#buttons").append($str);
	// var $export='<input class="btn btn-success" onclick="exportReport();" type="button"  value="前端导出" />';
	// $("#formElements").append($export);
	// var $str="&nbsp;&nbsp;";
	// $("#formElements").append($str);
	var $export='<span id="exportBtn"><input class="btn btn-success" onclick="openRequestWin();" type="button"  value="'+exportReportButton+'" /></span>';
	$("#buttons").append($export);
	var $br="<br/><br/>";
	$("#buttons").append($br);
	
	//获取lines
	var lines=getLines(headerId);
	var columns=getGridColumns();
	//渲染报表
	showReportTitle(columns);


	//判断是不是跳转过来的，是跳转过来的话设置参数的值
	if(paramsStr!=null && paramsStr!=undefined)
	{
		setParamsValue(code,paramsStr);
	}
}
//为参数数组排序
function sortParams(params)
{
	var param={};
	for(var i=0;i<params.length;i++)
	{
		for(var j=i+1;j<params.length;j++)
		{
			if(params[i].lineNumber>params[j].lineNumber)
			{
				param=params[i];
				params[i]=params[j];
				params[j]=param;
			}
		}
	}
	return params;
}
//渲染控件
function showFormElements(data,params,rowNum)
{
	//计算当前行最大宽度
	var rowWidth=0;
	for(var i=0;i<params.length;i++)
	{
		rowWidth+=100+params[i].showWidth+10+10;//50标题与框之间的距离，20不同参数之间的距离
	}
	//行div，添加在参数div中
	var $rowDiv='<div style="width:'+rowWidth+'px" class="rowParams" id="row'+rowNum+'"></div>';
	$("#formElements").append($rowDiv);

	//循环当前行div，加在当前行div中
    $.each(params,function(i,n){
    	if(this.display!="N" && this.paramsName!="data_authority_user_id"){
    	    var $beforeStr='<div id='+this.paramsName+'div class="cell" style="width:'+(100+this.showWidth+10)+'px"><span class="spanName">'+this.name+'</span><span class="quote">:</span>';
    	    var $afterStr='</div>';
	    	if(this.formElement=="SINGLE_SELECT")//select
			{
	    		var $str=$beforeStr;
	    		$str+="<span id="+this.paramsName+" style='width:"+this.showWidth+"px;vertical-align:middle;margin-left:8px'></span>";
	    		$str+=$afterStr;
	    		$("#row"+rowNum).append($str);
	    		showDropLists(this,null);
			}
	    	else if(this.formElement=="INPUT")//text
			{
	    		var $str=$beforeStr;
	    		$str+='<input type="text" id='+this.paramsName+' class="k-textbox" style="width:'+this.showWidth+'px;height:28px;vertical-align:middle;margin-left:8px">';
	    		$str+=$afterStr;
	    		$("#row"+rowNum).append($str);
	    		//设置默认值
	    		var dsId=data.dsId;
	    		var defaultValues=[];
	    		var defaultValue="";
	    		var defaultType=this.defaultType;
	    		if(defaultType=="SQL")
	    		{
	    			var paramId=this.queryParamsId;
	    			defaultValues=getSqlDefaultValue(paramId,dsId);
	    			if(defaultValues.length!=undefined && defaultValues.length>0)
	    			{
    					$("#"+this.paramsName).val(defaultValues[0].id);
	    			}
	    		}
	    		else if(defaultType=="STRING")
	    		{
					$("#"+this.paramsName).val(this.defaultValue);
	    		}
			}
	    	else if(this.formElement=="COMBOBOX")//combobox
			{
	    		var $str=$beforeStr;
	    		$str+="<span id="+this.paramsName+" style='width:"+this.showWidth+"px;vertical-align:middle;margin-left:8px'></span>";
	    		$str+=$afterStr;
	    		$("#row"+rowNum).append($str);
	    		showComboboxs(this,null);
			}
	    	else if(this.formElement=="MULTI_SELECT")//MultiSelect
			{
	    		var $str=$beforeStr;
	    		$str+="<span id="+this.paramsName+" style='width:"+this.showWidth+"px;vertical-align:middle;margin-left:8px'></span>";
	    		$str+=$afterStr;
	    		$("#row"+rowNum).append($str);
	    		showMultiSelects(this,null);
			}
	    	else if(this.formElement=="CHECKBOX")//checkbox
			{
				var $str=$beforeStr;
				$str+=showCheckboxs(this);
				$str+=$afterStr;
				$("#row"+rowNum).append($str);

				//设置默认值
				var defaultValue=this.defaultValue;
				var content=this.content;
				var contents=[];
				if(content!=null && content!="" && content.indexOf(",")>-1)
				{
					contents=content.split(",");
				}
				if(defaultValue==contents[0])
				{
					$("#" + this.paramsName).attr("checked", 'true');
				}
			}
	    	else if(this.formElement=="DATE")//Date
			{
	    		var $str=$beforeStr;
	    		$str+="<input id="+this.paramsName+" style='width:"+this.showWidth+"px;height:28px;vertical-align:middle;margin-left:8px'/>";
	    		$str+=$afterStr;
	    		$("#row"+rowNum).append($str);
	    		showDate(this,null);
			}
	    	else if(this.formElement=="TIME")//Time
			{
	    		var $str=$beforeStr;
	    		$str+="<input id="+this.paramsName+" style='width:"+this.showWidth+"px;vertical-align:middle;margin-left:8px'/>";
	    		$str+=$afterStr;
	    		$("#row"+rowNum).append($str);
	    		showTime(this,null);
			}
	    	else if(this.formElement=="LOV")//LOV
			{
	    		var $str=$beforeStr;
	    		$str+='<span class="popup" id="'+this.paramsName+'popup"><input placeholder="请输入" id='+this.paramsName+' onblur="lovOnBlur('+this.queryParamsId+');" class="lovInput"/>'
	    			 +'<span class="k-icon k-i-close popupClear show" id="'+this.paramsName+'popupClear"></span>'
	    			 +'<span class="popupSearch" id="'+this.paramsName+'popupSearch"><span class="k-icon k-i-search popupSearchIcon"></span></span></span>';
	    		$str+=$afterStr;
	    		$("#row"+rowNum).append($str);
	    		showLov(this);
	    		//设置默认值
	    		var dsId=data.dsId;
	    		var defaultValues=[];
	    		var defaultValue="";
	    		var defaultType=this.defaultType;
	    		if(defaultType=="SQL")
	    		{
	    			var defaultValueSql=this.defaultValue;
	    			defaultValues=getSqlContentSource(defaultValueSql,dsId);
	    			if(defaultValues.length!=undefined && defaultValues.length>0)
	    			{
	    				$("#"+this.paramsName).data("value",defaultValues[0].id);
	    				$("#"+this.paramsName).val(defaultValues[0].name);
	    			}
	    		}
	    		else if(defaultType=="STRING")
	    		{
	    			defaultValue=this.defaultValue;
	    			$("#"+this.paramsName).data("value",defaultValue);
    				$("#"+this.paramsName).val(defaultValue);
	    		}
			}
    	}
    });


}
/*********************************END-参数渲染************************************/


/*********************************获取数据源**************************************/
//获取字符串形式的下拉框数据源
function getStrContentSource(str)
{
	var options=[];
	var dataSource=[];
	if(str!=null && str!="" && str.indexOf(",")>0)
	{
		options=str.split(",");
		for(var i=0;i<options.length;i++)
		{
			var option=[];
			// update weisen.yang 2017-09-11 字符串下拉框无法显示
			if(options[i].indexOf(":")>0)
			{
				option=options[i].split(":");
				dataSource.push({"id":option[0],"name":option[1],"name2":option[2]});
			}
		}
	}
	else if(str!=null && str!="" && str.indexOf(":")>0)
	{
		var option=str.split(":");
		dataSource.push({"id":option[0],"name":option[1],"name2":option[2]});
	}
	return dataSource;
}
//获取SQL形式的下拉框数据源
function getSqlContentSource(paramId,dsId)
{
	var dataSource=[];
	$.ajax({
		url:  _basePath+"/rep/queryParams/getParamsValue",
		type: 'POST',
		data: {
			"paramId":paramId,
			"dsId":dsId
		},
		async: false,
		success: function (data) {
			if(data.success)
			{
				dataSource = data.rows;
			}
			else {
				kendo.ui.showInfoDialog({
					message:data.message
				});
				dataSource=[];
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {
			kendo.ui.showInfoDialog({
				message:error
			})
		}
	});
	return dataSource;
}

/*********************************END-获取数据源**********************************/

/*********************************获取默认值**************************************/
//获取sql类型的默认值
function getSqlDefaultValue(paramId,dsId)
{
	var defaultValues=[];
	$.ajax({
		url:  _basePath+"/rep/queryParams/getDefaultValue",
		type: 'POST',
		data: {
			"paramId":paramId,
			"dsId":dsId
		},
		async: false,
		success: function (data) {
			if(data.success) {
				defaultValues = data.rows;
			}
			else{
				kendo.ui.showInfoDialog({
					message:data.message
				});
				defaultValues=[];
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {
			kendo.ui.showInfoDialog({
				message:error
			})
		}
	});
	return defaultValues;
}
/*********************************END-获取默认值***********************************/

/*********************************生成参数控件*************************************/
//下拉单选
function showDropLists(param,defaultValue)
{
	var dataSource=[];
	var code=GetQueryString("code");
	var header=getHeader(code);
	var paramId=param.queryParamsId;
	var dsId=header.dsId;
	var contentSource=param.contentSource;
	
	//设置数据源
	if(contentSource=="SQL")
	{
		dataSource=getSqlContentSource(paramId,dsId);
	}
	else if(contentSource=="STRING")
	{
		dataSource=getStrContentSource(param.content);
	}

	//设置默认值
	var defaultValues=[];
	//var defaultValue="";
	var defaultType=param.defaultType;
	if(defaultValue!=null)
	{
	}
	else if(defaultType=="SQL")
	{
		defaultValues=getSqlDefaultValue(paramId,dsId);
		if(defaultValues.length!=undefined && defaultValues.length>0)
		{
			defaultValue=defaultValues[0].id;
		}
	}
	else if(defaultType=="STRING")
	{
		defaultValue=param.defaultValue;
	}

	$("#"+param.paramsName).kendoDropDownList({
        dataTextField: "name",
        dataValueField: "id",
        dataSource: dataSource,
        open:function () {
            $("#"+param.paramsName+"_listbox").width(param.showWidth);
            $("#"+param.paramsName+"_listbox").css("overflow","hidden");
            $("#"+param.paramsName+"_listbox").find("li").width(param.showWidth);
            $("#"+param.paramsName+"_listbox").find("li").height("24px");
            $("#"+param.paramsName+"_listbox").find("li").css("overflow","hidden");
        },
        value:defaultValue,
		template:function(item){
			return item.name2;
		}
    });
}

//下拉多选
function showMultiSelects(param,defauleValue)
{
	var dataSource=[];
	var code=GetQueryString("code");
	var header=getHeader(code);
	var paramId=param.queryParamsId;
	var dsId=header.dsId;
	var contentSource=param.contentSource;
	if(contentSource=="SQL")
	{
		dataSource=getSqlContentSource(paramId,dsId);
	}
	else if(contentSource=="STRING")
	{
		dataSource=getStrContentSource(param.content);
	}
	//设置默认值
	var defaultValues=[];
	// var defaultValue="";
	var defaultType=param.defaultType;
	if(defauleValue!=null)
	{}
	else if(defaultType=="SQL")
	{
		defaultValues=getSqlDefaultValue(paramId,dsId);
		if(defaultValues.length!=undefined && defaultValues.length>0)
		{
			defaultValue=defaultValues[0].id;
		}
	}
	else if(defaultType=="STRING")
	{
		defaultValue=param.defaultValue;
	}

	$("#"+param.paramsName).kendoMultiSelect({
        dataTextField: "name",
        dataValueField: "id",
        dataSource: dataSource,
        open:function () {
            $("#"+param.paramsName+"_listbox").width(param.showWidth);
            $("#"+param.paramsName+"_listbox").css("overflow","hidden");
            $("#"+param.paramsName+"_listbox").find("li").width(param.showWidth);
            $("#"+param.paramsName+"_listbox").find("li").height("24px");
            $("#"+param.paramsName+"_listbox").find("li").css("overflow","hidden");
        },
        value:defaultValue,
		template:function(item){
			return item.name2;
		}
    });
}

//combobox
function showComboboxs(param,defaultValue)
{
	var dataSource=[];
	var code=GetQueryString("code");
	var header=getHeader(code);
	var paramId=param.queryParamsId;
	var dsId=header.dsId;
	var contentSource=param.contentSource;
	if(contentSource=="SQL")
	{
		dataSource=getSqlContentSource(paramId,dsId);
	}
	else if(contentSource=="STRING")
	{
		dataSource=getStrContentSource(param.content);
	}
	//设置默认值
	var defaultValues=[];
	// var defaultValue="";
	var defaultType=param.defaultType;
	if(defaultValue!=null)
	{
	}
	else if(defaultType=="SQL")
	{
		defaultValues=getSqlDefaultValue(paramId,dsId);
		if(defaultValues.length!=undefined && defaultValues.length>0)
		{
			defaultValue=defaultValues[0].id;
		}
	}
	else if(defaultType=="STRING")
	{
		defaultValue=param.defaultValue;
	}
	$("#"+param.paramsName).kendoComboBox({
        dataTextField: "name",
        dataValueField: "id",
        filter: "contains",
        dataSource: dataSource,
        open:function () {
            $("#"+param.paramsName+"_listbox").width(param.showWidth);
            $("#"+param.paramsName+"_listbox").css("overflow","hidden");
            $("#"+param.paramsName+"_listbox").find("li").width(param.showWidth);
            $("#"+param.paramsName+"_listbox").find("li").height("24px");
            $("#"+param.paramsName+"_listbox").find("li").css("overflow","hidden");
        },
        value:defaultValue,
		template:function(item){
			return item.name2;
		}
    });
}
//checkbox
function showCheckboxs(param)
{
	var $str='<input type="checkbox" style="width:20px;height:20px;vertical-align:middle;margin-left: 8px;" id='+param.paramsName+' name='+param.paramsName+'/>';
	return $str;
}

//日期框
function showDate(param,defaultValue)
{
	//设置默认值
	var code=GetQueryString("code");
	var header=getHeader(code);
	var dsId=header.dsId;
	var paramId=param.queryParamsId;
	var defaultValues=[];
	// var defaultValue="";
	var defaultType=param.defaultType;
	if(defaultValue!=null)
	{}
	else if(param.defaultValue=="" || param.defaultValue==null)
	{
        /**
		 * update date by xiaoyong.luo
		 * 去除默认值
         */
        // defaultValue=new Date();
		defaultValue=null;
	}
	else
	{
		if(defaultType=="SQL")
		{
			defaultValues=getSqlDefaultValue(paramId,dsId);
			if(defaultValues.length!=undefined && defaultValues.length>0)
			{
				defaultValue=new Date(defaultValues[0].id);
			}
		}
		else if(defaultType=="STRING")
		{
			defaultValue=new Date(param.defaultValue);
		}
	}
	$("#"+param.paramsName).kendoDatePicker({
		culture:"zh-CN",
		format:"yyyy-MM-dd",
		value:defaultValue
	});
}

//时间框
function showTime(param,defaultValue)
{
	//设置默认值
	var code=GetQueryString("code");
	var header=getHeader(code);
	var dsId=header.dsId;
	var paramId=param.queryParamsId;
	var defaultValues=[];
	// var defaultValue="";
	var defaultType=param.defaultType;
	if(defaultValue!=null)
	{}
	else if(param.defaultValue=="" || param.defaultValue==null)
	{
		defaultValue=new Date();
	}
	else
	{
		if(defaultType=="SQL")
		{
			defaultValues=getSqlDefaultValue(paramId,dsId);
			if(defaultValues.length!=undefined && defaultValues.length>0)
			{
				defaultValue=new Date(defaultValues[0].id);
			}
		}
		else if(defaultType=="STRING")
		{
			defaultValue=new Date(param.defaultValue);
		}
	}
    $("#"+param.paramsName).kendoDateTimePicker({
        parseFormats: ["MM/dd/yyyy"],
        value:defaultValue
    });
}
/*******************************END生成参数控件**********************************/

/*******************************LOV部分*****************************************/
//渲染LOV输入框和弹出框按钮
function showLov(param)
{
	var id=param.paramsName;
	var content=param.content;
	//lov样式js
	$("#"+id+"popupClear").click(function(){
	    	$("#"+id).val("");
	    	$("#"+id).data("value","");
	    });
	    
	    $("#"+id+"popupSearch").click(function(){
	    	showLovWin(param);
	    });
	    
	    $("#"+id).bind({ 
		focus:function(e){	
			$("#"+id+"popup").addClass("box-shadow-popup");
			e.stopPropagation();
		}, 
		
		blur:function(){
			$("#"+id+"popup").removeClass("box-shadow-popup");
		} 
	}); 
	
	$("#"+id+"popup").hover(
		function(){
			$("#"+id+"popupClear").removeClass("show");
			$("#"+id+"popupSearch").addClass("bGColor");
		},
		function(){
			if($("#"+id+"lovInput").is(":focus")){
			}else{
				$("#"+id+"popupClear").addClass("show");
			    $("#"+id+"popupSearch").removeClass("bGColor");
			}
			
		}
		
	);
}
//渲染Lov弹出框
function showLovWin(param)
{
	var vsetName=param.content;
	var paramName=param.paramsName;
	globalVset=getVset(vsetName);
	var vsetId=globalVset.flexValueSetId;
	globalValidateTables=getValidateTables(vsetId);
	
	//渲染Lov弹出框
    $("#lovWin").kendoWindow({
    	width: globalVset.width,
    	height:globalVset.height,
    	modal: true,
        title: vsetName,
        actions: [
            "Pin",
            "Minimize",
            "Maximize",
            "Close"
        ],
        close:function()
        {
        	setTimeout(function(){
        		$("#"+param.paramsName).get(0).focus();
        	},500);     	
        },
        visible: false
    });
    
	//打开lov弹窗
	$("#lovWin").data("kendoWindow").center().open();
	//获取vset
	var vset=globalVset;
	//获取validateTables
	var validateTables=globalValidateTables;
    //渲染参数
    showLovValidateTables(vset,validateTables,param);
    //渲染结果表格
    showLovTable(paramName,validateTables);
	if(vset.delayedLoadingFlag=="N")
    {
  		showLovResult(vsetId);
    }
}
function getVset(vsetName)
{
	var vset={};
	$.ajax({
      url:  _basePath+"/rep/flexVset/getByVsetName",
      type: 'POST',
      data: {
      	"vsetName":vsetName
      },
      async: false,
      success: function (data) {
      	vset=data;
      },
      error: function (jqXHR, textStatus, errorThrown) {
      	kendo.ui.showInfoDialog({
        	message:error
    	})
      }
  });
	return vset;
}
//获取validateTables
function getValidateTables(vsetId)
{
	var validateTables=[];
	$.ajax({
      url:  _basePath+"/rep/validateTable/getByVsetId",
      type: 'POST',
      data: {
      	"vsetId":vsetId
      },
      async: false,
      success: function (data) {
      	validateTables=data.rows;
      },
      error: function (jqXHR, textStatus, errorThrown) {
      	kendo.ui.showInfoDialog({
        	message:error
    	})
      }
  });
	return validateTables;
}
//渲染LOV弹出框中validateTables参数
function showLovValidateTables(vset,validateTables,param)
{
	$("#lovWin").html("");

	var $str='<div id="validateTables"></div>';
	$("#lovWin").append($str);
	$str='<br/>'
	$("#lovWin").append($str);
	$str='<div id="vsetResult"></div>'; 
	$("#lovWin").append($str);


	if(validateTables.length!=undefined)
	{
		for(var i=0;i<validateTables.length;i++)
		{
			if(validateTables[i].enabledFlag=='Y' && validateTables[i].conditionFlag=='Y'){
				var $beforeStr='<div class="divStyle"><span class="spanName">'+validateTables[i].description+':</span>';
	    	    var $afterStr='</div>';
		        var $str=$beforeStr;
		    	$str+='<input type="text" id="lov'+validateTables[i].validateTableId+'" class="k-textbox" style="width:'+validateTables[i].width+'px;height:25px">';
		    	$str+=$afterStr;
		        $("#validateTables").append($str);

			}	
		}
	}
	var $str='<div style="clear:both"></div>';
	$("#validateTables").append($str);
	$str='<input class="btn btn-success" onclick="showLovResult('+vset.flexValueSetId+');" type="button"  value="'+query+'" />';
	$("#validateTables").append($str);
	$str="<br/>";
	$("#lovWin").append($str);
	var paramsName=param.paramsName;
	$str="<input class='btn btn-success' onclick='addValueToLov(\""+paramsName+"\");' type='button'  value='"+confirm+"' />";
	$("#lovWin").append($str);
}
//渲染LOV结果表格
function showLovTable(paramName,validateTables)
{
	var columns=[];
	columns=getvalidateTableColumns(validateTables);
	var dataSource=[];
	var lovGrid=$("#vsetResult").kendoGrid({
		dataSource: {
			data:dataSource
		},
		height: 400,
        resizable: true,
        scrollable: true,
        selectable: "multiple",
        columns: columns,
        editable: false,
        sortable:true,
        reorderable:true
  });


	lovGrid.on('dblclick', '.k-grid-content tr', function () {
		var row = lovGrid.data("kendoGrid").select();
		var data = lovGrid.data("kendoGrid").dataItem(row);
		addValueToLov(paramName,data);
	});

}
//将validateTables转换成grid列规范数组
function getvalidateTableColumns(validateTables)
{
	var columns=[];
	for(var i=0;i<validateTables.length;i++)
	{
		if(validateTables[i].enabledFlag=='Y'){
			var column={};
			column.field=validateTables[i].columnAlias;
			column.title=validateTables[i].description;
			column.width=validateTables[i].width;
			if(validateTables[i].hiddenFlag=='Y')
			{
				column.hidden=true;
			}
			columns.push(column);
		}
	}
	return columns;
}
//查询Lov结果
function showLovResult(vsetId)
{
	var timer=startModal();
	var validateTables=globalValidateTables;

	//拼接lovsql条件
	var lovCondition="";
	for(var i=0;i<validateTables.length;i++)
	{
		if(validateTables[i].enabledFlag=='Y' && validateTables[i].conditionFlag=='Y')
		{
			var value=$("#lov"+validateTables[i].validateTableId).val();
			if(value!="")
			{
				lovCondition+=" and "+validateTables[i].columnName+" like '%"+value+"%' ";
			}
		}
	}
	//获取header
	var code=GetQueryString("code");
	var header=getHeader(code);
	//获取headerId
	var headerId=header.repHeaderId;

	//获取参数
	var params=[];
	params=getParams(headerId);
	params=params.rows;

	//参数对象
	var paramsObject=getParamsValue(header,params);
	//设置vsetId
	paramsObject["vsetId"]=vsetId;
	//设置查询条件
	paramsObject["lovCondition"]=lovCondition;

	$.ajax({
		type: "POST",
		contentType: "application/json;charset=utf-8",
		url:  _basePath+"/rep/flexVset/getLovDataSource",
		data: JSON.stringify(paramsObject),
		dataType: "json",
		async: true,
      success: function (data) {
		  endModal(timer);
		  if(data.success)
		  {
			  dataSource=data.rows;
			  dataSource=analysisDataSource(dataSource);
			  var vsetDataSource = new kendo.data.DataSource({
				  data: dataSource
			  });

			  var grid=$("#vsetResult").data('kendoGrid');
			  grid.setDataSource(vsetDataSource);
		  }
		  else {
			  kendo.ui.showInfoDialog({
				  message:data.message
			  })
		  }
      },
      error: function (jqXHR, textStatus, errorThrown) {
		  endModal(timer);
		  kendo.ui.showInfoDialog({
			  message:error
    	})
      }
  });

}
//将lov值放到lov控件上
function addValueToLov(paramsName,data)
{
	var vsetValues = data;
	isLovOnBlur=false;
	if(vsetValues==undefined)
	{
		var grid = $("#vsetResult").data("kendoGrid");
		var dataRows = grid.items();
		var rowIndex = dataRows.index(grid.select());
		vsetValues = grid.dataItem(grid.select());
	}
	if(vsetValues==null)
	{
    	kendo.ui.showInfoDialog({
        	message:selectOne
    	});
		return false;
	}
	var value="";
	var validateTables=globalValidateTables;
	for(var i=0;i<validateTables.length;i++)
	{
		if(validateTables[i].columnFlag=="Y")
		{
			//判断是value还是text
			if(validateTables[i].valueField=="Y")
			{
				var field=validateTables[i].columnAlias;
				value=vsetValues[field];
			}
			if(validateTables[i].textField=="Y")
			{
				var field=validateTables[i].columnAlias;
				text=vsetValues[field];
			}
		}
		else if(validateTables[i].columnFlag=="O" && validateTables[i].textField!="")
		{
			//如果是其他控件，控件如果存在，则赋值
//			if($("#"+paramsName).is("input"))
//			{
//				var field=validateTables[i].columnAlias;
//				value=vsetValues[field];
//				$("#"+paramsName).val(value);
//			}
			if($("#"+validateTables[i].description).is("input"))
			{
				var field=validateTables[i].columnAlias;
				value=vsetValues[field];
				$("#"+validateTables[i].description).val(value);
			}
		}
	}
	//判断lov的value和text是不是为空
	if(value!="" && text!="")
	{
		$("#"+paramsName).val(text);
		$("#"+paramsName).data("value",value);
	}
	else
	{
    	kendo.ui.showInfoDialog({
        	message:informationNotEnough
    	})
	}
	$("#lovWin").data("kendoWindow").close();
	//执行扩展代码
	var vset=globalVset;
	if(vset.expandMethod!="")
	{
		eval(vset.expandMethod);
	}
}

//lov自动补全
function lovOnBlur(queryParamsId)
{
	if(isLovOnBlur)
	{
		var param = {};
		$.ajax({
			url: _basePath + "/rep/queryParams/getByParamId",
			type: 'POST',
			data: {
				"queryParamsId": queryParamsId,
			},
			async: false,
			success: function (data) {
				param = data;
			},
			error: function (jqXHR, textStatus, errorThrown) {
				kendo.ui.showInfoDialog({
					message: error
				})
			}
		});
		var lovValue = $("#" + param.paramsName).val();
		if (lovValue != "") {
			//获取header
			var code=GetQueryString("code");
			var header = getHeader(code);
			//获取headerId
			var headerId=header.repHeaderId;
			//获取参数
			var params=[];
			params=getParams(headerId);
			params=params.rows;

			//参数对象
			var paramsObject=getParamsValue(header,params);
			//获取vset对象
			var vset = getVset(param.content);
			//获取vsetId
			var vsetId = vset.flexValueSetId;
			//设置vsetId
			paramsObject["vsetId"]=vsetId;

			var validateTables = getValidateTables(vsetId);
			//获取查询字段
			var textName = "";
			var valueName = "";
			if (validateTables.length != undefined && validateTables.length != 0) {
				for (var i = 0; i < validateTables.length; i++) {
					if (validateTables[i].columnFlag == "Y" && validateTables[i].textField == "Y") {
						textName = validateTables[i].columnAlias;
					}
					if (validateTables[i].columnFlag == "Y" && validateTables[i].valueField == "Y") {
						valueName = validateTables[i].columnAlias;
					}
				}
			}
			//拼接查询字符串
			var lovCondition=" AND " + textName + " like '%" + lovValue + "%'";
			//设置查询条件
			paramsObject["lovCondition"]=lovCondition;
			var dataSource = [];
			$.ajax({
				type: "POST",
				contentType: "application/json;charset=utf-8",
				url:  _basePath+"/rep/flexVset/getLovDataSource",
				data: JSON.stringify(paramsObject),
				dataType: "json",
				async: false,
				success: function (data) {
					if(data.success)
					{
						dataSource = data.rows;
					}
					else {
						kendo.ui.showInfoDialog({
							message:data.message
						});
						return false;
					}
				},
				error: function (jqXHR, textStatus, errorThrown) {
					kendo.ui.showInfoDialog({
						message: error
					})
				}
			});
			if (dataSource.length != undefined) {
				if (dataSource.length == 1) {
					var lines = dataSource[0].data;
					var value = "";
					var text = "";
					if (lines[valueName] != undefined) {
						value = lines[valueName];
					}
					if (lines[textName] != undefined) {
						text = lines[textName];
					}
					$("#" + param.paramsName).val(text);
					$("#" + param.paramsName).data("value", value);
				}
				else if (dataSource.length == 0) {
					showLovWin(param);
					showLovResult(vsetId);
					$("#" + param.paramsName).val("");
					$("#" + param.paramsName).data("value", "");
				}
				else {
					showLovWin(param);
					dataSource = analysisDataSource(dataSource);
					var vsetDataSource = new kendo.data.DataSource({
						data: dataSource
					});

					var grid = $("#vsetResult").data('kendoGrid');
					grid.setDataSource(vsetDataSource);
				}
			}
		}
	}
	isLovOnBlur=true;
}


/**************************END-LOV代码*********************************/

/***********************获取列和参数并处理部分****************************/
//根据headerId获取line
function getLines(headerId){
	var lines=[];
	$.ajax({
		url:  _basePath+"/rep/reportLine/getLinesByHeaderId",
		type: 'POST',
		data: {
			"headerId":headerId
		},
		async: false,
		success: function (data) {
			lines=data.rows;
		},
		error: function (jqXHR, textStatus, errorThrown) {
			kendo.ui.showInfoDialog({
				message:error
			})
		}
	});
	return lines;
}

//根据headerId获取参数
function getParams(headerId){
	var paramsInformation=[];
	$.ajax({
		url:  _basePath+"/rep/queryParams/getParamsByHeaderId",
		type: 'POST',
		data: {
			"headerId":headerId
		},
		async: false,
		success: function (data) {
			paramsInformation=data;
		},
		error: function (jqXHR, textStatus, errorThrown) {
			kendo.ui.showInfoDialog({
				message:error
			})
		}
	});
	return paramsInformation;
}

//解析sql
function analysisSql(sql)
{
	var pattern =new RegExp("\\{{(.| )+?\\}}","igm");
	var strs=[];
	var params=[];
    strs=sql.match(pattern);
    if(strs!=null)
    {
        for(var i=0;i<strs.length;i++)
        {
            var param={};
            param.name=strs[i];
            strs[i]=strs[i].replace("{{","").replace("}}","")
            param.value=strs[i];
            params.push(param);
        }
    }
    params=rewoveSame(params);
	return params;
}

//去重
function rewoveSame(params)
{
	var newParams=[];

	if(params!="" && params!=null)
	{
		newParams.push(params[0]);
		for(var i=1;i<params.length;i++)
		{
			var b="N";
			for(var j=0;j<newParams.length;j++)
			{
				if(newParams[j].name==params[i].name)
				{
					b="Y";
				}
			}
			if(b=="N")
			{
				newParams.push(params[i]);
			}
		}
	}
	return newParams
}
/***********************END-获取列和参数并处理部分*************************/

/****************************获取所有控件的值*****************************/

//获取所有控件值
function getParamsValue(data,params){
	var sql="";
	sql=data.sqlText;
	var headerParams=analysisSql(sql);
	var nullValue="";

	var paramsStr={};
	var headerId=data.repHeaderId;
	for(var i=0;i<params.length;i++)
	{
		var value="";
		if(params[i].display!="" && params[i].paramsName!="data_authority_user_id")
		{
			if(params[i].formElement=="SINGLE_SELECT")//select,下拉框
			{
				value=$("#"+params[i].paramsName).data("kendoDropDownList").value();
			}
	    	else if(params[i].formElement=="INPUT")//text,输入框
			{
	    		value=$("#"+params[i].paramsName).val();
			}
	    	else if(params[i].formElement=="COMBOBOX")//combobox,下拉输入框
			{
	    		value=$("#"+params[i].paramsName).data("kendoComboBox").value();
			}
	    	else if(params[i].formElement=="MULTI_SELECT")//MultiSelect,下拉多选框
			{
	    		value=$("#"+params[i].paramsName).data("kendoMultiSelect").value();
			}
	    	else if(params[i].formElement=="CHECKBOX")//checkbox,多选框
			{
				var content=params[i].content;
				var contents=[];
				if(content!=null && content!="" && content.indexOf(",")>-1)
				{
					contents=content.split(",");
				}
				if($("#"+params[i].paramsName).is(':checked'))
				{
					value=contents[0];
				}
				else
				{
					value=contents[1];
				}
			}
	    	else if(params[i].formElement=="DATE")//时间框
			{
	    		value=$("#"+params[i].paramsName).data("kendoDatePicker").value();
	    		if(value!="" && value!=null)
				{
	        		value = new Date(value);
	        		value=value.getFullYear()+"-"+(value.getMonth()+1)+"-"+value.getDate();
				}
			}
	    	else if(params[i].formElement=="TIME")//TIME
			{
	    		value=$("#"+params[i].paramsName).data("kendoDateTimePicker").value();
	    		if(value!="" && value!=null)
				{
	    			value = new Date(value);
	        		value=value.getFullYear()+"-"+(value.getMonth()+1)+"-"+value.getDate()+" "+value.getHours()+"-"+value.getMinutes()+"-"+value.getSeconds();
				}
			}
	    	else if(params[i].formElement=="LOV")//LOV
			{
	    		value=$("#"+params[i].paramsName).data("value");
	    		if(value==undefined)
				{
	    			value=null;
				}
			}
		}
		else if(params[i].display=="N")
		{
			value=null;
		}
		
		var required=params[i].required;
		var display=params[i].display;
		if(required=="Y"&&display=="Y")
		{
			if(value=="")
			{
				nullValue+=params[i].name+",";
			}
		}
		for(var j=0;j<headerParams.length;j++)
		{
			if(params[i].paramsName==headerParams[j].value)
			{
				if(value=="")
				{
					value=null;
				}
				var name=headerParams[j].name;
				paramsStr[name]=value;
			}
		}
	}
	paramsStr.repHeaderId=headerId+"";
	paramsStr.isNotNull=true;
	if(nullValue!="")
	{
		paramsStr.nullValue=nullValue;
		paramsStr.isNotNull=false;
	}
	

	return paramsStr;
}
/****************************END-获取所有控件的值*****************************/

/******************************查询报表结果并显示*****************************/

/**
 * 获取报表结果
 * 将结果格式化
 * 获取表头结构
 * 将表头结构格式化
 * 将所有数据组合成kendoui对象
 * */

//查询报表结果
function showReportContent()
{
	var timer=startModal();
	var code=GetQueryString("code");
	var header = getHeader(code);
	var headerId=header.repHeaderId;
	
	var params=[];
	params=getParams(headerId);
	params=params.rows;

	//获取拼接好的参数
	var paramsObject=getParamsValue(header,params);
	//有未选择的参数框
	if(paramsObject.isNotNull==false)
	{
		var nullValue=paramsObject.nullValue;
		kendo.ui.showInfoDialog({
			message:nullValue+notEmpty
		});
		endModal(timer);
		return;
	}


	$.ajax({
		type: "POST",
		contentType: "application/json;charset=utf-8",
		url: _basePath+"/rep/reportHeader/getReportDataSource",
		data: JSON.stringify(paramsObject),
		dataType: "json",
		async: true,
		success: function (data) {
			endModal(timer);
			if(data.success==false)
			{
				kendo.ui.showInfoDialog({
					message:data.message
				});
			}
			else if(data.success==true)
			{
				data=analysisDataSource(data.rows);

				// var lines=getLines(headerId);
				// var model={};
				// var fields={};
                //
				// for(var i=0;i<lines.length;i++)
				// {
				// 	if((lines[i].dataType=="DATE" ||lines[i].dataType=="DATETIME" || lines[i].dataType=="TIMESTAMP") && lines[i].format!="")
				// 	{
				// 		fields[lines[i].columnName]={type:"date"};
				// 	}
				// }
				// model.fields=fields;

				var dataSource = new kendo.data.DataSource({
					data: data,
					// schema: {
					// 	model:model
					// }
				});


				var grid=$('#report').data('kendoGrid');
				grid.setDataSource(dataSource);


				//var columns=getReportColumns(lines);
				var columns=getGridColumns();
				grid.setOptions({
					//toolbar: ["excel"],
					columns: columns,
					excel: {
						fileName: header.name+".xlsx"
					},
				});
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {
			endModal(timer);
			kendo.ui.showInfoDialog({
				message:error
			})
		}
	});

}

//格式化报表数据源
function analysisDataSource(data)
{
	var dataSource={};
	var reportDataSources=[];
	for(var i=0;i<data.length;i++)
	{
		dataSource=data[i].data;
		reportDataSources.push(dataSource);
	}
	return reportDataSources;
}

//获取根节点
function getGridColumns()
{
	var code=GetQueryString("code");
	var header=getHeader(code);
	var headerId=header.repHeaderId;
	var columns=[];
	$.ajax({
		url:  _basePath+"/rep/reportLine/getGridColumn",
		type: 'POST',
		data: {
			"headerId":headerId
		},
		async: false,
		success: function (data) {
			data=sortColumns(data);
			var column=formatColumns(data);
			columns=column.columns;
		},
		error: function (jqXHR, textStatus, errorThrown) {
			kendo.ui.showInfoDialog({
				message:error
			})
		}
	});
	return columns;
}

function sortColumns(rootColumn)
{
	var children=rootColumn.columns;
	var column={};
	if(children.length!=undefined)
	{
		for(var i=0;i<children.length;i++)
		{
			for(var j=i+1;j<children.length;j++)
			{
				if(children[i].lineNumber>children[j].lineNumber)
				{
					column=children[i];
					children[i]=children[j];
					children[j]=column;
				}
			}
			children[i]=sortColumns(children[i]);
		}
	}
	return rootColumn;
}
//获取kendoui表头结构
function formatColumns(column)
{
	var children=column.columns;
	var newChildren=[];
	var child={};
	var newColumn={};
	newColumn.title=column.title;
	newColumn.headerAttributes={"style": "text-align :"+column.headerAlign+";vertical-align : middle"};

	if(children.length!=undefined)
	{
		if(column.columnType=="AUTO")
		{
			newColumn.field=column.field;
			newColumn.width=column.width;
			newColumn.hidden=column.hidden;
			newColumn.attributes={"style": "text-align:"+column.dataAlign};
			if(column.format!="" && column.format!=null)
			{
				newColumn.format="{0:"+column.format+"}";
			}
			if(column.isLink=="Y")
			{
				var lineTemplate=function(dataItem)
				{
					var lineField=column.field;
					var v = dataItem[lineField];
					var param=dataItem.linkParam;
					param='\''+param+'\'';
					return '<a href="javascript:void(0);" onclick="linkReport('+param+');">'+v+'</a>';
				};
				newColumn.template=lineTemplate;
			}
		}
		else
		{
			for(var i=0;i<children.length;i++)
			{
				child=formatColumns(children[i]);
				newChildren.push(child);
			}
			newColumn.columns=newChildren;
		}
	}
	return newColumn;
}


//展示kendoui表头
function showReportTitle(columns)
{
	var code=GetQueryString("code");
	var header=getHeader(code);
	var reportName=header.name+".xlsx";
	var dataSource=[];
    $("#report").css('border','none');
	$("#report").kendoGrid({
		//toolbar: ["excel"],
		dataSource: dataSource,
		resizable: true,
		scrollable: true,
		height:550,
		columns: columns,
		editable: false,
		sortable:true,
		reorderable:true
	});
	var grid=$('#report').data('kendoGrid');
	grid.setOptions({
		excel: {
			fileName: reportName
		},
	});
	//var grid=$("#report").kendoGrid();
}
//前端导出
function exportReport()
{
	var grid = $("#report").data("kendoGrid");
    grid.saveAsExcel();
}



/***************************END-查询报表结果并显示*****************************/



/***********************************后端导出****************************************/
function openRequestWin()
{
	//获取header
	var code=GetQueryString("code");
	var header=getHeader(code);
	//headerID
	var headerId=header.repHeaderId;
	paramsInformation=getParams(headerId);
	
	//声明一个params数组，如果params信息获取成功，则赋值，否则弹出提示
	params=[];
	if(paramsInformation.success==true)
	{
		params=paramsInformation.rows;
	}
	else
	{
    	kendo.ui.showInfoDialog({
        	message:paramsInformation.message
    	});
		return false;
	}
	//拼接参数字符串
	var paramsStr=exportParams(params);
	sessionStorage.paramsStr=paramsStr;
	//获取sql
	var sql=getParamsValue(header,params);
	sessionStorage.sql = sql;
	//报表唯一名
	var programName=header.programName;
	sessionStorage.programName=programName;
	//获取paramsObject
	var paramsObject=getParamsValue(header,params);
	//有未选择的参数框
	if(paramsObject.isNotNull==false)
	{
		var nullValue=paramsObject.nullValue;
		kendo.ui.showInfoDialog({
			message:nullValue+notEmpty
		});
		endModal(timer);
		return;
	}
	sessionStorage.paramsObject = JSON.stringify(paramsObject);

	
	//request窗口
	$("#scheduleRequest").kendoWindow({
		  content: "submit_schedule_request.html?paramsStr="+paramsStr+"&&sql="+sql,
		  iframe: true,
		  width: "500px",
    	  height:"330px",
          title: submitExportRequest,
          modal: true,
          actions: [
            "Pin",
            "Minimize",
            "Maximize",
            "Close"
          ],
          visible: false,
          close:function(){
        	  $("#scheduleRequest").html("");
          }
	});
	
	$("#scheduleRequest").data("kendoWindow").center().open();
}

//拼接params字符串
function exportParams(params)
{
	var paramsStr="";
	
	//声明行号，剩余参数数组和同行参数数组，剩余参数数组赋值为所有参数
	var rowNum=0;
	var restParams=params;
	var sameRowParams=[];
	
	//开始循环所有参数
	while(restParams.length!=0)
	{
		//循环下一行
		rowNum++;
		//赋值参数数组为剩余参数数组
		params=restParams;
		//清空剩余参数数组和同行参数数组
		restParams=[];
		sameRowParams=[];
		
		//循环参数数组，划分为当前行数组和剩余数组
		for(var i=0;i<params.length;i++)
		{
			if(params[i].rowNumber==rowNum)
			{
				sameRowParams.push(params[i]);
			}
			else
			{
				restParams.push(params[i]);
			}
		}

		//判断当前行是否有参数
		if(sameRowParams.length!=0)
		{
			//为同一行的参数按列排序
			sameRowParams=sortParams(sameRowParams);
			//渲染同一行的参数
			for(var i=0;i<sameRowParams.length;i++)
			{
				paramsStr+=getParamsStr(sameRowParams[i])+" &2& ";
			}
		}
		paramsStr+=" &3& ";
	}
	return paramsStr;
}
//获取参数名和值得拼接字符串
function getParamsStr(param)
{
	var paramsStr="";
	var value="";
	if(param.display!="N" && param.paramsName!="data_authority_user_id")
	{
		if(param.formElement=="SINGLE_SELECT")//select,下拉框
		{
			value=$("#"+param.paramsName).data("kendoDropDownList").value();
		}
    	else if(param.formElement=="INPUT")//text,输入框
		{
    		value=$("#"+param.paramsName).val();
		}
    	else if(param.formElement=="COMBOBOX")//combobox,下拉输入框
		{
    		value=$("#"+param.paramsName).data("kendoComboBox").value();
		}
    	else if(param.formElement=="MULTI_SELECT")//MultiSelect,下拉多选框
		{
    		value=$("#"+param.paramsName).data("kendoMultiSelect").value();
		}
    	else if(param.formElement=="CHECKBOX")//checkbox,多选框
		{
			var content=param.content;
			var contents=[];
			if(content!=null && content!="" && content.indexOf(",")>-1)
			{
				contents=content.split(",");
			}
			if($("#"+param.paramsName).is(':checked'))
			{
				value=contents[0];
			}
			else
			{
				value=contents[1];
			}
		}
    	else if(param.formElement=="DATE")//D,时间框
		{
    		value=$("#"+param.paramsName).data("kendoDatePicker").value();
    		if(value!="" && value!=null)
			{
        		value = new Date(value);
        		value=value.getFullYear()+"-"+(value.getMonth()+1)+"-"+value.getDate();
			}
		}
    	else if(param.formElement=="TIME")//TIME
		{
    		value=$("#"+param.paramsName).data("kendoDateTimePicker").value();
    		if(value!="" && value!=null)
			{
    			value = new Date(value);
        		value=value.getFullYear()+"-"+(value.getMonth()+1)+"-"+value.getDate()+" "+value.getHours()+"-"+value.getMinutes()+"-"+value.getSeconds();
			}
		}
    	else if(param.formElement=="LOV")//LOV
		{
    		value=$("#"+param.paramsName).data("value");
    		if(value==undefined)
			{
    			value=null;
			}
		}
	}
	paramsStr=param.name+":"+" &1& "+value+"  ";
	return paramsStr;
}
/********************************END0-后端导出************************************/

/***********************************等待框****************************************/

//等待框启动函数
function startModal(){
    toggleModel();
    var Modeltimer=null;
    var targetDiv=0;
    var divList=document.getElementsByClassName("moveDiv");
    Modeltimer=setInterval(function (){
        targetDiv=targetDiv%12;
        targetPre=(targetDiv+12)%12;
        targetone=targetDiv;
        targettwo=(targetDiv+1)%12;
        targetthree=(targetDiv+2)%12;
        divList[targetPre].style.background="#000";
        divList[targetDiv].style.background="#EEE";
        divList[targettwo].style.background="#AAA";
        divList[targetthree].style.background="#666";
        targetDiv++;
    },100);
    return Modeltimer;
}
//等待框结束函数
function endModal(timer){
    clearInterval(timer);
    toggleModel();
}
function toggleModel(){
    var e1 = document.getElementById('modal-overlay');
    e1.style.visibility =  (e1.style.visibility == "visible"  ) ? "hidden" : "visible";
}


/***********************************END-等待框****************************************/


/***********************************动态跳转部分***************************************/
function linkReport(dataItem)
{
	var code=dataItem.substring(dataItem.indexOf("=")+1,dataItem.indexOf("&paramsStr"));
	var headers=getHeaderInformation(code).rows;
	var header=headers[0];
	var headerName=header.name;
	closeTab("auto"+headerName);
	parent.openTab("auto"+headerName,headerName,'report/report.html?'+dataItem);
}

//解析参数字符串，通过与参数对比，调用设置参数值的方法设置值
function setParamsValue(code,paramsStr)
{
	// //隐藏查询按钮
	// $("#searchBtn").css("display","none");
	//从参数字符串中解析出参数对象
	var linkParam=analysisLinkParams(paramsStr);
	getReportContent(code,linkParam);
}
//从参数字符串中解析出参数对象
function analysisLinkParams(paramsStr)
{
	//用来保存参数字符串以逗号分割后的值
	var linkParamsStr=[];
	//用来保存每个参数名和值分割后的值
	var linkParamInfo=[];
	//用来保存切割后的所有参数
	//var linkParams=[];
	//每个参数对象
	var linkParam={};
	if(paramsStr.indexOf(",")>-1)
	{
		linkParamsStr=paramsStr.split(",");
		if(linkParamsStr.length!=undefined)
		{
			for (var i = 0; i < linkParamsStr.length; i++)
			{
				if(linkParamsStr[i].indexOf(":")>-1)
				{
					linkParamInfo = linkParamsStr[i].split(":");
					var name=linkParamInfo[0];
					linkParam[name]= linkParamInfo[1];
				}
			}
		}
		// else if(linkParamsStr.indexOf(":")>-1)
		// {
		// 	linkParamInfo = linkParamsStr.split(":");
		// 	linkParam[linkParamInfo[0]] = linkParamInfo[1];
		// }
	}
	else if(paramsStr.indexOf(":")>-1)
	{
		linkParamInfo = paramsStr.split(":");
		linkParam[linkParamInfo[0]] = linkParamInfo[1];
	}
	return linkParam;
}

function showLinkFormElements(params,rowNum,linkParam)
{
	//计算当前行最大宽度
	var rowWidth=0;
	for(var i=0;i<params.length;i++)
	{
		rowWidth+=100+params[i].showWidth+10+10;//50标题与框之间的距离，20不同参数之间的距离
	}
	//行div，添加在参数div中
	var $rowDiv='<div style="width:'+rowWidth+'px" class="rowParams" id="row'+rowNum+'"></div>';
	$("#formElements").append($rowDiv);

	$.each(params,function(i,n){
		if(this.display!="N" && this.paramsName!="data_authority_user_id"){
			var $beforeStr='<div id='+this.paramsName+'div class="cell" style="width:'+(100+this.showWidth+10)+'px"><span class="spanName">'+this.name+'</span><span class="quote">:</span>';
			var $afterStr='</div>';
			if(this.formElement=="SINGLE_SELECT")//select
			{
				var $str=$beforeStr;
				$str+="<span id="+this.paramsName+" style='width:"+this.showWidth+"px;vertical-align:middle;margin-left:8px'></span>";
				$str+=$afterStr;
				$("#row"+rowNum).append($str);
				var paramName=this.paramsName;
				var defaultValue=linkParam[paramName];
				showDropLists(this,defaultValue);
			}
			else if(this.formElement=="INPUT")//text
			{
				var $str=$beforeStr;
				$str+='<input type="text" id='+this.paramsName+' class="k-textbox" style="width:'+this.showWidth+'px;height:28px;vertical-align:middle;margin-left:8px">';
				$str+=$afterStr;
				$("#row"+rowNum).append($str);

				var paramName=this.paramsName;
				var defaultValue=linkParam[paramName];
				$("#"+this.paramsName).val(defaultValue);
			}
			else if(this.formElement=="COMBOBOX")//combobox
			{
				var $str=$beforeStr;
				$str+="<span id="+this.paramsName+" style='width:"+this.showWidth+"px;vertical-align:middle;margin-left:8px'></span>";
				$str+=$afterStr;
				$("#row"+rowNum).append($str);
				var paramName=this.paramsName;
				var defaultValue=linkParam[paramName];
				showComboboxs(this,defaultValue);
			}
			else if(this.formElement=="MULTI_SELECT")//MultiSelect
			{
				var $str=$beforeStr;
				$str+="<span id="+this.paramsName+" style='width:"+this.showWidth+"px;vertical-align:middle;margin-left:8px'></span>";
				$str+=$afterStr;
				$("#row"+rowNum).append($str);
				var paramName=this.paramsName;
				var defaultValue=linkParam[paramName];
				showMultiSelects(this,defaultValue);
			}
			else if(this.formElement=="CHECKBOX")//checkbox
			{
				var $str=$beforeStr;
				$str+=showCheckboxs(this);
				$str+=$afterStr;
				$("#row"+rowNum).append($str);

				//设置默认值
				var paramName=this.paramsName;
				var defaultValue=linkParam[paramName];
				var content=this.content;
				var contents=[];
				if(content!=null && content!="" && content.indexOf(",")>-1)
				{
					contents=content.split(",");
				}
				if(defaultValue==contents[0])
				{
					$("#" + this.paramsName).attr("checked", 'true');
				}
			}
			else if(this.formElement=="DATE")//Date
			{
				var $str=$beforeStr;
				$str+="<input id="+this.paramsName+" style='width:"+this.showWidth+"px;height:28px;vertical-align:middle;margin-left:8px'/>";
				$str+=$afterStr;
				$("#row"+rowNum).append($str);
				var paramName=this.paramsName;
				var defaultValue=linkParam[paramName];
				showDate(this,defaultValue);
			}
			else if(this.formElement=="TIME")//Time
			{
				var $str=$beforeStr;
				$str+="<input id="+this.paramsName+" style='width:"+this.showWidth+"px;vertical-align:middle;margin-left:8px'/>";
				$str+=$afterStr;
				$("#row"+rowNum).append($str);
				var paramName=this.paramsName;
				var defaultValue=linkParam[paramName];
				showTime(this,defaultValue);
			}
			else if(this.formElement=="LOV")//LOV
			{
				var $str=$beforeStr;
				$str+='<span class="popup" id="'+this.paramsName+'popup"><input placeholder="请输入" id='+this.paramsName+' onblur="lovOnBlur('+this.queryParamsId+');" class="lovInput"/>'
					+'<span class="k-icon k-i-close popupClear show" id="'+this.paramsName+'popupClear"></span>'
					+'<span class="popupSearch" id="'+this.paramsName+'popupSearch"><span class="k-icon k-i-search popupSearchIcon"></span></span></span>';
				$str+=$afterStr;
				$("#row"+rowNum).append($str);
				showLov(this);
				//设置默认值
				var paramName=this.paramsName;
				var defaultValue=linkParam[paramName];
				$("#"+this.paramsName).data("value",defaultValue);
				$("#"+this.paramsName).val(defaultValue);

			}
		}
	});
}

//发送请求，查询数据
function getReportContent(code,params)
{
	var timer=startModal();

	var headers=getHeaderInformation(code).rows;
	var header=headers[0];
	var headerId=header.repHeaderId;


	params.repHeaderId=headerId+"";

	$.ajax({
		type: "POST",
		contentType: "application/json;charset=utf-8",
		url: _basePath+"/rep/reportHeader/getReportDataSource",
		data: JSON.stringify(params),
		dataType: "json",
		async: true,
		success: function (data) {
			endModal(timer);
			if(data.success==false)
			{
				kendo.ui.showInfoDialog({
					message:data.message
				});
			}
			else if(data.success==true)
			{
				data=analysisDataSource(data.rows);

				var dataSource = new kendo.data.DataSource({
					data: data,
				});


				var grid=$('#report').data('kendoGrid');
				grid.setDataSource(dataSource);


				//var columns=getReportColumns(lines);
				var columns=getGridColumns();
				grid.setOptions({
					//toolbar: ["excel"],
					columns: columns,
					excel: {
						fileName: header.name+".xlsx"
					},
				});
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {
			endModal(timer);
			kendo.ui.showInfoDialog({
				message:error
			})
		}
	});
}
//关闭tab页
function closeTab(id){
	var parent = window.parent.parent.$("#mainTab");
	var mainTab = parent.data("kendoTabStrip");
	var idx = jQuery.inArray(id,mainTab.tabids),
		activeIndex = mainTab.tabGroup.children('li.k-state-active').index();
	if(idx == -1) return;
	mainTab.remove(idx);
	if (activeIndex == idx ) {
		if (mainTab.tabids.length >= idx + 1) mainTab.select(idx)
		else if (idx - 1 >= 0) mainTab.select(idx - 1);
	}
};

/***********************************END-动态跳转部分***********************************/
