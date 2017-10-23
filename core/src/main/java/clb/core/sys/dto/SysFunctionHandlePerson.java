package clb.core.sys.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.NotEmpty;

import com.hand.hap.mybatis.annotation.Condition;
import com.hand.hap.system.dto.BaseDTO;
import com.sun.istack.NotNull;
/*****
 * @author tiansheng.ye
 * @Date 2017-05-25
 */
@Table(name="FMS_SYS_FUNCTION_HANDLE_PERSON")
public class SysFunctionHandlePerson extends BaseDTO{

	private static final long serialVersionUID = 8297522854006852181L;

	@Id
	@GeneratedValue
	private Long handleId;
	
	@Column
	@NotEmpty
	private String functionCode;
	@Column
	@NotEmpty
	private String columnCode;
	@Column
	@NotEmpty
	private String columnValue;
	@NotNull
	@Column
	private Long sourceId;
	@NotNull
	@Column
	private Long userId;
	
	public Long getHandleId() {
		return handleId;
	}
	public void setHandleId(Long handleId) {
		this.handleId = handleId;
	}
	public Long getSourceId() {
		return sourceId;
	}
	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getFunctionCode() {
		return functionCode;
	}
	public void setFunctionCode(String functionCode) {
		this.functionCode = functionCode;
	}
	public String getColumnCode() {
		return columnCode;
	}
	public void setColumnCode(String columnCode) {
		this.columnCode = columnCode;
	}
	public String getColumnValue() {
		return columnValue;
	}
	public void setColumnValue(String columnValue) {
		this.columnValue = columnValue;
	}
	
	@Condition(operator=">=")
	private Date lastUpdateDate;

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	//任务数量
	@Transient
	private Integer orderTotal;
	public Integer getOrderTotal() {
		return orderTotal;
	}
	public void setOrderTotal(Integer orderTotal) {
		this.orderTotal = orderTotal;
	}
	@Transient
	private double taskTime;

	public double getTaskTime() {
		return taskTime;
	}
	public void setTaskTime(double taskTime) {
		this.taskTime = taskTime;
	}
	
}
