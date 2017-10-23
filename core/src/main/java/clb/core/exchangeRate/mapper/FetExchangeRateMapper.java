package clb.core.exchangeRate.mapper;

import java.util.Date;
import java.util.List;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.exchangeRate.dto.FetExchangeRate;

public interface FetExchangeRateMapper extends Mapper<FetExchangeRate>{
	
	List<FetExchangeRate> selectRateByCurrentTime(FetExchangeRate dto);

	List<FetExchangeRate> queryByForeignCurrencyAndBaseCurrency(FetExchangeRate rate);
	
}