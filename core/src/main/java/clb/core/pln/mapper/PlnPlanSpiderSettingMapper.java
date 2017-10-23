package clb.core.pln.mapper;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.pln.dto.PlnPlanSpiderSetting;

import java.util.List;

public interface PlnPlanSpiderSettingMapper extends Mapper<PlnPlanSpiderSetting>{
    /**
     * 查询数据
     * created by gan on 2017/3/30
     * lin.gan@hand-china.com
     */
    List<PlnPlanSpiderSetting> findAll(PlnPlanSpiderSetting plnPlanSpiderSetting);

    /**
     * 通过计划书请求编号查询爬虫名
     * created by gan on 2017/3/30
     * lin.gan@hand-china.com
     */
    String selectSpiderName(String requestNumber);

    Integer selectPlanSpiderCount(String requestNumber);


    /**
     * 通过计划书数据查询账户密码
     * created by gan on 2017/8/23
     * lin.gan@hand-china.com
     */
    PlnPlanSpiderSetting selectUserPass(PlnPlanSpiderSetting plnPlanSpiderSetting);
}