package clb.core.commission.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.commission.dto.CmnExtras;
import clb.core.commission.mapper.CmnExtrasMapper;
import clb.core.commission.service.ICmnExtrasService;

@Service
@Transactional
public class CmnExtrasServiceImpl extends BaseServiceImpl<CmnExtras> implements ICmnExtrasService{

    @Autowired
    private CmnExtrasMapper cmnExtrasMapper;

    @Override
    public List<CmnExtras> queryExtras(IRequest requestContext, CmnExtras dto, int page, int pageSize) {
        PageHelper.startPage(page,pageSize);
        return cmnExtrasMapper.queryExtras(dto);
    }

    @Override
    public List<CmnExtras> queryExtras(CmnExtras dto) {
        return cmnExtrasMapper.queryExtras(dto);
    }

    @Override
    public Long queryMaxVersion(CmnExtras cmnExtras) {
        Long maxVersion = cmnExtrasMapper.queryMaxVersion(cmnExtras);
        System.out.println(maxVersion);
        if (maxVersion==null){
            return 0L;
        }else {
            return maxVersion;
        }
    }

    @Override
    public List<CmnExtras> addVersion(IRequest request, CmnExtras dto, Long oldVersion) {
        CmnExtras CmnExtras = new CmnExtras();

        //查询前一版本数据
        CmnExtras.setItemId(dto.getItemId());
        CmnExtras.setContributionPeriod(dto.getContributionPeriod());
        CmnExtras.setCurrency(dto.getCurrency());
        CmnExtras.setPayMethod(dto.getPayMethod());
        CmnExtras.setPolicyholdersMinAge(dto.getPolicyholdersMinAge());
        CmnExtras.setPolicyholdersMaxAge(dto.getPolicyholdersMaxAge());
        CmnExtras.setVersion(oldVersion);
        CmnExtras CmnExtrasOld = cmnExtrasMapper.selectOne(CmnExtras);
        //更新前一版本数据(有效期至)
        //更新前一版本数据(有效期至 有效期自(新)-1)
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dto.getEffectiveStartDate());
        calendar.add(Calendar.DATE,-1);
        CmnExtrasOld.setEffectiveEndDate(calendar.getTime());
        cmnExtrasMapper.updateByPrimaryKeySelective(CmnExtrasOld);

        //创建新版本
        dto.setVersion(oldVersion+1);
        cmnExtrasMapper.insertSelective(dto);
        List<CmnExtras> list = new ArrayList<>();
        list.add(CmnExtrasOld);
        list.add(dto);
        return list;

    }
    
    @Override
	public boolean checkData(IRequest request, CmnExtras dto) {
		boolean check=true;
		//校验年龄是否重叠
		CmnExtras basic=new CmnExtras();
		basic.setSupplierId(dto.getSupplierId());
		basic.setItemId(dto.getItemId());
		basic.setContributionPeriod(dto.getContributionPeriod());
		basic.setCurrency(dto.getCurrency());
		basic.setPayMethod(dto.getPayMethod());
		List<CmnExtras> list=cmnExtrasMapper.select(basic);
		for(CmnExtras cb:list){
			if( (cb.getPolicyholdersMinAge()<=dto.getPolicyholdersMinAge() && dto.getPolicyholdersMinAge()<=cb.getPolicyholdersMaxAge())
					|| (cb.getPolicyholdersMaxAge()>=dto.getPolicyholdersMaxAge() && dto.getPolicyholdersMaxAge()>=cb.getPolicyholdersMinAge()) ){
//				throw new RuntimeException("新增异常：年龄不能与原有数据重叠！");
				check=false;
			}
			if( (dto.getPolicyholdersMinAge()<=cb.getPolicyholdersMinAge() && cb.getPolicyholdersMinAge()<=dto.getPolicyholdersMaxAge())
					|| (dto.getPolicyholdersMaxAge()>=cb.getPolicyholdersMaxAge() && cb.getPolicyholdersMaxAge()>=dto.getPolicyholdersMinAge()) ){
					check=false;
			}
		}
		return check;
	}
}