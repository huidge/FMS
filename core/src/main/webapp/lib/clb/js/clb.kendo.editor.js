document.write("<script language=javascript src='../lib/kendoui/js/kendo.editor.js'></script>");
/*document.write("<div id=\"editoreresult\" style=\"display: none;\"></div>");*/

document.write("<div id=\"editorWin\" style=\"display: none;\"><form><input type=\"file\" id=\"editorfiles\" name=\"files\"></input></form></div>");

$.extend({
	'clbKendoEditor': function(elementid){
		$("#"+elementid).kendoEditor({
			 tools: [
					"fontName",
					"fontSize",
					"formatting",
					"bold",
					"italic",
					"underline",
					"strikethrough",
					"justifyLeft",
					"justifyCenter",
					"justifyRight",
					"justifyFull",
					"insertUnorderedList",
					"insertOrderedList",
					"indent",
					"outdent",
					"subscript",
					"superscript",
					"foreColor",
					"backColor",
	                {
	                    name: "image",
	                    tooltip: "Insert image",
	                    template: "<button type='button' class='k-button' onclick='editorupload("+elementid+");'><span class='glyphicon glyphicon-picture' aria-hidden='true'/></button>"
	                }
	            ],
	            resizable: {
	                max: 500
	              }
	     });
	},
	'clbKendoUpload': function(ossWebPath,modularName,updateUrl){
		$("#editorfiles").kendoUpload({
			async        : {
				saveUrl: updateUrl
			},
			showFileList : false,
			upload       : function(e){
				e.data = {
						modularName:modularName,
						allowType:'jpg;png;',
						maxSize:20480
				}
			},
			success      : function(e){
				if(e.response.success){
			        var filePath=e.response.file.filePath;
			        filePath=ossWebPath+filePath;
			        var editor = $("#"+editorelementid).data("kendoEditor");
					editor.exec("insertHTML", { value: "<img src='"+filePath+"' >"});
					fileWin.close();
				}else{
					kendo.ui.showErrorDialog({
						title    : '错误',
						message  : e.response.message
					})
				}
			}
		});
	}
});

var editorelementid;
function editorupload(elementid){
	editorelementid=elementid.id;
	window.fileWin = $("#editorWin").kendoWindow({
		title: '上传',
		width: 400,
		height: 250,
		modal: true
	}).data("kendoWindow");
	fileWin.center().open();
}





