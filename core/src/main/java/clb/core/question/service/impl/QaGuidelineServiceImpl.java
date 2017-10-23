package clb.core.question.service.impl;

import clb.core.question.mapper.QaGuidelineMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import clb.core.common.utils.CommonUtil;
import clb.core.question.dto.QaGuideline;
import clb.core.question.dto.QaGuidelineFile;
import clb.core.question.service.IQaGuidelineFileService;
import clb.core.question.service.IQaGuidelineService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class QaGuidelineServiceImpl extends BaseServiceImpl<QaGuideline> implements IQaGuidelineService{

    @Autowired
    private QaGuidelineMapper mapper;
    @Autowired
    private IQaGuidelineFileService qaGuidelineFileService;

    @Override
    public List<QaGuideline> query(IRequest requestContext, QaGuideline qaGuideline) {

        return mapper.select(qaGuideline);
    }

	@Override
	public List<QaGuideline> add(IRequest requestCtx, List<QaGuideline> dto) throws Exception{
		try {
			QaGuideline qaGuideline = dto.get(0);
			if(qaGuideline != null){
				QaGuideline guideline = new QaGuideline();
				guideline.setGuidelineName(qaGuideline.getGuidelineName());
				guideline.setComments((qaGuideline.getGuidelineName()));
				
				QaGuideline insertSelective = insertSelective(requestCtx, guideline);
				
				List<QaGuidelineFile> fileGrid = qaGuideline.getFileGrid();
				if(fileGrid != null){
					for (QaGuidelineFile qaGuidelineFile : fileGrid) {
						qaGuidelineFile.setGuidelineId(insertSelective.getGuidelineId());
						Long rank = qaGuidelineFileService.queryMaxRank(qaGuidelineFile);
						qaGuidelineFile.setRank(rank == null ?  1 : rank +1); 
						qaGuidelineFileService.insertSelective(requestCtx, qaGuidelineFile);
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException();
		}
		return dto;
	}

	@Override
	public List<QaGuideline> submit(IRequest requestCtx, List<QaGuideline> dto)throws Exception {
		try {
			QaGuideline qaGuideline = dto.get(0);
			if(qaGuideline != null){
				
				QaGuideline update = updateByPrimaryKeySelective(requestCtx, qaGuideline);
				
				List<QaGuidelineFile> fileGrid = qaGuideline.getFileGrid();
				if(fileGrid != null){
					for (QaGuidelineFile qaGuidelineFile : fileGrid) {
						if(qaGuidelineFile.getGuidelineId() == null){
							qaGuidelineFile.setGuidelineId(update.getGuidelineId());
							if(qaGuidelineFile.getRank() == null){
								Long rank = qaGuidelineFileService.queryMaxRank(qaGuidelineFile);
								qaGuidelineFile.setRank(rank == null ?  1 : rank +1); 
								qaGuidelineFileService.insertSelective(requestCtx, qaGuidelineFile);
							}
						}else{
							qaGuidelineFileService.updateByPrimaryKeySelective(requestCtx, qaGuidelineFile);
						}
						
					} 
				}
			}
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
			throw new RuntimeException();
		}
		return dto;
	}
}