package clb.core.prc.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.prc.dto.PrcRadarLine;

public interface IPrcRadarLineService extends IBaseService<PrcRadarLine>, ProxySelf<IPrcRadarLineService>{

	List<PrcRadarLine> selectByAttSetID(IRequest requestContext, String attSetId, int page, int pageSize);
}