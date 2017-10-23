package clb.core.release.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import clb.core.release.dto.NtcArticle;

public interface INtcArticleService extends IBaseService<NtcArticle>, ProxySelf<INtcArticleService>{

	
	List<NtcArticle> queryByCondition(IRequest requestContext, NtcArticle dto, int page, int pageSize);

	List<NtcArticle> selectAllArticle(IRequest requestContext, NtcArticle dto, int page, int pageSize);
	
	public NtcArticle queryDetailById(IRequest requestContext, NtcArticle dto);
}