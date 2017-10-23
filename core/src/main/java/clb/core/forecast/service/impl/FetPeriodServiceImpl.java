package clb.core.forecast.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.exchangeRate.dto.FetExchangeRate;
import clb.core.forecast.dto.FetPeriod;
import clb.core.forecast.mapper.FetPeriodMapper;
import clb.core.forecast.service.IFetPeriodService;

@Service
@Transactional
public class FetPeriodServiceImpl extends BaseServiceImpl<FetPeriod> implements IFetPeriodService {

	@Autowired
	private FetPeriodMapper mapper;

	@Override
	public List<FetPeriod> addFetPeriod(IRequest requestCtx, List<FetPeriod> dto) throws Exception {
		
		List<FetPeriod> list = new ArrayList<>();
		list.addAll(dto);
		//查询数据库中的所有记录
		FetPeriod period = new FetPeriod();
		period.setHeadId(dto.get(0).getHeadId());
		List<FetPeriod> list2 = mapper.select(period);
		list.addAll(list2);
		
		//先迭代的判断提交的时间是否存在重叠
		Iterator<FetPeriod> iterator1 = list.iterator();
		while (iterator1.hasNext()) {
			FetPeriod next = iterator1.next();
			iterator1.remove();
			Iterator<FetPeriod> iterator2 = list.iterator();
			while (iterator2.hasNext()) {
				FetPeriod next2 = iterator2.next();
				if(compareTimeRange(next, next2)) {
					throw new RuntimeException("时间有效期重叠!!!");
				}
			}
		}
		return batchUpdate(requestCtx, dto);
	}
	
	/**
	 * 比较 2个对象的有效时间  判断有效时间范围是否重叠   如果重叠了 返回true 否则返回false
	 * @param old
	 * @param current
	 * @return
	 */
	public boolean compareTimeRange(FetPeriod old,FetPeriod current){
		
		boolean falg = false;
		Date a1 = old.getStartDate();
		Date a2 = old.getEndDate();
		
		Date b1 = current.getStartDate();
		Date b2 = current.getEndDate();
		
		if (a1.getTime() <= b2.getTime() & a2.getTime() >= b1.getTime()) {
			falg = true;
		}
		
		return falg;
	}

}