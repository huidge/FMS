package clb.core.production.mapper;

import clb.core.production.dto.PrdItemPaymode;
import com.hand.hap.mybatis.common.Mapper;

import java.util.List;

public interface PrdItemPaymodeMapper extends Mapper<PrdItemPaymode>{
	/**
	 * 在电子计划书模块  需要根据产品查询产品下的货币
	 * @param dto
	 * @return
	 */
	List<PrdItemPaymode> queryCurrencyByItemId(PrdItemPaymode dto);

	List<PrdItemPaymode> queryAllCurrency(PrdItemPaymode dto);
}