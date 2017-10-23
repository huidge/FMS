package clb.core.commission.service.impl;

import clb.core.commission.mapper.CmnTeamCommissionMapper;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.commission.dto.CmnTeamCommission;
import clb.core.commission.service.ICmnTeamCommissionService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@Service
@Transactional
public class CmnTeamCommissionServiceImpl extends BaseServiceImpl<CmnTeamCommission> implements ICmnTeamCommissionService{

    @Autowired
    private CmnTeamCommissionMapper cmnTeamCommissionMapper;


    @Override
    public List<CmnTeamCommission> queryBasic(IRequest requestContext, CmnTeamCommission dto, int page, int pageSize) {
        PageHelper.startPage(page,pageSize);
        return cmnTeamCommissionMapper.queryBasic(dto);
    }

    @Override
    public Long queryMaxVersion(CmnTeamCommission cmnTeamCommission) {
        Long maxVersion = cmnTeamCommissionMapper.queryMaxVersion(cmnTeamCommission);
        if (maxVersion==null){
            return 0L;
        }else {
            return maxVersion;
        }
    }

    @Override
    public List<CmnTeamCommission> addVersion(IRequest request, CmnTeamCommission dto, Long oldVersion) {
        CmnTeamCommission cmnTeamCommission = new CmnTeamCommission();

        //查询前一版本数据
        cmnTeamCommission.setChannelId(dto.getChannelId());
        cmnTeamCommission.setSubChannelId(dto.getSubChannelId());
        cmnTeamCommission.setVersion(oldVersion);
        CmnTeamCommission cmnTeamCommissionOld = cmnTeamCommissionMapper.selectOne(cmnTeamCommission);
        //更新前一版本数据(有效期至 有效期自(新)-1)
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dto.getEffectiveStartDate());
        calendar.add(Calendar.DATE,-1);
        cmnTeamCommissionOld.setEffectiveEndDate(calendar.getTime());
        cmnTeamCommissionMapper.updateByPrimaryKeySelective(cmnTeamCommissionOld);

        //创建新版本
        dto.setVersion(oldVersion+1);
        cmnTeamCommissionMapper.insertSelective(dto);
        List<CmnTeamCommission> list = new ArrayList<>();
        list.add(cmnTeamCommissionOld);
        list.add(dto);
        return list;
    }
}