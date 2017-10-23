package clb.core.pln.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.pln.dto.PlnEPlanTemplateBig;
import clb.core.pln.mapper.PlnEPlanTemplateBigMapper;
import clb.core.pln.service.IPlnEPlanTemplateBigService;

@Service
@Transactional
public class PlnEPlanTemplateBigServiceImpl extends BaseServiceImpl<PlnEPlanTemplateBig> implements IPlnEPlanTemplateBigService{
	@Autowired
	private PlnEPlanTemplateBigMapper mapper;
	
	/**
	 * 根据条件查询所有数据
	 */
	@Override
	public List<PlnEPlanTemplateBig> queryList(IRequest requestContext, PlnEPlanTemplateBig dto, int page,
			int pageSize) {
		PageHelper.startPage(page, pageSize);
		return mapper.queryList(dto);
	}
	/**
	 * 查询符合记录的总条数
	 */
	@Override
	public Long selectCount(IRequest requestContext, PlnEPlanTemplateBig dto) {
		return (long) mapper.selectCount(dto);
	}

}