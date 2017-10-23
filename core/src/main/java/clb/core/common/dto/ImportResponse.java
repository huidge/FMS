package clb.core.common.dto;

import com.hand.hap.system.dto.BaseDTO;

/*****
 * @author tiansheng.ye
 * @Date 2017/05/17
 * @Desc 导入反馈
 */
public class ImportResponse extends BaseDTO{

	private static final long serialVersionUID = -4114631733744945966L;
	
	private Integer lineId;
	
	private String type;
	
	private String fieldName;
	
	private String message;
	
	private Integer sheetNum;
	public ImportResponse(){
		
	}
	public ImportResponse(Integer sheetNum,Integer lineId,String type,String fieldName,String message){
		this.sheetNum=sheetNum;
		this.lineId=lineId;
		this.type=type;
		this.fieldName=fieldName;
		this.message=message;
	}

	public Integer getLineId() {
		return lineId;
	}

	public void setLineId(Integer lineId) {
		this.lineId = lineId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Integer getSheetNum() {
		return sheetNum;
	}

	public void setSheetNum(Integer sheetNum) {
		this.sheetNum = sheetNum;
	}

	
	
}
