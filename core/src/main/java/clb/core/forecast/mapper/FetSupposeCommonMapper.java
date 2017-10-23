package clb.core.forecast.mapper;

import java.util.List;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.forecast.dto.FetSupposeCommon;
import clb.core.forecast.dto.FetSupposeUpdateVersion;

public interface FetSupposeCommonMapper extends Mapper<FetSupposeCommon>{
	
	List<FetSupposeCommon> getOrderData(FetSupposeCommon dto);
	
	FetSupposeCommon getOne(FetSupposeCommon dto);
	
	List<FetSupposeCommon> getAll();
	
	List<Long> getOrderIds(FetSupposeCommon dto);

}