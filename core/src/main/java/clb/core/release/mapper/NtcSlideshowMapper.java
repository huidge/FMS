package clb.core.release.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.release.dto.NtcSlideshow;

public interface NtcSlideshowMapper extends Mapper<NtcSlideshow>{

	List<NtcSlideshow> queryByCondition(NtcSlideshow dto);

	Long queryMaxRank();

	NtcSlideshow queryUpRank(NtcSlideshow dto);

	NtcSlideshow queryDownRank(NtcSlideshow dto);

}