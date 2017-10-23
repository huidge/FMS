package clb.core.prc.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.prc.dto.PrcAttributeSet;
import clb.core.prc.mapper.PrcAttributeSetMapper;
import clb.core.prc.service.IPrcAttributeSetService;

@Service
@Transactional
public class PrcAttributeSetServiceImpl extends BaseServiceImpl<PrcAttributeSet> implements IPrcAttributeSetService{

	@Autowired
	private PrcAttributeSetMapper prcAttributeSetMapper;
	
	@Override
	public List<PrcAttributeSet> selectSetName() {
		return prcAttributeSetMapper.selectSetName();
	}

	@Override
	public List<PrcAttributeSet> selectByCondition(IRequest requestContext, PrcAttributeSet dto, int page,
			int pageSize) {
		PageHelper.startPage(page, pageSize);
		return prcAttributeSetMapper.selectByCondition(dto);
	}

}