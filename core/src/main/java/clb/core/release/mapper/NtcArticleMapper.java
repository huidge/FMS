package clb.core.release.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.release.dto.NtcArticle;

public interface NtcArticleMapper extends Mapper<NtcArticle>{
	

	List<NtcArticle> queryByCondition(NtcArticle dto);
	
	
	/**
	 * 查询所有文章信息
	 * @return
	 */
	List<NtcArticle> selectAllArticle(NtcArticle dto);

}