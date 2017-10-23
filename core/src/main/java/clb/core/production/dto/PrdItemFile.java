package clb.core.production.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.hand.hap.system.dto.BaseDTO;


@Table(name="fms_prd_item_file")
public class PrdItemFile extends BaseDTO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@Id
    @Column
    @GeneratedValue(generator="IDENTITY")
    private Long lineId;
	
	@Column
	private Long itemId;
	
	@Column
	private String type;

	@Column
	private Long fileId;
	
	@Column
	private String fileName;
	
	private Long downloadTimes;
	
	@Transient
	private String fileSize;
	
	@Transient
	private Date lastUpdateDate;
	
	@Transient
	private String name;
	
	@Transient
	private String midClass;
	
	@Transient
	private String bigClass;
	
	@Transient
	private String itemName;
	
	@Transient
	private Long supplierId;
	
	@Transient
	private Date uploadDate;
	
	@Transient
	private String filePath;
	
	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Long getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMidClass() {
		return midClass;
	}

	public void setMidClass(String midClass) {
		this.midClass = midClass;
	}

	public String getBigClass() {
		return bigClass;
	}

	public void setBigClass(String bigClass) {
		this.bigClass = bigClass;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public Long getLineId() {
		return lineId;
	}

	public void setLineId(Long lineId) {
		this.lineId = lineId;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Long getDownloadTimes() {
		return downloadTimes;
	}

	public void setDownloadTimes(Long downloadTimes) {
		this.downloadTimes = downloadTimes;
	}
}
