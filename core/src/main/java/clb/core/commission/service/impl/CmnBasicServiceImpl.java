package clb.core.commission.service.impl;

import clb.core.commission.mapper.CmnBasicMapper;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.commission.dto.CmnBasic;
import clb.core.commission.service.ICmnBasicService;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class CmnBasicServiceImpl extends BaseServiceImpl<CmnBasic> implements ICmnBasicService{

    @Autowired
    private CmnBasicMapper cmnBasicMapper;

    @Override
    public List<CmnBasic> queryBasic(IRequest requestContext, CmnBasic dto, int page, int pageSize) {
        PageHelper.startPage(page,pageSize);
        return cmnBasicMapper.queryBasic(dto);
    }

    @Override
    public List<CmnBasic> queryBasic(CmnBasic dto) {
        return cmnBasicMapper.queryBasic(dto);
    }

    @Override
    public Long queryMaxVersion(CmnBasic cmnBasic) {
        Long maxVersion = cmnBasicMapper.queryMaxVersion(cmnBasic);
        System.out.println(maxVersion);
        if (maxVersion==null){
            return 0L;
        }else {
            return maxVersion;
        }
    }

    @Override
    public List<CmnBasic> addVersion(IRequest request, CmnBasic dto, Long oldVersion) {
        CmnBasic cmnBasic = new CmnBasic();

        //查询前一版本数据
        cmnBasic.setItemId(dto.getItemId());
        cmnBasic.setContributionPeriod(dto.getContributionPeriod());
        cmnBasic.setCurrency(dto.getCurrency());
        cmnBasic.setPayMethod(dto.getPayMethod());
        cmnBasic.setPolicyholdersMinAge(dto.getPolicyholdersMinAge());
        cmnBasic.setPolicyholdersMaxAge(dto.getPolicyholdersMaxAge());
        cmnBasic.setVersion(oldVersion);
        CmnBasic cmnBasicOld = cmnBasicMapper.selectOne(cmnBasic);
        //更新前一版本数据(有效期至)
        //更新前一版本数据(有效期至 有效期自(新)-1)
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dto.getEffectiveStartDate());
        calendar.add(Calendar.DATE,-1);
        cmnBasicOld.setEffectiveEndDate(calendar.getTime());
        cmnBasicMapper.updateByPrimaryKeySelective(cmnBasicOld);

        //创建新版本
        dto.setVersion(oldVersion+1);
        cmnBasicMapper.insertSelective(dto);
        List<CmnBasic> list = new ArrayList<>();
        list.add(cmnBasicOld);
        list.add(dto);
        return list;

    }

	@Override
	public boolean checkData(IRequest request, CmnBasic dto) {
		boolean check=true;
		//校验年龄是否重叠
		CmnBasic basic=new CmnBasic();
		basic.setSupplierId(dto.getSupplierId());
		basic.setItemId(dto.getItemId());
		basic.setContributionPeriod(dto.getContributionPeriod());
		basic.setCurrency(dto.getCurrency());
		basic.setPayMethod(dto.getPayMethod());
		List<CmnBasic> list=cmnBasicMapper.select(basic);
		for(CmnBasic cb:list){
			if( (cb.getPolicyholdersMinAge()<=dto.getPolicyholdersMinAge() && dto.getPolicyholdersMinAge()<=cb.getPolicyholdersMaxAge())
					|| (cb.getPolicyholdersMaxAge()>=dto.getPolicyholdersMaxAge() && dto.getPolicyholdersMaxAge()>=cb.getPolicyholdersMinAge()) ){
//				throw new RuntimeException("新增异常：年龄不能与原有数据重叠！");
				check=false;
			}
			
			if( (dto.getPolicyholdersMinAge()<=cb.getPolicyholdersMinAge() && cb.getPolicyholdersMinAge()<=dto.getPolicyholdersMaxAge())
					|| (dto.getPolicyholdersMaxAge()>=cb.getPolicyholdersMaxAge() && cb.getPolicyholdersMaxAge()>=dto.getPolicyholdersMinAge()) ){
//				throw new RuntimeException("新增异常：年龄不能与原有数据重叠！");
				check=false;
			}
		}
		return check;
	}
}