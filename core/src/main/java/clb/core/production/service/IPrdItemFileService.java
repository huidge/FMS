package clb.core.production.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import clb.core.production.dto.PrdItemFile;

public interface IPrdItemFileService extends IBaseService<PrdItemFile>, ProxySelf<IPrdItemFileService>{

	List<PrdItemFile> selectByItemId(IRequest requestContext,PrdItemFile prdItemFile, int page, int pageSize);
}
