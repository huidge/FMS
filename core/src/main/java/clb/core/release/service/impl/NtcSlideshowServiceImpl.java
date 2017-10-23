package clb.core.release.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.release.dto.NtcSlideshow;
import clb.core.release.mapper.NtcSlideshowMapper;
import clb.core.release.service.INtcSlideshowService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NtcSlideshowServiceImpl extends BaseServiceImpl<NtcSlideshow> implements INtcSlideshowService{

	@Autowired
	private NtcSlideshowMapper ntcSlideshowMapper;
	
	@Override
	public List<NtcSlideshow> queryByCondition(IRequest requestContext, NtcSlideshow dto, int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		return ntcSlideshowMapper.queryByCondition(dto);
	}

	@Override
	public Long queryMaxRank() {
		return ntcSlideshowMapper.queryMaxRank();
	}

	@Override
	public NtcSlideshow queryUpRank(NtcSlideshow dto) {
		return ntcSlideshowMapper.queryUpRank(dto);
	}

	@Override
	public NtcSlideshow queryDownRank(NtcSlideshow dto) {
		return ntcSlideshowMapper.queryDownRank(dto);
	}

}