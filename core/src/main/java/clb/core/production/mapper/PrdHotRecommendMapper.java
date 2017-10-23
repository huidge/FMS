package clb.core.production.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.production.dto.PrdHotRecommend;

public interface PrdHotRecommendMapper extends Mapper<PrdHotRecommend>{
	
	/**
	 * 产品类型
	 * @param productType
	 * @return
	 */
    List<PrdHotRecommend> selectInfoByType(String productType);

}