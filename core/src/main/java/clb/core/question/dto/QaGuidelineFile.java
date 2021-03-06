package clb.core.question.dto;

import java.util.List;

/**
 * Auto Generated By Hap Code Generator
 **/

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;

import javax.persistence.Table;

import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@ExtensionAttribute(disable = true)
@Table(name = "fms_qa_guideline_file")
public class QaGuidelineFile extends BaseDTO {
    @Id
    @GeneratedValue
    private Long lineId;

    @NotNull
    private Long guidelineId;

    private Long fileId;
    
    private Long rank;

    private Long programId;

    private Long requestId;


    public void setLineId(Long lineId) {
        this.lineId = lineId;
    }

    public Long getLineId() {
        return lineId;
    }

    public void setGuidelineId(Long guidelineId) {
        this.guidelineId = guidelineId;
    }

    public Long getGuidelineId() {
        return guidelineId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getRequestId() {
        return requestId;
    }

    @Transient
    private String filePath;

    @Transient
    private String fileName;

    @Transient
    private Long fileSize;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    
    public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}

	@Transient
    private String fileType;

}
