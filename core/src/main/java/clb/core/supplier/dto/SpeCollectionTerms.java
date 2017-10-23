package clb.core.supplier.dto;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.hand.hap.system.dto.BaseDTO;
import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.transaction.annotation.Transactional;
/**
 * @name SpeSupplier
 * @description 付款条件DTO
 * @author bo.wu@hand-china.com 2017年4月24日20:45:59
 * @version 1.0 
 */

@Table(name = "fms_spe_collection_terms")
public class SpeCollectionTerms extends BaseDTO {
     @Id
     @GeneratedValue(generator="IDENTITY")
     @Column
     private Long termId;

     
     //条件编号
     @Column
     private String termCode;
     
     //付款方类型
     @Column
     @NotNull
   	 private String paymentCompanyType;

     //付款方名称
     @Column
     @NotNull
     private Long paymentCompanyId;

     //条件分类
     @NotEmpty
     @Column
     private String productDivision;

     //条件类型
     @NotEmpty
     @Column
     private String termType;
     
     //产品公司
     @Column
     private Long productCompanyId;

     @NotEmpty
     @Column
     private String baseDate;

     @NotNull
     @Column
     private Long settleDays;

     @NotEmpty
     @Column
     private String settleDate1;

     @Column
     private String settleDate2;

     @NotEmpty
     @Column
     private String statusCode;
     
     @Transient
     private String paymentCompanyName;
     
     @Transient
     private String productCompanyName;

	public Long getTermId() {
		return termId;
	}

	public void setTermId(Long termId) {
		this.termId = termId;
	}

	public String getTermCode() {
		return termCode;
	}

	public void setTermCode(String termCode) {
		this.termCode = termCode;
	}
	
	public String getPaymentCompanyType() {
		return paymentCompanyType;
	}

	public void setPaymentCompanyType(String paymentCompanyType) {
		this.paymentCompanyType = paymentCompanyType;
	}

	public Long getPaymentCompanyId() {
		return paymentCompanyId;
	}

	public void setPaymentCompanyId(Long paymentCompanyId) {
		this.paymentCompanyId = paymentCompanyId;
	}

	public String getProductDivision() {
		return productDivision;
	}

	public void setProductDivision(String productDivision) {
		this.productDivision = productDivision;
	}

	public String getTermType() {
		return termType;
	}

	public void setTermType(String termType) {
		this.termType = termType;
	}
	
	public Long getProductCompanyId() {
		return productCompanyId;
	}

	public void setProductCompanyId(Long productCompanyId) {
		this.productCompanyId = productCompanyId;
	}

	public String getBaseDate() {
		return baseDate;
	}

	public void setBaseDate(String baseDate) {
		this.baseDate = baseDate;
	}

	public Long getSettleDays() {
		return settleDays;
	}

	public void setSettleDays(Long settleDays) {
		this.settleDays = settleDays;
	}

	public String getSettleDate1() {
		return settleDate1;
	}

	public void setSettleDate1(String settleDate1) {
		this.settleDate1 = settleDate1;
	}

	public String getSettleDate2() {
		return settleDate2;
	}

	public void setSettleDate2(String settleDate2) {
		this.settleDate2 = settleDate2;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getPaymentCompanyName() {
		return paymentCompanyName;
	}

	public void setPaymentCompanyName(String paymentCompanyName) {
		this.paymentCompanyName = paymentCompanyName;
	}

	public String getProductCompanyName() {
		return productCompanyName;
	}

	public void setProductCompanyName(String productCompanyName) {
		this.productCompanyName = productCompanyName;
	}

    
     

     
}
