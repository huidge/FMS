package clb.core.question.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.question.dto.QaGuidelineFile;

import java.util.List;

public interface IQaGuidelineFileService extends IBaseService<QaGuidelineFile>, ProxySelf<IQaGuidelineFileService>{

	void removeByFileId(Long fileId);

	public List<QaGuidelineFile> queryFileInfo(QaGuidelineFile qaGuidelineFile);

	List<QaGuidelineFile> quertSort(IRequest requestContext, QaGuidelineFile dto, int page, int pageSize);

	QaGuidelineFile queryUpRank(QaGuidelineFile dto);

	QaGuidelineFile queryDownRank(QaGuidelineFile dto);
	Long queryMaxRank(QaGuidelineFile dto);
}