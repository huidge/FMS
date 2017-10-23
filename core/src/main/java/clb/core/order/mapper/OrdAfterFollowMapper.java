package clb.core.order.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.order.dto.OrdAfterFollow;

public interface OrdAfterFollowMapper extends Mapper<OrdAfterFollow>{
	/**
	 * 查询售后跟记录  根据售后id
	 * @param dto
	 * @return
	 */
	List<OrdAfterFollow> queryByAfterId(OrdAfterFollow dto);
	/**
	 * 查询最后一条记录
	 * @return
	 */
	OrdAfterFollow queryLastOrdAfterFollowByAfterId(OrdAfterFollow dto);

}