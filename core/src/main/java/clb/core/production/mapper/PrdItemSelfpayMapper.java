package clb.core.production.mapper;

import clb.core.production.dto.PrdItemSelfpay;
import com.hand.hap.mybatis.common.Mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface PrdItemSelfpayMapper extends Mapper<PrdItemSelfpay>{
	
	List<PrdItemSelfpay> selectByItemId(Long itemId);

	List<PrdItemSelfpay> querySelfpay(PrdItemSelfpay prdItemSelfpay);
	
	Long querySelfpayId(@Param(value="selfpay")String selfpay, @Param(value="itemId")Long itemId);

}