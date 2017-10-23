package clb.core.pln.mapper;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.pln.dto.PlnPlanSpiderUserpass;

import java.util.List;

public interface PlnPlanSpiderUserpassMapper extends Mapper<PlnPlanSpiderUserpass>{
    /**
     * 查询数据
     * created by gan on 2017/8/23
     * lin.gan@hand-china.com
     */
    List<PlnPlanSpiderUserpass> findAll(PlnPlanSpiderUserpass plnPlanSpiderUserpass);


}