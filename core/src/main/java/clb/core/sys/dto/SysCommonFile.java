package clb.core.sys.dto;

import java.util.Date;

/**Auto Generated By Hap Code Generator**/
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
@ExtensionAttribute(disable=true)
@Table(name = "fms_sys_common_file")
public class SysCommonFile extends BaseDTO {
     @Id
     @GeneratedValue
      private Long commonFileId;

     @NotNull
      private Long supplierId;

     @NotEmpty
      private String datumType;

     @NotEmpty
      private String useType;

      private String content;

      private Long fileId;

      private Long downloadTimes;

      private Long programId;

      private Long requestId;
      
      @Transient
      private String name;
      
      @Transient
      private String fileName;
      
      @Transient
      private String filePath;
      
      @Transient
      private Long fileSize;
      
      @Transient
      private Date uploadDate;
      
      @Transient
      private String fileType;
      
     public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public void setCommonFileId(Long commonFileId){
         this.commonFileId = commonFileId;
     }

     public Long getCommonFileId(){
         return commonFileId;
     }

     public void setSupplierId(Long supplierId){
         this.supplierId = supplierId;
     }

     public Long getSupplierId(){
         return supplierId;
     }

     public void setDatumType(String datumType){
         this.datumType = datumType;
     }

     public String getDatumType(){
         return datumType;
     }

     public void setUseType(String useType){
         this.useType = useType;
     }

     public String getUseType(){
         return useType;
     }

     public void setContent(String content){
         this.content = content;
     }

     public String getContent(){
         return content;
     }

     public void setFileId(Long fileId){
         this.fileId = fileId;
     }

     public Long getFileId(){
         return fileId;
     }

     public void setDownloadTimes(Long downloadTimes){
         this.downloadTimes = downloadTimes;
     }

     public Long getDownloadTimes(){
         return downloadTimes;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}
}
