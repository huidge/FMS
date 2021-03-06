package clb.core.commission.dto;

/**Auto Generated By Hap Code Generator**/
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import javax.persistence.Table;
import com.hand.hap.system.dto.BaseDTO;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

@ExtensionAttribute(disable=true)
@Table(name = "fms_cmn_basic")
public class CmnBasic extends BaseDTO {
    @Id
    @GeneratedValue
    private Long basicId;

    @NotEmpty
    private Long supplierId;

    @NotEmpty
    private Long itemId;

    @NotEmpty
    private String contributionPeriod;

    @NotEmpty
    private String currency;

    @NotEmpty
    private String payMethod;

    @NotNull
    private Long policyholdersMinAge;

    @NotNull
    private Long policyholdersMaxAge;

    @NotEmpty
    private String priceType;

    @NotEmpty
    private BigDecimal theFirstYear;

    @NotEmpty
    private BigDecimal theSecondYear;

    @NotEmpty
    private BigDecimal theThirdYear;

    @NotEmpty
    private BigDecimal theFourthYear;

    @NotEmpty
    private BigDecimal theFifthYear;

    @NotEmpty
    private BigDecimal theSixthYear;

    @NotEmpty
    private BigDecimal theSeventhYear;

    @NotEmpty
    private BigDecimal theEightYear;

    @NotEmpty
    private BigDecimal theNinthYear;

    @NotEmpty
    private BigDecimal theTenthYear;

    @NotNull
    private Long version;

    private Date effectiveStartDate;

    private Date effectiveEndDate;

    @Transient
    private String productCompany;

    @Transient
    private String itemCode;

    @Transient
    private String itemName;

    @Transient
    private String policyHoldersAge;

    public String getPolicyHoldersAge() {
        return policyHoldersAge;
    }

    public void setPolicyHoldersAge(String policyHoldersAge) {
        this.policyHoldersAge = policyHoldersAge;
    }

    public String getProductCompany() {
        return productCompany;
    }

    public void setProductCompany(String productCompany) {
        this.productCompany = productCompany;
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

    public void setBasicId(Long basicId){
        this.basicId = basicId;
    }

    public Long getBasicId(){
        return basicId;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public void setContributionPeriod(String contributionPeriod){
        this.contributionPeriod = contributionPeriod;
    }

    public String getContributionPeriod(){
        return contributionPeriod;
    }

    public void setCurrency(String currency){
        this.currency = currency;
    }

    public String getCurrency(){
        return currency;
    }

    public void setPayMethod(String payMethod){
        this.payMethod = payMethod;
    }

    public String getPayMethod(){
        return payMethod;
    }

    public void setPolicyholdersMinAge(Long policyholdersMinAge){
        this.policyholdersMinAge = policyholdersMinAge;
    }

    public Long getPolicyholdersMinAge(){
        return policyholdersMinAge;
    }

    public void setPolicyholdersMaxAge(Long policyholdersMaxAge){
        this.policyholdersMaxAge = policyholdersMaxAge;
    }

    public Long getPolicyholdersMaxAge(){
        return policyholdersMaxAge;
    }

    public void setPriceType(String priceType){
        this.priceType = priceType;
    }

    public String getPriceType(){
        return priceType;
    }

    public BigDecimal getTheFirstYear() {
        return theFirstYear;
    }

    public void setTheFirstYear(BigDecimal theFirstYear) {
        this.theFirstYear = theFirstYear;
    }

    public BigDecimal getTheSecondYear() {
        return theSecondYear;
    }

    public void setTheSecondYear(BigDecimal theSecondYear) {
        this.theSecondYear = theSecondYear;
    }

    public BigDecimal getTheThirdYear() {
        return theThirdYear;
    }

    public void setTheThirdYear(BigDecimal theThirdYear) {
        this.theThirdYear = theThirdYear;
    }

    public BigDecimal getTheFourthYear() {
        return theFourthYear;
    }

    public void setTheFourthYear(BigDecimal theFourthYear) {
        this.theFourthYear = theFourthYear;
    }

    public BigDecimal getTheFifthYear() {
        return theFifthYear;
    }

    public void setTheFifthYear(BigDecimal theFifthYear) {
        this.theFifthYear = theFifthYear;
    }

    public BigDecimal getTheSixthYear() {
        return theSixthYear;
    }

    public void setTheSixthYear(BigDecimal theSixthYear) {
        this.theSixthYear = theSixthYear;
    }

    public BigDecimal getTheSeventhYear() {
        return theSeventhYear;
    }

    public void setTheSeventhYear(BigDecimal theSeventhYear) {
        this.theSeventhYear = theSeventhYear;
    }

    public BigDecimal getTheEightYear() {
        return theEightYear;
    }

    public void setTheEightYear(BigDecimal theEightYear) {
        this.theEightYear = theEightYear;
    }

    public BigDecimal getTheNinthYear() {
        return theNinthYear;
    }

    public void setTheNinthYear(BigDecimal theNinthYear) {
        this.theNinthYear = theNinthYear;
    }

    public BigDecimal getTheTenthYear() {
        return theTenthYear;
    }

    public void setTheTenthYear(BigDecimal theTenthYear) {
        this.theTenthYear = theTenthYear;
    }

    public void setVersion(Long version){
        this.version = version;
    }

    public Long getVersion(){
        return version;
    }

    public void setEffectiveStartDate(Date effectiveStartDate){
        this.effectiveStartDate = effectiveStartDate;
    }

    public Date getEffectiveStartDate(){
        return effectiveStartDate;
    }

    public void setEffectiveEndDate(Date effectiveEndDate){
        this.effectiveEndDate = effectiveEndDate;
    }

    public Date getEffectiveEndDate(){
        return effectiveEndDate;
    }

}
