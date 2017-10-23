package clb.core.commission.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import clb.core.channel.mapper.CnlProSupRelationMapper;
import clb.core.commission.dto.CmnSupplierItemRelation;
import clb.core.commission.mapper.CmnSupplierItemRelationMapper;
import clb.core.commission.service.ICmnSupplierItemRelationService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CmnSupplierItemRelationServiceImpl extends BaseServiceImpl<CmnSupplierItemRelation> implements ICmnSupplierItemRelationService{
	@Autowired
	private CmnSupplierItemRelationMapper cmnSupplierItemRelationMapper;
	@Override
	public List<CmnSupplierItemRelation> selectByCondition(IRequest requestContext, CmnSupplierItemRelation dto,
			int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		return cmnSupplierItemRelationMapper.selectByCondition(dto);
	}

}