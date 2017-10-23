package clb.core.forecast.service.impl;

import java.util.List;

import clb.core.forecast.dto.FetPeriod;
import clb.core.forecast.mapper.FetPeriodMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.forecast.dto.FetPeriodHead;
import clb.core.forecast.mapper.FetPeriodHeadMapper;
import clb.core.forecast.service.IFetPeriodHeadService;

@Service
@Transactional
public class FetPeriodHeadServiceImpl extends BaseServiceImpl<FetPeriodHead> implements IFetPeriodHeadService{
	
	@Autowired
	private FetPeriodHeadMapper fetPeriodHeadMapper;

	@Autowired
	private FetPeriodMapper fetPeriodMapper;
	
	@Override
	public List<FetPeriodHead> selectAllFiled(IRequest requestContext, FetPeriodHead dto, int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		return fetPeriodHeadMapper.selectAllFiled(dto);
	}

	@Override
	@Transactional
	public void copyPeriod(IRequest request, FetPeriodHead dto) {
		// 保留headId
		Long headId = dto.getHeadId();
		/* 插入头表,获取新headId */
		dto.setHeadId(null);
		Long newHeadId = this.insertSelective(request, dto).getHeadId();

		/* 获取行数据 */
		FetPeriod fetPeriod = new FetPeriod();
		fetPeriod.setHeadId(headId);
		List<FetPeriod> periods = fetPeriodMapper.select(fetPeriod);

		/* 将行数据复制 */
		for (FetPeriod period : periods) {
			period.setHeadId(newHeadId);
			period.setPeriodId(null);
			fetPeriodMapper.insertSelective(period);
		}
	}
}