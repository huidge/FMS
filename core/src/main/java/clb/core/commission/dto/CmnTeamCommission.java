package clb.core.commission.dto;

/**Auto Generated By Hap Code Generator**/
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import javax.persistence.Table;
import com.hand.hap.system.dto.BaseDTO;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
@ExtensionAttribute(disable=true)
@Table(name = "fms_cmn_team_commission")
public class CmnTeamCommission extends BaseDTO {
    @Id
    @GeneratedValue
    private Long lineId;

    @NotNull
    private Long channelId;

    @NotNull
    private Long subChannelId;

    private Double theFirstYear;

    private Double theSecondYear;

    private Double theThirdYear;

    private Double theFourthYear;

    private Double theFifthYear;

    private Double theSixthYear;

    private Double theSeventhYear;

    private Double theEightYear;

    private Double theNinthYear;

    private Double theTenthYear;

    @NotNull
    private Long version;

    private Date effectiveStartDate;

    private Date effectiveEndDate;

    private Long programId;

    private Long requestId;

    @Transient
    private Long parentChannelId;

    @Transient
    private String channelName;

    @Transient
    private String parentChannelName;

    @Transient
    private String subChannelName;

    public Long getParentChannelId() {
        return parentChannelId;
    }

    public void setParentChannelId(Long parentChannelId) {
        this.parentChannelId = parentChannelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getParentChannelName() {
        return parentChannelName;
    }

    public void setParentChannelName(String parentChannelName) {
        this.parentChannelName = parentChannelName;
    }

    public String getSubChannelName() {
        return subChannelName;
    }

    public void setSubChannelName(String subChannelName) {
        this.subChannelName = subChannelName;
    }

    public void setLineId(Long lineId){
        this.lineId = lineId;
    }

    public Long getLineId(){
        return lineId;
    }

    public void setChannelId(Long channelId){
        this.channelId = channelId;
    }

    public Long getChannelId(){
        return channelId;
    }

    public void setSubChannelId(Long subChannelId){
        this.subChannelId = subChannelId;
    }

    public Long getSubChannelId(){
        return subChannelId;
    }

    public Double getTheFirstYear() {
        return theFirstYear;
    }

    public void setTheFirstYear(Double theFirstYear) {
        this.theFirstYear = theFirstYear;
    }

    public Double getTheSecondYear() {
        return theSecondYear;
    }

    public void setTheSecondYear(Double theSecondYear) {
        this.theSecondYear = theSecondYear;
    }

    public Double getTheThirdYear() {
        return theThirdYear;
    }

    public void setTheThirdYear(Double theThirdYear) {
        this.theThirdYear = theThirdYear;
    }

    public Double getTheFourthYear() {
        return theFourthYear;
    }

    public void setTheFourthYear(Double theFourthYear) {
        this.theFourthYear = theFourthYear;
    }

    public Double getTheFifthYear() {
        return theFifthYear;
    }

    public void setTheFifthYear(Double theFifthYear) {
        this.theFifthYear = theFifthYear;
    }

    public Double getTheSixthYear() {
        return theSixthYear;
    }

    public void setTheSixthYear(Double theSixthYear) {
        this.theSixthYear = theSixthYear;
    }

    public Double getTheSeventhYear() {
        return theSeventhYear;
    }

    public void setTheSeventhYear(Double theSeventhYear) {
        this.theSeventhYear = theSeventhYear;
    }

    public Double getTheEightYear() {
        return theEightYear;
    }

    public void setTheEightYear(Double theEightYear) {
        this.theEightYear = theEightYear;
    }

    public Double getTheNinthYear() {
        return theNinthYear;
    }

    public void setTheNinthYear(Double theNinthYear) {
        this.theNinthYear = theNinthYear;
    }

    public Double getTheTenthYear() {
        return theTenthYear;
    }

    public void setTheTenthYear(Double theTenthYear) {
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

    public void setProgramId(Long programId){
        this.programId = programId;
    }

    public Long getProgramId(){
        return programId;
    }

    public void setRequestId(Long requestId){
        this.requestId = requestId;
    }

    public Long getRequestId(){
        return requestId;
    }

}
