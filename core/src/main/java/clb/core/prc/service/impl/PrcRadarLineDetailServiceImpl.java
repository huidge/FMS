package clb.core.prc.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.prc.dto.PrcRadarLineDetail;
import clb.core.prc.mapper.PrcRadarLineDetailMapper;
import clb.core.prc.service.IPrcRadarLineDetailService;

@Service
@Transactional
public class PrcRadarLineDetailServiceImpl extends BaseServiceImpl<PrcRadarLineDetail> implements IPrcRadarLineDetailService{

	@Autowired
	private PrcRadarLineDetailMapper prcRadarLineDetailMapper;
	
	@Override
	public List<PrcRadarLineDetail> selectByLineId(IRequest requestContext, String lineId, int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		return prcRadarLineDetailMapper.selectByLineId(Long.valueOf(lineId));
	}

	@Override
	public List<PrcRadarLineDetail> selectAttSetInfo(PrcRadarLineDetail AttSetInfo) {
		return prcRadarLineDetailMapper.selectAttSetInfo(AttSetInfo);
	}

	@Override
	public PrcRadarLineDetail selectLineDetailInfo(Long AttSetId, String detailName,Long detailId) {
		return prcRadarLineDetailMapper.selectLineDetailInfo(AttSetId, detailName,detailId);
	}

}