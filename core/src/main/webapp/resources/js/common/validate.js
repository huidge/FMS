/**
 * kendoUi通用校验组件
 * @author bo.wu@hand-china.com 
 */

/**
 * 功能性校验数组
 */
var functionArray = [];

/**
 * 唯一性校验数组
 */
var uniqueArray = [];

/**
 * 必输校验数组
 */
var emptyArray = [];

/**
 * 根据值向数组添加元素
 * @param arr 数组
 * @param val 值
 */
function addByValue(arr, val) {
  for(var i=0; i<arr.length; i++) {
    if(arr[i] == val) {
      return;
    }
  }
  arr.push(val);
}

/**
 * 根据值移除数组元素
 * @param arr 数组
 * @param val 值
 */
function removeByValue(arr, val) {
	  for(var i=0; i<arr.length; i++) {
	    if(arr[i] == val) {
	      arr.splice(i, 1);
	      break;
	    }
	  }
	}
/**
 * 唯一性校验
 * @param value 元素的id
 * @param id 	grid的id，若为空则为form 
 */
function uniqueValidator(key,id){
	//viewModel数据
	var data = viewModel.model;
	var formurl = data.formurl;
	var gridurl = data.gridurl;
	if(id == "" || id == null){
		//要校验的数据
		var value = $("#"+key).val();
		if(value == "")return;
		//获取根节点
		var parent = getParentById(key);
		//获取字段名
		var name = parent.find('label')[0].innerText;
		//如果是新增数据，直接调用url判断即可
		if(data.__status == "add"){
			$.ajax({
				 url: formurl+"?"+key+"="+value,
	             type: "POST",
	             dataType: "json",
	             async:false,
                 success:function(json){
                	 if(json.total != 0){
                		addMessage(parent,'notunique'+key,name+validateNotUnique);
                		addByValue(uniqueArray,key);
                		return;
                	 }else{
 	               		removeMessage('notunique'+key);
 	               		removeByValue(uniqueArray,key);
 	               	}
                 }
			})
		}
		//不是新增数据，需要判断_token是否相同
		else if(data.__status == "update"){
			$.ajax({
				 url: formurl+"?"+key+"="+value,
	             type: "POST",
	             async:false,
	             dataType: "json",
                 success:function(json){
	                 if(json.total != 0){
	               		 if(json.total >= 2){
	               			addMessage(parent,'notunique'+key,name+validateNotUnique);
	               			addByValue(uniqueArray,key);
	               			return;
	               		 }else{
	               			 var rows = json.rows;
	               			 if(rows[0]._token != data._token){
	               				addMessage(parent,'notunique'+key,name+validateNotUnique);
	               				addByValue(uniqueArray,key);
	               				return;
	               			 }else{
	               				removeMessage('notunique'+key);
	               				removeByValue(uniqueArray,key);
	               			 }
	               		 }
	               	}else{
	               		removeMessage('notunique'+key);
	               		removeByValue(uniqueArray,key);
	               	}
                }
			})
		}
	}
	//grid的唯一性校验(待开发)
	else{
		//首先判断所有修改数据是否有重复
		var grid = $("#"+id).data("kendoGrid");
	}
}

/**
 * 添加校验信息 
 * @param 父级元素
 * @param 元素id
 * @param 消息
 */
function addMessage(parent,type,message){
	var children = parent.children();
	for(var i=0;i<children.length;i++){
		if(children[i].id == type){
			return;
		}
	}
	var index = message.lastIndexOf("*");
	if(index != -1){
		var str1 = message.substring(0,index);
		var str2 = message.substring(index+1,message.length);
		message = str1+str2;
	}
	var html='<span id="'+type+'"  class="k-widget k-tooltip k-tooltip-validation k-invalid-msg" role="alert"><span class="k-icon k-warning"> </span>'+message+'</span>';
	parent.append(html);
}

/**
 * 移除校验信息 
 */
function removeMessage(type){
	$("#"+type).remove();
}

/**
 * 校验必输
 */
function validateEmpty(id){
	var value = $("#"+id).val();
	if(value == ""){
		var parent = getParentById(id);
		var name = parent.find('label')[0].innerText;
		addMessage(parent,'notempty'+id,name+validateNotEmpty);
		addByValue(emptyArray,id);
		return false;
	}else{
		removeMessage('notempty'+id);
		removeByValue(emptyArray,id);
		return true;
	}
}

/**
 * 校验必输和唯一(提交时)
 */
function submitValidate(ids){
	for(var i=0;i<ids.length;i++){
		var require = document.getElementById(ids[i]).getAttribute("require");
		if(require == 'true' && $.inArray(ids[i],emptyArray) == -1){
			validateEmpty(ids[i]);
		}
		var unique = document.getElementById(ids[i]).getAttribute("unique");
		if(unique == 'true' && $.inArray(ids[i],uniqueArray) == -1){
			uniqueValidator(ids[i]);
		}
	}
	if(emptyArray.length != 0 || uniqueArray.length != 0 || functionArray.length != 0){
		var height = 0;
		if(emptyArray.length != 0)height = $("#"+emptyArray[0]).parent().offset().top;
		else if(uniqueArray.length != 0)height = $("#"+uniqueArray[0]).parent().offset().top;
		else if(functionArray.length != 0)height = $("#"+functionArray[0]).parent().offset().top;
		$(document).scrollTop(height);
		return false;
	}
	return true;
}

/**
 * 获取父元素 
 */
function getParentById(key){
	var parent = $("#"+key).parent();
	//获取根节点
	while(parent[0].className != "form-group"){
		parent = parent.parent();
	}
	return parent;
}

/**
 * 根据form的id获取组件id
 */
function getFormIdsByFormId(formId){
	var formElement = $("#"+formId)[0];
	var ids = [];
	for(var i=0;i<formElement.length;i++){
		if(formElement[i].id != '' && formElement[i].id != undefined){
			ids.push(formElement[i].id);
		}
	}
	return ids;
}

function dateCheck(id){    
	var date = $("#"+id).val(); 
	if(!validateEmpty(id)){
		return;
	}
	var result = date.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);    
	if (result == null){    	
		addByValue(functionArray,"dateerror"+id);
		var parent = getParentById(id);
		addMessage(parent,"dateerror"+id,validateDateError);    	    	
		return;    
	}    
	var d = new Date(result[1], result[3] - 1, result[4]);    
	if(!(d.getFullYear() == result[1] && (d.getMonth() + 1) == result[3] && d.getDate() == result[4])){    	
		addByValue(functionArray,"dateerror"+id);
		var parent = getParentById(id);
		addMessage(parent,"dateerror"+id,validateDateError);    	    	
		return;    
	}
	removeByValue(functionArray,"dateerror"+id);
	removeMessage("dateerror"+id);
}

function telCheck(id,length){
	var tel = $("#"+id).val(); 
	if(length == 11){
		var result = tel.match(/(^((0\d{2,3})-)(\d{7,8})(-(\d{3,}))?$)|(^1(3|4|5|7|8)\d{9}$)/); 
	}else if(length == 8){
		var result = tel.match(/(^\d{8}$)/); 
	}else if(length == 9){
		var result = tel.match(/(^\d{9}$)/); 
	}
	if (result == null){ 
		addByValue(functionArray,"telerror"+id);
		var parent = getParentById(id);
		addMessage(parent,"telerror"+id,validateTelError);    	
		return;    
	}else{
		removeByValue(functionArray,"telerror"+id);
		removeMessage("telerror"+id);
	} 
}

function emailCheck(id){
	var email = $("#"+id).val(); 
	var result = email.match(/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/); 
	if (result == null){ 
		addByValue(functionArray,"emailerror"+id);
		var parent = getParentById(id);
		addMessage(parent,"emailerror"+id,validateEmailError);    	
		return;    
	}else{
		removeByValue(functionArray,"emailerror"+id);
		removeMessage("emailerror"+id);
	} 
}

/*
function dateCheck(from,to) {
    var fromdate = $("#"+from).val();
    var todate = $("#"+to).val();
    if(dateFormatCheck(from) && dateFormatCheck(to)){
    	var fromTransformDate = new Date(fromdate);
    	var toTransformDate = new Date(todate);
    	if(fromTransformDate > toTransformDate){
    		var fromParent = getParentById(from);
    		var toParent = getParentById(to);
    		addMessage(fromParent,from,'开始日期不能大于截止日期！');
    		addMessage(toParent,to,'截止日期不能小于开始日期！');
    	}else{
    		removeMessage(from);
    		removeMessage(to);
    	}
    }
   
}

function dateFormatCheck(id){
	var date = $("#"+id).val();
	var result = date.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
    if (result == null){
    	$('#'+id).val('');
    	validateEmpty(id);
    	return false;
    }
    var d = new Date(result[1], result[3] - 1, result[4]);
    if(!(d.getFullYear() == result[1] && (d.getMonth() + 1) == result[3] && d.getDate() == result[4])){
    	$('#'+id).val('');
    	validateEmpty(id);
    	return false;
    }
    return true;
}*/