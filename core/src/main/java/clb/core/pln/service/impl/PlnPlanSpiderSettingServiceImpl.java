package clb.core.pln.service.impl;

import clb.core.pln.mapper.PlnPlanSpiderSettingMapper;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.pln.dto.PlnPlanSpiderSetting;
import clb.core.pln.service.IPlnPlanSpiderSettingService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PlnPlanSpiderSettingServiceImpl extends BaseServiceImpl<PlnPlanSpiderSetting> implements IPlnPlanSpiderSettingService{

    @Autowired
    private PlnPlanSpiderSettingMapper plnPlanSpiderSettingMapper;

    @Override
    public List<PlnPlanSpiderSetting> findAll(IRequest request, PlnPlanSpiderSetting sorDeliveries, int page, int pagesize) {
        /**
         *created by 甘霖  on 2017年3月30日
         *lin.gan@hand-china.com
         */
        PageHelper.startPage(page, pagesize);
        return plnPlanSpiderSettingMapper.findAll(sorDeliveries);
    }
}