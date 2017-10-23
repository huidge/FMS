package clb.core.course.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.course.dto.TrnSupport;
import clb.core.course.mapper.TrnSupportMapper;
import clb.core.course.service.ITrnSupportService;

@Service
@Transactional
public class TrnSupportServiceImpl extends BaseServiceImpl<TrnSupport> implements ITrnSupportService{
	@Autowired
    private TrnSupportMapper trnSupportMapper;
	
	@Override
	public List<TrnSupport> selectAllField(IRequest requestContext, TrnSupport trnSupport, int page, int pagesize) {
		PageHelper.startPage(page, pagesize);
		List<TrnSupport> list = trnSupportMapper.selectAllField(trnSupport);
		return list;
	}

	@Override
	public Long supportQueryTotal(IRequest requestContext, TrnSupport trnSupport) {
		Long list = trnSupportMapper.supportQueryTotal(trnSupport);
		return list;
	}


	@Override
	public void updateAmountInvalid(IRequest requestContext) {
		TrnSupport trnSupport2 = new TrnSupport();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<TrnSupport> list = trnSupportMapper.updateAmountInvalid(trnSupport2);
		for (TrnSupport trnSupport : list) {
			Date amountOperationDate = trnSupport.getAmountOperationDate();
			if(amountOperationDate != null){
				Calendar ca=Calendar.getInstance();
				ca.setTime(amountOperationDate);
				ca.add(Calendar.HOUR_OF_DAY, 24);
				Date time = ca.getTime();
				//System.out.println(time.getTime());
				//System.out.println(new Date().getTime());
				if (time.getTime() < new Date().getTime()) {
	                //System.out.println("超时");
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