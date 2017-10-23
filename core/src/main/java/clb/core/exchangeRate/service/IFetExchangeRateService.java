package clb.core.exchangeRate.service;

import java.util.List;

import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.exchangeRate.dto.FetExchangeRate;

public interface IFetExchangeRateService extends IBaseService<FetExchangeRate>, ProxySelf<IFetExchangeRateService>{

	List<FetExchangeRate> queryByForeignCurrencyAndBaseCurrency(FetExchangeRate rate);

	List<FetExchangeRate> selectRateByCurrentTime(FetExchangeRate rate);

}