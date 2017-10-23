package clb.core.fnd.dto;

/**Auto Generated By Hap Code Generator**/
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.NotEmpty;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;

@ExtensionAttribute(disable = true)
@Table(name = "fnd_file_object")
public class FileObject extends BaseDTO {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7072467631674278671L;

	@Id
	@GeneratedValue
	private Long objectId;

	@NotEmpty
	private String objectUrl;

	private String objectType;

	@NotEmpty
	private String objectName;
	
	@NotEmpty
	private String bucketName;
	
	@NotEmpty
	private String objectKey;
	
	private Long objectSize;
	
	@Transient
    private String isPrivate;

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	public Long getObjectId() {
		return objectId;
	}

	public void setObjectUrl(String objectUrl) {
		this.objectUrl = objectUrl;
	}

	public String getObjectUrl() {
		return objectUrl;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getObjectName() {
		return objectName;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getObjectKey() {
		return objectKey;
	}

	public void setObjectKey(String objectKey) {
		this.objectKey = objectKey;
	}

	public void setObjectSize(Long objectSize) {
		this.objectSize = objectSize;
	}

	public Long getObjectSize() {
		return objectSize;
	}

	public String getIsPrivate() {
		return isPrivate;
	}

	public void setIsPrivate(String isPrivate) {
		this.isPrivate = isPrivate;
	}

}
