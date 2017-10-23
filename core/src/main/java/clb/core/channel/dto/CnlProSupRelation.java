package clb.core.channel.dto;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import com.hand.hap.system.dto.BaseDTO;


@Table(name = "fms_cnl_pro_sup_relation")
public class CnlProSupRelation extends BaseDTO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6395070032355109277L;

	/**
	 * 主键id
	 */
	@Id
    @Column
    @GeneratedValue
    private Long id;
	
	/**
	 * 供应商名称
	 */
	@Transient
	private String name;
	
	/**
	 * 供应商编号
	 */
	@Transient
	private String supplierCode;
	
	/**
	 * 产品编号
	 */
	@Transient
	private String itemCode;
	
	/**
	 * 产品名称
	 */
	@Transient
	private String itemName;
	
	/**
	 * 供款期
	 */
	@Column
	private String contributionPeriod;
	
	/**
	 * 渠道分类
	 */
	@Column
	private String channelClassCode;
	
	/**
	 * 渠道id
	 */
	@Column
	private Long channelId;
	
	/**
	 * 渠道名称
	 */
	@Transient
	private String channelName;
	
	/**
	 * 渠道编号
	 */
	@Transient
	private String channelCode;
	
	
	@Column
	@NotNull
	private Long supplierId;
	
	@Column
	private Long productId;
	
	@NotBlank
	private String bigClass;
	
	private String midClass;
	
	private String minClass;
	
	private Long prdSupplierId;
	@Transient
	private String prdSupplierName;

	public Long getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSupplierCode() {
		return supplierCode;
	}

	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getContributionPeriod() {
		return contributionPeriod;
	}

	public void setContributionPeriod(String contributionPeriod) {
		this.contributionPeriod = contributionPeriod;
	}

	public String getChannelClassCode() {
		return channelClassCode;
	}

	public void setChannelClassCode(String channelClassCode) {
		this.channelClassCode = channelClassCode;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	
	

	public String getBigClass() {
		return bigClass;
	}

	public void setBigClass(String bigClass) {
		this.bigClass = bigClass;
	}

	public String getMidClass() {
		return midClass;
	}

	public void setMidClass(String midClass) {
		this.midClass = midClass;
	}

	public String getMinClass() {
		return minClass;
	}

	public void setMinClass(String minClass) {
		this.minClass = minClass;
	}

	public Long getPrdSupplierId() {
		return prdSupplierId;
	}

	public void setPrdSupplierId(Long prdSupplierId) {
		this.prdSupplierId = prdSupplierId;
	}

	public String getPrdSupplierName() {
		return prdSupplierName;
	}

	public void setPrdSupplierName(String prdSupplierName) {
		this.prdSupplierName = prdSupplierName;
	}

	public Long getChannelId() {
		return channelId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

}
