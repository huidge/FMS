package clb.core.pln.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.common.Mapper;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import clb.core.pln.dto.PlnEPlanTemplateDetail;
import clb.core.pln.dto.PlnEPlanTemplateSmall;
import clb.core.pln.mapper.PlnEPlanTemplateSmallMapper;
import clb.core.pln.service.IPlnEPlanTemplateSmallService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlnEPlanTemplateSmallServiceImpl extends BaseServiceImpl<PlnEPlanTemplateSmall> implements IPlnEPlanTemplateSmallService{
	
	@Autowired
	private PlnEPlanTemplateSmallMapper mapper;
	
	@Override
	public List<PlnEPlanTemplateSmall> selectByCondition(PlnEPlanTemplateSmall plnEPlanTemplateSmall) {
		return mapper.select(plnEPlanTemplateSmall);
	}
	/**
	 * 根据条件查询所有数据
	 */
	@Override
	public List<PlnEPlanTemplateSmall> queryList(IRequest requestContext, PlnEPlanTemplateSmall dto, int page,
			int pageSize) {
		PageHelper.startPage(page, pageSize);
		return mapper.queryList(dto);
	}
	
	/**
	 * 查询符合记录的总条数
	 */
	@Override
	public Long selectCount(IRequest requestContext, PlnEPlanTemplateSmall dto) {
		return (long) mapper.selectCount(dto);
	}


}