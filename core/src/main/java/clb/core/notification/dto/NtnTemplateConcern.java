package clb.core.notification.dto;

/**Auto Generated By Hap Code Generator**/
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import javax.persistence.Table;
import com.hand.hap.system.dto.BaseDTO;
import java.util.Date;
import javax.validation.constraints.NotNull;

@ExtensionAttribute(disable=true)
@Table(name = "fms_ntn_template_concern")
public class NtnTemplateConcern extends BaseDTO {
    @Id
    @GeneratedValue
    private Long lineId;

    private Long userId;

    @NotNull
    private String backgroundAppid;

    @NotNull
    private String wechatOpenid;

    private String wechatSecretKey;

    private Date wechatBindDate;

    private String wechatBindType;

    @NotNull
    private String wechatConcernType;

    private Long appAccountId;

    private String appSecretKey;

    private Date appBindDate;

    private Long aqumonAccountId;

    private String aqumonSecretKey;

    private Date aqumonBindDate;

    private Long programId;

    private Long requestId;


    public void setLineId(Long lineId){
        this.lineId = lineId;
    }

    public Long getLineId(){
        return lineId;
    }

    public void setUserId(Long userId){
        this.userId = userId;
    }

    public Long getUserId(){
        return userId;
    }

    public void setBackgroundAppid(String backgroundAppid){
        this.backgroundAppid = backgroundAppid;
    }

    public String getBackgroundAppid(){
        return backgroundAppid;
    }

    public void setWechatOpenid(String wechatOpenid){
        this.wechatOpenid = wechatOpenid;
    }

    public String getWechatOpenid(){
        return wechatOpenid;
    }

    public void setWechatSecretKey(String wechatSecretKey){
        this.wechatSecretKey = wechatSecretKey;
    }

    public String getWechatSecretKey(){
        return wechatSecretKey;
    }

    public void setWechatBindDate(Date wechatBindDate){
        this.wechatBindDate = wechatBindDate;
    }

    public Date getWechatBindDate(){
        return wechatBindDate;
    }

    public void setWechatBindType(String wechatBindType){
        this.wechatBindType = wechatBindType;
    }

    public String getWechatBindType(){
        return wechatBindType;
    }

    public void setWechatConcernType(String wechatConcernType){
        this.wechatConcernType = wechatConcernType;
    }

    public String getWechatConcernType(){
        return wechatConcernType;
    }

    public void setAppAccountId(Long appAccountId){
        this.appAccountId = appAccountId;
    }

    public Long getAppAccountId(){
        return appAccountId;
    }

    public void setAppSecretKey(String appSecretKey){
        this.appSecretKey = appSecretKey;
    }

    public String getAppSecretKey(){
        return appSecretKey;
    }

    public void setAppBindDate(Date appBindDate){
        this.appBindDate = appBindDate;
    }

    public Date getAppBindDate(){
        return appBindDate;
    }

    public void setAqumonAccountId(Long aqumonAccountId){
        this.aqumonAccountId = aqumonAccountId;
    }

    public Long getAqumonAccountId(){
        return aqumonAccountId;
    }

    public void setAqumonSecretKey(String aqumonSecretKey){
        this.aqumonSecretKey = aqumonSecretKey;
    }

    public String getAqumonSecretKey(){
        return aqumonSecretKey;
    }

    public void setAqumonBindDate(Date aqumonBindDate){
        this.aqumonBindDate = aqumonBindDate;
    }

    public Date getAqumonBindDate(){
        return aqumonBindDate;
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
