package clb.core.question.mapper;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.question.dto.QaGuidelineFile;

import java.util.List;

public interface QaGuidelineFileMapper extends Mapper<QaGuidelineFile>{

	void removeByFileId(Long fileId);

	public List<QaGuidelineFile> queryFileInfo(QaGuidelineFile qaGuidelineFile);

	List<QaGuidelineFile> quertSort(QaGuidelineFile dto);

	QaGuidelineFile queryUpRank(QaGuidelineFile dto);

	QaGuidelineFile queryDownRank(QaGuidelineFile dto);

	Long queryMaxRank(QaGuidelineFile dto);
}