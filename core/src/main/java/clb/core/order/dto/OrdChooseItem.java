package clb.core.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

public class OrdChooseItem {

    private Long channelId;

    private Long channelCommissionLineId;

    private Long productSupplierId;

    private Long supplierId;

    private String supplierName;

    private String bigClass;

    private Long itemId;

    private String itemCode;

    private String itemName;

    private String contributionPeriod;

    private Long sublineId;

    private String currency;

    private String currencyCode;

    private String theFirstYear;

    private String stockCode;

    private String annualInterest;

    private String marketValue;

    private String existFlag;

    public Long getProductSupplierId() {
        return productSupplierId;
    }

    public void setProductSupplierId(Long productSupplierId) {
        this.productSupplierId = productSupplierId;
    }

    public String getExistFlag() {
        return existFlag;
    }

    public void setExistFlag(String existFlag) {
        this.existFlag = existFlag;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getChannelCommissionLineId() {
        return channelCommissionLineId;
    }

    public void setChannelCommissionLineId(Long channelCommissionLineId) {
        this.channelCommissionLineId = channelCommissionLineId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getBigClass() {
        return bigClass;
    }

    public void setBigClass(String bigClass) {
        this.bigClass = bigClass;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
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

    public Long getSublineId() {
        return sublineId;
    }

    public void setSublineId(Long sublineId) {
        this.sublineId = sublineId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getTheFirstYear() {
        return theFirstYear;
    }

    public void setTheFirstYear(String theFirstYear) {
        this.theFirstYear = theFirstYear;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getAnnualInterest() {
        return annualInterest;
    }

    public void setAnnualInterest(String annualInterest) {
        this.annualInterest = annualInterest;
    }

    public String getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(String marketValue) {
        this.marketValue = marketValue;
    }

}
