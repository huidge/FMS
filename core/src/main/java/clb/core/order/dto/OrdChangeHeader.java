package clb.core.order.dto;

/**wanjun.feng@hand-china.com    2017/9/21**/
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.hand.hap.core.annotation.Children;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.hand.hap.system.dto.BaseDTO;
import java.util.Date;
import java.util.List;
@ExtensionAttribute(disable=true)
@Table(name = "fms_ord_change_header")
public class OrdChangeHeader extends BaseDTO {
    private static final long serialVersionUID = -8242037397910129290L;

    @Id
    @GeneratedValue
    private Long headerId;

    @NotNull
    private Long orderId;

    @NotNull
    private Date updateTime;

    @NotNull
    private Long userId;

    @Transient
    private String userName;
    
    @Children
    @Transient
    private List<OrdChangeLine> ordChangeLineList;

    public Long getHeaderId() {
        return headerId;
    }

    public void setHeaderId(Long headerId) {
        this.headerId = headerId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<OrdChangeLine> getOrdChangeLineList() {
        return ordChangeLineList;
    }

    public void setOrdChangeLineList(List<OrdChangeLine> ordChangeLineList) {
        this.ordChangeLineList = ordChangeLineList;
    }
}
