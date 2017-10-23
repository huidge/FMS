package clb.core.prc.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.prc.dto.PrcRadarLineDetail;

public interface IPrcRadarLineDetailService extends IBaseService<PrcRadarLineDetail>, ProxySelf<IPrcRadarLineDetailService>{

	List<PrcRadarLineDetail> selectByLineId(IRequest requestContext, String lineId, int page, int pageSize);

	List<PrcRadarLineDetail> selectAttSetInfo(PrcRadarLineDetail AttSetInfo);
	
	PrcRadarLineDetail selectLineDetailInfo(Long AttSetId, String detailName,Long detailId);
}