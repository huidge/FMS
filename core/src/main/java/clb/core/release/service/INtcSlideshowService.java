package clb.core.release.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.release.dto.NtcSlideshow;

public interface INtcSlideshowService extends IBaseService<NtcSlideshow>, ProxySelf<INtcSlideshowService>{

	List<NtcSlideshow> queryByCondition(IRequest requestContext, NtcSlideshow dto, int page, int pageSize);

	Long queryMaxRank();

	NtcSlideshow queryUpRank(NtcSlideshow dto);

	NtcSlideshow queryDownRank(NtcSlideshow dto);

}