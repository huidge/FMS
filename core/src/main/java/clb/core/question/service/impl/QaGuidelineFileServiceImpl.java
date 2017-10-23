package clb.core.question.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.question.dto.QaGuidelineFile;
import clb.core.question.mapper.QaGuidelineFileMapper;
import clb.core.question.service.IQaGuidelineFileService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class QaGuidelineFileServiceImpl extends BaseServiceImpl<QaGuidelineFile> implements IQaGuidelineFileService{
	
	@Autowired
	private QaGuidelineFileMapper mapper;
	
	@Override
	public void removeByFileId(Long fileId) {
		mapper.removeByFileId(fileId);
		
	}

	public List<QaGuidelineFile> queryFileInfo(QaGuidelineFile qaGuidelineFile){
		return mapper.queryFileInfo(qaGuidelineFile);
	}
	/**
	 * 排序查询
	 */
	@Override
	public List<QaGuidelineFile> quertSort(IRequest requestContext, QaGuidelineFile dto, int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		return mapper.quertSort(dto);
	}

	@Override
	public QaGuidelineFile queryUpRank(QaGuidelineFile dto) {
		return mapper.queryUpRank(dto);
	}

	@Override
	public QaGuidelineFile queryDownRank(QaGuidelineFile dto) {
		return mapper.queryDownRank(dto);
	}

	@Override
	public Long queryMaxRank(QaGuidelineFile dto) {
		return mapper.queryMaxRank(dto);
	}

}