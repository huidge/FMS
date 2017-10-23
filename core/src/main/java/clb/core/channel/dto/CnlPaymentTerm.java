package clb.core.channel.dto;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.NotEmpty;

import com.hand.hap.system.dto.BaseDTO;
/**
 * Created by wanjun.feng on 2017/4/19.
 */
@Table(name = "fms_cnl_payment_term")
public class CnlPaymentTerm extends BaseDTO {
    private static final long serialVersionUID = -5133005895825970498L;

    @Id
	@Column
	@GeneratedValue
	private Long paymentTermId;
    
    @Column
    private String paymentTermCode;
    
    @Column
    @NotEmpty
    private String channelClassCode;

	@Column
	private Long channelId;
	
	@Column
	@NotEmpty
	private String className;

	@Column
	@NotEmpty
	private String basicDate;

	@Column
	private String calmPeriodNumber;

	@Column
	private String calmPeriodType;

	@Column
	@NotEmpty
	private String balanceNumber;

	@Column
	@NotEmpty
	private String balanceDate1;

	@Column
    private String balanceDate2;

	@Column
	@NotEmpty
	private String enabledFlag;
    
    @Transient
    private String channelName;
    
    public Long getPaymentTermId() {
        return paymentTermId;
    }

    public void setPaymentTermId(Long paymentTermId) {
        this.paymentTermId = paymentTermId;
    }

    public String getPaymentTermCode() {
        return paymentTermCode;
    }

    public void setPaymentTermCode(String paymentTermCode) {
        this.paymentTermCode = paymentTermCode;
    }

    public String getChannelClassCode() {
        return channelClassCode;
    }

    public void setChannelClassCode(String channelClassCode) {
        this.channelClassCode = channelClassCode;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getBasicDate() {
        return basicDate;
    }

    public void setBasicDate(String basicDate) {
        this.basicDate = basicDate;
    }

    public String getCalmPeriodNumber() {
        return calmPeriodNumber;
    }

    public void setCalmPeriodNumber(String calmPeriodNumber) {
        this.calmPeriodNumber = calmPeriodNumber;
    }

    public String getCalmPeriodType() {
        return calmPeriodType;
    }

    public void setCalmPeriodType(String calmPeriodType) {
        this.calmPeriodType = calmPeriodType;
    }

    public String getBalanceNumber() {
        return balanceNumber;
    }

    public void setBalanceNumber(String balanceNumber) {
        this.balanceNumber = balanceNumber;
    }

    public String getBalanceDate1() {
        return balanceDate1;
    }

    public void setBalanceDate1(String balanceDate1) {
        this.balanceDate1 = balanceDate1;
    }

    public String getBalanceDate2() {
        return balanceDate2;
    }

    public void setBalanceDate2(String balanceDate2) {
        this.balanceDate2 = balanceDate2;
    }

    public String getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}