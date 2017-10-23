package clb.core.prc.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.prc.dto.PrcRadarLine;
import clb.core.prc.mapper.PrcRadarLineMapper;
import clb.core.prc.service.IPrcRadarLineService;

@Service
@Transactional
public class PrcRadarLineServiceImpl extends BaseServiceImpl<PrcRadarLine> implements IPrcRadarLineService{

	@Autowired
	private PrcRadarLineMapper prcRadarLineMapper;
	
	@Override
	public List<PrcRadarLine> selectByAttSetID(IRequest requestContext, String attSetId, int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		return prcRadarLineMapper.selectByAttSetID(Long.valueOf(attSetId));
	}

}