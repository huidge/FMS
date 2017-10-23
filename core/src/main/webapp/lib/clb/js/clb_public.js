/**
 * kendoUi kendoDropDownList级联组件
 * @author xiaoyong.luo@hand-china.com
 */


/**
 * 以文件的方式引入这个插件，在IE中会报错
 */
$.extend({
    /**
     * 国家、省、市 三级级动
     *
     * @param nation object类型，对应"国家"。包括 元素Id(带#)、初始数据源、label 三个属性
     * @param province 	object类型，对应"省份"。包括 元素Id(带#)、初始数据源、label 三个属性
     * @param city 	object类型，对应"国家"城市。包括 元素Id(带#)、初始数据源、label 三个属性
     */
    'clbCascade': function(nation, province, city){
        //利用最顶层对组件实行初始化
        $(nation.elementId).kendoDropDownList({
            optionLabel: nation.label?nation.label:'--国家--',  //当label属性没有传值的时候的时候，默认显示国家
            valuePrimitive: true,
            dataTextField: "meaning",
            dataValueField: "value",
            dataSource: nation.source,
            //当最顶层发生 change 事件之后，对子层进行筛选
            change: function(){
                var nationValue = $(nation.elementId).val();    //获取最顶层的值
                var provinces = [];
                //根据顶层筛选子层的值
                for (var i = 0; i < province.source.length; i++) {
                    if (nationValue && nationValue == province.source[i].parentValue) {
                        provinces.push(province.source[i]);
                    }
                }
                //更新子层数据源（只是更新dataSource这个值是不起作用的）
                $(province.elementId).kendoDropDownList({
                    optionLabel: province.label?province.label:'--省份--',
                    valuePrimitive: true,
                    dataTextField: "meaning",
                    dataValueField: "value",
                    dataSource: provinces,
                    //子层再更新自己下面的子层
                    change: function(){
                        var provinceValue = $(province.elementId).val();
                        var citys = [];
                        for(var i = 0; i < city.source.length; i++){
                            if(provinceValue && provinceValue == city.source[i].parentValue ){
                                citys.push(city.source[i]);
                            }
                        }

                        $(city.elementId).kendoDropDownList({
                            optionLabel: city.label?city.label:'--城市--',
                            valuePrimitive: true,
                            dataTextField: "meaning",
                            dataValueField: "value",
                            dataSource: citys,
                        });
                    },
                });


                //初始化城市
                var provinceValue = $(province.elementId).val();
                var citys = [];
                for(var k = 0; k < city.source.length; k++){
                    if(provinceValue && provinceValue == city.source[k].parentValue ){
                        citys.push(city.source[k]);
                    }
                }
                $(city.elementId).kendoDropDownList({
                    optionLabel: city.label?city.label:'--城市--',
                    valuePrimitive: true,
                    dataTextField: "meaning",
                    dataValueField: "value",
                    dataSource: citys,
                });
            },
            //当最顶 databBound 的时候，对子层进行筛选
            dataBound: function(){
                var nationValue = $(nation.elementId).val();
                var provinces = [];
                for (var i = 0; i < province.source.length; i++) {
                    if (nationValue && nationValue == province.source[i].parentValue) {
                        provinces.push(province.source[i]);
                    }
                }
                $(province.elementId).kendoDropDownList({
                    optionLabel: province.label?province.label:'--省份--',
                    valuePrimitive: true,
                    dataTextField: "meaning",
                    dataValueField: "value",
                    dataSource: provinces,
                    change: function(){
                        var provinceValue = $(province.elementId).val();
                        var citys = [];
                        for(var i = 0; i < city.source.length; i++){
                            if(provinceValue && provinceValue == city.source[i].parentValue ){
                                citys.push(city.source[i]);
                            }
                        }

                        $(city.elementId).kendoDropDownList({
                            optionLabel: city.label?city.label:'--城市--',
                            valuePrimitive: true,
                            dataTextField: "meaning",
                            dataValueField: "value",
                            dataSource: citys,
                        });
                    },
                    dataBound: function(){
                        var provinceValue = $(province.elementId).val();
                        var citys = [];
                        for(var i = 0; i < city.source.length; i++){
                            if(provinceValue && provinceValue == city.source[i].parentValue ){
                                citys.push(city.source[i]);
                            }
                        }

                        $(city.elementId).kendoDropDownList({
                            optionLabel: city.label?city.label:'--城市--',
                            valuePrimitive: true,
                            dataTextField: "meaning",
                            dataValueField: "value",
                            dataSource: citys,
                        });
                    },
                });
            },
        });
    },


    /**
     *  将数字转换千分位格式
     *
     *  @param elementId 元素Id(带#)
     */
    'clbFormatNum': function(elementId){
        var number = $(elementId).val();
        number = number.replace(/\,/g, "");
        var isNum = /^((0|[1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/;
        if (!isNum.test(number)) {
            $(elementId).val(0);
            return false;
        }

        number = number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
        $(elementId).val(number);
        return false;
    },


    /**
     * 校验
     * @param elementId
     * @param regx
     * @param error
     * @param global
     */
    'clbValidate': function(elementId,regx,error,global){
        $(elementId).bind("blur", function(){
            var number = $(elementId).val();
            if(number && !regx.test(number)){
                $(elementId).val(error);
                $(elementId).css("color","red");
                global.error = global.error + error;
            }
            return false;
        });

        $(elementId).bind("focus", function(){
            if( error == $(elementId).val() ){
                $(elementId).css('color', '');
                $(elementId).val('');
                global.error = global.error.replace(error, '');
            }
            return false;
        });
    },

});

