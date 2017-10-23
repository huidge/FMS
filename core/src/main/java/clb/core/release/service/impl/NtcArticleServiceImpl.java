package clb.core.release.service.impl;

import clb.core.common.utils.CommonUtil;
import clb.core.release.dto.NtcAnnouncement;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hap.system.dto.ResponseData;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.release.dto.NtcArticle;
import clb.core.release.mapper.NtcArticleMapper;
import clb.core.release.service.INtcArticleService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NtcArticleServiceImpl extends BaseServiceImpl<NtcArticle> implements INtcArticleService{
	
	@Autowired
	private NtcArticleMapper mapper;
	
	@Override
	public List<NtcArticle> queryByCondition(IRequest requestContext, NtcArticle dto, int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		List<NtcArticle> list = mapper.queryByCondition(dto);
		return list;
	}

	// 接口:公告列表查询
	@Override
	public List<NtcArticle> selectAllArticle(IRequest requestContext, NtcArticle dto, int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		dto.setUserId(requestContext.getUserId());
		List<NtcArticle> list = mapper.selectAllArticle(dto);
		return list;
	}
	
	@Override
	public NtcArticle queryDetailById(IRequest requestContext, NtcArticle dto) {
		NtcArticle newBean=mapper.selectByPrimaryKey(dto.getArticleId());
		dto.setArticleId(null);
		dto.setEndDate(newBean.getReleaseDate());
		PageHelper.startPage(1, 10);
		List<NtcArticle> nextlist = mapper.queryByCondition(dto);
		if(nextlist.size()>1){
			newBean.setNextBean(nextlist.get(1));
		}
		dto.setEndDate(null);
		dto.setStartDate(newBean.getReleaseDate());
		PageHelper.startPage(1, 10);
		List<NtcArticle> lastlist = mapper.queryByCondition(dto);
		if(lastlist.size()>1){
			newBean.setLastBean(lastlist.get(lastlist.size()-1));
		}
		return newBean;
	}
}