/**
 * Forbidden modifying,all right reserved.
 * Created on 2017/3/10.
 * @author young
 */

// =====================GRID OPERATION===============================================start
/**
 * extract grid Object from id
 * @param _divId
 * @author young
 * @returns {*|jQuery}
 */
function gridUtils_gridFromDivId(param_divId) {
    return $("#"+param_divId).data("kendoGrid");
}

function gridUtils_setTopLevelOptions(param_gridDivId, param_key, param_value) {
    var ops = {};
    ops[""+param_key] = param_value;
    // gridUtils_gridFromDivId(param_gridDivId).setOptions({param_key:param_value}); already fixed
    gridUtils_gridFromDivId(param_gridDivId).setOptions(ops);
}
/**
 * allow user to edit the whole grid
 * @author young
 * @param param_gridDivId
 */
function gridUtils_allowEditWholeGrid(param_gridDivId) {
    gridUtils_setTopLevelOptions(param_gridDivId,"editable",true);
}

/**
 * forbidden editing the whole grid
 * @author young
 * @param param_gridDivId
 */
function gridUtils_forbidEditWholeGrid(param_gridDivId) {
    gridUtils_setTopLevelOptions(param_gridDivId,"editable",false);
}

/**
 * switch off editing mode of columns
 * @author young
 * @param param_gridDivId
 * @param param_columnIndexArray array,example:[0,1,2].index starts from 0.
 */
function gridUtils_forbidEditColumns(param_gridDivId, param_columnIndexArray) {
    var _thisGrid = gridUtils_gridFromDivId(param_gridDivId);
    _thisGrid.OP_PMS_forbidEditColumns=[];
    $.each(param_columnIndexArray, function (key, ele) {
        _thisGrid.OP_PMS_forbidEditColumns.push(ele);
    });
    gridUtils_setTopLevelOptions(param_gridDivId,"edit",function (e) {
        // make compatible for old edit function if exists
        var localGrid = gridUtils_gridFromDivId(param_gridDivId);
        if(!localGrid || !localGrid.OP_PMS_forbidEditColumns){
            return;
        }else if(localGrid.OP_PMS_forbidEditColumns===[]){
            return;
        }else {
            var nowIndex = e.container[0].cellIndex;
            if(localGrid.OP_PMS_forbidEditColumns.indexOf(nowIndex) > -1){
                e.container.children().attr("readonly","readonly");
                // e.container.children().attr("disabled","disabled");//need not consider for nested or customized editor
                e.container.children().click(function (event) {
                    event.preventDefault();
                });
                e.container.context.innerHTML = '<span></span>';
            }
        }
    });
}

/**
 * hide columns of given indexes.
 * @param param_gridDivId
 * @param param_columnIndexArray array of column index,the index start from 0.
 */
function gridUtils_hideColumns(param_gridDivId, param_columnIndexArray) {
    console.log(param_columnIndexArray);
    var _thisGrid = gridUtils_gridFromDivId(param_gridDivId);
    var columns = _thisGrid.columns;
    var totalWidth = _thisGrid.table[0].clientWidth;
    var totalWeight = 0;
    var counter = -1;
    $.each(columns, function (key, ele) {
        counter ++;
        if(param_columnIndexArray.indexOf(counter) > -1){
            ele["hap_ext_op_permission"]="hide";
        }else if(!ele["hap_ext_op_permission"]){
            totalWeight += ele["width"];
        }
    });

    $.each(columns, function (key, ele) {
        if(!ele["hap_ext_op_permission"]){
            ele["width"] = (totalWidth/totalWeight)*ele["width"];
        }
    });

    $.each(param_columnIndexArray, function (key, _index) {
        _thisGrid.hideColumn(_index);
    });
}

/**
 * flexible controller of nested components applied in grid cell
 * @author young
 * @param param_htmlTagName html tag name,such as:a、input、button
 * @param param_tagAttributeName tag attribute name,such as:name、class;you can define your own attribute as well,for example:OP_PMS_NAME
 * @param param_tagAttributeValue value of tag attribute
 * @param param_callbackFunction js callback function,for example: function(ele){ele.attr("disabled","disabled");}，ele is a js object which will be injected automatically represented for this nested obj
 */
function gridUtils_controlNestedComponents(param_htmlTagName, param_tagAttributeName, param_tagAttributeValue, param_callbackFunction) {
    var temp = param_htmlTagName+"["+param_tagAttributeName+"='"+param_tagAttributeValue+"']";
    $.each($(temp), function (key, value) {
        param_callbackFunction($(value));
    });
}

/**
 * disable buttons in single column(you can remove all buttons which is of same value of OP_PMS_NAME attribute actually)
 * @author young
 * @param param_tagAttributeValue value of "OP_PMS_NAME" attribute
 */
function gridUtils_disableButtonInColumn(param_tagAttributeValue) {
    gridUtils_controlNestedComponents("a","OP_PMS_NAME",param_tagAttributeValue,
        function (ele) {
            ele.attr("disabled","disabled");
            ele.unbind();
            ele.prop("onclick",null);
            ele.click(function (event) {
                event.preventDefault();
            });
        }
    );
}

/**
 * remove buttons in single column(you can remove all buttons which is of same value of OP_PMS_NAME attribute actually).
 * <br>
 *     notice:caution you that forbidden using in single column which contains single type button.
 * @param param_tagAttributeValue
 */
function gridUtils_deleteButtonInColumn(param_tagAttributeValue) {
    // this.parentNode.removeChild(this);
    gridUtils_controlNestedComponents("a","OP_PMS_NAME",param_tagAttributeValue,
        function (ele) {
            // ele.parentNode.removeChild(ele);
            ele.remove();
        }
    );
}

// =====================GRID OPERATION================================================end



// =====================BASIC OPERATION================================================start
function core_apply_action(param_tagAttr, param_tagAttrVal, param_val, param_callbackFunction) {
    var temp = "["+param_tagAttr+"='"+param_tagAttrVal+"']";
    $.each($(temp), function (key, value) {
        param_callbackFunction($(value),param_val);
    });
}

function core_extractElementId(param_ele) {
    var id_str = param_ele.attr("ID");
    return id_str+"";
}

/**
 * 判断是否已经应用了规则，对于已经应用过规则的元素不再重复应用.
 * @param param_ele
 * @param param_mask_suffix 应用过规则的标志位后缀，与"op_pms_mask_"组成标识符，值为"APPLIED"
 * @returns {boolean}
 */
function core_hasAppliedRule(param_ele,param_mask_suffix) {
    var mask_str = param_ele.attr("op_pms_mask_"+param_mask_suffix);
    if(mask_str && mask_str.toUpperCase()=="APPLIED"){
        return true;
    }
    return false;
}

function core_setAppledMask(param_ele,param_mask_suffix) {
    param_ele.attr("op_pms_mask_"+param_mask_suffix,"APPLIED");
}
// =====================BASIC OPERATION================================================end
// =====================GRID COMMON OPERATION================================================start
function COMMON_GRID_displayFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal,param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"COMMON_GRID_displayFunction")){
            if("N"===val){
                ele.attr("hidden","hidden");
                ele.hide();
            }
            else if("Y"===val){
                ele.show();
            }
            core_setAppledMask(ele,"COMMON_GRID_displayFunction");
        }
    });
}

function COMMON_GRID_requireFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal,param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"COMMON_GRID_requireFunction")){
            if("Y"===val){
                ele.attr("required","required");
            }
            else if("N"===val){
                ele.removeAttr("required");
            }
            core_setAppledMask(ele,"COMMON_GRID_requireFunction");
        }
    });
}

function COMMON_GRID_readonlyFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal,param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"COMMON_GRID_readonlyFunction")){
            if("Y"===val){
                ele.attr("readonly","readonly");
            }
            else if("N"===val){
                ele.removeAttr("readonly");
            }
            core_setAppledMask(ele,"COMMON_GRID_readonlyFunction");
        }
    });
}

function COMMON_GRID_disableFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal,param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"COMMON_GRID_disableFunction")){
            if("Y"===val){
                ele.attr("disabled","disabled");
                ele.unbind();
                ele.prop("onclick",null);
                ele.click(function (event) {
                    event.preventDefault();
                });
            }
            else if("N"===val){
                //suggest do nothing
                // ele.removeAttr("disabled");
            }
            core_setAppledMask(ele,"COMMON_GRID_disableFunction");
        }
    });
}
// =====================GRID COMMON OPERATION================================================end
// =====================GRID grid OPERATION================================================start
function GRID_grid_displayFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal,param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"GRID_grid_displayFunction")){
            if("Y"===val){
            }
            else if("N"===val){
                ele.attr("hidden","hidden");
                ele.hide();
            }
            core_setAppledMask(ele,"GRID_grid_displayFunction");
        }
    });
}
function GRID_grid_requireFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal,param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"GRID_grid_requireFunction")){
            //just do nothing for whole grid
            if("Y"===val){
            }
            else if("N"===val){
            }
            core_setAppledMask(ele,"GRID_grid_requireFunction");
        }
    });
}
function GRID_grid_readonlyFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal,param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"GRID_grid_readonlyFunction")){
            if("Y"===val){
                gridUtils_forbidEditWholeGrid(core_extractElementId(ele));
            }
            else if("N"===val){
                gridUtils_allowEditWholeGrid(core_extractElementId(ele));
            }
            core_setAppledMask(ele,"GRID_grid_readonlyFunction");
        }
    });
}
function GRID_grid_disableFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal,param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"GRID_grid_disableFunction")){
            if("Y"===val){
                ele.attr("disabled","disabled");
            }
            else if("N"===val){
                //suggest do nothing
                // ele.removeAttr("disabled");
            }
            core_setAppledMask(ele,"GRID_grid_disableFunction");
        }
    });
}
// =====================GRID grid OPERATION================================================end
// =====================GRID grid column OPERATION================================================start
//TODO 重新设计参数模板
/**
 * just handle hide columns.it means hidden column when call this function.
 * @param param_tagAttr
 * @param param_tagAttrVal
 * @param param_val index array of columns(is going to be hidden),start from 0;
 * @constructor
 */
function GRID_grid_column_displayFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal,param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"GRID_grid_column_displayFunction")){
            var idStr = core_extractElementId(ele);
            gridUtils_hideColumns(idStr,param_val);

            core_setAppledMask(ele,"GRID_grid_column_displayFunction");
        }
    });
}
/**
 * <b>目前无合适解决方案</b>
 * @param param_tagAttr
 * @param param_tagAttrVal
 * @param param_val
 * @constructor
 */
function GRID_grid_column_requireFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal,param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"GRID_grid_column_requireFunction")){
            if("Y"===val){
            }
            else if("N"===val){
            }

            core_setAppledMask(ele,"GRID_grid_column_requireFunction");
        }
    });
}
/**
 * it means disable edit for columns when this function is called.
 * @param param_tagAttr
 * @param param_tagAttrVal
 * @param param_val
 * @constructor
 */
function GRID_grid_column_readonlyFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal,param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"GRID_grid_column_readonlyFunction")){
            gridUtils_forbidEditColumns(core_extractElementId(ele),param_val);
            // console.log(core_extractElementId(ele));
            // console.log(param_val);

            core_setAppledMask(ele,"GRID_grid_column_readonlyFunction");
        }


    });
}
/**
 * <b>目前无合适解决方案</b>
 * @param param_tagAttr
 * @param param_tagAttrVal
 * @param param_val
 * @constructor
 */
function GRID_grid_column_disableFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal,param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"GRID_grid_column_disableFunction")){
            if("Y"===val){
            }
            else if("N"===val){
            }

            core_setAppledMask(ele,"GRID_grid_column_disableFunction");
        }
    });
}
// =====================GRID grid column OPERATION================================================end
// =====================GRID button OPERATION================================================start
function GRID_button_displayFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal,param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"GRID_button_displayFunction")){
            if("Y"===val){
                ele.removeAttr("hidden","hidden");
                ele.show();
            }
            else if("N"===val){
                ele.attr("hidden","hidden");
                ele.hide();
            }

            core_setAppledMask(ele,"GRID_button_displayFunction");
        }
    });
}
function GRID_button_requireFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal,param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"GRID_button_requireFunction")){
            //just do nothing for button
            if("Y"===val){
            }
            else if("N"===val){
            }

            core_setAppledMask(ele,"GRID_button_requireFunction");
        }
    });
}
function GRID_button_readonlyFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal,param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"GRID_button_readonlyFunction")){
            //just do nothing for button
            if("Y"===val){
            }
            else if("N"===val){
            }

            core_setAppledMask(ele,"GRID_button_readonlyFunction");
        }
    });
}
function GRID_button_disableFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal,param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"GRID_button_disableFunction")){
            if("Y"===val){
                ele.attr("disabled","disabled");
                ele.unbind();
                ele.prop("onclick",null);
                ele.click(function (event) {
                    event.preventDefault();
                });
                console.log("已经设置为Disable");
            }
            else if("N"===val){
                //suggest do nothing, for we have removed all events,...
            }

            core_setAppledMask(ele,"GRID_button_disableFunction");
        }
    });
}
// =====================GRID button OPERATION================================================end
// =====================FORM COMMON OPERATION================================================start
function COMMON_FORM_displayFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal,param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"COMMON_FORM_displayFunction")){
            if("N"===val){
                ele.attr("hidden","hidden");
                ele.hide();
            }
            else if("Y"===val){
                ele.show();
            }
            core_setAppledMask(ele,"COMMON_FORM_displayFunction");
        }
    });
}

function COMMON_FORM_requireFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal,param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"COMMON_FORM_requireFunction")){
            if("Y"===val){
                ele.attr("required","required");
            }
            else if("N"===val){
                ele.removeAttr("required");
            }
            core_setAppledMask(ele,"COMMON_FORM_requireFunction");
        }
    });
}

function COMMON_FORM_readonlyFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal,param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"COMMON_FORM_readonlyFunction")){
            if("Y"===val){
                ele.attr("readonly","readonly");
                ele.css("background", "#DDDDDD");
            }
            else if("N"===val){
                ele.removeAttr("readonly");
            }
            core_setAppledMask(ele,"COMMON_FORM_readonlyFunction");
        }
    });
}

function COMMON_FORM_disableFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal,param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"COMMON_FORM_disableFunction")){
            if("Y"===val){
                ele.attr("disabled","disabled");
                ele.css("background", "#DDDDDD");
                ele.unbind();
                ele.prop("onclick",null);
                ele.click(function (event) {
                    event.preventDefault();
                });
            }
            else if("N"===val){
                //suggest do nothing
                // ele.removeAttr("disabled");
            }
            core_setAppledMask(ele,"COMMON_FORM_disableFunction");
        }
    });
}
// =====================FORM COMMON OPERATION================================================end
// =====================FORM button OPERATION================================================start
function FORM_button_displayFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal,param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"FORM_button_displayFunction")){
            if("Y"===val){
                ele.removeAttr("hidden","hidden");
                ele.show();
            }
            else if("N"===val){
                ele.attr("hidden","hidden");
                ele.hide();
            }

            core_setAppledMask(ele,"FORM_button_displayFunction");
        }
    });
}
function FORM_button_requireFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal,param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"FORM_button_requireFunction")){
            //just do nothing for button
            if("Y"===val){
            }
            else if("N"===val){
            }

            core_setAppledMask(ele,"FORM_button_requireFunction");
        }
    });
}
function FORM_button_readonlyFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal,param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"FORM_button_readonlyFunction")){
            //just do nothing for button
            if("Y"===val){
            }
            else if("N"===val){
            }

            core_setAppledMask(ele,"FORM_button_readonlyFunction");
        }
    });
}
function FORM_button_disableFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal,param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"FORM_button_disableFunction")){
            if("Y"===val){
                ele.attr("disabled","disabled");
                ele.unbind();
                ele.prop("onclick",null);
                ele.click(function (event) {
                    event.preventDefault();
                });
            }
            else if("N"===val){
                //do nothing
            }

            core_setAppledMask(ele,"FORM_button_disableFunction");
        }
    });
}

// =====================FORM button OPERATION================================================end

// =====================FORM input OPERATION=========================================start
function FORM_input_displayFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal + "Div",param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"FORM_input_displayFunction")){
            if("N"===val){
                ele.attr("hidden","hidden");
                ele.hide();
            }
            else if("Y"===val){
                ele.show();
            }
            core_setAppledMask(ele,"FORM_input_displayFunction");
        }
    });
}
// =====================FORM input OPERATION=========================================end

// =====================FORM kendoComboBox OPERATION=========================================start
function FORM_kendoComboBox_displayFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal + "Div",param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"FORM_kendoComboBox_displayFunction")){
            if("N"===val){
                ele.attr("hidden","hidden");
                ele.hide();
            }
            else if("Y"===val){
                ele.show();
            }
            core_setAppledMask(ele,"FORM_kendoComboBox_displayFunction");
        }
    });
}

function FORM_kendoComboBox_disableFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal,param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"FORM_kendoComboBox_disableFunction")){
            if("Y"===val){
                ele.data("kendoComboBox").enable(false);
            }
            else if("N"===val){
                ele.data("kendoComboBox").enable(true);
            }
            core_setAppledMask(ele,"FORM_kendoComboBox_disableFunction");
        }
    });
}
// =====================FORM kendoComboBox OPERATION=========================================end

// =====================FORM kendoCheckbox OPERATION=========================================start
function FORM_kendoCheckbox_displayFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal + "Div",param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"FORM_kendoCheckbox_displayFunction")){
            if("N"===val){
                ele.attr("hidden","hidden");
                ele.hide();
            }
            else if("Y"===val){
                ele.show();
            }
            core_setAppledMask(ele,"FORM_kendoCheckbox_displayFunction");
        }
    });
}

function FORM_kendoCheckbox_disableFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal,param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"FORM_kendoCheckbox_disableFunction")){
            if("Y"===val){
                ele.data("kendoCheckbox").enable(false);
            }
            else if("N"===val){
                //suggest do nothing
                // ele.removeAttr("disabled");
            }
            core_setAppledMask(ele,"FORM_kendoCheckbox_disableFunction");
        }
    });

}
// =====================FORM kendoCheckbox OPERATION=========================================end

// =====================FORM kendoLov OPERATION=========================================start
function FORM_kendoLov_displayFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal + "Div",param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"FORM_kendoLov_displayFunction")){
            if("N"===val){
                ele.attr("hidden","hidden");
                ele.hide();
            }
            else if("Y"===val){
                ele.show();
            }
            core_setAppledMask(ele,"FORM_kendoLov_displayFunction");
        }
    });
}

function FORM_kendoLov_disableFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal,param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"FORM_kendoLov_disableFunction")){
            if("Y"===val){
                ele.data("kendoLov").enable(false);
            }
            else if("N"===val){
                ele.data("kendoLov").enable(true);
            }
            core_setAppledMask(ele,"FORM_kendoLov_disableFunction");
        }
    });

}
// =====================FORM kendoLov OPERATION=========================================end

// =====================FORM kendoDateTimePicker OPERATION=========================================start
function FORM_kendoDateTimePicker_displayFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal + "Div",param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"FORM_kendoDateTimePicker_displayFunction")){
            if("N"===val){
                ele.attr("hidden","hidden");
                ele.hide();
            }
            else if("Y"===val){
                ele.show();
            }
            core_setAppledMask(ele,"FORM_kendoDateTimePicker_displayFunction");
        }
    });
}

function FORM_kendoDateTimePicker_disableFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal,param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"FORM_kendoDateTimePicker_disableFunction")){
            if("Y"===val){
                ele.data("kendoDateTimePicker").enable(false);
            }
            else if("N"===val){
                ele.data("kendoDateTimePicker").enable(true);
            }
            core_setAppledMask(ele,"FORM_kendoDateTimePicker_disableFunction");
        }
    });

}
// =====================FORM kendoDateTimePicker OPERATION=========================================end


// =====================FORM kendoNumericTextBox OPERATION=========================================start
function FORM_kendoNumericTextBox_displayFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal + "Div",param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"FORM_kendoNumericTextBox_displayFunction")){
            if("N"===val){
                ele.attr("hidden","hidden");
                ele.hide();
            }
            else if("Y"===val){
                ele.show();
            }
            core_setAppledMask(ele,"FORM_kendoNumericTextBox_displayFunction");
        }
    });
}

function FORM_kendoNumericTextBox_disableFunction(param_tagAttr, param_tagAttrVal, param_val) {
    core_apply_action(param_tagAttr,param_tagAttrVal,param_val,function (ele, val) {
        if(!core_hasAppliedRule(ele,"FORM_kendoNumericTextBox_disableFunction")){
            if("Y"===val){
                ele.data("kendoNumericTextBox").enable(false);
            }
            else if("N"===val){
                ele.data("kendoNumericTextBox").enable(true);
            }
            core_setAppledMask(ele,"FORM_kendoNumericTextBox_disableFunction");
        }
    });

}
// =====================FORM kendoNumericTextBox OPERATION=========================================end

