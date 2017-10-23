package clb.core.sys.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.course.dto.TrnSupport;
import clb.core.course.mapper.TrnSupportMapper;
import clb.core.sys.service.ISysMimuteJobSupportService;

@Service
@Transactional
public class SysMimuteJobSupportServiceImpl extends BaseServiceImpl<TrnSupport> implements ISysMimuteJobSupportService {

	@Autowired
	private TrnSupportMapper trnSupportMapper;

	@Override
	public void updateAmountInvalid(IRequest requestContext) {
		TrnSupport trnSupport2 = new TrnSupport();
		List<TrnSupport> list = trnSupportMapper.updateAmountInvalid(trnSupport2);
		for (TrnSupport trnSupport : list) {
			Date amountOperationDate = trnSupport.getAmountOperationDate();
			if(amountOperationDate != null){
				Calendar ca=Calendar.getInstance();
				ca.setTime(amountOperationDate);
				ca.add(Calendar.HOUR_OF_DAY, 24);
				Date time = ca.getTime();
				if (time.getTime() < new Date().getTime()) {
	                TrnSupport trnSupport4 = new TrnSupport();
	                trnSupport4.setSupportId(trnSupport.getSupportId());
	                trnSupport4.setStatus("CANCEL");
	                trnSupport4.setCostContent("超过24小时未支付");
	                updateByPrimaryKeySelective(requestContext, trnSupport4);
	            }
			}
		}
	}
}
