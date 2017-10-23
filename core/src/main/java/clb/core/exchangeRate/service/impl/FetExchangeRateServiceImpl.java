package clb.core.exchangeRate.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.exchangeRate.dto.FetExchangeRate;
import clb.core.exchangeRate.mapper.FetExchangeRateMapper;
import clb.core.exchangeRate.service.IFetExchangeRateService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FetExchangeRateServiceImpl extends BaseServiceImpl<FetExchangeRate> implements IFetExchangeRateService{
	
	@Autowired
	private FetExchangeRateMapper fetExchangeRateMapper;
	
	@Override
	public List<FetExchangeRate> queryByForeignCurrencyAndBaseCurrency(FetExchangeRate rate) {
		return fetExchangeRateMapper.queryByForeignCurrencyAndBaseCurrency(rate);
	}

	@Override
	public List<FetExchangeRate> selectRateByCurrentTime(FetExchangeRate rate) {
		return fetExchangeRateMapper.selectRateByCurrentTime(rate);
	}

}