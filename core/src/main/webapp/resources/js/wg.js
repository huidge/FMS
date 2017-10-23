/*
 *time:2017/01/14
 *sheng.wu01@hand-china.com
 * 将gird的id传递过来
 * 页面清除数据**/
function cleanData(Grid) {
    grid = $("#"+Grid).data("kendoGrid");
    var checked=grid.selectedDataItems();
    if(checked.length){
        $.each(checked, function (i, v) {
            v['cleanFlag'] = 'Y';
            grid.dataSource.remove(v)
        })
    }else{
        var a=grid._data.length;
        var v=grid._data[--a];
        v['cleanFlag'] = 'Y'; 
        grid.dataSource.remove(v)
    }
}
/*
 * time:2017/03/24
 * xiang.ding@hand-china.com
 * 将gird的id传递过来
 * 页面清除选中数据，必须有选中的数据
 */
function cleanSelectData(Grid) {
    grid = $("#"+Grid).data("kendoGrid");
    var checked=grid.selectedDataItems();
    if(checked.length){
    	kendo.ui.showConfirmDialog({
            title:$l('hap.tip.info'),
            message: $l('hap.tip.clean_confirm')
        }).done(function (event) {
            if (event.button == 'OK') {
            	 $.each(checked, function (i, v) {
                     v['cleanFlag'] = 'Y';
                     grid.dataSource.remove(v);
                 })
            }
        }) 
    }else{
    	kendo.ui.showInfoDialog({
            message: $l('hap.tip.selectrow')
        })
    }
}
/*
 *time:2017/02/27
 *sheng.wu01@hand-china.com
 * 将form的id传递过来
 * _fromId from的Id
 * canEdit 是否可以编辑 true：不可编辑；false：可以编辑
 * _start 影响的开始元素
 * _end   影响的结束元素
 * **/
function formCantEdit(_fromId,cantEdit,_start,_end){
    var formId=document.getElementById(_fromId);
    var i=0;
    if(_start!=null&&_start!=""){
        i=_start;
    }
    var fLength=formId.elements.length;
    if(_end!=null&&_end!=""){
        fLength=_end;
    }
    for (i;i<fLength;i++)
    {
        var obj=$("#" + formId.elements[i].id);
        if(cantEdit){
            obj.css({
                "background-image" : "none",
                "background-color" : "#F5F5F5"
            })
        }else {
            obj.css({
                "background-image" : "none",
                "background-color" : "#FFFFFF"
            })
        }
        obj.attr("readonly", cantEdit);
    }
}
/*
 *LOV不可编辑
 *例子：lovCantEdit("字段名",true)
 */
function lovCantEdit(_field,_edit){
    $("#"+_field+"").data('kendoLov').readonly(_edit);
    if(_edit){
        $("input[name='"+_field+"_input']").css({
            "background-image" : "none",
            "background-color" : "#F5F5F5"
        })
        $("input[name='"+_field+"_input']").parent().css({
            "background-image" : "none",
            "background-color" : "#F5F5F5"
        })
    }else{
        $("input[name='"+_field+"_input']").css({
            "background-image" : "none",
            "background-color" : "#FFF"
        })
        $("input[name='"+_field+"_input']").parent().css({
            "background-image" : "none",
            "background-color" : "#FFF"
        })
    }
}

/**时间框,多语言框，不可编辑
 *例子：时间不可编辑 dateTLCantEdit("定义的Id","kendoDatePicker",true);
 *多语言不可编辑 dateTLCantEdit("定义的Id","kendoTLEdit",true)
 **/
function dateTLCantEdit(_id,_edit,_type){
	_type = _type || 'kendoDatePicker';
    $("#"+_id).data(_type).readonly(_edit);
    if(_edit){
        $("#"+_id+"").css({
            "background-image" : "none",
            "background-color" : "#F5F5F5"
        })
        $("#"+_id+"").parent().css({
            "background-image" : "none",
            "background-color" : "#F5F5F5"
        })
    }else{
        $("#"+_id+"").css({
            "background-image" : "none",
            "background-color" : "#FFF"
        })
        $("#"+_id+"").parent().css({
            "background-image" : "none",
            "background-color" : "#FFF"
        })
    }
}
/**
 *combox不可编辑 例子：combox不可编辑
 *comboxCantEdit("字段名",true);
 **/
function comboxCantEdit(_field,_edit){
    $("#"+_field+"").data('kendoComboBox').readonly(_edit);
    if(_edit){
        $("input[aria-owns='"+_field+"_listbox']").css({
            "background-image" : "none",
            "background-color" : "#F5F5F5"
        })
        $("input[aria-owns='"+_field+"_listbox']").parent().css({
            "background-image" : "none",
            "background-color" : "#F5F5F5"
        })
    }else{
        $("input[aria-owns='"+_field+"_listbox']").css({
            "background-image" : "none",
            "background-color" : "#FFF"
        })
        $("input[aria-owns='"+_field+"_listbox']").parent().css({
            "background-image" : "none",
            "background-color" : "#FFF"
        })
    }
}
/**
 *DropDownList不可编辑
 * 例子：combox不可编辑 dropDownListCantEdit("字段名",true);
* */
function dropDownListCantEdit(_field,_edit){
    $("#"+_field+"").data('kendoDropDownList').readonly(_edit);
    if(_edit){
        $("span[aria-owns='"+_field+"_listbox']").css({
            "background-image" : "none",
            "background-color" : "#F5F5F5"
        })
        $("span[aria-owns='"+_field+"_listbox']").children().css({
            "background-image" : "none",
            "background-color" : "#F5F5F5"
        })
    }else{
        $("span[aria-owns='"+_field+"_listbox']").css({
            "background-image" : "none",
            "background-color" : "#FFF"
        })
        $("span[aria-owns='"+_field+"_listbox']").children().css({
            "background-image" : "none",
            "background-color" : "#FFF"
        })
    }
}
/*
*隐藏显示
 *time:2017/02/28
 *sheng.wu01@hand-china.com
 * _id  隐藏的内容的父Id
 * imgId 图片的Id
 * _basePath 基础路径
 * **/
var flag=true;
function formQueryHide(_id,_basePath, imgId) {
    if (flag == true) {
        $("#" + imgId).attr("src", "" + _basePath + "/resources/images/down.png");

        $("#" + _id).hide()
        flag = false;
        $(window).resize();
    } else {
        flag = true;
        $("#" + _id).show();
        $("#" + imgId).attr("src", "" + _basePath + "/resources/images/up.png");
        $(window).resize();
    }
}

/*
 *time:2017/02/27
 *sheng.wu01@hand-china.com
 * 输入框是否可以编辑
 * textId id传递过来
 * cantEdit 输入框是否可以编辑 true：不可编辑；false：可以编辑
 * *
 *
 * */
function textCantEdit(textId,cantEdit) {
    var obj = $("#" + textId);
    if(cantEdit){
        obj.css({
            "background-image" : "none",
            "background-color" : "#F5F5F5"
        })
    }else{
        obj.css({
            "background-image" : "none",
            "background-color" : "#FFF"
        })
    }

    obj.attr("readonly", cantEdit);
}

//LOV不可编辑，传入name
function lovEdit(_name){
    $("input[name="+_name+"_input"+"]").parent().removeClass("k-state-default").addClass("k-state-disabled").unbind();
    $("input[name="+_name+"_input"+"]").attr({"readonly":"true","aria-disabled":"true"}).css("background", "#DDDDDD").unbind();
    $("input[name="+_name+"_input"+"]").next().next().unbind();
}

/*
 *time:2017/02/24
 *sheng.wu01@hand-china.com
 *在页面定义对应的 id
 * 省市县下拉定义**/
/*
*在页面内将对应的Id赋值就会加载
页面如果没有配置对应的Id,该组件不会加载
*
* */

function queryAreaRedis(){
    var myProvinceData = [];
    function myProvinceToJson(data) {
        this.areaName = data.areaName;
        this.province = data.province;
    }
    $.ajax({
        url : _basePath+'/fnd/area/queryRedis',
        type : 'POST',
        contentType : "application/json;charset=utf-8",
        cache : true,
        dataType : 'json',
        async : false,
        success : function(data) {
            for (var i = 0; i < data.length; i++) {
                myProvinceData.push(new myProvinceToJson(data[i]));
            }
        }
    });
    return myProvinceData;

}

var provinceDropDownListId="-1";   //省组件Id
var cityDropDownListId="-1";       //市组件Id
var regionDropDownListId="-1";     //县组件Id
$(document).ready(function(){
    if(provinceDropDownListId!="-1"){
        var myData = queryAreaRedis();
        console.log(myData);
        var mySecondData=[];
        var mySecondCopy=[];
        var myThirdData=[];

        function mySecondToJson(data) {
            this.areaName = data.areaName;
            this.city = data.city;
        }

        function myThirdToJson(data) {
            this.areaName = data.areaName;
        }

        if(typeof($("#"+provinceDropDownListId).val()) != 'undefined'){
            var p= $("#"+provinceDropDownListId).val();
            for (var i in myData) {
                if (myData[i].areaName == p) {
                    for (var j = 0; j < myData[i].province.length; j++) {
                        mySecondData.push(new mySecondToJson(myData[i].province[j]));
                    }
                }
            }
        }
        if(typeof($("#"+cityDropDownListId).val()) != 'undefined'){
            var c= $("#"+cityDropDownListId).val();
            for (var i in mySecondData) {
                if (mySecondData[i].areaName == c) {
                    for (var j = 0; j < mySecondData[i].city.length; j++) {
                        myThirdData.push(new myThirdToJson(mySecondData[i].city[j]));
                    }
                }
            }
        }
        //初始化控件
        var firstDropDownList = $("#"+provinceDropDownListId).kendoDropDownList({
            optionLabel:provinceName,
            dataTextField:"areaName",
            dataValueField:"areaName",
            dataSource:{
                data:myData
            },
            valuePrimitive: true,
            change:function() {
                if(cityDropDownListId!="-1"){
                var mySecondData = [];
                if (regionDropDownListId != "-1") {
                thirdDropDownList.dataSource.data(mySecondData);
                }
                for (var i in myData) {
                    if (myData[i].areaName == firstDropDownList.dataItem().areaName) {
                        for (var j = 0; j < myData[i].province.length; j++) {
                            mySecondData.push(new mySecondToJson(myData[i].province[j]));
                        }
                    }
                }
                mySecondCopy = mySecondData;
                secondDropDownList.dataSource.data(mySecondData);
              }
            }
        }).data("kendoDropDownList");

        if(cityDropDownListId!="-1") {
            var secondDropDownList = $("#" + cityDropDownListId).kendoDropDownList({
                optionLabel: cityName,
                dataTextField: "areaName",
                dataValueField: "areaName",
                dataSource: {
                    data: mySecondData
                },
                valuePrimitive: true,
                change: function () {
                    if (regionDropDownListId != "-1") {
                        var myThirdData = [];
                        for (var i in mySecondCopy) {
                            if (mySecondCopy[i].areaName == secondDropDownList.dataItem().areaName) {
                                for (var j = 0; j < mySecondCopy[i].city.length; j++) {
                                    myThirdData.push(new myThirdToJson(mySecondCopy[i].city[j]));
                                }
                            }
                        }
                        thirdDropDownList.dataSource.data(myThirdData);
                    }
                }
            }).data("kendoDropDownList");
            if (regionDropDownListId != "-1") {
                var thirdDropDownList = $("#" + regionDropDownListId).kendoDropDownList({
                    optionLabel: regionName,
                    valuePrimitive: true,
                    dataTextField: "areaName",
                    dataValueField: "areaName",
                    dataSource: {
                        data: myThirdData
                    },
                }).data("kendoDropDownList");
            }
        }
    }
  })

/**
 * @autor xiang.ding@hand-china.com
 * @param _id
 * @param _edit
 * numericTextBox编辑框不可编辑
 */
function numericTextBoxCantEdit(_id,_edit){
	$("#"+_id).data("kendoNumericTextBox").readonly(_edit);
	if(_edit){
		$("#"+_id).data("kendoNumericTextBox").wrapper.find("input").css({
			"background-image" : "none",
			"background-color" : "#F5F5F5"
		});
        $("#"+_id).data("kendoNumericTextBox").wrapper.find("input").parent().css({
            "background-image" : "none",
            "background-color" : "#F5F5F5"
        })
	}else{
		$("#"+_id).data("kendoNumericTextBox").wrapper.find("input").css({
			"background-image" : "none",
			"background-color" : "#FFF"
		});
        $("#"+_id).data("kendoNumericTextBox").wrapper.find("input").parent().css({
            "background-image" : "none",
            "background-color" : "#FFF"
        })
	}
}

/*
 * time:2017/06/08
 * gaokuo.dai@hand-china.com
 * 将gird的id传递过来，不要带#号
 * 复制Grid中选中数据并插入Grid中
 */
function copySelectedDataInGrid(gridId){
	if(!gridId || gridId == '') return;
	var grid = $('#'+gridId).data('kendoGrid');
	var selectDatas = grid.selectedDataItems();
	if(!selectDatas || selectDatas.length <= 0) return;
	$(selectDatas).each(function(i,v){
		//复制出来的对象必须输源对象的深拷贝，不然会出错
		grid.addRow();
		var newLine = grid.dataSource.data()[0];
		for(var k in v){
			if(k == 'uid' || k == '_events' || k == '_handlers') continue;
			if(typeof v[k] == 'function') continue;
			newLine[k] = v[k];
		}
		newLine.contactId = null;
		newLine.dirty = true;
		grid.refresh();
	});
}
/**
 * 数字千分位化
 * @param mVal 值
 * @param iAccuracy 位数
 * @returns {string}
 */
function formatMoney(mVal, iAccuracy){
    mVal = mVal || 0;
    var fTmp = 0.00;//临时变量
    var iFra = 0;//小数部分
    var iInt = 0;//整数部分
    var aBuf = new Array(); //输出缓存
    var bPositive = true; //保存正负值标记(true:正数)
    /**
     * 输出定长字符串，不够补0
     * <li>闭包函数</li>
     * @param int iVal 值
     * @param int iLen 输出的长度
     */
    function funZero(iVal, iLen){
        var sTmp = iVal.toString();
        var sBuf = new Array();
        for(var i=0,iLoop=iLen-sTmp.length; i<iLoop; i++)
            sBuf.push('0');
        sBuf.push(sTmp);
        return sBuf.join('');
    };

    if (typeof(iAccuracy) === 'undefined')
        iAccuracy = 2;
    bPositive = (mVal >= 0);//取出正负号
    fTmp = (isNaN(fTmp = parseFloat(mVal))) ? 0 : Math.abs(fTmp);//强制转换为绝对值数浮点
    //所有内容用正数规则处理
    iInt = parseInt(fTmp); //分离整数部分
    iFra = parseInt((fTmp - iInt) * Math.pow(10,iAccuracy) + 0.5); //分离小数部分(四舍五入)

    do{
        aBuf.unshift(funZero(iInt % 1000, 3));
    }while((iInt = parseInt(iInt/1000)));
    aBuf[0] = parseInt(aBuf[0]).toString();//最高段区去掉前导0
    return ((bPositive)?'':'-') + aBuf.join(',') +'.'+ ((0 === iFra)?'00':funZero(iFra, iAccuracy));
}
/**
 * wsheng.wu01@hand-china.com
 * 页面加载显示
 */
function loadingShow(){
    pb.value(false);
    var top = ($(window).height() - $("#model").height())/2;
    var left = ($(window).width() - $("#model").width())/2;
    var scrollTop = $(document).scrollTop();
    var scrollLeft = $(document).scrollLeft();
    $("#model").css( { position : 'absolute', opacity:0.5, 'top' : top + scrollTop, left : left + scrollLeft } ).show();
}
/**
 *  wsheng.wu01@hand-china.com
 * 页面加载隐藏显示
 */
function loadingHide(){
    pb.value(true);
    $("#model").hide();
}
/**
 * 保存头行数据(1个form,0个1个或多个 grid).
 *
 * <ul>
 * <li>opts.url: 提交的url</li>
 * <li>opts.type|method: 提交的 http method (默认 POST)</li>
 * <li>opts.formModel: form绑定的 model</li>
 * <li>opts.asArray: form 作为数组提交(默认 true)</li>
 * <li>opts.grid: grid name 和 dom</li>
 *      <ul>
 *          <li>key:bindName</li>
 *          <li>value:grid dom</li>
 *      </ul>
 * <li>opts.success: success回调函数</li>
 * <li>opts.failure: failure回调函数</li>
 * </ul>
 * @param opts
 */
function definitionSubmitForm (opts) {
    opts = opts || {};
    var url = opts.url;
    if (!opts.formModel || !url) {
        return;
    }
    opts.asArray = ('asArray' in opts) ? (!!opts.asArray) : true;
    var grids = opts.grid || {};

    var header = opts.formModel.toJSON();
    var changedDs = {};
    $.each(grids, function (bindName, grid) {
        var ds = grid.data("kendoGrid").dataSource;
        if (!ds.hasChanges())return;
        changedDs[bindName] = ds;
        header[bindName] = []
        $.each(ds.data(), function (idx, data) {
            if (data.dirty === true) {
                var d = data.toJSON();
                d['__status'] = data.isNew() ? 'add' : 'update';
                d['__id'] = data.uid;
                header[bindName].push(d);
            }
        });
    })
    loadingShow();
    $.ajax({
        url: url,
        async: opts.async||false,
        type: opts.type || opts.method || 'POST',
        contentType: opts.contentType || 'application/json',
        data: kendo.stringify(opts.asArray ? [header] : header),
        success: function (result) {
            loadingHide();
            if (result.success === true) {
                var h = opts.asArray ? (result.rows[0] || {}) : result;
                if (opts.formModel.set) {
                    $.each(h, function (k, v) {
                        opts.formModel.set(k, v);
                    })
                } else
                    $.extend(opts.formModel, h);
                delete opts.formModel['__status'];
                $.each(changedDs, function (bindName, source) {
                    $.each(h[bindName] || [], function (i, r) {
                        var _r = source.getByUid(r.__id);
                        if (_r) {
                            if (r.__status == 'delete') {
                                //source.remove(_r)
                            } else {
                                delete r['__status'];
                                delete r['__id'];
                                _r.accept(r);
                            }
                        }
                    });
                    grids[bindName].find(".k-dirty").removeClass("k-dirty");
                });
                if (opts.success instanceof Function) {
                    opts.success(result)
                } else {
                    Hap.showTip($l('hap.tip.success'))
                }
            } else {
                if (opts.failure instanceof Function) {
                    opts.failure(result)
                } else {
                    kendo.ui.showErrorDialog({
                        title: $l('hap.error'),
                        width: 400,
                        message: result.message
                    })
                }
            }
        }
    });
}

/**
 * @Description 由传的field和title动态生成列
 * @param  field
 * @param  title
 * @author xiang.ding@hand-china.com	2017年4月7日下午13:10:24
 */
getColumn = function(field, title){
	var column = {
		field: field,
        title: title,
        width: 100,
        headerAttributes: {
            "class": "table-header-cell",
            style: "text-align: center"
        },
        attributes: {
            "class": "table-cell",
            style: "text-align: center"
        },
    };
	return column;
}

/**
 * 翻译快码
 * @param codeSet 快码集合
 * @param value 快码value
 * @returns 快码meaning or ''
 */
function fastCode(codeSet, value) {
	var v = value;
	codeSet = codeSet || [];
	$.each(codeSet,function(i, n) {
		if ((n.value || '').toLowerCase() == (v || '').toLowerCase()) {
			v = n.meaning;
			return v;
		}
	});
	return v || '';
}