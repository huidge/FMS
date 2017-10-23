package clb.core.pln.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import clb.core.pln.dto.PlnEPlanTemplateBig;
import clb.core.pln.dto.PlnEPlanTemplateDetail;
import clb.core.pln.mapper.PlnEPlanTemplateDetailMapper;
import clb.core.pln.service.IPlnEPlanTemplateDetailService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlnEPlanTemplateDetailServiceImpl extends BaseServiceImpl<PlnEPlanTemplateDetail> implements IPlnEPlanTemplateDetailService{
	
	@Autowired
	private PlnEPlanTemplateDetailMapper mapper;
	
	@Override
	public List<PlnEPlanTemplateDetail> selectByCondition(PlnEPlanTemplateDetail plnEPlanTemplateDetail) {
		
		return mapper.select(plnEPlanTemplateDetail);
	}
	/**
	 * 根据条件查询所有数据
	 */
	@Override
	public List<PlnEPlanTemplateDetail> queryList(IRequest requestContext, PlnEPlanTemplateDetail dto, int page,
			int pageSize) {
		PageHelper.startPage(page, pageSize);
		return mapper.queryList(dto);
	}
	
	/**
	 * 查询符合记录的总条数
	 */
	@Override
	public Long selectCount(IRequest requestContext, PlnEPlanTemplateDetail dto) {
		return (long) mapper.selectCount(dto);
	}

}