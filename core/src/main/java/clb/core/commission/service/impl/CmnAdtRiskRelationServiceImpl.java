package clb.core.commission.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.commission.dto.CmnAdtRiskRelation;
import clb.core.commission.mapper.CmnAdtRiskRelationMapper;
import clb.core.commission.service.ICmnAdtRiskRelationService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CmnAdtRiskRelationServiceImpl extends BaseServiceImpl<CmnAdtRiskRelation> implements ICmnAdtRiskRelationService{
	@Autowired
	private CmnAdtRiskRelationMapper mapper;
	
	@Override
	public List<CmnAdtRiskRelation> queryAll(IRequest requestContext, CmnAdtRiskRelation dto, int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		return mapper.queryAll(dto);
	}
	/**
	 * 查询 产品公司+附加险+主险组合  是否存在  by 产品公司+附加险 主险组合为空
	 */
	@Override
	public List<CmnAdtRiskRelation> queryByCondition(IRequest requestCtx, CmnAdtRiskRelation cmnAdtRiskRelation) {
		return mapper.queryByCondition(cmnAdtRiskRelation);
	}

}