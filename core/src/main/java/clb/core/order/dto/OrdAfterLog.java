package clb.core.order.dto;

/**Auto Generated By Hap Code Generator**/
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.hand.hap.system.dto.BaseDTO;
import java.util.Date;
import javax.validation.constraints.NotNull;

@ExtensionAttribute(disable = true)
@Table(name = "fms_ord_after_log")
public class OrdAfterLog extends BaseDTO {
	@Id
	@GeneratedValue
	private Long logId;

	@NotNull
	private Long generalId;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date statusDate;

	private String status;
	
	@Transient
	private String statusMeaning;//状态的含义 前端使用

	private String idType;

	private String comment;
	@Transient
	private String expressCompany;//快递公司
	@Transient
	private String expressNum;//快递编号

	private Long programId;

	private Long requestId;

	public void setLogId(Long logId) {
		this.logId = logId;
	}

	public Long getLogId() {
		return logId;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setProgramId(Long programId) {
		this.programId = programId;
	}

	public Long getProgramId() {
		return programId;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	public Long getRequestId() {
		return requestId;
	}

	public Long getGeneralId() {
		return generalId;
	}

	public void setGeneralId(Long generalId) {
		this.generalId = generalId;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getExpressCompany() {
		return expressCompany;
	}

	public void setExpressCompany(String expressCompany) {
		this.expressCompany = expressCompany;
	}

	public String getExpressNum() {
		return expressNum;
	}

	public void setExpressNum(String expressNum) {
		this.expressNum = expressNum;
	}

	public String getStatusMeaning() {
		return statusMeaning;
	}

	public void setStatusMeaning(String statusMeaning) {
		this.statusMeaning = statusMeaning;
	}
}
