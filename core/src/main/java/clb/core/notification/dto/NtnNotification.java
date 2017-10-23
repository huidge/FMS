package clb.core.notification.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.hand.hap.system.dto.BaseDTO;

/****
 * @author tiansheng.ye
 * @Date 2017-05-22
 */
@Table(name = "FMS_NTN_NOTIFICATION")
public class NtnNotification extends BaseDTO {
	private static final long serialVersionUID = 2937511056682595590L;

	 @Id
     @GeneratedValue
     private Long notificationId;

	 @Column
	 @NotNull
	 private Long userId;
	 
	 @Column
     @NotEmpty
     private String title;

	 @Column
     @NotEmpty
     private String content;
	 
	 @Column
      @NotEmpty
      private String status;
	 
	 private String importantFlag;

	 @Column
      private String requestUrl;

	@Transient
	 private Date showDate;

	public Long getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(Long notificationId) {
		this.notificationId = notificationId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public String getImportantFlag() {
		return importantFlag;
	}

	public void setImportantFlag(String importantFlag) {
		this.importantFlag = importantFlag;
	}

	public Date getShowDate() {
		return showDate;
	}

	public void setShowDate(Date showDate) {
		this.showDate = showDate;
	}
}
