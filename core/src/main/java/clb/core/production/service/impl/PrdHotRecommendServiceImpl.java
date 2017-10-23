package clb.core.production.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.production.dto.PrdHotRecommend;
import clb.core.production.mapper.PrdHotRecommendMapper;
import clb.core.production.service.IPrdHotRecommendService;

@Service
@Transactional
public class PrdHotRecommendServiceImpl extends BaseServiceImpl<PrdHotRecommend> implements IPrdHotRecommendService{

	@Autowired
	private PrdHotRecommendMapper prdHotRecommendMapper;
	
	@Override
	public List<PrdHotRecommend> selectInfoByType(IRequest requestCtx, String productType, int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		return prdHotRecommendMapper.selectInfoByType(productType);
	}

}